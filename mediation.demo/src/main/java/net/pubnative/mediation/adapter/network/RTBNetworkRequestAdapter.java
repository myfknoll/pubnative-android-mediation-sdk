package net.pubnative.mediation.adapter.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;

import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeRequest.Asset;

import net.pubnative.AdvertisingIdClient;
import net.pubnative.advertising_id_client.BuildConfig;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.network.PubnativeHttpRequest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RTBNetworkRequestAdapter extends PubnativeNetworkRequestAdapter
        implements PubnativeHttpRequest.Listener {

    private static final   String TAG           = RTBNetworkRequestAdapter.class.getSimpleName();
    protected static final String ENDPOINT      = "endpoint";
    protected static final int    ICON_SIZE     = 80;
    protected static final int    BANNER_WIDTH  = 1200;
    protected static final int    BANNER_HEIGHT = 627;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public RTBNetworkRequestAdapter(Map data) {

        super(data);
    }
    //==============================================================================================
    // PubnativeNetworkRequestAdapter methods
    //==============================================================================================

    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context == null || mData == null) {
            invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String endpoint = (String) mData.get(ENDPOINT);
            if (endpoint == null) {
                invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                createRequest(context, endpoint);
            }
        }
    }

    protected void createRequest(Context context, final String endpoint) {

        Log.v(TAG, "createRequest: " + endpoint);
        AdvertisingIdClient.getAdvertisingId(context, new AdvertisingIdClient.Listener() {

            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {

                startBid(adInfo, endpoint);
            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {

                startBid(null, endpoint);
            }
        });
    }

    @Override
    public void onPubnativeHttpRequestStart(PubnativeHttpRequest request) {

        Log.v(TAG, "onPubnativeHttpRequestStart");
    }

    @Override
    public void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result) {

        Log.v(TAG, "onPubnativeHttpRequestStart");
        if (TextUtils.isEmpty(result)) {
            invokeFailed(PubnativeException.REQUEST_NO_FILL);
        } else {
            // TODO: Use Google's openrtb repository to generate the response JSON
            // TODO: Send the necessary data about the bid IN BACKGROUND so it doesn't affects the
            // TODO: adapter response time
            // TODO: Parse json into model
            // TODO:
            invokeLoaded(null);
        }
    }

    @Override
    public void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception) {

        Log.v(TAG, "onPubnativeHttpRequestFail");
        invokeFailed(exception);
    }

    //==============================================================================================
    // RTBNetworkRequestAdapter methods
    //==============================================================================================
    protected void startBid(Context context, AdvertisingIdClient.AdInfo info, String endpoint) {

        String json = getBidRequest(context, info).toString();
        // TODO: Test creation of this
        PubnativeHttpRequest request = new PubnativeHttpRequest();
        request.setPOSTString(json);
        request.start(context, endpoint, this);
    }

    // BID composition
    //----------------------------------------------------------------------------------------------
    protected BidRequest getBidRequest(Context context, AdvertisingIdClient.AdInfo info) {
        // Obtain ID
        String id = UUID.randomUUID().toString();
        // Return request
        return BidRequest.newBuilder()
                         .setId(id)
                         .addImp(getImpression(id))
                         .setApp(getApp(context))
                         .setDevice(getDevice(context, info))
                         .build();
    }

    @SuppressWarnings({"MissingPermission"})
    protected BidRequest.Device getDevice(Context context, AdvertisingIdClient.AdInfo info) {

        BidRequest.Device device = BidRequest.Device.newBuilder().build();
        // IP
        String ip = getIP(context);
        if (!TextUtils.isEmpty(ip)) {
            device = BidRequest.Device.newBuilder(device)
                                      .setIp(ip)
                                      .build();
        }
        // Carrier
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        if (!TextUtils.isEmpty(carrierName)) {
            device = BidRequest.Device.newBuilder(device)
                                      .setCarrier(carrierName)
                                      .build();
        }
        // IFA
        if(info != null) {
            device = BidRequest.Device.newBuilder(device)
                                      .setIfa(info.getId())
                                      .setLmt(info.isLimitAdTrackingEnabled())
                                      .build();
        }
        // Others
        String ua = new WebView(context).getSettings().getUserAgentString();
        BidRequest.Device.DeviceType deviceType = BidRequest.Device.DeviceType.PHONE;
        if (isTablet(context)) {
            deviceType = BidRequest.Device.DeviceType.TABLET;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        device = BidRequest.Device.newBuilder(device)
                                  .setUa(ua)
                                  .setGeo(getGeo(context))
                                  .setDnt(true)
                                  // (LMT) set before
                                  // (IP) set before
                                  // ipv6
                                  .setDevicetype(deviceType)
                                  .setMake(Build.MANUFACTURER)
                                  .setModel(Build.MODEL)
                                  .setOs("android")
                                  .setOsv(String.valueOf(Build.VERSION.SDK_INT))
                                  // hwv
                                  .setH(displayMetrics.heightPixels)
                                  .setW(displayMetrics.widthPixels)
                                  // ppi
                                  // pxratio
                                  // js
                                  // flashver
                                  .setLanguage(Locale.getDefault().getDisplayLanguage())
                                  // (CARRIER) set before
                                  .setConnectiontype(getConnectionType(context))
                                  // (IFA) set before
                                  // didsha1
                                  // didmd5
                                  // dpidsha1
                                  // dpidmd5
                                  // macsha1
                                  // macmd5
                                  .build();
        return device;
    }

    protected BidRequest.Geo getGeo(Context context) {

        BidRequest.Geo geo = BidRequest.Geo.newBuilder().build();
        // LOCATION
        Location location = getLastLocation(context);
        if (location != null) {
            geo = BidRequest.Geo.newBuilder(geo)
                                .setLat(location.getLatitude())
                                .setLon(location.getLongitude())
                                .setType(BidRequest.Geo.LocationType.GPS_LOCATION)
                                .build();
        }
        // Other
        TelephonyManager manager = getTelephonyManager(context);
        geo = BidRequest.Geo.newBuilder(geo)
                            // lat
                            // long
                            // type
                            .setCountry(manager.getNetworkCountryIso())
                            // region
                            // regionfips104
                            // metro
                            // city
                            // zip
                            // utcoffset
                            .build();
        return geo;
    }

    protected BidRequest.App getApp(Context context) {

        return BidRequest.App.newBuilder()
                             .setBundle(BuildConfig.APPLICATION_ID)
                             .setVer(BuildConfig.VERSION_NAME)
                             .setName(context.getApplicationInfo().name)
                             .build();
    }

    protected BidRequest.Imp getImpression(String id) {

        return BidRequest.Imp.newBuilder()
                             .setId(id)
                             .setNative(getNative())
                             .build();
    }

    protected Native getNative() {

        return Native.newBuilder()
                     .setRequestNative(getNativeRequest())
                     .build();
    }

    protected NativeRequest getNativeRequest() {

        return NativeRequest.newBuilder()
                            .setVer("1.0")
                            .setPlcmtcnt(1)
                            .addAllAssets(getNativeAssets())
                            .build();
    }

    protected List<Asset> getNativeAssets() {

        List<Asset> result = new ArrayList<>();
        // Title
        result.add(Asset.newBuilder()
                        .setRequired(true)
                        .setTitle(Asset.Title.newBuilder())
                        .build());
        // Description
        result.add(Asset.newBuilder()
                        .setRequired(true)
                        .setData(Asset.Data.newBuilder()
                                           .setLen(100)
                                           .setType(Asset.Data.DataAssetType.DESC))
                        .build());
        // CTA
        result.add(Asset.newBuilder()
                        .setRequired(true)
                        .setData(Asset.Data.newBuilder()
                                           .setType(Asset.Data.DataAssetType.CTATEXT)
                                           .setLen(50))
                        .build());
        // ICON
        result.add(Asset.newBuilder()
                        .setRequired(true)
                        .setImg(Asset.Image.newBuilder()
                                           .setType(Asset.Image.ImageAssetType.ICON)
                                           .setH(ICON_SIZE)
                                           .setW(ICON_SIZE))
                        .build());
        // BANNER
        result.add(Asset.newBuilder()
                        .setRequired(true)
                        .setImg(Asset.Image.newBuilder()
                                           .setType(Asset.Image.ImageAssetType.MAIN)
                                           .setH(BANNER_WIDTH)
                                           .setW(BANNER_HEIGHT))
                        .build());
        // Rating
        result.add(Asset.newBuilder()
                        .setRequired(false)
                        .setData(Asset.Data.newBuilder()
                                           .setType(Asset.Data.DataAssetType.RATING))
                        .build());
        return result;
    }
    // HELPERS
    //----------------------------------------------------------------------------------------------
    //    /**
//     * Get the IP of current Wi-Fi connection
//     * @return IP as string
//     */
//    private String getIP() {
//        try {
//            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            int ipAddress = wifiInfo.getIpAddress();
//            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
//                                 (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
//                                 (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
//        } catch (Exception ex) {
//            Log.e(TAG, ex.getMessage());
//            return null;
//        }
//    }
//
//
//    /**
//     * Get IP For mobile
//     */
//    public static String getMobileIP() {
//
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        String ipaddress = inetAddress.getHostAddress().toString();
//                        return ipaddress;
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Log.e(TAG, "Exception in Get IP Address: " + ex.toString());
//        }
//        return null;
//    }

    protected boolean isTablet(Context context) {

        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    @SuppressWarnings({"MissingPermission"})
    protected Location getLastLocation(Context context) {

        Location result = null;
        if (isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION) &&
            isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            for (String provider : manager.getProviders(true)) {
                result = manager.getLastKnownLocation(provider);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"MissingPermission"})
    protected String getIP(Context context) {

        String result = null;
        if (isPermissionGranted(context, Manifest.permission.ACCESS_WIFI_STATE)) {
            try {
                InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
            }
            result = Formatter.formatIpAddress(getWifiServiceManager(context).getConnectionInfo()
                                                                             .getIpAddress());
        }
        return result;
    }

    protected BidRequest.Device.ConnectionType getConnectionType(Context context) {

        BidRequest.Device.ConnectionType result = BidRequest.Device.ConnectionType.CONNECTION_UNKNOWN;
        switch (getNetworkType(context)) {
            case ConnectivityManager.TYPE_WIFI: {
                result = BidRequest.Device.ConnectionType.WIFI;
            }
            break;
            case ConnectivityManager.TYPE_MOBILE: {
                result = getMobileConnection(context);
            }
            break;
            default:
        }
        return result;
    }

    protected BidRequest.Device.ConnectionType getMobileConnection(Context context) {

        BidRequest.Device.ConnectionType result = BidRequest.Device.ConnectionType.CELL_UNKNOWN;

        switch (getTelephonyManager(context).getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                result = BidRequest.Device.ConnectionType.CELL_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                /**
                 From this link https://goo.gl/R2HOjR ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                 EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.

                 Where CDMA2000 https://goo.gl/1y10WI .CDMA2000 is a family of 3G[1] mobile technology standards for sending voice,
                 data, and signaling data between mobile phones and cell sites.
                 */
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                //Log.d("Type", "3g");
                //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                //Some cases are added after  testing(real) in device with 3g enable data
                //and speed also matters to decide 3g network type
                //http://goo.gl/bhtVT
                result = BidRequest.Device.ConnectionType.CELL_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                //No specification for the 4g but from wiki
                //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                //https://goo.gl/9t7yrR
                result = BidRequest.Device.ConnectionType.CELL_4G;
                break;
        }
        return result;
    }

    protected int getNetworkType(Context context) {

        int result = -1;
        NetworkInfo info = getConnectivityManager(context).getActiveNetworkInfo();
        if (info != null &&
            info.isConnected()) {
            result = info.getType();
        }
        return result;
    }

    protected boolean isPermissionGranted(Context context, String permission) {

        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    protected TelephonyManager getTelephonyManager (Context context) {

        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    protected WifiManager getWifiServiceManager (Context context) {

        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    protected WindowManager getWindowServiceManager (Context context) {

        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    protected ConnectivityManager getConnectivityManager (Context context) {

        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
