package net.pubnative.library.managers;

import java.util.HashSet;

import net.pubnative.library.PubnativeContract.Response;
import net.pubnative.library.managers.task.InvokeLinkTask;
import net.pubnative.library.managers.task.InvokeLinkTask.InvokeLinkTaskListener;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.util.IdUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

public class TrackingManager implements InvokeLinkTaskListener
{
    private static TrackingManager instance;
    private static final String    SHARED_FILE        = "net.pubnative.library.managers.TrackingManager";
    private static final String    CONFIRMED_URLS_SET = "net.pubnative.library.managers.TrackingManager.confirmed_urls";
    private static final String    PENDING_URLS_SET   = "net.pubnative.library.managers.TrackingManager.pending_urls";
    private static boolean         isTracking         = false;                                                           ;

    private static TrackingManager getInstance()
    {
        if (TrackingManager.instance == null)
        {
            TrackingManager.instance = new TrackingManager();
        }
        return TrackingManager.instance;
    }

    private static HashSet<String> getSharedSet(final Context context, String set)
    {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE, 0);
        return (HashSet<String>) preferences.getStringSet(set, null);
    }

    private static void putToSharedSet(final Context context, String set, String value)
    {
        HashSet<String> sharedSet = TrackingManager.getSharedSet(context, set);
        if (sharedSet == null)
        {
            sharedSet = new HashSet<String>();
        }
        if (!sharedSet.contains(value))
        {
            sharedSet.add(value);
            Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
            editablePreferences.putStringSet(set, sharedSet);
            editablePreferences.apply();
        }
    }

    private static void removeFromSharedSet(final Context context, String set, String value)
    {
        HashSet<String> sharedSet = TrackingManager.getSharedSet(context, set);
        if (sharedSet == null)
        {
            sharedSet = new HashSet<String>();
        }
        if (sharedSet.contains(value))
        {
            sharedSet.remove(value);
            Editor editablePreferences = context.getSharedPreferences(SHARED_FILE, 0).edit();
            editablePreferences.putStringSet(set, sharedSet);
            editablePreferences.apply();
        }
    }

    public static boolean isTrackedBeacon(Context context, NativeAdModel ad, String beacon)
    {
        boolean result = false;
        HashSet<String> confirmedAds = TrackingManager.getSharedSet(context, CONFIRMED_URLS_SET);
        String beaconURL = ad.getBeaconURL(beacon);
        if (confirmedAds != null && confirmedAds.contains(beaconURL))
        {
            result = true;
        }
        return result;
    }

    public static void TrackBeacon(Context context, NativeAdModel ad, String beacon)
    {
        HashSet<String> confirmedAds = TrackingManager.getSharedSet(context, CONFIRMED_URLS_SET);
        if (confirmedAds == null)
        {
            confirmedAds = new HashSet<String>();
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
            TrackingManager.putToSharedSet(context, PENDING_URLS_SET, beaconURL.toString());
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
            Object[] trackingURLs = TrackingManager.getSharedSet(context, PENDING_URLS_SET).toArray();
            if (trackingURLs.length > 0)
            {
                String trackingURL = (String) TrackingManager.getSharedSet(context, PENDING_URLS_SET).toArray()[0];
                new InvokeLinkTask(context, TrackingManager.getInstance(), trackingURL).execute();
            }
            else
            {
                isTracking = false;
            }
        }
    }

    @Override
    public void onInvokeLinkTaskFinished(InvokeLinkTask task)
    {
        TrackingManager.putToSharedSet(task.context, CONFIRMED_URLS_SET, task.link);
        TrackingManager.removeFromSharedSet(task.context, PENDING_URLS_SET, task.link);
        isTracking = false;
        TrackingManager.trackNext(task.context);
    }
}
