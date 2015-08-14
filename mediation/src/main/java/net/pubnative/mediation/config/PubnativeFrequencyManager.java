package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by davidmartin on 15/08/15.
 */
public class PubnativeFrequencyManager
{
    protected static final String IMPRESSION_PREFERENCES_KEY = "net.pubnative.mediation.frequency_manager";
    protected static final String IMPRESSION_DAY_COUNT_KEY   = "freq_day_count";
    protected static final String IMPRESSION_HOUR_COUNT_KEY  = "freq_hour_count";
    protected static final String IMPRESSION_LAST_UPDATE_KEY = "freq_millis";

    public static void logImpression(Context context)
    {
        PubnativeFrequencyManager.updateImpressionCount(context);

        int dayCount = PubnativeFrequencyManager.getImpressionCount(context, IMPRESSION_DAY_COUNT_KEY);
        int hourCount = PubnativeFrequencyManager.getImpressionCount(context, IMPRESSION_HOUR_COUNT_KEY);

        PubnativeFrequencyManager.setImpressionCount(context, IMPRESSION_DAY_COUNT_KEY, ++dayCount);
        PubnativeFrequencyManager.setImpressionCount(context, IMPRESSION_HOUR_COUNT_KEY, ++hourCount);
    }

    public static int getCurrentDailyCount(Context context)
    {
        PubnativeFrequencyManager.updateImpressionCount(context);
        return PubnativeFrequencyManager.getImpressionCount(context, IMPRESSION_DAY_COUNT_KEY);
    }

    public static int getCurrentHourlyCount(Context context)
    {
        PubnativeFrequencyManager.updateImpressionCount(context);
        return PubnativeFrequencyManager.getImpressionCount(context, IMPRESSION_HOUR_COUNT_KEY);
    }

    protected static void updateImpressionCount(Context context)
    {
        if(context != null)
        {
            Calendar storedCalendar = PubnativeFrequencyManager.getLastUpdate(context);
            if (storedCalendar != null)
            {
                Calendar dayCalendar = Calendar.getInstance();
                dayCalendar.add(Calendar.DAY_OF_MONTH, -1);
                if (storedCalendar.before(dayCalendar))
                {
                    PubnativeFrequencyManager.setImpressionCount(context, IMPRESSION_DAY_COUNT_KEY, 0);
                    PubnativeFrequencyManager.setImpressionCount(context, IMPRESSION_HOUR_COUNT_KEY, 0);
                }
                else
                {
                    Calendar hourCalendar = Calendar.getInstance();
                    hourCalendar.add(Calendar.HOUR_OF_DAY, -1);
                    if (storedCalendar.before(hourCalendar))
                    {
                        PubnativeFrequencyManager.setImpressionCount(context, IMPRESSION_HOUR_COUNT_KEY, 0);
                    }
                }
            }
            PubnativeFrequencyManager.setLastUpdate(context, Calendar.getInstance());
        }
    }

    protected static void setImpressionCount(Context context, String trackingKey, int value)
    {
        if(context != null && !TextUtils.isEmpty(trackingKey))
        {
            SharedPreferences.Editor editor = PubnativeFrequencyManager.getPreferencesEditor(context);
            if (editor != null)
            {
                if (value == 0)
                {
                    editor.remove(trackingKey);
                }
                else
                {
                    editor.putInt(trackingKey, value);
                }
                editor.apply();
            }
        }
    }

    protected static int getImpressionCount(Context context, String trackingKey)
    {
        int result = 0;
        if(!TextUtils.isEmpty(trackingKey))
        {
            SharedPreferences preferences = PubnativeFrequencyManager.getPreferences(context);
            if (preferences != null)
            {
                result = preferences.getInt(trackingKey, 0);
            }
        }
        return result;
    }

    protected static void setLastUpdate(Context context, Calendar calendar)
    {
        if (context != null)
        {
            SharedPreferences.Editor editor = PubnativeFrequencyManager.getPreferencesEditor(context);
            if(editor != null)
            {
                if (calendar == null)
                {
                    editor.remove(IMPRESSION_LAST_UPDATE_KEY);
                }
                else
                {
                    editor.putLong(IMPRESSION_LAST_UPDATE_KEY, calendar.getTimeInMillis());
                }
                editor.apply();
            }
        }
    }

    protected static Calendar getLastUpdate(Context context)
    {
        Calendar result = null;
        if (context != null)
        {
            SharedPreferences preferences = PubnativeFrequencyManager.getPreferences(context);
            if (preferences != null && preferences.contains(IMPRESSION_LAST_UPDATE_KEY))
            {
                long frequencyMillis = preferences.getLong(IMPRESSION_LAST_UPDATE_KEY, 0);
                result = Calendar.getInstance();
                result.setTimeInMillis(frequencyMillis);
            }
        }
        return result;
    }

    protected static SharedPreferences.Editor getPreferencesEditor(Context context)
    {
        SharedPreferences.Editor result = null;
        if (context != null)
        {
            result = PubnativeFrequencyManager.getPreferences(context)
                                              .edit();
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
