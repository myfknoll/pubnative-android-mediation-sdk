package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import net.pubnative.mediation.model.PubnativeConfigModel;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeConfigManager
{
    protected static final String SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String CONFIG_KEY = "net.pubnative.mediation.config";

    private PubnativeConfigManager(){};

    public static synchronized PubnativeConfigModel config(Context context, String app_token)
    {
        PubnativeConfigModel currentConfig = null;

        // 2. Get stored config
        String storedConfigString = PubnativeConfigManager.getConfigString(context);

        if (storedConfigString != null)
        {
            Gson gson = new Gson();
            currentConfig = gson.fromJson(storedConfigString, PubnativeConfigModel.class);
        }

        // TODO: if(currentModel == null || configStoredMinutes > currentModel.conf_refresh)
        // TODO: {
        // TODO:    String newConfigString = [DOWNLOAD A NEW CONFIG];
        // TODO:    PubnativeConfigModel newConfig = gson.fromJson(newConfigString, PubnativeConfigModel.class);
        // TODO:    if(newConfig != null)
        // TODO:    {
        // TODO:        SharedPreferences.Editor editor = prefs.edit();
        // TODO:        editor.putString(gson.toJson(newModel));
        // TODO:        editor.commit();
        // TODO:        currentModel = newModel;
        // TODO:    }
        // TODO: }

        return currentConfig;
    }

    protected static SharedPreferences getSharedPreferences(Context context)
    {
        return context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
    }

    protected static void setConfigString(Context context, String config)
    {
        SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CONFIG_KEY, config);
        editor.commit();
    }

    protected static String getConfigString(Context context)
    {
        String result = null;

        SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
        result = preferences.getString(CONFIG_KEY, null);

        return result;
    }

    protected static boolean hasConfig(Context context)
    {
        boolean result = false;

        SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
        result =  preferences.contains(CONFIG_KEY);

        return result;
    }
}
