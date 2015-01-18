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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import ca.mudar.parkcatcher.Const;

public class AnimHelper {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void crossfade(final View fadeOutView, final View fadeInView) {
        if (!Const.SUPPORTS_HONEYCOMB_MR1) {
            fadeOutView.setVisibility(View.INVISIBLE);
            fadeInView.setVisibility(View.VISIBLE);
            return;
        }

        fadeInView.setAlpha(0f);
        fadeInView.setVisibility(View.VISIBLE);

        fadeOutView.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeOutView.setVisibility(View.INVISIBLE);
                        fadeInView.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .setListener(null);
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void updateTextView(final TextView view, final String text) {
        if (!Const.SUPPORTS_HONEYCOMB_MR1) {
            view.setText(text);
            return;
        }

        view.setAlpha(1f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setText(text);

                        view.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .setListener(null);
                    }
                });
    }

}
