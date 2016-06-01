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
import android.util.Log;
import android.view.ViewGroup;

import java.util.Map;

public class PubnativeLibraryNetworkFeedBannerAdapter extends PubnativeNetworkFeedBannerAdapter {

    private static String TAG = PubnativeLibraryNetworkFeedBannerAdapter.class.getSimpleName();

    /**
     * Creates a new instance of PubnativeLibraryNetworkFeedBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeLibraryNetworkFeedBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkInterstitialAdapter
    //==============================================================================================
    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        // TODO: Add once library is released
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        // TODO: Add once library is released
        return result;
    }

    @Override
    public void show(ViewGroup container) {

        Log.v(TAG, "show");
        // TODO: Add once library is released
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        // TODO: Add once library is released
    }

    @Override
    public void hide() {

        Log.v(TAG, "destroy");
        // TODO: Add once library is released
    }
}
