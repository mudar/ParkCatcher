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
 * - copied from iosched
 * - renamed package 
 */

package ca.mudar.parkcatcher.io;

import ca.mudar.parkcatcher.io.JsonHandler.JsonHandlerException;
import ca.mudar.parkcatcher.utils.ParserUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;

import android.content.ContentResolver;

import java.io.IOException;
import java.io.InputStream;

/**
 * Executes an {@link HttpUriRequest} and passes the result as an
 * {@link XmlPullParser} to the given {@link XmlHandler}.
 */
public class RemoteExecutor {
    @SuppressWarnings("unused")
    private static final String TAG = "RemoteExecutor";
    private final HttpClient mHttpClient;
    private final ContentResolver mResolver;

    public RemoteExecutor(HttpClient httpClient, ContentResolver resolver) {
        mHttpClient = httpClient;
        mResolver = resolver;
    }

    /**
     * Execute a {@link HttpGet} request, passing a valid response through
     * {@link XmlHandler#parseAndApply(XmlPullParser, ContentResolver)}.
     */

    public void executeGet(String url, JsonHandler handler) throws JsonHandlerException {
        final HttpUriRequest request = new HttpGet(url);
        execute(request, handler);
    }

    public void execute(HttpUriRequest request, JsonHandler handler) throws JsonHandlerException {
        try {
            final HttpResponse resp = mHttpClient.execute(request);
            final int status = resp.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new JsonHandlerException("Unexpected server response " + resp.getStatusLine()
                        + " for " + request.getRequestLine());
            }

            final InputStream input = resp.getEntity().getContent();
            try {
                final JSONTokener parser = ParserUtils.newJsonTokenerParser(input);
                handler.parseAndApply(parser, mResolver);
            } finally {
                if (input != null)
                    input.close();
            }
        } catch (JsonHandlerException e) {
            throw e;
        } catch (IOException e) {
            throw new JsonHandlerException("Problem reading remote response for "
                    + request.getRequestLine(), e);
        }

    }
}
