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
 */

package ca.mudar.parkcatcher.io;

import ca.mudar.parkcatcher.io.JsonHandler.HandlerException;

import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Opens a local {@link Resources#getXml(int)} and passes the resulting
 * {@link XmlPullParser} to the given {@link XmlHandler}.
 */
public class LocalExecutor {
    @SuppressWarnings("unused")
    private static final String TAG = "LocalExecutor";
    
    private ContentResolver mResolver;

    public LocalExecutor(ContentResolver resolver) {
        mResolver = resolver;
    }

    /**
     * Loads the JSON text resource with the given ID and returns the JSON
     * content.
     */
    public static String loadResourceJson(Context context, int resource) throws IOException {
        InputStream is = context.getResources().openRawResource(resource);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        return writer.toString();
    }

    public void execute(Context context, int resId, JsonHandler handler) throws HandlerException {
        JSONTokener parser;
        try {
            parser = new JSONTokener(loadResourceJson(context, resId));
            handler.parseAndApply(parser, mResolver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
