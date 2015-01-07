/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modifications:
 * - Copied from IOSched
 * - Renamed package
 * - Replaced XMLParser by JSONTokener
 * - Removed reference to Blocks and Tracks
 * - Replaced ScheduleContract by SecurityContract  
 */

package ca.mudar.parkcatcher.utils;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import ca.mudar.parkcatcher.provider.ParkingContract;
import ca.mudar.parkcatcher.provider.ParkingContract.SyncColumns;

/**
 * Various utility methods used by JsonHandler implementations.
 */
public class ParserUtils {
    protected static final String TAG = "ParserUtils";

    /** Used to sanitize a string to be {@link Uri} safe. */
    private static final Pattern sSanitizePattern = Pattern.compile("[^a-z0-9-_]");
    private static final Pattern sParenPattern = Pattern.compile("\\(.*?\\)");

    /** Used to split a comma-separated string. */
    private static final Pattern sCommaPattern = Pattern.compile("\\s*,\\s*");

    private static Time sTime = new Time();

    /**
     * Sanitize the given string to be {@link Uri} safe for building
     * {@link ContentProvider} paths.
     */
    public static String sanitizeId(String input) {
        return sanitizeId(input, false);
    }

    /**
     * Sanitize the given string to be {@link Uri} safe for building
     * {@link ContentProvider} paths.
     */
    public static String sanitizeId(String input, boolean stripParen) {
        if (input == null)
            return null;
        if (stripParen) {
            // Strip out all parenthetical statements when requested.
            input = sParenPattern.matcher(input).replaceAll("");
        }
        return sSanitizePattern.matcher(input.toLowerCase()).replaceAll("");
    }

    /**
     * Split the given comma-separated string, returning all values.
     */
    public static String[] splitComma(CharSequence input) {
        if (input == null)
            return new String[0];
        return sCommaPattern.split(input);
    }

    /**
     * @author Andrew Pearson @link http://blog.andrewpearson.org/2010/07/android-why-to-use-json-and-how-to-use.html}
     * @param input URL of JSON resources
     * @return String Raw content, requires use of JSONArray() and
     *         getJSONObject()
     * @throws IOException
     * @throws JSONException
     */
    public static JSONTokener newJsonTokenerParser(InputStream input) throws IOException {
        String queryResult = "";

        BufferedInputStream bis = new BufferedInputStream(input);
        ByteArrayBuffer baf = new ByteArrayBuffer(50);
        int read = 0;
        int bufSize = 512;
        byte[] buffer = new byte[bufSize];
        while (true) {

            read = bis.read(buffer);
            if (read == -1) {
                break;
            }
            baf.append(buffer, 0, read);
        }
        final byte[] ba = baf.toByteArray();
        queryResult = new String(ba);

        JSONTokener data = new JSONTokener(queryResult);
        return data;
    }

    public static JSONTokener newJsonTokenerParser(URL url) throws IOException {

        final URLConnection tc = url.openConnection();

        final BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
        String line;
        String str = "";
        try {
            while ((line = in.readLine()) != null) {
                str += line;
            }

        } catch (IOException e) {
            throw e;
        } finally {
            in.close();
        }

        try {
            final JSONTokener parser = new JSONTokener(str);
            return parser;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Parse the given string as a RFC 3339 timestamp, returning the value as
     * milliseconds since the epoch.
     */
    public static long parseTime(String time) {
        sTime.parse3339(time);
        return sTime.toMillis(false);
    }

    /**
     * Query and return the {@link SyncColumns#UPDATED} time for the requested
     * {@link Uri}. Expects the {@link Uri} to reference a single item.
     */
    public static long queryItemUpdated(Uri uri, ContentResolver resolver) {
        final String[] projection = {
                SyncColumns.UPDATED
        };
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            } else {
                return ParkingContract.UPDATED_NEVER;
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Query and return the newest {@link SyncColumns#UPDATED} time for all
     * entries under the requested {@link Uri}. Expects the {@link Uri} to
     * reference a directory of several items.
     */
    public static long queryDirUpdated(Uri uri, ContentResolver resolver) {
        final String[] projection = {
                "MAX(" + SyncColumns.UPDATED + ")"
        };
        final Cursor cursor = resolver.query(uri, projection, null, null, null);
        try {
            cursor.moveToFirst();
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }

    /** XML tag constants used by the Atom standard. */
    public interface AtomTags {
        String ENTRY = "entry";
        String UPDATED = "updated";
        String TITLE = "title";
        String LINK = "link";
        String CONTENT = "content";

        String REL = "rel";
        String HREF = "href";
    }
}
