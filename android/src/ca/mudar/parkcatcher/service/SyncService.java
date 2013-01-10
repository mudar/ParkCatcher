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
 * - Imported from IOSched 
 * - Changed package name
 * - Changed parsed data
 * - Removed SharedPreferences and versions management
 * - Removed the ResultReceiver
 */

package ca.mudar.parkcatcher.service;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.Api;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.io.JsonHandler.HandlerException;
import ca.mudar.parkcatcher.io.LocalExecutor;
import ca.mudar.parkcatcher.io.PanelsCodesHandler;
import ca.mudar.parkcatcher.io.PanelsCodesRulesHandler;
import ca.mudar.parkcatcher.io.PanelsHandler;
import ca.mudar.parkcatcher.io.PostsHandler;
import ca.mudar.parkcatcher.io.RemoteExecutor;
import ca.mudar.parkcatcher.provider.ParkingContract;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Background {@link Service} that synchronizes data living in
 * {@link PlacemarkProvider}. Reads data from remote sources
 */
public class SyncService extends IntentService {
    private static final String TAG = "SyncService";

    public static final String EXTRA_STATUS_RECEIVER =
            "ca.mudar.parkcatcher.extra.STATUS_RECEIVER";

    public static final int STATUS_RUNNING = 0x1;
    public static final int STATUS_ERROR = 0x2;
    public static final int STATUS_FINISHED = 0x3;
    public static final int STATUS_IGNORED = 0x4;

    private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private LocalExecutor mLocalExecutor;
    private RemoteExecutor mRemoteExecutor;

    public SyncService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final HttpClient httpClient = getHttpClient(this);
        final ContentResolver resolver = getContentResolver();

        mLocalExecutor = new LocalExecutor(resolver);
        mRemoteExecutor = new RemoteExecutor(httpClient, resolver);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final boolean isLocal = intent.getBooleanExtra(Const.INTENT_EXTRA_SERVICE_LOCAL, false);
        final boolean isRemote = intent.getBooleanExtra(Const.INTENT_EXTRA_SERVICE_REMOTE, false);

        final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
        if (receiver != null) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        }

        try {
            // Bulk of sync work, performed by executing several fetches from
            // local and online sources.
            if (isLocal) {
                syncLocal();
                ((ParkingApp) getApplicationContext()).setHasLoadedData(true);
            }
            if (isRemote) {
                syncRemote();
                ((ParkingApp) getApplicationContext()).setHasLoadedData(true);
            }

        } catch (HandlerException e) {
            e.printStackTrace();

            if (receiver != null) {
                /**
                 * Pass back error to surface listener
                 */
                final Bundle bundle = new Bundle();
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }

        if (receiver != null) {
            receiver.send(STATUS_FINISHED, Bundle.EMPTY);
        }
    }

    /**
     * Fill database using local raw JSON data. Placeholders, code is not used.
     * 
     * @throws HandlerException
     */
    private void syncLocal() throws HandlerException {
        final long startLocal = System.currentTimeMillis();

        mLocalExecutor.execute(this, R.raw.panels_codes, new PanelsCodesHandler(
                ParkingContract.CONTENT_AUTHORITY));
        mLocalExecutor.execute(this, R.raw.panels_codes_rules, new PanelsCodesRulesHandler(
                ParkingContract.CONTENT_AUTHORITY));
        /**
         * Posts and Panels may create memory issues on some devices.
         */
        mLocalExecutor.execute(this, R.raw.posts,
                new PostsHandler(ParkingContract.CONTENT_AUTHORITY));
        mLocalExecutor.execute(this, R.raw.panels,
                new PanelsHandler(ParkingContract.CONTENT_AUTHORITY));

        Log.i(TAG, "Local sync duration: " + (System.currentTimeMillis() - startLocal) + " ms");
    }

    /**
     * Fill database using remote JSON data from the API. Tables processed are
     * `posts` and `panels`
     * 
     * @throws HandlerException
     */
    private void syncRemote() throws HandlerException {
        // Log.v(TAG, "Started remote sync...");
        final long startRemote = System.currentTimeMillis();

        mRemoteExecutor.executeGet(Const.Api.PANELS_CODES,
                new PanelsCodesHandler(ParkingContract.CONTENT_AUTHORITY));
        // Log.v(TAG, "PanelCodes synched");
        mRemoteExecutor.executeGet(Const.Api.PANELS_CODES_RULES,
                new PanelsCodesRulesHandler(ParkingContract.CONTENT_AUTHORITY));
        // Log.v(TAG, "PanelCodesRules synched");

        /**
         * Pagination is used for Posts and Panels to avoid memory issues.
         */
        for (int i = 0; i < Api.PAGES_POSTS; i++) {
            Log.v(TAG, "Syncing Posts # " + (i + 1));
            final String urlApi = String.format(Api.POSTS, i * Api.PAGINATION, Api.PAGINATION);
            mRemoteExecutor.executeGet(urlApi,
                    new PostsHandler(ParkingContract.CONTENT_AUTHORITY));
        }
        // Log.v(TAG, "Posts synched");

        for (int i = 0; i < Api.PAGES_PANELS; i++) {
            Log.v(TAG, "Syncing Panels # " + (i + 1));
            final String urlApi = String.format(Api.PANELS, i * Api.PAGINATION, Api.PAGINATION);
            mRemoteExecutor.executeGet(urlApi,
                    new PanelsHandler(ParkingContract.CONTENT_AUTHORITY));
        }
        // Log.v(TAG, "Panels synched");

        Log.i(TAG, "Remote sync duration: " + (System.currentTimeMillis() - startRemote) + " ms");
    }

    /**
     * Generate and return a {@link HttpClient} configured for general use,
     * including setting an application-specific user-agent string.
     */
    private static HttpClient getHttpClient(Context context) {
        final HttpParams params = new BasicHttpParams();

        // Use generous timeouts for slow mobile networks
        HttpConnectionParams.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
        HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);

        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setUserAgent(params, buildUserAgent(context));

        final DefaultHttpClient client = new DefaultHttpClient(params);

        client.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                // Add header to accept gzip content
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                // Inflate any responses compressed with gzip
                final HttpEntity entity = response.getEntity();
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        return client;
    }

    /**
     * Build and return a user-agent string that can identify this application
     * to remote servers. Contains the package name and version code.
     */
    private static String buildUserAgent(Context context) {
        try {
            final PackageManager manager = context.getPackageManager();
            final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            // Some APIs require "(gzip)" in the user-agent string.
            return info.packageName + "/" + info.versionName
                    + " (" + info.versionCode + ") (gzip)";
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped
     * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
     */
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

}
