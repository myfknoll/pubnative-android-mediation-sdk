// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.request;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.pubnative.library.models.APIRequestResponseModel;
import net.pubnative.library.models.PubnativeAdModel;
import net.pubnative.library.network.PubnativeAPIRequest;
import net.pubnative.library.utils.AndroidAdvertisingIDTask;
import net.pubnative.library.utils.Crypto;
import net.pubnative.library.utils.SystemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PubnativeRequest implements AndroidAdvertisingIDTask.Listener,
                                         PubnativeAPIRequest.Listener {

    private static         String              TAG                = PubnativeRequest.class.getSimpleName();
    private static final   String              NATIVE_TYPE_URL    = "native";
    protected static final String              BASE_URL           = "http://api.pubnative.net/api/partner/v2/promotions";
    protected              Context             mContext           = null;
    protected              Endpoint            mEndpoint          = null;
    protected              Map<String, String> mRequestParameters = new HashMap<String, String>();
    protected              Listener            mListener          = null;

    /**
     * These are the various types of adds pubnative support
     */
    public enum Endpoint {
        NATIVE,
    }
    //==============================================================================================
    // REQUEST PARAMETERS
    //==============================================================================================

    /**
     * These are the various types of parameters
     */
    public interface Parameters {

        String APP_TOKEN                  = "app_token";
        String BUNDLE_ID                  = "bundle_id";
        String ANDROID_ADVERTISER_ID      = "android_advertiser_id";
        String ANDROID_ADVERTISER_ID_SHA1 = "android_advertiser_id_sha1";
        String ANDROID_ADVERTISER_ID_MD5  = "android_advertiser_id_md5";
        String ICON_SIZE                  = "icon_size";
        String BANNER_SIZE                = "banner_size";
        String OS                         = "os";
        String DEVICE_MODEL               = "device_model";
        String OS_VERSION                 = "os_version";
        String NO_USER_ID                 = "no_user_id";
        String PARTNER                    = "partner";
        String LOCALE                     = "locale";
        String PORTRAIT_BANNER_SIZE       = "portrait_banner_size";
        String DEVICE_RESOLUTION          = "device_resolution";
        String DEVICE_TYPE                = "device_type";
        String AD_COUNT                   = "ad_count";
        String ZONE_ID                    = "zone_id";
        String LAT                        = "lat";
        String LONG                       = "long";
        String GENDER                     = "gender";
        String AGE                        = "age";
        String KEYWORDS                   = "keywords";
    }
    //==============================================================================================
    // LISTENER
    //==============================================================================================

    /**
     * Listener interface used to start Pubnative request with success and failure callbacks.
     */
    public interface Listener {

        /**
         * Invoked when PubnativeRequest request is success
         *
         * @param request Request object used for making the request
         * @param ads     List of ads received
         */
        void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads);

        /**
         * Invoked when PubnativeRequest request fails
         *
         * @param request Request object used for making the request
         * @param ex      Exception that caused the failure
         */
        void onPubnativeRequestFailed(PubnativeRequest request, Exception ex);
    }
    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets parameters required to make the pub native request
     *
     * @param key   key name of parameter
     * @param value actual value of parameter
     */
    public void setParameter(String key, String value) {

        Log.v(TAG, "setParameter: " + key + " : " + value);
        if (TextUtils.isEmpty(key)) {
            Log.e(TAG, "Invalid key passed for parameter");
            return;
        }
        if (TextUtils.isEmpty(value)) {
            mRequestParameters.remove(key);
        } else {
            mRequestParameters.put(key, value);
        }
    }

    /**
     * Starts pub native request, This function make the ad request to the pubnative server. It makes asynchronous network request in the background.
     *
     * @param endpoint endpoint of ad (ex: NATIVE)
     * @param listener valid nativeRequestListener to track ad request callbacks.
     */
    public void start(Context context, Endpoint endpoint, Listener listener) {

        Log.v(TAG, "start");
        if (listener != null) {
            mListener = listener;
            if (endpoint != null && context != null) {
                mContext = context;
                mEndpoint = endpoint;
                setDefaultParameters();
                if (!mRequestParameters.containsKey(Parameters.ANDROID_ADVERTISER_ID)) {
                    new AndroidAdvertisingIDTask().setListener(this).execute(mContext);
                } else {
                    sendNetworkRequest();
                }
            } else {
                invokeOnPubnativeRequestFailure(new IllegalArgumentException("start - Arguments cannot be null"));
            }
        } else {
            Log.e(TAG, "start - Request started without listener, dropping call");
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    protected void setDefaultParameters() {

        Log.v(TAG, "setDefaultParameters");
        if (!mRequestParameters.containsKey(Parameters.BUNDLE_ID)) {
            mRequestParameters.put(Parameters.BUNDLE_ID, SystemUtils.getPackageName(mContext));
        }
        if (!mRequestParameters.containsKey(Parameters.OS)) {
            mRequestParameters.put(Parameters.OS, "android");
        }
        if (!mRequestParameters.containsKey(Parameters.DEVICE_MODEL)) {
            mRequestParameters.put(Parameters.DEVICE_MODEL, Build.MODEL);
        }
        if (!mRequestParameters.containsKey(Parameters.OS_VERSION)) {
            mRequestParameters.put(Parameters.OS_VERSION, Build.VERSION.RELEASE);
        }
        if (!mRequestParameters.containsKey(Parameters.DEVICE_RESOLUTION)) {
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            mRequestParameters.put(Parameters.DEVICE_RESOLUTION, dm.widthPixels + "x" + dm.heightPixels);
        }
        if (!mRequestParameters.containsKey(Parameters.DEVICE_TYPE)) {
            mRequestParameters.put(Parameters.DEVICE_TYPE, SystemUtils.isTablet(mContext) ? "tablet" : "phone");
        }
        if (!mRequestParameters.containsKey(Parameters.LOCALE)) {
            mRequestParameters.put(Parameters.LOCALE, Locale.getDefault().getLanguage());
        }
        // If none of lat and long is sent by the client then only we add default values. We can't alter client's parameters.
        if (SystemUtils.isLocationPermissionGranted(mContext)) {
            Location location = SystemUtils.getLastLocation(mContext);
            if (location != null &&
                !mRequestParameters.containsKey(Parameters.LAT) &&
                !mRequestParameters.containsKey(Parameters.LONG)) {
                mRequestParameters.put(Parameters.LAT, String.valueOf(location.getLatitude()));
                mRequestParameters.put(Parameters.LONG, String.valueOf(location.getLongitude()));
            }
        }
    }

    protected String getRequestURL() {

        Log.v(TAG, "getRequestURL");
        String url = null;
        if (mEndpoint != null) {
            switch (mEndpoint) {
                case NATIVE:
                    // Creating base URI
                    Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
                    uriBuilder.appendPath(NATIVE_TYPE_URL);
                    // Appending parameters
                    for (String key : mRequestParameters.keySet()) {
                        String value = mRequestParameters.get(key);
                        if (key != null && value != null) {
                            uriBuilder.appendQueryParameter(key, value);
                        }
                    }
                    // Final URL
                    url = uriBuilder.build().toString();
                    break;
                default:
                    // Error: Invalid ENDPOINT
                    Log.e(TAG, "getRequestURL - type not recognized");
                    break;
            }
        }
        return url;
    }

    /**
     * This function will create and send the network request.
     * It consider that <code>type<code/> is already provided so that it can prepare the request URL.
     */
    protected void sendNetworkRequest() {

        Log.v(TAG, "sendNetworkRequest");
        String url = getRequestURL();
        if (url == null) {
            invokeOnPubnativeRequestFailure(new Exception("sendNetworkRequest - Error getting request URL"));
        } else {
            PubnativeAPIRequest.send(url, this);
        }
    }

    //==============================================================================================
    // Listener Helpers
    //==============================================================================================
    protected void invokeOnPubnativeRequestSuccess(List<PubnativeAdModel> ads) {

        Log.v(TAG, "invokeOnPubnativeRequestSuccess");
        if (mListener != null) {
            mListener.onPubnativeRequestSuccess(this, ads);
        }
    }

    protected void invokeOnPubnativeRequestFailure(Exception exception) {

        Log.v(TAG, "invokeOnPubnativeRequestFailure: " + exception);
        if (mListener != null) {
            mListener.onPubnativeRequestFailed(this, exception);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // AndroidAdvertisingIDTask.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAndroidAdIdTaskFinished(String result) {

        Log.v(TAG, "onAndroidAdIdTaskFinished");
        if (TextUtils.isEmpty(result)) {
            mRequestParameters.put(Parameters.NO_USER_ID, "1");
        } else {
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID, result);
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID_SHA1, Crypto.sha1(result));
            mRequestParameters.put(Parameters.ANDROID_ADVERTISER_ID_MD5, Crypto.md5(result));
        }
        sendNetworkRequest();
    }

    // PubnativeAPIRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeAPIRequestResponse(String response) {

        Log.v(TAG, "onPubnativeAPIRequestResponse");
        if (TextUtils.isEmpty(response)) {
            invokeOnPubnativeRequestFailure(new Exception("Server response empty"));
        } else {
            try {
                APIRequestResponseModel model = new Gson().fromJson(response, APIRequestResponseModel.class);
                if (model != null) {
                    if (APIRequestResponseModel.Status.OK.equals(model.status)) {
                        // SUCCESS
                        invokeOnPubnativeRequestSuccess(model.ads);
                    } else {
                        // ERROR: request error
                        invokeOnPubnativeRequestFailure(new Exception(model.error_message));
                    }
                } else {
                    // ERROR: parsing error
                    invokeOnPubnativeRequestFailure(new Exception("Response error"));
                }
            } catch (JsonSyntaxException exception) {
                // ERROR: json error
                invokeOnPubnativeRequestFailure(exception);
            }
        }
    }

    @Override
    public void onPubnativeAPIRequestError(Exception error) {

        Log.v(TAG, "onPubnativeAPIRequestError: " + error);
        invokeOnPubnativeRequestFailure(error);
    }
}
