// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.List;

public class PubnativeTestCredentials {

    public static final String PREF_FILE           = "pubnative_pref_file";
    public static final String PREF_KEY_PLACEMENTS = "placements_key";
    public static final String PREF_KEY_APP_TOKEN  = "app_token_key";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static List<String> getStoredPlacements(Context context) {
        List<String> placements = null;
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String json = sharedPreferences.getString(PREF_KEY_PLACEMENTS, null);
            if (!TextUtils.isEmpty(json)) {
                placements = new Gson().fromJson(json, List.class);
            }
        }
        return placements;
    }

    public static void setStoredPlacements(Context context, List<String> placements) {
        if (context != null) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if (placements == null || placements.size() == 0) {
                editor.remove(PREF_KEY_PLACEMENTS);
            } else {
                editor.putString(PREF_KEY_PLACEMENTS, new Gson().toJson(placements));
            }
            editor.apply();
        }
    }

    public static String getStoredAppToken(Context context) {
        String appToken = null;
        if (context != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            appToken = sharedPreferences.getString(PREF_KEY_APP_TOKEN, null);
        }
        return appToken;
    }

    public static void setStoredAppToken(Context context, String appToken) {
        if (context != null && !TextUtils.isEmpty(appToken)) {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            editor.putString(PREF_KEY_APP_TOKEN, appToken).apply();
        }
    }
}
