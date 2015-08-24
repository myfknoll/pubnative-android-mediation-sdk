package net.pubnative.mediation.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import net.pubnative.mediation.R;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidmartin on 24/08/15.
 */
public class PubnativeTrackingDataModel
{
    protected static final String CONNECTION_TYPE_CELLULAR = "cellular";
    protected static final String CONNECTION_TYPE_WIFI     = "wifi";

    // Tracking info
    public String       network;
    public List<String> attempted_networks;
    public String       placement_id;
    public String       pub_app_version;
    public String       pub_app_bundle_id;
    public String       os_version;
    public String       sdk_version;
    public String       user_uid; // android advertiser id
    public String       connection_type; //typ “wifi” or “cellular"
    public String       device_name;
    public String       ad_format_code;
    public String       creative_url; // Creative selected from the ad_format_code value of the config
    public Boolean      video_start;
    public Boolean      video_complete;

    // User info
    public Integer      age;
    public String       education;
    public List<String> interests;
    public String       gender;
    public Boolean      iap; // In app purchase enabled, Just open it for the user to fill
    public Float        iap_total; // In app purchase total spent, just open for the user to fill

    public void addInterest(String interest)
    {
        if (this.interests == null)
        {
            this.interests = new ArrayList();
        }
        this.interests.add(interest);
    }

    public void addAttemptedNetwork(String network)
    {
        if (this.attempted_networks == null)
        {
            this.attempted_networks = new ArrayList();
        }
        this.attempted_networks.add(network);
    }

    public void reset()
    {
        this.network = null;
        this.attempted_networks = null;
    }

    public void fillDefaults(Context context)
    {
        if (context != null)
        {
            PackageInfo info = PubnativeDeviceUtils.getPackageInfo(context);
            if (info != null)
            {
                this.pub_app_version = info.versionName;
                this.pub_app_bundle_id = info.packageName;
            }
            this.os_version = Build.VERSION.RELEASE;
            this.device_name = Build.MODEL;
            this.sdk_version = context.getResources().getString(R.string.version);

            // AAID
            String androidAdvertisingId = PubnativeDeviceUtils.getAndroidAdvertisingID(context);
            if (androidAdvertisingId != null)
            {
                this.user_uid = androidAdvertisingId;
            }

            // Connection type
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null)
            {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected())
                {
                    String connectionType = CONNECTION_TYPE_CELLULAR;
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                    {
                        connectionType = CONNECTION_TYPE_WIFI;
                    }
                    this.connection_type = connectionType;
                }
            }
        }
    }
}
