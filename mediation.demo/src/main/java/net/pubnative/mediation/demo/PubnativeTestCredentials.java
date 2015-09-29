package net.pubnative.mediation.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by rahul on 29/9/15.
 */
public class PubnativeTestCredentials
{
    public static final String PREF_FILE           = "pubnative_pref_file";
    public static final String PREF_KEY_PLACEMENTS = "placements_key";
    public static final String PREF_KEY_APP_TOKEN  = "app_token_key";

    private static SharedPreferences getSharedPreferences(Context context)
    {
        return context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor(Context context)
    {
        return getSharedPreferences(context).edit();
    }

    public static List<String> getStoredPlacements(Context context)
    {
        List<String> placements = null;
        if (context != null)
        {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String json = sharedPreferences.getString(PREF_KEY_PLACEMENTS, null);
            if (!TextUtils.isEmpty(json))
            {
                placements = new Gson().fromJson(json, List.class);
            }
        }
        return placements;
    }

    public static void setStoredPlacements(Context context, List<String> placements)
    {
        if (context != null)
        {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            if (placements == null || placements.size() == 0)
            {
                editor.remove(PREF_KEY_PLACEMENTS);
            }
            else
            {
                editor.putString(PREF_KEY_PLACEMENTS, new Gson().toJson(placements));
            }
            editor.apply();
        }
    }

    public static String getStoredAppToken(Context context)
    {
        String appToken = null;
        if (context != null)
        {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            appToken = sharedPreferences.getString(PREF_KEY_APP_TOKEN, null);
        }
        return appToken;
    }

    public static void setStoredAppToken(Context context, String appToken)
    {
        if (context != null && !TextUtils.isEmpty(appToken))
        {
            SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
            editor.putString(PREF_KEY_APP_TOKEN, appToken).apply();
        }
    }
}
