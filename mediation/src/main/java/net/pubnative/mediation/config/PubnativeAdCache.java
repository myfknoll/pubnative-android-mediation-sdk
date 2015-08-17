package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by davidmartin on 15/08/15.
 */
public class PubnativeAdCache
{
    public static final String AD_CACHE_PREFERENCES_KEY = "net.pubnative.mediation.ad_cache";
    public static final String AD_CACHE_LIST_KEY        = "ad_list";

//    public synchronized void logImpression(Context context, PubnativeAdModel ad)
//    {
//        if (ad == null)
//        {
//            // Drop the call for null ads
//            return;
//        }
//
//        Long currentTimestamp = new Long(System.currentTimeMillis());
//
//
//        Map<Long, PubnativeCacheAdModel> pacingCache = this.getCache(context);
//        if (pacingCache == null)
//        {
//            pacingCache = new HashMap();
//        }
//        pacingCache.put(currentTimestamp, new PubnativeCacheAdModel(ad));
//        this.setCache(context, pacingCache);
//    }
//
//    private Map<Long, PubnativeCacheAdModel> getCache(Context context)
//    {
//        Map<Long, PubnativeCacheAdModel> result = null;
//        SharedPreferences preferences = this.getPreferences(context);
//        //        String cachedAdsString = preferences.getString(PACING_CACHE_KEY, null);
//
//        //        if (!TextUtils.isEmpty(cachedAdsString))
//        //        {
//        //            Gson gson = new Gson();
//        //            result = gson.fromJson(cachedAdsString, Map.class);
//        //        }
//        return result;
//    }
//
//    private void setCache(Context context, Map<Long, PubnativeCacheAdModel> cachedAds)
//    {
//        SharedPreferences.Editor preferencesEditor = this.getPreferences(context)
//                                                         .edit();
//        Gson gson = new Gson();
//        String impressionsString = gson.toJson(cachedAds);
//        //        preferencesEditor.putString(PACING_CACHE_KEY, gson.toJson(impressionsString));
//        preferencesEditor.apply();
//    }
//
//    private void updateCache(Context context, PubnativeAdFormatModel model)
//    {
//        Calendar overdueDate = model.getPacingOverdueCalendar();
//        if (overdueDate != null)
//        {
//            Calendar pacingDate = Calendar.getInstance();
//
//            Map<Long, PubnativeCacheAdModel> cachedAdsLog = this.getCache(context);
//
//            Iterator<Map.Entry<Long, PubnativeCacheAdModel>> iterator = cachedAdsLog.entrySet()
//                                                                                    .iterator();
//            while (iterator.hasNext())
//            {
//                Map.Entry<Long, PubnativeCacheAdModel> entry = iterator.next();
//                Long timestamp = entry.getKey();
//                pacingDate.setTimeInMillis(timestamp);
//                if (pacingDate.before(overdueDate))
//                {
//                    iterator.remove();
//                }
//            }
//
//            this.setCache(context, cachedAdsLog);
//        }
//    }

    private SharedPreferences.Editor getPreferencesEditor(Context context)
    {
        return this.getPreferences(context)
                   .edit();
    }

    private SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(AD_CACHE_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }
}
