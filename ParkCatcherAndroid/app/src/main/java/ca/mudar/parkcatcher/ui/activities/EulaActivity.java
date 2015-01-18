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

package ca.mudar.parkcatcher.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.LocalAssets;
import ca.mudar.parkcatcher.R;

public class EulaActivity extends ActionBarActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "EulaActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eula);

        WebView v = (WebView) findViewById(R.id.webview);
        v.setWebViewClient(new MyWebViewClient());
        v.setVisibility(View.VISIBLE);
        v.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        v.loadUrl("file:///android_asset/" + LocalAssets.LICENSE);
    }

    public void acceptEula(View v) {
        Intent intent = new Intent();
        setResult(Const.RequestCodes.EULA, intent);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    public void declineEula(View v) {
        Intent intent = new Intent();
        setResult(Const.RequestCodes.EULA, intent);
        setResult(RESULT_CANCELED, intent);
        this.finish();
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

}
