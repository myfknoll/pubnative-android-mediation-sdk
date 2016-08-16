package net.pubnative.mediation.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.pubnative.mediation.Pubnative;
import net.pubnative.mediation.config.PubnativeConfigService;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeConfigRequestModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PubnativeConfigUtils {

    private   static       String TAG                       = PubnativeConfigUtils.class.getSimpleName();
    protected static final String SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String CONFIG_STRING_KEY         = "config";
    protected static final String APP_TOKEN_STRING_KEY      = "appToken";
    protected static final String REQUEST_PARAMS_STRING_KEY = "request_params";
    protected static final String TIMESTAMP_LONG_KEY        = "config.timestamp";
    protected static final String REFRESH_LONG_KEY          = "refresh";
    protected static final String CONFIG_DOWNLOAD_BASE_URL  = "http://ml.pubnative.net/ml/v1/config";
    protected static final String APP_TOKEN_KEY             = "app_token";
    protected static final int    REQUEST_CODE              = 0;

    //----------------------------------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------------------------------
    /**
     * Used to get stored config for request.
     *
     * @param context valid context.
     * @return        returns config params if already stored, null if not.
     */
    public static PubnativeConfigModel getStoredConfig(Context context) {

        Log.v(TAG, "getStoredConfig");
        PubnativeConfigModel currentConfig = null;
        String configString = getStoredConfigString(context);
        if (!TextUtils.isEmpty(configString)) {
            try {
                currentConfig = new Gson().fromJson(configString, PubnativeConfigModel.class);
            } catch (Exception e) {
                Log.e(TAG, "getStoredConfig - Error: " + e);
            }
        }
        // Ensure not returning an invalid getConfig
        if (currentConfig == null || currentConfig.isEmpty()) {
            currentConfig = null;
        }
        return currentConfig;
    }

    /**
     * gets config download url.
     *
     * @param request request params.
     * @return        valid string url.
     */
    public static String getConfigDownloadUrl(PubnativeConfigRequestModel request) {

        Log.v(TAG, "getConfigDownloadUrl");
        Uri.Builder uriBuilder = Uri.parse(getConfigDownloadBaseUrl(request.context)).buildUpon();
        uriBuilder.appendQueryParameter(APP_TOKEN_KEY, request.appToken);
        if (request.extras != null && !request.extras.isEmpty()) {
            for (String key : request.extras.keySet()) {
                String value = request.extras.get(key);
                uriBuilder.appendQueryParameter(key, value);
            }
            //store request params in app AppPreferences
            PubnativeConfigUtils.setStoredParams(request.context, new Gson().toJson(request.extras));
        }else{
            PubnativeConfigUtils.setStoredParams(request.context, null);
        }
        return uriBuilder.build().toString();
    }

    /**
     * updates delivery manager cache for requests.
     *
     * @param context          valid context.
     * @param downloadedConfig downloaded config data from server.
     */
    public static void updateDeliveryManagerCache(Context context, PubnativeConfigModel downloadedConfig) {

        Log.v(TAG, "updateDeliveryManagerCache");
        PubnativeConfigModel storedConfig = getStoredConfig(context);
        if (storedConfig != null) {
            Set<String> storePlacementIds = storedConfig.placements.keySet();
            for (String placementId : storePlacementIds) {
                // check if new config contains that placement.
                PubnativePlacementModel newPlacement = downloadedConfig.placements.get(placementId);
                PubnativePlacementModel storedPlacement = storedConfig.placements.get(placementId);
                if (newPlacement == null) {
                    PubnativeDeliveryManager.resetHourlyImpressionCount(context, placementId);
                    PubnativeDeliveryManager.resetDailyImpressionCount(context, placementId);
                    PubnativeDeliveryManager.resetPacingCalendar(placementId);
                } else {
                    // Check if impression cap (hour) changed
                    if (storedPlacement.delivery_rule.imp_cap_hour != newPlacement.delivery_rule.imp_cap_hour) {
                        PubnativeDeliveryManager.resetHourlyImpressionCount(context, placementId);
                    }
                    // check if impression cap (day) changed
                    if (storedPlacement.delivery_rule.imp_cap_day != newPlacement.delivery_rule.imp_cap_day) {
                        PubnativeDeliveryManager.resetDailyImpressionCount(context, placementId);
                    }
                    // check if pacing cap changed
                    if (storedPlacement.delivery_rule.pacing_cap_minute != newPlacement.delivery_rule.pacing_cap_minute
                            || storedPlacement.delivery_rule.pacing_cap_hour != newPlacement.delivery_rule.pacing_cap_hour) {
                        PubnativeDeliveryManager.resetPacingCalendar(placementId);
                    }
                }
            }
        }
    }

    /**
     * updates user config with config stored on server.
     *
     * @param context     valid context.
     * @param appToken    valid apptoken.
     * @param configModel config stored on server to update local configs.
     */
    public static void updateConfig(Context context, String appToken, PubnativeConfigModel configModel) {

        Log.v(TAG, "updateConfig");
        if (context != null) {
            if (TextUtils.isEmpty(appToken) || configModel == null || configModel.isEmpty()) {
                clean(context);
            } else {
                setStoredConfig(context, configModel);
                setStoredAppToken(context, appToken);
                setStoredTimestamp(context, System.currentTimeMillis());
                if (configModel.globals.containsKey(PubnativeConfigModel.GLOBAL.REFRESH)) {
                    Double refresh = (Double) configModel.globals.get(PubnativeConfigModel.GLOBAL.REFRESH);
                    setStoredRefresh(context, refresh.longValue());
                    scheduleConfigUpdate(context, refresh.longValue(), appToken);
                }
            }
        }
    }

    /**
     * checks config needs update.
     *
     * @param request request params.
     * @return        true if needs update, false otherwise.
     */
    public static boolean configNeedsUpdate(PubnativeConfigRequestModel request) {

        Log.v(TAG, "configNeedsUpdate");
        boolean result = false;
        String storedConfigString = getStoredConfigString(request.context);
        String storedAppToken = getStoredAppToken(request.context);
        Long refresh = getStoredRefresh(request.context);
        Long storedTimestamp = getStoredTimestamp(request.context);
        Long currentTimestamp = System.currentTimeMillis();
        if (TextUtils.isEmpty(storedConfigString)) {
            // There is no stored config
            result = true;
        } else if (!storedAppToken.equals(request.appToken)) {
            // Stored config is different than the requested app token
            result = true;
        } else if (refresh == null ||
                storedTimestamp == null) {
            // There is no previous refresh or timestamp stored
            result = true;
        } else if (TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp) >= refresh) {
            // refresh time was elapsed
            result = true;
        }
        return result;
    }

    /**
     * resets alarm for next config update call.
     *
     * @param request valid request params.
     */
    public static void reScheduleConfigUpdate(PubnativeConfigRequestModel request){

        Log.v(TAG, "reScheduleConfigUpdate");
        Long refresh = getStoredRefresh(request.context);
        Long storedTimestamp = getStoredTimestamp(request.context);
        Long currentTimestamp = System.currentTimeMillis();
        Long remainingTime = refresh - (TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp));
        if(remainingTime > 0){
            scheduleConfigUpdate(request.context, TimeUnit.MINUTES.toMillis(remainingTime), request.appToken);
        }
    }

    /**
     * Completely resets all stored config data.
     *
     * @param context valid context object.
     */
    public static void clean(Context context) {

        Log.v(TAG, "clean");
        setStoredAppToken(context, null);
        setStoredTimestamp(context, null);
        setStoredRefresh(context, null);
        setStoredConfig(context, null);
    }
    //----------------------------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------------------------

    private static void scheduleConfigUpdate(Context context, Long refreshTime, String appToken){

        Log.v(TAG, "scheduleConfigUpdate");
        Long startTimeStamp = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(refreshTime);
        Intent intent = new Intent(context, PubnativeConfigService.class);
        Bundle bundle = new Bundle();
        bundle.putString(APP_TOKEN_KEY, appToken);
        // get request params from Apppreferences (targeting or request params like age, gender).
        String extras = getStoredParams(context);
        if(!TextUtils.isEmpty(extras)){
            HashMap map = new Gson().fromJson(extras, new TypeToken<HashMap<String, String>>(){}.getType());
            if(map != null && !map.isEmpty()){
                bundle.putSerializable(Pubnative.EXTRAS, map); // add extras in intent
            }
        }
        intent.putExtras(bundle);
        //setting up alarm for next call to update config
        AlarmManager alarmManager =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, startTimeStamp, pendingIntent);
    }

    //==============================================================================================
    // SHARED PREFERENCES
    //==============================================================================================
    // CONFIG URL
    //----------------------------------------------------------------------------------------------
    protected static String getConfigDownloadBaseUrl(Context context) {

        Log.v(TAG, "getConfigDownloadBaseUrl");
        String configDownloadBaseUrl = CONFIG_DOWNLOAD_BASE_URL;
        PubnativeConfigModel storedConfig = getStoredConfig(context);
        if (storedConfig != null && !storedConfig.isEmpty()) {
            String configUrl = (String) storedConfig.globals.get(PubnativeConfigModel.GLOBAL.CONFIG_URL);
            if (!TextUtils.isEmpty(configUrl)) {
                configDownloadBaseUrl = configUrl;
            }
        }
        return configDownloadBaseUrl;
    }
    //----------------------------------------------------------------------------------------------
    // CONFIG
    //----------------------------------------------------------------------------------------------

    protected static synchronized String getStoredConfigString(Context context) {

        Log.v(TAG, "getStoredConfigString");
        return getStringSharedPreference(context, CONFIG_STRING_KEY);
    }

    protected static synchronized void setStoredConfig(Context context, PubnativeConfigModel config) {

        Log.v(TAG, "setStoredConfig");
        // ensuring the string "null" is not getting saved.
        String configString = (config != null) ? new Gson().toJson(config) : null;
        setStringSharedPreference(context, CONFIG_STRING_KEY, configString);
    }
    //----------------------------------------------------------------------------------------------
    // APP_TOKEN
    //----------------------------------------------------------------------------------------------

    public static String getStoredAppToken(Context context) {

        Log.v(TAG, "getStoredAppToken");
        return getStringSharedPreference(context, APP_TOKEN_STRING_KEY);
    }

    protected static void setStoredAppToken(Context context, String appToken) {

        Log.v(TAG, "getStoredAppToken");
        setStringSharedPreference(context, APP_TOKEN_STRING_KEY, appToken);
    }
    //----------------------------------------------------------------------------------------------
    // TIMESTAMP
    //----------------------------------------------------------------------------------------------

    protected static Long getStoredTimestamp(Context context) {

        Log.v(TAG, "getStoredTimestamp");
        return getLongSharedPreference(context, TIMESTAMP_LONG_KEY);
    }

    protected static void setStoredTimestamp(Context context, Long timestamp) {

        Log.v(TAG, "getStoredTimestamp");
        setLongSharedPreference(context, TIMESTAMP_LONG_KEY, timestamp);
    }
    //----------------------------------------------------------------------------------------------
    // REFRESH
    //----------------------------------------------------------------------------------------------

    protected static Long getStoredRefresh(Context context) {

        Log.v(TAG, "getStoredRefresh");
        return getLongSharedPreference(context, REFRESH_LONG_KEY);
    }

    protected static void setStoredRefresh(Context context, Long refresh) {

        Log.v(TAG, "setStoredRefresh");
        setLongSharedPreference(context, REFRESH_LONG_KEY, refresh);
    }

    //----------------------------------------------------------------------------------------------
    // REQUEST PARAMS
    //----------------------------------------------------------------------------------------------
    public static String getStoredParams(Context context) {

        Log.v(TAG, "getStoredParams");
        return getStringSharedPreference(context, REQUEST_PARAMS_STRING_KEY);
    }

    public static void setStoredParams(Context context, String requestParams) {

        Log.v(TAG, "setStoredParams");
        setStringSharedPreference(context, REQUEST_PARAMS_STRING_KEY, requestParams);
    }
    //----------------------------------------------------------------------------------------------
    // String
    //----------------------------------------------------------------------------------------------

    protected static String getStringSharedPreference(Context context, String key) {

        Log.v(TAG, "getStringSharedPreference");
        String result = null;
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences preferences = getSharedPreferences(context);
            if (preferences != null && preferences.contains((key))) {
                result = preferences.getString(key, null);
            }
        }
        return result;
    }

    protected static void setStringSharedPreference(Context context, String key, String value) {

        Log.v(TAG, "setStringSharedPreference");
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (context != null && !TextUtils.isEmpty(key) && editor != null) {
            if (TextUtils.isEmpty(value)) {
                editor.remove(key);
            } else {
                editor.putString(key, value);
            }
            editor.apply();
        }
    }
    //----------------------------------------------------------------------------------------------
    // Long
    //----------------------------------------------------------------------------------------------

    protected static Long getLongSharedPreference(Context context, String key) {

        Log.v(TAG, "getLongSharedPreference");
        Long result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        if (context != null && preferences.contains(key)) {
            Long value = preferences.getLong(key, 0);
            if (value > 0) {
                result = value;
            }
        }
        return result;
    }

    protected static void setLongSharedPreference(Context context, String key, Long value) {

        Log.v(TAG, "setLongSharedPreference");
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            if (editor != null) {
                if (value == null) {
                    editor.remove(key);
                } else {
                    editor.putLong(key, value);
                }
                editor.apply();
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    // BASE SharedPreferences item
    //----------------------------------------------------------------------------------------------

    protected static SharedPreferences getSharedPreferences(Context context) {

        Log.v(TAG, "getSharedPreferences");
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
        }
        return result;
    }
}
