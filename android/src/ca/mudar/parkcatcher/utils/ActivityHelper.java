
package ca.mudar.parkcatcher.utils;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.AboutActivity;
import ca.mudar.parkcatcher.ui.activities.HelpActivity;
import ca.mudar.parkcatcher.ui.activities.MyPreferenceActivity;
import ca.mudar.parkcatcher.ui.activities.MyPreferenceActivityHC;

import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Intent;

public class ActivityHelper {
    @SuppressWarnings("unused")
    private static final String TAG = "ActivityHelper";

    protected Activity mActivity;

    /**
     * Instance Creator.
     * 
     * @param activity
     * @return
     */
    public static ActivityHelper createInstance(Activity activity) {
        System.setProperty("http.keepAlive", "false");
        return new ActivityHelper(activity);
    }

    /**
     * The Constructor.
     * 
     * @param activity
     */
    protected ActivityHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * @param item The selected menu item
     * @param indexSection The current section
     * @return boolean
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        int id = item.getItemId();

        if (id == R.id.menu_help) {
            intent = new Intent(mActivity, HelpActivity.class);
            mActivity.startActivity(intent);
            return true;
        }
        else if (id == R.id.menu_settings) {

            if (Const.SUPPORTS_HONEYCOMB) {
                intent = new Intent(mActivity, MyPreferenceActivityHC.class);
            } else {
                intent = new Intent(mActivity, MyPreferenceActivity.class);
            }

            mActivity.startActivity(intent);
            return true;
        }
        else if (id == R.id.menu_about) {
            intent = new Intent(mActivity, AboutActivity.class);
            mActivity.startActivity(intent);
            return true;
        }

        return false;
    }
}
