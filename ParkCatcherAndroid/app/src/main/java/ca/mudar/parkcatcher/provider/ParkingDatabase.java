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

package ca.mudar.parkcatcher.provider;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.provider.ParkingContract.FavoritesColumns;
import ca.mudar.parkcatcher.provider.ParkingContract.PanelsCodesColumns;
import ca.mudar.parkcatcher.provider.ParkingContract.PanelsCodesRulesColumns;
import ca.mudar.parkcatcher.provider.ParkingContract.PanelsColumns;
import ca.mudar.parkcatcher.provider.ParkingContract.PostsColumns;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ParkingDatabase extends SQLiteAssetHelper {
    private static final String TAG = "ParkingDatabase";

    public static interface Tables {
        final String POSTS = "posts";
        final String PANELS = "panels";
        final String PANELS_CODES = "panels_codes";
        final String PANELS_CODES_RULES = "panels_codes_rules";
        final String FAVORITES = "favorites";

        // final String POSTS_JOIN_PANELS_PANELS_CODES = " posts "
        // + "INNER JOIN panels ON posts.id_post = panels.id_post "
        // +
        // "INNER JOIN panels_codes ON panels.id_panel_code = panels_codes._id ";

        final String POSTS_JOIN_PANELS_PANELS_CODES_FAVORITES = " posts "
                + "INNER JOIN panels ON posts.id_post = panels.id_post "
                + "INNER JOIN panels_codes ON panels.id_panel_code = panels_codes._id "
                + "LEFT JOIN favorites ON posts.id_post = favorites.id_post ";
        final String POSTS_JOIN_ALL = " posts "
                + "INNER JOIN panels ON posts.id_post = panels.id_post "
                + "INNER JOIN panels_codes ON panels.id_panel_code = panels_codes._id "
                + "LEFT JOIN panels_codes_rules ON panels.id_panel_code = panels_codes_rules.id_panel_code "
                + "LEFT JOIN favorites ON posts.id_post = favorites.id_post ";

        final String POSTS_JOIN_PANELS_PANELS_CODES_RULES = " posts "
                + "INNER JOIN panels ON posts.id_post = panels.id_post "
                + "INNER JOIN panels_codes_rules ON panels.id_panel_code = panels_codes_rules.id_panel_code ";

        final String POSTS_JOIN_FAVORITES_PANELS_PANELS_CODES = " posts "
                + "INNER JOIN favorites ON posts.id_post = favorites.id_post "
                + "INNER JOIN panels ON posts.id_post = panels.id_post "
                + "INNER JOIN panels_codes ON panels.id_panel_code = panels_codes._id ";

        final String POSTS_JOIN_FAVORITES_PANELS_PANELS_CODES_RULES = " posts "
                + "INNER JOIN favorites ON posts.id_post = favorites.id_post "
                + "INNER JOIN panels ON posts.id_post = panels.id_post "
                + "INNER JOIN panels_codes_rules ON panels.id_panel_code = panels_codes_rules.id_panel_code ";

        // final String PANELS_JOIN_PANELS_CODES_FAVORITES = " panels "
        // +
        // "INNER JOIN panels_codes ON panels.id_panel_code = panels_codes._id "
        // + "LEFT JOIN favorites ON panels.id_post = favorites.id_post ";

        final String PANELS_JOIN_PANELS_CODES_RULES = " panels "
                + "INNER JOIN panels_codes_rules ON panels.id_panel_code = panels_codes_rules.id_panel_code ";
        
        final String POSTS_JOIN_FAVORITES = " posts "
                + "INNER JOIN favorites ON posts.id_post = favorites.id_post ";

    }

    /** {@code INDEXES} clauses. */
    private interface Indexes {
        final String POSTS_ID_POST = "p_id_post";
        final String POSTS_LAT = "p_lat";
        final String POSTS_LNG = "p_lng";
        final String POSTS_GEO_DISTANCE = "p_geo_distance";

        final String PANELS_ID_PANEL = "pnl_id_panel";
        final String PANELS_ID_POST = "pnl_id_post";
        final String PANELS_ID_PANEL_CODE = "pnl_id_panel_code";

        final String CODES_CODE = "c_code";

        final String RULES_ID_PANEL_CODE = "r_id_panel_code";
        final String RULES_DAY_END = "r_day_end";
        final String RULES_DAY_START = "r_day_start";
        final String RULES_HOUR_DURATION = "r_hour_duration";
        final String RULES_MINUTES_DURATION = "r_minutes_duration";
        final String RULES_HOUR_START = "r_hour_start";
        final String RULES_HOUR_END = "r_hour_end";

        final String FAVORITES_ID_POST = "f_id_post";
    }

    /** {@code REFERENCES} clauses. */
    private interface References {
        String ID_POST = "REFERENCES " + Tables.POSTS + "(" + PostsColumns.ID_POST + ")";
        String ID_PANEL_CODE = "REFERENCES " + Tables.PANELS_CODES + "(" + BaseColumns._ID + ")";
    }

    public ParkingDatabase(Context context) {

        super(context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
        // String cacheDB = getTempUrl(context, Const.API_DATABASE + ".zip");

        // if (cacheDB != null) {
        // super.mArchivePath = cacheDB;
        // }
    }

    /**
     * Used to download the .DB file from remote server, in order to reduce
     * impact on storage.
     * 
     * @param context
     * @param sUrl
     * @return
     */
    private String getTempUrl(Context context, String sUrl) {
        String fileName = Uri.parse(sUrl).getLastPathSegment();

        try {
            URL url = new URL(sUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(context.getFilesDir() + "/" + fileName);

            byte data[] = new byte[1024];

            int count;
            while ((count = input.read(data)) != -1) {

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getFilesDir() + "/" + fileName;
    }

    // @Override
    public void onCreateOrig(SQLiteDatabase db) {
        Log.v(TAG, "Creating database. DB name: " + Const.DATABASE_NAME);
        Log.w(TAG, "Creating 4 database tables: " + Tables.POSTS + ", " + Tables.PANELS + ", "
                + Tables.PANELS_CODES + " and " + Tables.PANELS_CODES_RULES);

        /**
         * Posts, has 4 indexes
         */
        db.execSQL("CREATE TABLE " + Tables.POSTS + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + PostsColumns.ID_POST + " INTEGER NOT NULL, "
                + PostsColumns.LNG + " DECIMAL(15,12) NOT NULL, "
                + PostsColumns.LAT + " DECIMAL(15,12) NOT NULL, "
                + PostsColumns.GEO_DISTANCE + " INTEGER NOT NULL DEFAULT '0')");
        db.execSQL("CREATE UNIQUE INDEX " + Indexes.POSTS_ID_POST
                + " ON " + Tables.POSTS
                + " (" + PostsColumns.ID_POST + ")");
        db.execSQL("CREATE INDEX " + Indexes.POSTS_LAT
                + " ON " + Tables.POSTS
                + " (" + PostsColumns.LAT + ")");
        db.execSQL("CREATE INDEX " + Indexes.POSTS_LNG
                + " ON " + Tables.POSTS
                + " (" + PostsColumns.LNG + ")");
        db.execSQL("CREATE INDEX " + Indexes.POSTS_GEO_DISTANCE
                + " ON " + Tables.POSTS
                + " (" + PostsColumns.GEO_DISTANCE + ")");

        /**
         * Panels, has 3 indexes including 2 references
         */
        db.execSQL("CREATE TABLE " + Tables.PANELS + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PanelsColumns.ID_PANEL + " INTEGER NOT NULL, "
                + PanelsColumns.ID_POST + " INTEGER " + References.ID_POST + ","
                + PanelsColumns.ID_PANEL_CODE + " INTEGER " + References.ID_PANEL_CODE + ")");
        db.execSQL("CREATE UNIQUE INDEX " + Indexes.PANELS_ID_PANEL
                + " ON " + Tables.PANELS
                + " (" + PanelsColumns.ID_PANEL + ")");
        db.execSQL("CREATE INDEX " + Indexes.PANELS_ID_POST
                + " ON " + Tables.PANELS
                + " (" + PanelsColumns.ID_POST + ")");
        db.execSQL("CREATE INDEX " + Indexes.PANELS_ID_PANEL_CODE
                + " ON " + Tables.PANELS
                + " (" + PanelsColumns.ID_PANEL_CODE + ")");

        /**
         * Panels Codes, has 1 index
         */
        db.execSQL("CREATE TABLE " + Tables.PANELS_CODES + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, "
                + PanelsCodesColumns.CODE + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, "
                + PanelsCodesColumns.DESCRIPTION + " TEXT NOT NULL, "
                + PanelsCodesColumns.TYPE_DESC + " TEXT DEFAULT NULL)");
        db.execSQL("CREATE INDEX " + Indexes.CODES_CODE
                + " ON " + Tables.PANELS_CODES
                + " (" + PanelsCodesColumns.CODE + ")");

        /**
         * Panels Codes Rules, has 7 indexes including 1 reference
         */
        db.execSQL("CREATE TABLE " + Tables.PANELS_CODES_RULES + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE , "
                + PanelsCodesRulesColumns.ID_PANEL_CODE + " INTEGER " + References.ID_PANEL_CODE
                + ","
                + PanelsCodesRulesColumns.MINUTES_DURATION + " INTEGER NOT NULL DEFAULT '0', "
                + PanelsCodesRulesColumns.HOUR_START + " INTEGER DEFAULT NULL, "
                + PanelsCodesRulesColumns.HOUR_END + " INTEGER DEFAULT NULL, "
                + PanelsCodesRulesColumns.HOUR_DURATION + " INTEGER DEFAULT NULL, "
                + PanelsCodesRulesColumns.DAY_START + " INTEGER DEFAULT NULL, "
                + PanelsCodesRulesColumns.DAY_END + " INTEGER DEFAULT NULL)");
        db.execSQL("CREATE INDEX " + Indexes.RULES_ID_PANEL_CODE
                + " ON " + Tables.PANELS_CODES_RULES
                + " (" + PanelsCodesRulesColumns.ID_PANEL_CODE + ")");
        db.execSQL("CREATE INDEX " + Indexes.RULES_DAY_END
                + " ON " + Tables.PANELS_CODES_RULES
                + " (" + PanelsCodesRulesColumns.DAY_END + ")");
        db.execSQL("CREATE INDEX " + Indexes.RULES_DAY_START
                + " ON " + Tables.PANELS_CODES_RULES
                + " (" + PanelsCodesRulesColumns.DAY_START + ")");
        db.execSQL("CREATE INDEX " + Indexes.RULES_HOUR_DURATION
                + " ON " + Tables.PANELS_CODES_RULES
                + " (" + PanelsCodesRulesColumns.HOUR_DURATION + ")");
        db.execSQL("CREATE INDEX " + Indexes.RULES_MINUTES_DURATION
                + " ON " + Tables.PANELS_CODES_RULES
                + " (" + PanelsCodesRulesColumns.MINUTES_DURATION + ")");
        db.execSQL("CREATE INDEX " + Indexes.RULES_HOUR_START
                + " ON " + Tables.PANELS_CODES_RULES
                + " (" + PanelsCodesRulesColumns.HOUR_START + ")");
        db.execSQL("CREATE INDEX " + Indexes.RULES_HOUR_END
                + " ON " + Tables.PANELS_CODES_RULES
                + " (" + PanelsCodesRulesColumns.HOUR_END + ")");

        /**
         * Favorites, has 1 index/reference
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.FAVORITES + " ( "
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + FavoritesColumns.ID_POST + " INTEGER UNIQUE NOT NULL " + References.ID_POST + ","
                + FavoritesColumns.LABEL + " TEXT NULL )");
        db.execSQL("CREATE INDEX " + Indexes.FAVORITES_ID_POST
                + " ON " + Tables.FAVORITES
                + " (" + FavoritesColumns.ID_POST + ")");
    }

    // @Override
    public void onUpgradeOrig(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ". Old data will be destroyed, except for " + Tables.FAVORITES + ". DB name: "
                + Const.DATABASE_NAME);

        // Start by dropping indexes
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.POSTS_ID_POST);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.POSTS_LAT);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.POSTS_LNG);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.POSTS_GEO_DISTANCE);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.PANELS_ID_PANEL);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.PANELS_ID_POST);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.PANELS_ID_PANEL_CODE);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.CODES_CODE);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.RULES_ID_PANEL_CODE);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.RULES_DAY_END);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.RULES_DAY_START);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.RULES_HOUR_DURATION);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.RULES_MINUTES_DURATION);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.RULES_HOUR_START);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.RULES_HOUR_END);
        db.execSQL("DROP INDEX IF EXISTS " + Indexes.FAVORITES_ID_POST);

        // Follow by dropping tables, except Favorites!
        db.execSQL("DROP TABLE IF EXISTS " + Tables.POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PANELS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PANELS_CODES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PANELS_CODES_RULES);
        // db.execSQL("DROP TABLE IF EXISTS " + Tables.FAVORITES);

        onCreate(db);
    }
}
