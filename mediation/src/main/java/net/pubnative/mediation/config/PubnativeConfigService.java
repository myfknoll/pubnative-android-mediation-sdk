// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
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

package net.pubnative.mediation.config;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.network.PubnativeHttpRequest;
import net.pubnative.mediation.utils.PubnativeConfigUtils;

import java.util.HashMap;

import static net.pubnative.mediation.Pubnative.APP_TOKEN_KEY;
import static net.pubnative.mediation.Pubnative.EXTRAS;

public class PubnativeConfigService extends Service {
    private static String TAG                   = PubnativeConfigService.class.getSimpleName();
    public  static boolean sIsConfigDownloading = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "onStartCommand");
        Bundle bundle = intent.getExtras();
        String appToken = bundle.getString(APP_TOKEN_KEY);
        HashMap<String, String> extras = null;
        if(bundle.containsKey(EXTRAS)){
            extras = (HashMap)bundle.getSerializable(EXTRAS);
            //store params in app AppPreferences
           PubnativeConfigUtils.setStoredParams(this, new Gson().toJson(extras));
        }
        if(PubnativeConfigUtils.configNeedsUpdate(this, appToken)){
            downloadConfig(appToken, this, extras);
        }else{
            // if user calls init() again and again refresh alarm manager for config update,
            // same when device get restarted or reboot.
            PubnativeConfigUtils.reScheduleConfigUpdate(this, appToken);
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.v(TAG, "onBind");
        return null;
    }

    protected static void processConfigDownloadResponse(Context context, String result, String appToken) {

        Log.v(TAG, "processConfigDownloadResponse");
        if (TextUtils.isEmpty(result)) {
            // In case of server error problem, we serve the stored config
            Log.e(TAG, "downloadConfig - Error, empty response");
        } else {
            try {
                PubnativeConfigAPIResponseModel response = new Gson().fromJson(result, PubnativeConfigAPIResponseModel.class);
                if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                    // Update delivery manager's tracking data
                    PubnativeConfigUtils.updateDeliveryManagerCache(context, response.config);
                    // Saving config string
                    PubnativeConfigUtils.updateConfig(context, appToken, response.config);
                    sIsConfigDownloading = false;
                } else {
                    Log.e(TAG, "downloadConfig - Error: " + response.error_message);
                }
            } catch (Exception e) {
                Log.e(TAG, "downloadConfig - Error: " + e);
            }
        }
    }

    protected synchronized void downloadConfig(final String appToken, final Context context, HashMap<String, String> map) {

        Log.v(TAG, "downloadConfig");
        PubnativeHttpRequest http = new PubnativeHttpRequest();
        http.start(this, PubnativeConfigUtils.getConfigDownloadUrl(appToken, context, map), new PubnativeHttpRequest.Listener() {

            @Override
            public void onPubnativeHttpRequestStart(PubnativeHttpRequest request) {

                Log.v(TAG, "onPubnativeHttpRequestStart");
            }

            @Override
            public void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result) {

                Log.v(TAG, "onPubnativeHttpRequestFinish");
                processConfigDownloadResponse(context, result, appToken);
            }

            @Override
            public void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception) {

                Log.v(TAG, "onPubnativeHttpRequestFail: " + exception.toString());
            }
        });
    }
}
