package net.pubnative.mediation.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by davidmartin on 24/08/15.
 */
public class PubnativeDeviceUtils
{
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

    public static String getPublicIpAddress()
    {
        String ipAddress = null;
        try
        {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://ip2country.sourceforge.net/ip2c.php?format=JSON");
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            String responseString = EntityUtils.toString(httpEntity);
            JSONObject responseJSON = new JSONObject(responseString);
            ipAddress = responseJSON.getString("ip");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ipAddress;
    }
}
