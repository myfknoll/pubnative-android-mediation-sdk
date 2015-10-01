package net.pubnative.mediation.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

/**
 * Created by davidmartin on 24/08/15.
 */
public class PubnativeDeviceUtils
{
    /**
     * Gets you the PackageInfo object based on the Context object passed in.
     *
     * @param context valid context object.
     * @return PackageInfo object if context is valid, else null
     */
    public static PackageInfo getPackageInfo(Context context)
    {
        PackageInfo result = null;
        try
        {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        }
        catch (Exception e)
        {
            System.out.println("PubnativeDeviceUtils.getPackageInfo - Error:" + e);
        }
        return result;
    }

    /**
     * Gets you the android advertising id.
     * Note: This method should be called from a non-UI thread.
     *
     * @param context valid Context object
     * @return android advertising id if available, else null.
     */
    public static String getAndroidAdvertisingID(Context context)
    {
        AdvertisingIdClient.Info adInfo = null;
        try
        {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        }
        catch (Exception e)
        {
            System.out.println("PubnativeDeviceUtils.getAndroidAdvertisingID - Error:" + e);
        }

        String androidAdvertisingID = null;
        if (adInfo != null)
        {
            androidAdvertisingID = adInfo.getId();
        }
        return androidAdvertisingID;
    }
}
