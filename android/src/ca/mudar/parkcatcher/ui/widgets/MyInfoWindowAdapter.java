
package ca.mudar.parkcatcher.ui.widgets;

import ca.mudar.parkcatcher.R;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

public class MyInfoWindowAdapter implements InfoWindowAdapter {

    private final View mView;

    public MyInfoWindowAdapter(Activity activity) {

        mView = activity.getLayoutInflater().inflate(R.layout.custom_info_window, null);
    }

    /**
     * Override to transform Snippet String into SpannableString, allowing the use of line-separators. 
     */
    @Override
    public View getInfoContents(Marker marker) {

        final String snippet = marker.getSnippet();
        final TextView snippetUi = ((TextView) mView.findViewById(R.id.snippet));
        if (snippet == null) {
            snippetUi.setVisibility(View.GONE);
        } else {
            final SpannableString snippetText = new SpannableString(snippet);
            snippetUi.setText(snippetText);
        }
        return mView;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

}
