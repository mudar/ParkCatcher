
package ca.mudar.parkcatcher.ui.fragments;

import ca.mudar.parkcatcher.R;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends SherlockFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        /**
         * Display version number in the About header.
         */
        ((TextView) root.findViewById(R.id.about_project_version))
                .setText(String.format(getResources().getString(R.string.about_project_version),
                        getResources().getString(R.string.app_version)));

        /**
         * Handle web links.
         */
        MovementMethod method = LinkMovementMethod.getInstance();
        ((TextView) root.findViewById(R.id.about_credits_1)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_credits_2)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_credits_3)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_open_data)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_project_url)).setMovementMethod(method);

        return root;
    }
}
