package net.pubnative.library.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.library.PubnativeContract.Response;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.task.AsyncHttpTask;
import net.pubnative.library.util.IdUtil;

import java.util.ArrayList;
import java.util.List;

public class TrackingManager
{
    private static final String    SHARED_FILE        = "net.pubnative.library.managers.TrackingManager";
    private static final String    CONFIRMED_URLS_SET = "net.pubnative.library.managers.TrackingManager.confirmed_urls";
    private static final String    PENDING_URLS_SET   = "net.pubnative.library.managers.TrackingManager.pending_urls";
    private static boolean         isTracking         = false;                                                           ;

    private static List<String> getSharedList(final Context context, String set)
    {
        List<String> result = null;
        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE, 0);
        if(preferences != null && preferences.contains(set))
        {
            String sharedListString = preferences.getString(set, null);

            if (sharedListString != null)
            {
                Gson gson = new Gson();
                result = gson.fromJson(sharedListString, List.class);
            }
        }
        return result;
    }

    private static void putToSharedList(final Context context, String set, String value)
    {
        List<String> sharedList = TrackingManager.getSharedList(context, set);
        if (sharedList == null)
        {
            sharedList = new ArrayList<String>();
        }
        if (!sharedList.contains(value))
        {
            sharedList.add(value);
            Gson gson = new Gson();
            String sharedListString = gson.toJson(sharedList);

            Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
            editablePreferences.putString(set, sharedListString);
            editablePreferences.apply();
        }
    }

    private static void removeFromSharedList(final Context context, String set, String value)
    {
        List<String> sharedList = TrackingManager.getSharedList(context, set);
        if (sharedList != null && sharedList.contains(value))
        {
            sharedList.remove(value);
            Gson gson = new Gson();
            String sharedListString = gson.toJson(sharedList);

            Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
            editablePreferences.putString(set, sharedListString);
            editablePreferences.apply();
        }
    }

    public synchronized static boolean isTrackedBeacon(Context context, NativeAdModel ad, String beacon)
    {
        boolean result = false;
        List<String> confirmedAds = TrackingManager.getSharedList(context, CONFIRMED_URLS_SET);
        String beaconURL = ad.getBeaconURL(beacon);
        if (confirmedAds != null && confirmedAds.contains(beaconURL))
        {
            result = true;
        }
        return result;
    }

    public synchronized static void TrackBeacon(Context context, NativeAdModel ad, String beacon)
    {
        List<String> confirmedAds = TrackingManager.getSharedList(context, CONFIRMED_URLS_SET);
        if (confirmedAds == null)
        {
            confirmedAds = new ArrayList<String>();
        }
        String beaconString = ad.getBeaconURL(beacon);
        Uri.Builder beaconURLBuilder = Uri.parse(beaconString).buildUpon();
        if (beacon.equals(Response.NativeAd.Beacon.TYPE_IMPRESSION))
        {
            if (ad.app_details != null && ad.app_details.store_id != null && IdUtil.isPackageInstalled(context, ad.app_details.store_id))
            {
                beaconURLBuilder.appendQueryParameter("installed", "1");
            }
        }
        Uri beaconURL = beaconURLBuilder.build();
        if (!confirmedAds.contains(beaconURL.toString()))
        {
            TrackingManager.putToSharedList(context, PENDING_URLS_SET, beaconURL.toString());
            TrackingManager.trackNext(context);
        }
        else
        {
            // Do nothing, the ad was previously confirmed
        }
    }

    private static void trackNext(Context context)
    {
        if (!isTracking)
        {
            isTracking = true;
            Object[] trackingURLs = TrackingManager.getSharedList(context, PENDING_URLS_SET).toArray();
            if (trackingURLs.length > 0)
            {
                String trackingURL = (String) TrackingManager.getSharedList(context, PENDING_URLS_SET).toArray()[0];
                AsyncHttpTask task = new AsyncHttpTask(context);
                task.setListener(new AsyncHttpTask.AsyncHttpTaskListener()
                {
                    @Override
                    public void onAsyncHttpTaskFinished(AsyncHttpTask task, String result)
                    {
                        Log.d("Pubnative", "tracked beacon success: " + result);
                        TrackingManager.onTrackSuccess(task.getContext(), task.getHttpUrl());
                    }

                    @Override
                    public void onAsyncHttpTaskFailed(AsyncHttpTask task, Exception e)
                    {
                        Log.d("Pubnative", "tracked beacon error: " + e);
                        TrackingManager.onTrackFailed(task.getContext(), task.getHttpUrl());
                    }
                });
                task.execute(trackingURL);
            }
            else
            {
                isTracking = false;
            }
        }
    }

    private static void onTrackSuccess(Context context, String url)
    {
        TrackingManager.putToSharedList(context, CONFIRMED_URLS_SET, url);
        TrackingManager.removeFromSharedList(context, PENDING_URLS_SET, url);
        TrackingManager.isTracking = false;
        TrackingManager.trackNext(context);
    }

    private static void onTrackFailed(Context context, String url)
    {
        TrackingManager.removeFromSharedList(context, PENDING_URLS_SET, url);
        TrackingManager.putToSharedList(context, PENDING_URLS_SET, url);
        TrackingManager.isTracking = false;
        TrackingManager.trackNext(context);
    }
}
