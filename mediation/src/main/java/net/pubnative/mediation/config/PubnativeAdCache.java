package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeCacheAdModel;
import net.pubnative.mediation.model.PubnativeDeliveryRuleModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by davidmartin on 15/08/15.
 */
public class PubnativeAdCache
{
    public static final String ADS_CACHE_PREFERENCES_KEY    = "net.pubnative.mediation.ad_cache";
    public static final String ADS_CACHE_LIST_KEY           = "ad_list";
    public static final String ADS_CACHE_TIMESTAMP_KEY      = "last_update";

    public static void updateAdsCache(Context context, PubnativeDeliveryRuleModel deliveryRules)
    {
        if (context != null)
        {
            Long lastUpdate = PubnativeAdCache.getLastUpdate(context);
            if (lastUpdate != null && deliveryRules != null)
            {
                Calendar overdueCalendar = Calendar.getInstance();
                if(deliveryRules.isPacingCapActive())
                {
                    int calendarPacingField = Calendar.HOUR_OF_DAY;
                    if(deliveryRules.pacing_cap_minute > 0)
                    {
                        calendarPacingField = Calendar.MINUTE;
                    }
                    overdueCalendar.set(calendarPacingField, -1);
                }

                if(Calendar.getInstance().before(overdueCalendar))
                {
                    PubnativeAdCache.setCachedAds(context, null);
                }
            }
        }
    }

    public static void cacheAd(Context context, PubnativeAdModel model)
    {
        if (context != null && model != null)
        {
            List<PubnativeCacheAdModel> adsCache = PubnativeAdCache.getCachedAds(context);
            if (adsCache == null)
            {
                adsCache = new ArrayList();
            }
            PubnativeCacheAdModel cacheModel = new PubnativeCacheAdModel(model);
            adsCache.add(cacheModel);
            PubnativeAdCache.setCachedAds(context, adsCache);
        }
    }

    public static List<PubnativeCacheAdModel> getCachedAds(Context context)
    {
        List<PubnativeCacheAdModel> result = null;
        if (context != null)
        {
            SharedPreferences preferences = PubnativeAdCache.getPreferences(context);
            String cacheString = preferences.getString(ADS_CACHE_LIST_KEY, null);
            if (!TextUtils.isEmpty(cacheString))
            {
                try
                {
                    result = new Gson().fromJson(cacheString, List.class);
                }
                catch (Exception e)
                {
                    System.out.println("Pubnative - error retrieving ads cache");
                }
            }
        }
        return result;
    }

    protected static void setCachedAds(Context context, List<PubnativeCacheAdModel> adsCache)
    {
        if (context != null)
        {
            SharedPreferences.Editor preferencesEditor = PubnativeAdCache.getPreferences(context).edit();
            if (adsCache == null || adsCache.size() == 0)
            {
                preferencesEditor.remove(ADS_CACHE_LIST_KEY);
                preferencesEditor.apply();
                PubnativeAdCache.setLastUpdate(context, System.currentTimeMillis());
            }
            else
            {
                try
                {
                    String cacheString = new Gson().toJson(adsCache);
                    preferencesEditor.putString(ADS_CACHE_LIST_KEY, cacheString);
                    preferencesEditor.apply();
                    PubnativeAdCache.setLastUpdate(context, System.currentTimeMillis());
                }
                catch (Exception e)
                {
                    System.out.println("Pubnative - error setting ads cache");
                }
            }
        }
    }

    protected static Long getLastUpdate(Context context)
    {
        Long result = null;
        if (context != null)
        {
            SharedPreferences preferences = PubnativeAdCache.getPreferences(context);
            result = preferences.getLong(ADS_CACHE_TIMESTAMP_KEY, 0);
            result = result > 0 ? result : null; // Return null or valid timestamp
        }
        return result;
    }

    protected static void setLastUpdate(Context context, Long timestamp)
    {
        if (context != null)
        {
            SharedPreferences.Editor editor = PubnativeAdCache.getPreferencesEditor(context);
            if (timestamp == null || timestamp <= 0)
            {
                editor.remove(ADS_CACHE_TIMESTAMP_KEY);
            }
            else
            {
                editor.putLong(ADS_CACHE_TIMESTAMP_KEY, timestamp);
            }
            editor.apply();
        }
    }

    private static SharedPreferences.Editor getPreferencesEditor(Context context)
    {
        return PubnativeAdCache.getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(ADS_CACHE_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
}
