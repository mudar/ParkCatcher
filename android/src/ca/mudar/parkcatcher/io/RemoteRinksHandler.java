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

package ca.mudar.parkcatcher.io;

import ca.mudar.parkcatcher.Const.DbValues;
import ca.mudar.parkcatcher.providers.RinksContract.Boroughs;
import ca.mudar.parkcatcher.providers.RinksContract.BoroughsColumns;
import ca.mudar.parkcatcher.providers.RinksContract.Parks;
import ca.mudar.parkcatcher.providers.RinksContract.ParksColumns;
import ca.mudar.parkcatcher.providers.RinksContract.Rinks;
import ca.mudar.parkcatcher.providers.RinksContract.RinksColumns;
import ca.mudar.parkcatcher.utils.Helper;
import ca.mudar.parkcatcher.utils.Lists;
import ca.mudar.parkcatcher.utils.ParserUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class RemoteRinksHandler extends JsonHandler {
    @SuppressWarnings("unused")
    private static final String TAG = "RemoteRinksHandler";

    public RemoteRinksHandler(String authority) {
        super(authority);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(JSONTokener jsonTokener,
            ContentResolver resolver) throws JSONException, IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        /**
         * Using 3 different builders for readability!
         */
        ContentProviderOperation.Builder builderBoroughs;
        ContentProviderOperation.Builder builderParks;
        ContentProviderOperation.Builder builderRinks;

        CharSequence createdAt = DateFormat.format(DbValues.DATE_FORMAT, new Date());

        JSONArray boroughs = new JSONArray(jsonTokener);
        final int totalBoroughs = boroughs.length();

        if (totalBoroughs == 0) {
            return batch;
        }

        String rinkName;
        String rinkDesc;
        String rinkDescEnglish;
        String[] splitName;

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
            String boroughId = borough.optString(RemoteTags.BOROUGH_ID);
            if (boroughId.equals(RemoteValues.STRING_NULL)) {
                boroughId = ParserUtils.sanitizeId(borough.optString(RemoteTags.BOROUGH_NAME));
            }

            builderBoroughs = ContentProviderOperation.newInsert(Boroughs.CONTENT_URI);

            builderBoroughs.withValue(BoroughsColumns.BOROUGH_ID, boroughId);
            builderBoroughs.withValue(BoroughsColumns.BOROUGH_NAME,
                    borough.optString(RemoteTags.BOROUGH_NAME));
            builderBoroughs.withValue(BoroughsColumns.BOROUGH_UPDATED_AT,
                    borough.optString(RemoteTags.BOROUGH_UPDATED_AT));
            builderBoroughs.withValue(BoroughsColumns.BOROUGH_CREATED_AT, createdAt);

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
                 * Get Park info
                 */
                builderParks = ContentProviderOperation.newInsert(Parks.CONTENT_URI);

                String parkName = rink.optString(RemoteTags.PARK_NAME);
                String parkId = ParserUtils.sanitizeId(parkName) + "-" + boroughId;
                builderParks.withValue(ParksColumns.PARK_ID, parkId);
                builderParks.withValue(ParksColumns.PARK_BOROUGH_ID, boroughId);
                builderParks.withValue(ParksColumns.PARK_NAME, "%s " + parkName);

                String geoLat = rink.optString(RemoteTags.PARK_LAT).trim();
                String geoLng = rink.optString(RemoteTags.PARK_LNG).trim();
                if ((geoLat.equals(RemoteValues.STRING_NULL))
                        || (geoLng.equals(RemoteValues.STRING_NULL))) {
                    continue;
                }
                builderParks.withValue(ParksColumns.PARK_GEO_LAT, geoLat);
                builderParks.withValue(ParksColumns.PARK_GEO_LNG, geoLng);

                if (!rink.optString(RemoteTags.PARK_ADDRESS).trim()
                        .equals(RemoteValues.STRING_NULL)) {
                    builderParks.withValue(ParksColumns.PARK_ADDRESS,
                            Helper.capitalize(rink.optString(RemoteTags.PARK_ADDRESS)));
                }

                String phone = rink.optString(RemoteTags.PARK_PHONE);
                if (!phone.trim().equals(RemoteValues.STRING_NULL)) {
                    String phoneExtension = rink.optString(RemoteTags.PARK_PHONE_EXT).trim();
                    if (!phoneExtension.equals(RemoteValues.STRING_NULL)) {
                        phone = phone + PhoneNumberUtils.PAUSE + phoneExtension;
                    }
                    builderParks.withValue(ParksColumns.PARK_PHONE, phone);
                }

                builderParks.withValue(ParksColumns.PARK_CREATED_AT, createdAt);

                batch.add(builderParks.build());

                /**
                 * Get Rink info
                 */
                builderRinks = ContentProviderOperation.newInsert(Rinks.CONTENT_URI);

                splitName = rink.optString(RemoteTags.RINK_NAME).split(",");
                rinkName = splitName[1].replace(RemoteValues.RINK_TYPE_PSE_SUFFIX, "")
                        .replace(RemoteValues.RINK_TYPE_PPL_SUFFIX, "")
                        .replace(RemoteValues.RINK_TYPE_PP_SUFFIX, "")
                        .replace(RemoteValues.RINK_TYPE_C_SUFFIX, "")
                        .trim();
                rinkName = Helper.capitalize(rinkName);
                rinkDesc = splitName[0].trim();
                rinkDescEnglish = "";

                builderRinks.withValue(RinksColumns.RINK_ID, rink.optString(RemoteTags.RINK_ID));
                builderRinks.withValue(RinksColumns.RINK_PARK_ID, parkId);
                // builderRinks.withValue(RinksColumns.RINK_KIND_ID,
                // ApiStringHelper.getTypeIndex(rink.optString(RemoteTags.RINK_KIND_ID)));
                builderRinks.withValue(RinksColumns.RINK_NAME, rinkName);
                builderRinks.withValue(RinksColumns.RINK_DESC_FR, rinkDesc);
                builderRinks.withValue(RinksColumns.RINK_DESC_EN, rinkDescEnglish);

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

                builderRinks.withValue(RinksColumns.RINK_CREATED_AT, createdAt);

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

        final String BOROUGH_ID = "cle";
        final String BOROUGH_NAME = "nom_arr";
        final String BOROUGH_UPDATED_AT = "date_maj";

        final String RINK_ID = "id";
        final String RINK_NAME = "nom";
        final String RINK_IS_CLEARED = "deblaye";
        final String RINK_IS_FLOODED = "arrose";
        final String RINK_IS_RESURFACED = "resurface";

        final String PARK_NAME = "parc";
        final String PARK_ADDRESS = "adresse";
        final String PARK_PHONE = "tel";
        final String PARK_PHONE_EXT = "ext";

        final String PARK_LAT = "lat";
        final String PARK_LNG = "lng";
    }

    public static interface RemoteValues {

        final String RINK_TYPE_PP = "PP"; // paysagée
        final String RINK_TYPE_PPL = "PPL"; // patin libre
        final String RINK_TYPE_PSE = "PSE"; // sport d'équipe
        final String RINK_TYPE_C = "C"; // citoyens

        final String RINK_TYPE_PSE_SUFFIX = "(PSE)";
        final String RINK_TYPE_PPL_SUFFIX = "(PPL)";
        final String RINK_TYPE_PP_SUFFIX = "(PP)";
        final String RINK_TYPE_C_SUFFIX = "(C)";

        final String RINK_CONDITION_EXCELLENT = "excellente";
        final String RINK_CONDITION_GOOD = "bonne";
        final String RINK_CONDITION_BAD = "mauvaise";

        final String BOOLEAN_TRUE = "true";
        final String BOOLEAN_FALSE = "false";
        final String STRING_NULL = "null";
    }
}
