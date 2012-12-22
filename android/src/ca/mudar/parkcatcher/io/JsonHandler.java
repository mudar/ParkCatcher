/*
    Park Catcher Montréal
    Find a free parking in the nearest residential street when driving in
    Montréal. A Montréal Open Data project.

    Copyright (C) 2012 Mudar Noufal <mn@mudar.ca>

    This file is part of Park Catcher Montréal.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Based on XmlHandler, modified for JSON.
 */

package ca.mudar.parkcatcher.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Abstract class that handles reading and parsing an {@link JSONArray} into a
 * set of {@link ContentProviderOperation}. It catches exceptions and rethrows
 * them as {@link JSONHandlerException}. Any local {@link ContentProvider}
 * exceptions are considered unrecoverable.
 * <p>
 * This class is only designed to handle simple one-way synchronization.
 */
public abstract class JsonHandler {
    @SuppressWarnings("unused")
    private static final String TAG = "JSONHandler";

    private final String mAuthority;

    public JsonHandler(String authority) {
        mAuthority = authority;
    }

    /**
     * Parse the given {@link JSONTokener}, turning into a series of
     * {@link ContentProviderOperation} that are immediately applied using the
     * given {@link ContentResolver}.
     */
    public void parseAndApply(JSONTokener jsonTokener, ContentResolver resolver)
            throws JsonHandlerException {
        try {
            final ArrayList<ContentProviderOperation> batch = parse(jsonTokener, resolver);
            resolver.applyBatch(mAuthority, batch);
        } catch (IOException e) {
            throw new JsonHandlerException("Problem reading response", e);
        } catch (JSONException e) {
            throw new JsonHandlerException("Problem parsing JSON response", e);
        } catch (RemoteException e) {
            throw new RuntimeException("Problem applying batch operation", e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException("Problem applying batch operation", e);
        }
    }

    /**
     * Parse the given {@link JSONTokener}, returning a set of
     * {@link ContentProviderOperation} that will bring the
     * {@link ContentProvider} into sync with the parsed data.
     */
    public abstract ArrayList<ContentProviderOperation> parse(JSONTokener jsonTokener,
            ContentResolver resolver) throws JSONException, IOException;

    /**
     * General {@link IOException} that indicates a problem occurred while
     * parsing a {@link JSONTokener}.
     */
    public static class JsonHandlerException extends IOException {
        private static final long serialVersionUID = -2336169334778645289L;

        public JsonHandlerException(String message) {
            super(message);
        }

        public JsonHandlerException(String message, Throwable cause) {
            super(message);
            initCause(cause);
        }

        @Override
        public String toString() {
            if (getCause() != null) {
                return getLocalizedMessage() + ": " + getCause();
            } else {
                return getLocalizedMessage();
            }
        }
    }
}
