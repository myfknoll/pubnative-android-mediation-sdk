package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import net.pubnative.mediation.R;
import net.pubnative.mediation.model.PubnativeConfigModel;
import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.InputStream;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeConfigManager
{
    protected static final String SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String CONFIG_KEY = "net.pubnative.mediation.config";

    private PubnativeConfigManager(){};

    public static synchronized PubnativeConfigModel config(Context context)
    {
        PubnativeConfigModel result = null;

        // 1. Ensure that there is an stored config
        if(!PubnativeConfigManager.hasConfig(context))
        {
            InputStream configStream = context.getResources().openRawResource(R.raw.default_config);
            String defaultConfigString = PubnativeStringUtils.readTextFromInputStream(configStream);
            PubnativeConfigManager.setConfig(context, defaultConfigString);
        }

        // 2. Get stored config
        String storedConfigString = PubnativeConfigManager.getConfig(context);
        Gson gson = new Gson();

        PubnativeConfigModel currentModel = gson.fromJson(storedConfigString, PubnativeConfigModel.class);

        // TODO: [SYNC] Download config from the server >> newModel
        // TODO: if(model.version < newModel.version)
        // TODO: {
        // TODO:    SharedPreferences.Editor editor = prefs.edit();
        // TODO:    editor.putString(gson.toJson(newModel));
        // TODO:    editor.commit();
        // TODO:    currentModel = newModel;
        // TODO: }

        result = currentModel;

        return result;
    }

    protected static SharedPreferences getSharedPreferences(Context context)
    {
        return context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
    }

    protected static synchronized void setConfig(Context context, String config)
    {
        SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CONFIG_KEY, config);
        editor.commit();
    }

    protected static String getConfig(Context context)
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
