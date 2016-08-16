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
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.config.model.PubnativeConfigRequestModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.network.PubnativeHttpRequest;
import net.pubnative.mediation.utils.PubnativeConfigUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.pubnative.mediation.Pubnative.APP_TOKEN_KEY;
import static net.pubnative.mediation.Pubnative.EXTRAS;

public class PubnativeConfigService extends Service {

    private   static String                            TAG    = PubnativeConfigService.class.getSimpleName();
    protected static List<PubnativeConfigRequestModel> sQueue = null;
    protected static boolean                           sIdle  = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(TAG, "onStartCommand");
        Bundle bundle = intent.getExtras();
        String appToken = bundle.getString(APP_TOKEN_KEY);
        HashMap<String, String> extras = null;
        if (bundle.containsKey(EXTRAS)) {
            extras = (HashMap) bundle.getSerializable(EXTRAS);
        }
        PubnativeConfigRequestModel item = new PubnativeConfigRequestModel();
        item.context = this;
        item.appToken = appToken;
        item.extras = extras;
        enqueueRequest(item);
        doNextConfigRequest();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.v(TAG, "onBind");
        return null;
    }
    //----------------------------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------------------------
    protected void doNextConfigRequest() {

        Log.v(TAG, "doNextConfigRequest");
        PubnativeConfigRequestModel item = dequeueRequest();
        if (sIdle && item != null) {
            sIdle = false;
            getNextConfig(item);
        }
    }

    private void getNextConfig(PubnativeConfigRequestModel requestModel) {

        Log.v(TAG, "getNextConfig: " + requestModel.appToken);
        if (PubnativeConfigUtils.configNeedsUpdate(requestModel)) {
            downloadConfig(requestModel);
        } else {
            sIdle = true;
            PubnativeConfigUtils.reScheduleConfigUpdate(requestModel);
        }
    }

    protected synchronized void downloadConfig(final PubnativeConfigRequestModel requestModel) {

        Log.v(TAG, "downloadConfig");
        PubnativeHttpRequest http = new PubnativeHttpRequest();
        http.start(this, PubnativeConfigUtils.getConfigDownloadUrl(requestModel), new PubnativeHttpRequest.Listener() {

            @Override
            public void onPubnativeHttpRequestStart(PubnativeHttpRequest request) {

                Log.v(TAG, "onPubnativeHttpRequestStart");
            }

            @Override
            public void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result) {

                Log.v(TAG, "onPubnativeHttpRequestFinish");
                processConfigDownloadResponse(requestModel, result);
                invokeLoaded();
            }

            @Override
            public void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception) {

                Log.v(TAG, "onPubnativeHttpRequestFail: " + exception.toString());
                invokeLoaded();
            }
        });
    }

    protected static void processConfigDownloadResponse(PubnativeConfigRequestModel request, String result) {

        Log.v(TAG, "processConfigDownloadResponse");
        if (TextUtils.isEmpty(result)) {
            Log.e(TAG, "downloadConfig - Error, empty response");
        } else {
            try {
                PubnativeConfigAPIResponseModel response = new Gson().fromJson(result, PubnativeConfigAPIResponseModel.class);
                if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                    // Update delivery manager's tracking data
                    PubnativeConfigUtils.updateDeliveryManagerCache(request.context, response.config);
                    // Saving config string
                    PubnativeConfigUtils.updateConfig(request.context, request.appToken, response.config);
                } else {
                    Log.e(TAG, "downloadConfig - Error: " + response.error_message);
                }
            } catch (Exception e) {
                Log.e(TAG, "downloadConfig - Error: " + e);
            }
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoaded() {

        Log.v(TAG, "invokeLoaded");
        sIdle = true;
        doNextConfigRequest();
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================
    protected static void enqueueRequest(PubnativeConfigRequestModel item) {

        Log.v(TAG, "enqueueRequest");
        if (item != null) {
            if (sQueue == null) {
                sQueue = new ArrayList<PubnativeConfigRequestModel>();
            }
            sQueue.add(item);
        }
    }

    protected static PubnativeConfigRequestModel dequeueRequest() {

        Log.v(TAG, "dequeueRequest");
        PubnativeConfigRequestModel result = null;
        if (sQueue != null && sQueue.size() > 0) {
            result = sQueue.remove(0);
        }
        return result;
    }
}
