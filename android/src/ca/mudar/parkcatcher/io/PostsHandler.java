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

import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.utils.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;

import java.io.IOException;
import java.util.ArrayList;

public class PostsHandler extends JsonHandler {
    @SuppressWarnings("unused")
    private static final String TAG = "PostsHandler";

    public PostsHandler(String authority) {
        super(authority);
    }

    @Override
    public ArrayList<ContentProviderOperation> parse(JSONTokener jsonTokener,
            ContentResolver resolver) throws JSONException, IOException {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        ContentProviderOperation.Builder builderPosts;

        JSONObject jsonObject = new JSONObject(jsonTokener);

        final String status = jsonObject.getString(RemoteTags.STATUS);
        if (!RemoteValues.STATUS_OK.equals(status)) {
            return batch;
        }

        final int nbPosts = jsonObject.getInt(RemoteTags.COUNT);
        // final int version = jsonObject.getInt(RemoteTags.VERSION);
        // final JSONArray columns =
        // jsonObject.getJSONArray(RemoteTags.COLUMNS);

        final JSONArray posts = jsonObject
                .getJSONArray(jsonObject.getString(RemoteTags.NAME));

        if ((posts.length() != nbPosts) || (nbPosts == 0)) {
            return batch;
        }

        jsonObject = null;
        for (int i = 0; i < nbPosts; i++) {

            final JSONArray post = posts.getJSONArray(i);
            if (post == null) {
                continue;
            }

            builderPosts =
                    ContentProviderOperation.newInsert(Posts.CONTENT_URI);

            builderPosts.withValue(Posts.ID_POST, post.getInt(0));
            builderPosts.withValue(Posts.LNG, post.getDouble(1));
            builderPosts.withValue(Posts.LAT, post.getDouble(2));

            batch.add(builderPosts.build());
        }

        // Log.v(TAG, "batch done. Size = " + batch.size());

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
