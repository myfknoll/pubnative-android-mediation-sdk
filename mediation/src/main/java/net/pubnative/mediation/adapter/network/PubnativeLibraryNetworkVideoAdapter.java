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

package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.pubnative.library.video.PubnativeVideo;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeLibraryNetworkVideoAdapter
        extends PubnativeNetworkVideoAdapter implements PubnativeVideo.Listener {

    private static String TAG = PubnativeLibraryNetworkVideoAdapter.class.getSimpleName();

    protected PubnativeVideo mVideo;

    /**
     * Creates a new instance of PubnativeLibraryNetworkVideoAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeLibraryNetworkVideoAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeLibraryNetworkVideoAdapter
    //==============================================================================================
    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String appToken = (String) mData.get(KEY_APP_TOKEN);
            if (TextUtils.isEmpty(appToken)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                mVideo = new PubnativeVideo();
                mVideo.setListener(this);
                mVideo.load(context, appToken);
            }
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mVideo != null) {
            result = mVideo.isReady();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mVideo != null) {
            mVideo.show();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mVideo != null) {
            mVideo.destroy();
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // PubnativeVideo.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeVideoLoadFinish(PubnativeVideo video) {

        Log.v(TAG, "onPubnativeVideoLoadFinish");
        mVideo.show();
    }

    @Override
    public void onPubnativeVideoLoadFail(PubnativeVideo video, Exception exception) {

        Log.v(TAG, "onPubnativeVideoLoadFail");
    }

    @Override
    public void onPubnativeVideoShow(PubnativeVideo video) {

        Log.v(TAG, "onPubnativeVideoShow");
    }

    @Override
    public void onPubnativeVideoHide(PubnativeVideo video) {

        Log.v(TAG, "onPubnativeVideoHide");
    }

    @Override
    public void onPubnativeVideoStart(PubnativeVideo video) {

        Log.v(TAG, "onPubnativeVideoStart");
    }

    @Override
    public void onPubnativeVideoFinish(PubnativeVideo video) {

        Log.v(TAG, "onPubnativeVideoFinish");
    }

    @Override
    public void onPubnativeVideoClick(PubnativeVideo video) {

        Log.v(TAG, "onPubnativeVideoClick");
    }
}
