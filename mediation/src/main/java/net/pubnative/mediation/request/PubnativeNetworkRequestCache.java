package net.pubnative.mediation.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.mediation.model.PubnativeAdFormatModel;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeCacheAdModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by davidmartin on 05/08/15.
 */
public class PubnativeNetworkRequestCache
{
    protected static final String REQUEST_CACHE_PREFERENCES = "net.pubnative.mediation.request.cache";
    protected static final String IMPRESSION_LOG_KEY        = "impression_log";
    protected static final String PACING_CACHE_KEY          = "ads_cache";

    public synchronized void update(Context context, PubnativeAdFormatModel model)
    {
        this.updateCache(context, model);
        this.updateImpressionLog(context, model);
    }

    public synchronized List<PubnativeAdModel> getPacingCache(Context context)
    {
        return new ArrayList<PubnativeAdModel>(this.getCache(context)
                                                   .values());
    }

    public synchronized int getLogSize(Context context)
    {
        return this.getImpressionLog(context)
                   .size();
    }

    public synchronized void logImpression(Context context, PubnativeAdModel ad)
    {
        if (ad == null)
        {
            // Drop the call for null ads
            return;
        }

        Long currentTimestamp = new Long(System.currentTimeMillis());

        List<Long> impressions = this.getImpressionLog(context);
        if (impressions == null)
        {
            impressions = new ArrayList<>();
        }
        impressions.add(currentTimestamp);
        this.setImpressionLog(context, impressions);

        Map<Long, PubnativeCacheAdModel> pacingCache = this.getCache(context);
        if (pacingCache == null)
        {
            pacingCache = new HashMap<>();
        }
        pacingCache.put(currentTimestamp, new PubnativeCacheAdModel(ad));
        this.setCache(context, pacingCache);
    }

    private Map<Long, PubnativeCacheAdModel> getCache(Context context)
    {
        Map<Long, PubnativeCacheAdModel> result = null;
        SharedPreferences preferences = this.getPreferences(context);
        String cachedAdsString = preferences.getString(PACING_CACHE_KEY, null);

        if (!TextUtils.isEmpty(cachedAdsString))
        {
            Gson gson = new Gson();
            result = gson.fromJson(cachedAdsString, Map.class);
        }
        return result;
    }

    private void setCache(Context context, Map<Long, PubnativeCacheAdModel> cachedAds)
    {
        SharedPreferences.Editor preferencesEditor = this.getPreferences(context)
                                                         .edit();
        Gson gson = new Gson();
        String impressionsString = gson.toJson(cachedAds);
        preferencesEditor.putString(PACING_CACHE_KEY, gson.toJson(impressionsString));
        preferencesEditor.apply();
    }

    private void updateCache(Context context, PubnativeAdFormatModel model)
    {
        Calendar overdueDate = model.getPacingOverdueCalendar();
        if(overdueDate != null)
        {
            Calendar pacingDate = Calendar.getInstance();

            Map<Long, PubnativeCacheAdModel> cachedAdsLog = this.getCache(context);

            Iterator<Map.Entry<Long, PubnativeCacheAdModel>> iterator = cachedAdsLog.entrySet()
                                                                                    .iterator();
            while (iterator.hasNext())
            {
                Map.Entry<Long, PubnativeCacheAdModel> entry = iterator.next();
                Long timestamp = entry.getKey();
                pacingDate.setTimeInMillis(timestamp);
                if (pacingDate.before(overdueDate))
                {
                    iterator.remove();
                }
            }

            this.setCache(context, cachedAdsLog);
        }
    }

    private List<Long> getImpressionLog(Context context)
    {
        List<Long> result = null;
        SharedPreferences preferences = this.getPreferences(context);
        String frequenciesString = preferences.getString(IMPRESSION_LOG_KEY, null);
        if (!TextUtils.isEmpty(frequenciesString))
        {
            Gson gson = new Gson();
            result = gson.fromJson(frequenciesString, List.class);
        }
        return result;
    }

    private void setImpressionLog(Context context, List<Long> impressionLog)
    {
        SharedPreferences.Editor preferencesEditor = this.getPreferences(context)
                                                         .edit();
        Gson gson = new Gson();
        String impressionsString = gson.toJson(impressionLog);
        preferencesEditor.putString(IMPRESSION_LOG_KEY, gson.toJson(impressionsString));
        preferencesEditor.apply();
    }

    private void updateImpressionLog(Context context, PubnativeAdFormatModel model)
    {
        List<Long> log = this.getImpressionLog(context);

        // 1. Clean current impressionLog of old logs for the window
        Calendar overdueDate = model.getImpressionOverdueCalendar();
        if(overdueDate != null)
        {
            Calendar frequencyDate = Calendar.getInstance();

            Iterator<Long> iterator = log.iterator();
            while (iterator.hasNext())
            {
                Long frequencyTimestamp = iterator.next();
                frequencyDate.setTimeInMillis(frequencyTimestamp);
                if (frequencyDate.before(overdueDate))
                {
                    iterator.remove();
                }
            }

            // 2. Set the cleaned log
            this.setImpressionLog(context, log);
        }
    }

    private SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(REQUEST_CACHE_PREFERENCES, Context.MODE_PRIVATE);
    }
}
