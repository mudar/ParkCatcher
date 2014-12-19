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

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import ca.mudar.parkcatcher.provider.ParkingContract.PanelsCodesRules;
import ca.mudar.parkcatcher.utils.Lists;

public class PanelsCodesRulesHandler extends JsonHandler {
    @SuppressWarnings("unused")
    private static final String TAG = "PanelsCodesRulesHandler";

    public PanelsCodesRulesHandler(String authority) {
        super(authority);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(JSONTokener jsonTokener,
            ContentResolver resolver) throws JSONException, IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        ContentProviderOperation.Builder builderPanelsCodesRules;

        JSONObject jsonObject = new JSONObject(jsonTokener);

        final String status = jsonObject.optString(RemoteTags.STATUS);
        if (!RemoteValues.STATUS_OK.equals(status)) {
            return batch;
        }

        final int nbPanelsCodesRules = jsonObject.optInt(RemoteTags.COUNT);
        // final int version = jsonObject.optInt(RemoteTags.VERSION);
        // final JSONArray columns =
        // jsonObject.optJSONArray(RemoteTags.COLUMNS);

        final JSONArray panelsCodesRules = jsonObject
                .optJSONArray(jsonObject.optString(RemoteTags.NAME));

        if ((panelsCodesRules.length() != nbPanelsCodesRules) || (nbPanelsCodesRules == 0)) {
            return batch;
        }

        jsonObject = null;

        for (int i = 0; i < nbPanelsCodesRules; i++) {
            final JSONArray panelCodeRule = panelsCodesRules.optJSONArray(i);
            if (panelCodeRule == null) {
                continue;
            }

            final int id = panelCodeRule.optInt(0);
            final int idPanelCode = panelCodeRule.optInt(1);
            final int minutesDuration = panelCodeRule.optInt(2);
            final int hourStart = panelCodeRule.optInt(3);
            final int hourEnd = panelCodeRule.optInt(4);
            final int hourDuration = panelCodeRule.optInt(5);
            final int dayStart = panelCodeRule.optInt(6);
            final int dayEnd = panelCodeRule.optInt(7);

            builderPanelsCodesRules = ContentProviderOperation
                    .newInsert(PanelsCodesRules.CONTENT_URI);

            builderPanelsCodesRules.withValue(PanelsCodesRules._ID, id);
            builderPanelsCodesRules.withValue(PanelsCodesRules.ID_PANEL_CODE, idPanelCode);
            builderPanelsCodesRules.withValue(PanelsCodesRules.MINUTES_DURATION, minutesDuration);
            builderPanelsCodesRules.withValue(PanelsCodesRules.HOUR_START, hourStart);
            builderPanelsCodesRules.withValue(PanelsCodesRules.HOUR_END, hourEnd);
            builderPanelsCodesRules.withValue(PanelsCodesRules.HOUR_DURATION, hourDuration);
            builderPanelsCodesRules.withValue(PanelsCodesRules.DAY_START, dayStart);
            builderPanelsCodesRules.withValue(PanelsCodesRules.DAY_END, dayEnd);

            batch.add(builderPanelsCodesRules.build());
        }

        return batch;
    }

    /**
     * Remote columns
     */
    private static interface RemoteTags {
        final String STATUS = "status";
        final String NAME = "name";
        // final String VERSION = "version";
        final String COUNT = "count";
        // final String COLUMNS = "columns";
    }

    public static interface RemoteValues {
        final String STATUS_OK = "ok"; // OK
    }
}
