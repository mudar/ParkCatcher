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

import ca.mudar.parkcatcher.provider.ParkingContract.PanelsCodes;
import ca.mudar.parkcatcher.utils.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import java.io.IOException;
import java.util.ArrayList;

public class PanelsCodesHandler extends JsonHandler {
    @SuppressWarnings("unused")
    private static final String TAG = "PanelsCodesHandler";

    public PanelsCodesHandler(String authority) {
        super(authority);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(JSONTokener jsonTokener,
            ContentResolver resolver) throws JSONException, IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        ContentProviderOperation.Builder builderPanelsCodes;

        JSONObject jsonObject = new JSONObject(jsonTokener);

        final String status = jsonObject.getString(RemoteTags.STATUS);
        if (!RemoteValues.STATUS_OK.equals(status)) {
            return batch;
        }

        final int nbPanelsCodes = jsonObject.getInt(RemoteTags.COUNT);
        // final int version = jsonObject.getInt(RemoteTags.VERSION);
        // final JSONArray columns =
        // jsonObject.getJSONArray(RemoteTags.COLUMNS);

        final JSONArray panelsCodes = jsonObject
                .getJSONArray(jsonObject.getString(RemoteTags.NAME));

        if ((panelsCodes.length() != nbPanelsCodes) || (nbPanelsCodes == 0)) {
            return batch;
        }

        jsonObject = null;

        for (int i = 0; i < nbPanelsCodes; i++) {
            final JSONArray panelCode = panelsCodes.getJSONArray(i);
            if (panelCode == null) {
                continue;
            }

            final int id = panelCode.getInt(0);
            final String code = panelCode.getString(1);
            final String desc = panelCode.getString(2);
            final int type = panelCode.getInt(3);

            builderPanelsCodes =
                    ContentProviderOperation.newInsert(PanelsCodes.CONTENT_URI);

            builderPanelsCodes.withValue(PanelsCodes._ID, id);
            builderPanelsCodes.withValue(PanelsCodes.CODE, code);
            builderPanelsCodes.withValue(PanelsCodes.DESCRIPTION, desc);
            builderPanelsCodes.withValue(PanelsCodes.TYPE_DESC, type);

            batch.add(builderPanelsCodes.build());
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

        final int TYPE_PARKING = 1; // "STATIONNEMENT"
        final int TYPE_PAID = 2; // "STAT-$"
    }
}
