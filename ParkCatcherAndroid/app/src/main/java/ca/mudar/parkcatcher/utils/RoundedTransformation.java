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
/*
 * By Andy Prock
 * Source: https://gist.github.com/aprock/6213395
 * Modified to round topLeft and bottomLeft corners only
 */

package ca.mudar.parkcatcher.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

// enables hardware accelerated rounded corners
// original idea here : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
public class RoundedTransformation implements com.squareup.picasso.Transformation {
    private static final boolean LEFT_CORNERS_ONLY = true;
    private final int radius;
    private final int margin;  // dp
    private final String key;

    // radius is corner radii in dp
    // margin is the board in dp
    public RoundedTransformation(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
        this.key = "rounded(radius=" + radius + ", margin=" + margin + ")";
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final int width = source.getWidth();
        final int halfWidth = width / 2;
        final int height = source.getHeight();

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        if (LEFT_CORNERS_ONLY) {
            canvas.drawRect(new RectF(halfWidth, margin, width - margin, height - margin), paint);
            canvas.drawRoundRect(new RectF(margin, margin, halfWidth + radius, height - margin), radius, radius, paint);
        } else {
            canvas.drawRoundRect(new RectF(margin, margin, width - margin, height - margin), radius, radius, paint);
        }

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return key;
    }
}