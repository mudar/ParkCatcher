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

package ca.mudar.parkcatcher.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;

public class IntentHelper {
    private static final String SEND_INTENT_TYPE = "text/plain";

    public static Intent startPlaystoreActivityForResult(Context context, int resource) {

        return null;
    }

    /**
     * Launch playstore (with fallback)
     *
     * @param context     Context
     * @param packageName String resource id of the app package name
     * @return
     */
    public static void sendPlaystoreIntent(Context context, int packageName) {
        final Resources res = context.getResources();
        final String sPackageName = res.getString(packageName);
        // Open playstore
        try {
            // Try market:// intent
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                    String.format(res.getString(R.string.uri_playstore), sPackageName)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.addFlags(Const.SUPPORTS_LOLLIPOP ?
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT : Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Fallback to http:// playstore website
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(
                    String.format(res.getString(R.string.url_playstore_fallback), sPackageName)));
            context.startActivity(intent);
        }
    }

    /**
     * Native Share, using resources
     *
     * @param context
     * @param subjectExtra
     * @param textExtra
     */
    public static void sendShareIntent(Context context, int subjectExtra, int textExtra) {
        final Resources res = context.getResources();
        sendShareIntent(context, res.getString(subjectExtra), res.getString(textExtra));
    }

    /**
     * Native Share, using strings
     *
     * @param context
     * @param subjectExtra
     * @param textExtra
     */
    public static void sendShareIntent(Context context, String subjectExtra, String textExtra) {
        final Bundle extras = new Bundle();
        extras.putString(Intent.EXTRA_SUBJECT, subjectExtra);
        extras.putString(Intent.EXTRA_TEXT, textExtra);

        final Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType(SEND_INTENT_TYPE);
        sendIntent.putExtras(extras);
        context.startActivity(sendIntent);
    }
}
