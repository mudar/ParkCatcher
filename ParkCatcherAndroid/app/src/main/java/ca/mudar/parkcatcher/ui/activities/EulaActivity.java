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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.LocalAssets;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.base.ToolbarActivity;
import ca.mudar.parkcatcher.utils.EulaHelper;

public class EulaActivity extends ToolbarActivity {
    private static final String TAG = "EulaActivity";

    public static Intent newIntent(Context context) {
        final Intent intent = new Intent(context, EulaActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_eula);
        setContentView(R.layout.activity_eula);
        getActionBarToolbar();

        if (EulaHelper.hasAcceptedEula(this)) {
            // Hide the footer and show the upArrow
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            findViewById(R.id.btn_eula_accept).setVisibility(View.GONE);
            findViewById(R.id.eula_footer_border).setVisibility(View.GONE);
        }

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
