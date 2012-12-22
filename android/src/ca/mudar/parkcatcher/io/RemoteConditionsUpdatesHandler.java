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

package ca.mudar.parkcatcher.io;

import ca.mudar.parkcatcher.providers.RinksContract.Boroughs;
import ca.mudar.parkcatcher.providers.RinksContract.BoroughsColumns;
import ca.mudar.parkcatcher.providers.RinksContract.Rinks;
import ca.mudar.parkcatcher.providers.RinksContract.RinksColumns;
import ca.mudar.parkcatcher.utils.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import java.io.IOException;
import java.util.ArrayList;

public class RemoteConditionsUpdatesHandler extends JsonHandler {
    @SuppressWarnings("unused")
    private static final String TAG = "RemoteConditionsUpdatesHandler";

    public RemoteConditionsUpdatesHandler(String authority) {
        super(authority);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(JSONTokener jsonTokener,
            ContentResolver resolver) throws JSONException, IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        // Random rand = new Random();

        /**
         * Using 2 different builders for readability!
         */
        ContentProviderOperation.Builder builderBoroughs;
        ContentProviderOperation.Builder builderRinks;

        JSONArray boroughs = new JSONArray(jsonTokener);
        final int totalBoroughs = boroughs.length();

        if (totalBoroughs == 0) {
            return batch;
        }

        JSONObject rink;
        JSONObject borough;

        for (int i = 0; i < totalBoroughs; i++) {
            /**
             * Get Borough info
             */
            try {
                borough = (JSONObject) boroughs.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            /**
             * Get Borough key or create one if doesn't exist.
             */
            builderBoroughs = ContentProviderOperation.newUpdate(Boroughs.CONTENT_URI);

            builderBoroughs.withSelection(BoroughsColumns.BOROUGH_NAME + "=?", new String[] {
                    borough.optString(RemoteTags.BOROUGH_NAME)
            });
            builderBoroughs.withValue(BoroughsColumns.BOROUGH_UPDATED_AT,
                    borough.optString(RemoteTags.BOROUGH_UPDATED_AT));

            batch.add(builderBoroughs.build());

            /**
             * Get rink info and clean name and description. English description
             * is translated manually!
             */
            JSONArray rinks = (JSONArray) borough.get(RemoteTags.OBJECT_RINKS);
            final int totalRinks = rinks.length();

            for (int j = 0; j < totalRinks; j++) {
                try {
                    rink = (JSONObject) rinks.getJSONObject(j);
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }

                /**
                 * Get Rink info
                 */
                builderRinks = ContentProviderOperation.newUpdate(Rinks.CONTENT_URI);

                builderRinks.withSelection(RinksColumns.RINK_ID + "=?", new String[] {
                        rink.optString(RemoteTags.RINK_ID)
                });

                builderRinks.withValue(RinksColumns.RINK_IS_CLEARED,
                        rink.optString(RemoteTags.RINK_IS_CLEARED)
                                .equals(RemoteValues.BOOLEAN_TRUE));
                builderRinks.withValue(RinksColumns.RINK_IS_FLOODED,
                        rink.optString(RemoteTags.RINK_IS_FLOODED)
                                .equals(RemoteValues.BOOLEAN_TRUE));
                builderRinks.withValue(RinksColumns.RINK_IS_RESURFACED,
                        rink.optString(RemoteTags.RINK_IS_RESURFACED).equals(
                                RemoteValues.BOOLEAN_TRUE));
                // builderRinks.withValue(RinksColumns.RINK_CONDITION,
                // ApiStringHelper.getConditionIndex(rink.optString(RemoteTags.RINK_IS_OPEN),
                // rink.optString(RemoteTags.RINK_CONDITION)));
                // int condition = rand.nextInt(4);
                // builderRinks.withValue(RinksColumns.RINK_CONDITION,
                // condition);

                batch.add(builderRinks.build());
            }
        }

        return batch;
    }

    /**
     * Remote columns
     */
    private static interface RemoteTags {

        final String OBJECT_RINKS = "patinoires";

        final String BOROUGH_NAME = "nom_arr";
        final String BOROUGH_UPDATED_AT = "date_maj";

        final String RINK_ID = "id";
        final String RINK_IS_CLEARED = "deblaye";
        final String RINK_IS_FLOODED = "arrose";
        final String RINK_IS_RESURFACED = "resurface";
    }

    public static interface RemoteValues {

        final String RINK_CONDITION_EXCELLENT = "excellente";
        final String RINK_CONDITION_GOOD = "bonne";
        final String RINK_CONDITION_BAD = "mauvaise";

        final String BOOLEAN_TRUE = "true";
        final String BOOLEAN_FALSE = "false";
        final String STRING_NULL = "null";
    }
}
