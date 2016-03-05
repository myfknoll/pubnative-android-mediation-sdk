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

package net.pubnative.library.tracking;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import net.pubnative.library.utils.SystemUtils;

public class PubnativeImpressionTracker {

    private static       String           TAG                             = PubnativeImpressionTracker.class.getSimpleName();
    private static final float            VISIBILITY_PERCENTAGE_THRESHOLD = 0.50f;
    private static final long             VISIBILITY_TIME_THRESHOLD       = 1000;
    private static final long             VISIBILITY_CHECK_INTERVAL       = 200;
    protected            Listener         mListener                       = null;
    private              View             mView                           = null;
    private              ViewTreeObserver mViewTreeObserver               = null;
    private              Thread           mCheckImpressionThread          = null;
    private              boolean          mIsTrackingInProgress           = false;
    private              boolean          mTrackingShouldStop             = false;
    private              Handler          mHandler                        = null;
    private              String           mImpressionUrl                  = null;
    //==============================================================================================
    // LISTENER
    //==============================================================================================

    /**
     * Listener for callbacks about tracking behaviour
     */
    public interface Listener {

        /**
         * Calledn when the impression is detected
         *
         * @param view view where the impression was deteted
         */
        void onTrackerImpression(View view);
    }

    //==============================================================================================
    // CONFIRM IMPRESSION
    //==============================================================================================
    private class CheckImpressionRunnable implements Runnable {

        @Override
        public void run() {
            // note first visible time
            long startTimestamp = System.currentTimeMillis();
            Log.v(TAG, "checkImpression - started");
            // loop to make sure view is visible on screen for at least 1sec
            while (true) {
                long elapsedTime = System.currentTimeMillis() - startTimestamp;
                Log.v(TAG, "checkImpression - Current elapsed visible time: " + elapsedTime + "ms");
                // If view is already tracked or not visible it returns from the loop without confirming impression
                if (mTrackingShouldStop) {
                    Log.v(TAG, "checkImpression - Tracking was cancelled");
                    stopImpressionTracking();
                    break;
                } else if (!SystemUtils.isVisibleOnScreen(mView, VISIBILITY_PERCENTAGE_THRESHOLD)) {
                    Log.v(TAG, "checkImpression - view is not visible in the screen, we will stop checking");
                    stopImpressionTracking();
                    break;
                } else {
                    if (elapsedTime >= VISIBILITY_TIME_THRESHOLD) {
                        Log.v(TAG, "checkImpression - impression confirmed");
                        invokeOnTrackerImpression();
                        PubnativeTrackingManager.track(mView.getContext(), mImpressionUrl);
                        mViewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
                        mViewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener);
                        stopImpressionTracking();
                        break;
                    } else {
                        try {
                            // pausing thread for 200ms (VISIBILITY_CHECK_INTERVAL)
                            Thread.sleep(VISIBILITY_CHECK_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * Constructor
     *
     * @param view          ad view
     * @param listener      listener for callbacks
     */
    public PubnativeImpressionTracker(View view, String impressionUrl, Listener listener) {

        if (view == null) {
            Log.e(TAG, "Error: No view to track, dropping call");
        } else {
            mHandler = new Handler();
            mListener = listener;
            mView = view;
            mImpressionUrl = impressionUrl;
            mViewTreeObserver = mView.getViewTreeObserver();
        }
    }

    /**
     * This method stops tracking of the configured view
     */
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        mViewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
        mViewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener);
        mTrackingShouldStop = true;
        stopImpressionTracking();
        mListener = null;
    }

    /**
     * This method starts tracking of the configured view
     */
    public void startTracking() {

        Log.v(TAG, "startTracking");
        // Impression tracking
        mViewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
        mViewTreeObserver.addOnScrollChangedListener(onScrollChangedListener);
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    private ViewTreeObserver.OnGlobalLayoutListener  onGlobalLayoutListener  = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {

            checkImpression();
        }
    };
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {

        @Override
        public void onScrollChanged() {

            checkImpression();
        }
    };

    private void checkImpression() {

        if (mIsTrackingInProgress || mTrackingShouldStop) {
            return;
        }
        if (SystemUtils.isVisibleOnScreen(mView, VISIBILITY_PERCENTAGE_THRESHOLD)) {
            mIsTrackingInProgress = true;
            if (mCheckImpressionThread == null) {
                mCheckImpressionThread = new Thread(new CheckImpressionRunnable());
            }
            mCheckImpressionThread.start();
        }
    }

    private void stopImpressionTracking() {

        mIsTrackingInProgress = false;
        if (mCheckImpressionThread != null) {
            mCheckImpressionThread.interrupt();
            mCheckImpressionThread = null;
        }
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================

    protected void invokeOnTrackerImpression() {

        Log.v(TAG, "invokeOnTrackerImpression");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onTrackerImpression(mView);
                }
            }
        });
    }
}
