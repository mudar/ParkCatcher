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

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;

import ca.mudar.parkcatcher.Const;

public class TypefaceHelper {
    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(Const.TypeFaces._COUNT);

    public static Typeface getTypeface(Context context, String typefaceName) {
        final String typefaceCode = typefaceName.replace("fonts/", "");
        Typeface typeface = sTypefaceCache.get(typefaceCode);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(),
                    typefaceName);

            // Cache the loaded Typeface
            sTypefaceCache.put(typefaceCode, typeface);
        }

        return typeface;
    }
}
