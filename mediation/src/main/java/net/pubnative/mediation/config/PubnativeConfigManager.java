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

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeConfigRequestModel;
import net.pubnative.mediation.utils.PubnativeConfigUtils;

import java.util.ArrayList;
import java.util.List;

public class PubnativeConfigManager {

    private static   String                            TAG    = PubnativeConfigManager.class.getSimpleName();
    protected static List<PubnativeConfigRequestModel> sQueue = null;
    protected static boolean                           sIdle  = true;
    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface for callbacks when the requested config gets downloaded
     */
    public interface Listener {

        /**
         * Invoked when config manager returns a config.
         *
         * @param configModel PubnativeConfigModel object when cached/download config is available, else null.
         */
        void onConfigLoaded(PubnativeConfigModel configModel);
    }

    //==============================================================================================
    // PubnativeConfigManager
    //==============================================================================================
    // Singleton
    //----------------------------------------------------------------------------------------------
    private PubnativeConfigManager() {
        // do some initialization here may be.
    }

    //----------------------------------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------------------------------
    /**
     * Gets a config asynchronously with listener callback, downloading a new one when outdated
     *
     * @param context  valid context object
     * @param appToken unique identification key provided by Pubnative for mediation sdk
     * @param listener listener to be used for tracking the config loaded callback
     */
    public synchronized static void getConfig(Context context, String appToken, PubnativeConfigManager.Listener listener) {

        Log.v(TAG, "getConfig: " + appToken);
        if (listener == null) {
            Log.e(TAG, "getConfig - Error: listener is null, dropping this call");
        } else if (context == null) {
            // ensuring null config is returned
            Log.e(TAG, "getConfig - Error: context is null");
            invokeLoaded(null, listener);
        } else if (TextUtils.isEmpty(appToken)) {
            Log.e(TAG, "getConfig - Error: app token is null");
            invokeLoaded(null, listener);
        } else {
            PubnativeConfigRequestModel item = new PubnativeConfigRequestModel();
            item.context = context;
            item.appToken = appToken;
            item.listener = listener;
            enqueueRequest(item);
            doNextConfigRequest();
        }
    }
    //----------------------------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------------------------

    protected static void doNextConfigRequest() {

        Log.v(TAG, "doNextConfigRequest");
        PubnativeConfigRequestModel item = dequeueRequest();
        if (sIdle && item != null) {
            sIdle = false;
            serveStoredConfig(item);
        }
    }

    protected static void serveStoredConfig(PubnativeConfigRequestModel request) {

        Log.v(TAG, "serveStoredConfig");
        invokeLoaded(PubnativeConfigUtils.getStoredConfig(request.context), request.listener);
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected static void invokeLoaded(PubnativeConfigModel configModel, PubnativeConfigManager.Listener listener) {

        Log.v(TAG, "invokeLoaded");
        if (listener != null) {
            listener.onConfigLoaded(configModel);
        }
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
