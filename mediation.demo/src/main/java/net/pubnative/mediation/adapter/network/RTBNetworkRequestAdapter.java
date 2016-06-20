package net.pubnative.mediation.adapter.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;

import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidRequest;
import com.google.openrtb.OpenRtb.BidRequest.Imp.Native;
import com.google.openrtb.OpenRtb.NativeRequest;
import com.google.openrtb.OpenRtb.NativeRequest.Asset;
import com.google.openrtb.json.OpenRtbJsonFactory;

import net.pubnative.AdvertisingIdClient;
import net.pubnative.advertising_id_client.BuildConfig;
import net.pubnative.mediation.adapter.model.RTBNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.network.PubnativeHttpRequest;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
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

    protected void createRequest(final Context context, final String endpoint) {

        Log.v(TAG, "createRequest: " + endpoint);
        AdvertisingIdClient.getAdvertisingId(context, new AdvertisingIdClient.Listener() {

            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {

                startBid(context, adInfo, endpoint);
            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {

                startBid(context, null, endpoint);
            }
        });
    }

    //==============================================================================================
    // RTBNetworkRequestAdapter methods
    //==============================================================================================
    protected void startBid(Context context, AdvertisingIdClient.AdInfo info, String endpoint) {

        Log.v(TAG, "startBid");
        try {
            BidRequest request = getBidRequest(context, info);
            String requestJSON = OpenRtbJsonFactory.create()
                                                   .newWriter()
                                                   .writeBidRequest(request);
            PubnativeHttpRequest http = new PubnativeHttpRequest();
            http.setPOSTString(requestJSON);
            http.start(context, endpoint, this);
        } catch (IOException e) {
            invokeFailed(e);
        }
    }

    // BID composition
    //----------------------------------------------------------------------------------------------
    protected BidRequest getBidRequest(Context context, AdvertisingIdClient.AdInfo info) {

        Log.v(TAG, "getBidRequest");
        // Obtain ID
        String id = UUID.randomUUID().toString();
        double bidFloor = 0.01;
        String bidFloorCur = "USD";
        BidRequest.AuctionType auctionType = BidRequest.AuctionType.FIRST_PRICE;
        int timeout = 1000;
        List<String> wseats = new ArrayList<>();
        wseats.add("");
        List<String> currencies = new ArrayList<>();
        currencies.add("USD");
        // Return request
        BidRequest.Builder builder = BidRequest.newBuilder();
        builder.setId(id)
               .addImp(getImpression(id, bidFloor, bidFloorCur))
               .setApp(getApp(context))
               .setDevice(getDevice(context, info))
               .setUser(getUser(context, info))
               .setAt(auctionType)
               .setTmax(timeout);
        for (String wseat : wseats) {
            builder.addWseat(wseat);
        }
        for (String currency : currencies) {
            builder.addCur(currency);
        }
        return builder.build();
    }

    private BidRequest.User.Builder getUser(Context context, AdvertisingIdClient.AdInfo info) {

        BidRequest.User.Builder builder = BidRequest.User.newBuilder();
        if (info != null && !info.isLimitAdTrackingEnabled()) {
            builder.setId(info.getId());
        }
        return builder;
    }

    @SuppressWarnings({"MissingPermission"})
    protected BidRequest.Device.Builder getDevice(Context context,
            AdvertisingIdClient.AdInfo info) {

        BidRequest.Device.Builder builder = BidRequest.Device.newBuilder();
        // IP
        String ip = getIP(context);
        if (!TextUtils.isEmpty(ip)) {
            builder.setIp(ip);
        }
        // Carrier
        String carrierName = getTelephonyManager(context).getNetworkOperatorName();
        if (!TextUtils.isEmpty(carrierName)) {
            builder.setCarrier(carrierName);
        }
        // IFA
        if (info != null) {
            builder.setLmt(info.isLimitAdTrackingEnabled());
            if (!info.isLimitAdTrackingEnabled()) {
                builder.setIfa(info.getId());
            }
        }
        // Others
        String ua = new WebView(context).getSettings().getUserAgentString();
        BidRequest.Device.DeviceType deviceType = BidRequest.Device.DeviceType.PHONE;
        if (isTablet(context)) {
            deviceType = BidRequest.Device.DeviceType.TABLET;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowServiceManager(context).getDefaultDisplay().getMetrics(displayMetrics);
        builder.setUa(ua)
               .setGeo(getGeo(context))
               .setDnt(true)
               .setDevicetype(deviceType)
               .setMake(Build.MANUFACTURER)
               .setModel(Build.MODEL)
               .setOs("android")
               .setOsv(String.valueOf(Build.VERSION.SDK_INT))
               .setH(displayMetrics.heightPixels)
               .setW(displayMetrics.widthPixels)
               .setLanguage(Locale.getDefault().getDisplayLanguage())
               .setConnectiontype(getConnectionType(context));
        return builder;
    }

    protected BidRequest.Geo.Builder getGeo(Context context) {

        BidRequest.Geo.Builder builder = BidRequest.Geo.newBuilder();
        // LOCATION
        Location location = getLastLocation(context);
        if (location != null) {
            builder.setLat(location.getLatitude())
                   .setLon(location.getLongitude())
                   .setType(BidRequest.Geo.LocationType.GPS_LOCATION)
                   .build();
        }
        builder.setCountry(getTelephonyManager(context).getNetworkCountryIso());
        return builder;
    }

    protected BidRequest.App getApp(Context context) {

        ApplicationInfo app = context.getApplicationInfo();
        return BidRequest.App.newBuilder()
                             .setBundle(BuildConfig.APPLICATION_ID)
                             .setVer(BuildConfig.VERSION_NAME)
//                             .setName(app.name)
                             .build();
    }

    protected BidRequest.Imp getImpression(String id, double bidFloor, String bidFloorCur) {

        return BidRequest.Imp.newBuilder()
                             .setId(id)
                             .setNative(getNative())
                             .setBidfloor(bidFloor)
                             .setBidfloorcur(bidFloorCur)
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
                        .setId(RTBNetworkModel.RTBAssetTitleID)
                        .setRequired(true)
                        .setTitle(Asset.Title.newBuilder()
                                             .setLen(50))
                        .build());
        // Description
        result.add(Asset.newBuilder()
                        .setId(RTBNetworkModel.RTBAssetDescriptionlID)
                        .setRequired(true)
                        .setData(Asset.Data.newBuilder()
                                           .setLen(100)
                                           .setType(Asset.Data.DataAssetType.DESC))
                        .build());
        // CTA
        result.add(Asset.newBuilder()
                        .setId(RTBNetworkModel.RTBAssetCTAID)
                        .setRequired(true)
                        .setData(Asset.Data.newBuilder()
                                           .setType(Asset.Data.DataAssetType.CTATEXT))
                        .build());
        // ICON
        result.add(Asset.newBuilder()
                        .setId(RTBNetworkModel.RTBAssetIconID)
                        .setRequired(true)
                        .setImg(Asset.Image.newBuilder()
                                           .setType(Asset.Image.ImageAssetType.ICON)
                                           .setH(ICON_SIZE)
                                           .setW(ICON_SIZE))
                        .build());
        // BANNER
        result.add(Asset.newBuilder()
                        .setId(RTBNetworkModel.RTBAssetBannerID)
                        .setRequired(true)
                        .setImg(Asset.Image.newBuilder()
                                           .setType(Asset.Image.ImageAssetType.MAIN)
                                           .setH(BANNER_WIDTH)
                                           .setW(BANNER_HEIGHT))
                        .build());
        // Rating
        result.add(Asset.newBuilder()
                        .setId(RTBNetworkModel.RTBAssetRatingID)
                        .setRequired(false)
                        .setData(Asset.Data.newBuilder()
                                           .setType(Asset.Data.DataAssetType.RATING))
                        .build());
        return result;
    }
    // HELPERS
    //----------------------------------------------------------------------------------------------
    //

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

    protected String getIP(Context context) {

        String result = null;
        switch (getNetworkType(context)) {
            case ConnectivityManager.TYPE_WIFI:
                result = getWifiIP(context);
                break;
            case ConnectivityManager.TYPE_MOBILE:
                result = getMobileIP();
                break;
        }
        return result;
    }

    protected String getWifiIP(Context context) {

        String result = null;
        try {
            WifiInfo wifiInfo = getWifiServiceManager(context).getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            result = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                                   (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                                   (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {
            // Do nothing
        }
        return result;
    }

    protected String getMobileIP() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception in Get IP Address: " + ex.toString());
        }
        return null;
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

    protected TelephonyManager getTelephonyManager(Context context) {

        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    protected WifiManager getWifiServiceManager(Context context) {

        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    protected WindowManager getWindowServiceManager(Context context) {

        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    protected ConnectivityManager getConnectivityManager(Context context) {

        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeHttpRequest.Listener
    //----------------------------------------------------------------------------------------------
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
            try {
                OpenRtb.BidResponse response = OpenRtbJsonFactory.create()
                                                                 .newReader()
                                                                 .readBidResponse(result);
                invokeLoaded(new RTBNetworkModel(response));
            } catch (IOException e) {
                invokeFailed(e);
            }
        }
    }

    @Override
    public void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception) {

        Log.v(TAG, "onPubnativeHttpRequestFail");
        invokeFailed(exception);
    }
}
