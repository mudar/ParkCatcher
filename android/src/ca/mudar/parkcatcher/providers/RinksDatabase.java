/*
    Patiner Montréal for Android.
    Information about outdoor rinks in the city of Montréal: conditions,
    services, contact, map, etc.

    Copyright (C) 2010 Mudar Noufal <mn@mudar.ca>

    This file is part of Patiner Montréal for Android.

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

package ca.mudar.parkcatcher.providers;



import ca.mudar.parkcatcher.providers.RinksContract.BoroughsColumns;
import ca.mudar.parkcatcher.providers.RinksContract.FavoritesColumns;
import ca.mudar.parkcatcher.providers.RinksContract.ParksColumns;
import ca.mudar.parkcatcher.providers.RinksContract.RinksColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class RinksDatabase extends SQLiteOpenHelper {
    private static final String TAG = "RinksDatabase";

    private static final String DATABASE_NAME = "patinoires_mtl";
    private static final int DATABASE_VERSION = 10;

    public static interface Tables {
        final String BOROUGHS = "boroughs";
        final String PARKS = "parks";
        final String RINKS = "rinks";
        final String FAVORITES = "favorites";

        final String BOROUGHS_JOIN_PARKS_RINKS = "boroughs "
                + "LEFT OUTER JOIN parks ON boroughs.borough_id=parks.borough_id "
                + "LEFT OUTER JOIN rinks ON parks.park_id=rinks.park_id ";

        final String BOROUGHS_JOIN_PARKS_RINKS_FAVORITES = "boroughs "
                + "LEFT OUTER JOIN parks ON boroughs.borough_id=parks.park_borough_id "
                + "LEFT OUTER JOIN rinks ON parks.park_id=rinks.rink_park_id "
                + "LEFT OUTER JOIN favorites ON rinks.rink_rink_id=favorites.rink_id ";
        final String PARKS_JOIN_RINKS_FAVORITES = "parks "
                + "LEFT OUTER JOIN rinks ON parks.park_id=rinks.rink_park_id "
                + "LEFT OUTER JOIN favorites ON rinks.rink_rink_id=favorites.rink_id ";
    }

    public RinksDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Creating database tables. DB name: " + DATABASE_NAME);
        Log.w(TAG, "Creating 4 database tables: " + Tables.BOROUGHS + ", " + Tables.PARKS + ", "
                + Tables.RINKS + " and " + Tables.FAVORITES);

        db.execSQL("CREATE TABLE " + Tables.BOROUGHS + " ( "
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + BoroughsColumns.BOROUGH_ID + " TEXT NOT NULL , "
                + BoroughsColumns.BOROUGH_NAME + " TEXT NOT NULL DEFAULT '' COLLATE UNICODE, "
                + BoroughsColumns.BOROUGH_CREATED_AT + " DATE , "
                + BoroughsColumns.BOROUGH_UPDATED_AT
                + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP , "
                + "UNIQUE (" + BoroughsColumns.BOROUGH_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.PARKS + " ( "
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ParksColumns.PARK_ID + " TEXT NOT NULL , "
                + ParksColumns.PARK_BOROUGH_ID + " TEXT NOT NULL , "
                + ParksColumns.PARK_NAME + " TEXT NOT NULL DEFAULT '' COLLATE UNICODE, "
                + ParksColumns.PARK_GEO_LAT + " TEXT NULL , "
                + ParksColumns.PARK_GEO_LNG + " TEXT NULL , "
                + ParksColumns.PARK_GEO_DISTANCE + " INTEGER DEFAULT '0' , "
                + ParksColumns.PARK_ADDRESS + " TEXT NULL , "
                + ParksColumns.PARK_PHONE + " TEXT NULL , "
                + ParksColumns.PARK_CREATED_AT + " DATE , "
                + "UNIQUE (" + ParksColumns.PARK_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.RINKS + " ( "
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RinksColumns.RINK_ID + " INTEGER NOT NULL , "
                + RinksColumns.RINK_PARK_ID + " TEXT NOT NULL , "
                + RinksColumns.RINK_KIND_ID + " INTEGER NOT NULL DEFAULT '0' , "
                + RinksColumns.RINK_NAME + " TEXT NOT NULL COLLATE UNICODE , "
                + RinksColumns.RINK_DESC_FR + " TEXT NULL , "
                + RinksColumns.RINK_DESC_EN + " TEXT NULL , "
                + RinksColumns.RINK_IS_CLEARED + " BOOLEAN NOT NULL DEFAULT '0' , "
                + RinksColumns.RINK_IS_FLOODED + " BOOLEAN NOT NULL DEFAULT '0' , "
                + RinksColumns.RINK_IS_RESURFACED + " BOOLEAN NOT NULL DEFAULT '0' , "
                + RinksColumns.RINK_CONDITION + " INTEGER NOT NULL DEFAULT '0' ,"
                + RinksColumns.RINK_CREATED_AT + " DATE , "
                + "UNIQUE (" + RinksColumns.RINK_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.FAVORITES + " ( "
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + FavoritesColumns.FAVORITE_RINK_ID + " INTEGER UNIQUE NOT NULL );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ". Old data will be destroyed, except for " + Tables.FAVORITES + ". DB name: "
                + DATABASE_NAME);

        db.execSQL("DROP TABLE IF EXISTS " + Tables.BOROUGHS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PARKS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.RINKS);
        // db.execSQL("DROP TABLE IF EXISTS " + Tables.FAVORITES);

        onCreate(db);
    }
}
