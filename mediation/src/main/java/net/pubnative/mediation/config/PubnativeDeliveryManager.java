package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidmartin on 15/08/15.
 */
public class PubnativeDeliveryManager
{
    protected static final String                IMPRESSION_PREFERENCES_KEY    = "net.pubnative.mediation.frequency_manager";
    protected static final String                IMPRESSION_COUNT_DAY_APPEND   = "_impression_count_day";
    protected static final String                IMPRESSION_COUNT_HOUR_APPEND  = "_impression_count_hour";
    protected static final String                IMPRESSION_LAST_UPDATE_APPEND = "_impression_last_update";
    protected              Map<String, Calendar> currentPacing                 = new HashMap<String, Calendar>();

    protected static PubnativeDeliveryManager instance = null;

    private PubnativeDeliveryManager() {}

    protected static synchronized PubnativeDeliveryManager getInstance()
    {
        if (PubnativeDeliveryManager.instance == null)
        {
            PubnativeDeliveryManager.instance = new PubnativeDeliveryManager();
        }
        return PubnativeDeliveryManager.instance;
    }

    public static Calendar getPacingCalendar(String placementID)
    {
        Calendar result = null;
        if (PubnativeDeliveryManager.getInstance().currentPacing.containsKey(placementID))
        {
            result = PubnativeDeliveryManager.getInstance().currentPacing.get(placementID);
        }
        return result;
    }

    public static void updatePacingCalendar(String placementID)
    {
        PubnativeDeliveryManager.getInstance().currentPacing.put(placementID, Calendar.getInstance().getInstance());
    }

    public static void logImpression(Context context, String placementID)
    {
        int dayCount = PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID);
        int hourCount = PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID);

        PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID, ++dayCount);
        PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, ++hourCount);
    }

    public static int getCurrentDailyCount(Context context, String placementID)
    {
        return PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID);
    }

    public static int getCurrentHourlyCount(Context context, String placementID)
    {
        return PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID);
    }

    protected static void updateImpressionCount(Context context, String placementID)
    {
        if (context != null && !TextUtils.isEmpty(placementID))
        {
            Calendar storedCalendar = PubnativeDeliveryManager.getImpressionLastUpdate(context, placementID);
            if (storedCalendar != null)
            {
                Calendar dayCalendar = Calendar.getInstance();
                dayCalendar.add(Calendar.DAY_OF_MONTH, -1);
                if (storedCalendar.before(dayCalendar))
                {
                    PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID, 0);
                    PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, 0);
                }
                else
                {
                    Calendar hourCalendar = Calendar.getInstance();
                    hourCalendar.add(Calendar.HOUR_OF_DAY, -1);
                    if (storedCalendar.before(hourCalendar))
                    {
                        PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, 0);
                    }
                }
            }
            PubnativeDeliveryManager.setImpressionLastUpdate(context, placementID, Calendar.getInstance());
        }
    }

    protected static void setImpressionCount(Context context, String impressionCapType, String placementID, int value)
    {
        if (context != null && !TextUtils.isEmpty(impressionCapType) && !TextUtils.isEmpty(placementID))
        {
            SharedPreferences.Editor editor = PubnativeDeliveryManager.getPreferencesEditor(context);
            if (editor != null)
            {
                String placementTrackingKey = placementID.concat(impressionCapType);
                if (value == 0)
                {
                    editor.remove(placementTrackingKey);
                }
                else
                {
                    editor.putInt(placementTrackingKey, value);
                }
                editor.apply();
            }
        }
    }

    protected static int getImpressionCount(Context context, String impressionCapType, String placementID)
    {
        PubnativeDeliveryManager.updateImpressionCount(context, placementID);
        int result = 0;
        if (context != null && !TextUtils.isEmpty(impressionCapType) && !TextUtils.isEmpty(placementID))
        {
            SharedPreferences preferences = PubnativeDeliveryManager.getPreferences(context);
            if (preferences != null)
            {
                String placementTrackingKey = placementID.concat(impressionCapType);
                result = preferences.getInt(placementTrackingKey, 0);
            }
        }
        return result;
    }

    protected static void setImpressionLastUpdate(Context context, String placementID, Calendar calendar)
    {
        if (context != null && !TextUtils.isEmpty(placementID))
        {
            SharedPreferences.Editor editor = PubnativeDeliveryManager.getPreferencesEditor(context);
            if (editor != null)
            {
                String placementLastUpdateKey = placementID.concat(IMPRESSION_LAST_UPDATE_APPEND);
                if (calendar == null)
                {
                    editor.remove(placementLastUpdateKey);
                }
                else
                {
                    editor.putLong(placementLastUpdateKey, calendar.getTimeInMillis());
                }
                editor.apply();
            }
        }
    }

    protected static Calendar getImpressionLastUpdate(Context context, String placementID)
    {
        Calendar result = null;
        if (context != null && !TextUtils.isEmpty(placementID))
        {
            SharedPreferences preferences = PubnativeDeliveryManager.getPreferences(context);
            if (preferences != null)
            {
                String placementLastUpdateKey = placementID.concat(IMPRESSION_LAST_UPDATE_APPEND);
                long frequencyMillis = preferences.getLong(placementLastUpdateKey, 0);
                if (frequencyMillis > 0)
                {
                    result = Calendar.getInstance();
                    result.setTimeInMillis(frequencyMillis);
                }
            }
        }
        return result;
    }

    protected static SharedPreferences.Editor getPreferencesEditor(Context context)
    {
        SharedPreferences.Editor result = null;
        if (context != null)
        {
            result = PubnativeDeliveryManager.getPreferences(context).edit();
        }
        return result;
    }

    protected static SharedPreferences getPreferences(Context context)
    {
        SharedPreferences result = null;
        if (context != null)
        {
            result = context.getSharedPreferences(IMPRESSION_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
        return result;
    }
}
