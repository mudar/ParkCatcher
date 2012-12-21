
package ca.mudar.parkcatcher.ui.activities;

import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.ui.fragments.AboutFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class AboutActivity extends SherlockFragmentActivity {
    protected static final String TAG = "AboutActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        ((ParkingApp) getApplicationContext()).updateUiLanguage();
        
        FragmentManager fm = getSupportFragmentManager();
        
        if (fm.findFragmentById(android.R.id.content) == null) {
            AboutFragment about = new AboutFragment();
            fm.beginTransaction().add(android.R.id.content, about).commit();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
