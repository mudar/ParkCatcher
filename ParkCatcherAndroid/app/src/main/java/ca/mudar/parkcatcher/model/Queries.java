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

package ca.mudar.parkcatcher.model;

import ca.mudar.parkcatcher.provider.ParkingContract;

public class Queries {

    public static interface PostsOverlays {
        int _TOKEN = 100;

        final String[] PROJECTION = new String[]{
                ParkingContract.Posts.ID_POST,
                ParkingContract.Posts.LAT,
                ParkingContract.Posts.LNG,
                ParkingContract.PanelsCodes.CONCAT_DESCRIPTION,
                ParkingContract.Posts.IS_STARRED
        };
        final int ID_POST = 0;
        final int LAT = 1;
        final int LNG = 2;
        final int CONCAT_DESCRIPTION = 3;
        final int IS_STARRED = 4;
    }

    public static interface Favorites {
        int _TOKEN = 110;
        final String[] FAVORITES_SUMMARY_PROJECTION = new String[]{
                ParkingContract.Posts._ID,
                ParkingContract.Posts.ID_POST,
                ParkingContract.Favorites.LABEL,
                ParkingContract.Posts.GEO_DISTANCE,
                ParkingContract.Posts.IS_FORBIDDEN,
        };
        final int _ID = 0;
        final int ID_POST = 1;
        final int LABEL = 2;
        final int GEO_DISTANCE = 3;
        final int IS_FORBIDDEN = 4;
    }

}
