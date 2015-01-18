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

import android.net.Uri;

import java.util.List;

import ca.mudar.parkcatcher.Const;

public class WebsiteUriHelper {

    public static String getAddressFromUri(Uri uri) {
        String address = null;

        List<String> pathSegments = uri.getPathSegments();

        // http://www.capteurdestationnement.com/map/search/2/15.5/12/h2w2e7

        if ((pathSegments.size() == 6)
                && (pathSegments.get(0).equals(Const.BundleExtras.URL_PATH_MAP))
                && (pathSegments.get(1).equals(Const.BundleExtras.URL_PATH_SEARCH))) {

            try {
                address = pathSegments.get(5);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return address;
    }
}
