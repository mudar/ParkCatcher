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
 * -Imported from IOSched 
 * -Changed package name
 * -Changed Resources package name
 * -Replaced R.string.eula_text by asstes/euls.html (adding lines 73-89)
 * -Added MyWebViewClient to handle links in the eula
 * -Replaced dialog by activity
 * -Removed AsyncTask
 */

package ca.mudar.parkcatcher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.PrefsNames;
import ca.mudar.parkcatcher.ui.activities.EulaActivity;

/**
 * A helper for showing EULAs and storing a {@link SharedPreferences} bit
 * indicating whether the user has accepted.
 */
public class EulaHelper {
    protected static String TAG = "EulaHelper";

    public static boolean hasAcceptedEula(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PrefsNames.HAS_ACCEPTED_EULA, false);
    }

    /**
     * Show End User License Agreement.
     * 
     * @param accepted True IFF user has accepted license already, which means
     *            it can be dismissed. If the user hasn't accepted, then the
     *            EULA must be accepted or the program exits.
     * @param activity Activity started from.
     */

    public static void showEula(final boolean accepted, final Activity activity) {
        Intent intent = new Intent(activity, EulaActivity.class);
        activity.startActivityForResult(intent, Const.RequestCodes.EULA);
    }

    public static boolean acceptEula(int resultCode, final Activity activity) {
        if (resultCode == Activity.RESULT_OK) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity
                    .getApplicationContext());
            prefs.edit().putBoolean(PrefsNames.HAS_ACCEPTED_EULA, true).commit();
            return true;
        }
        else {
            Log.v(TAG, "User has declined the End User License Agreement!");
            return false;
        }
    }

}
