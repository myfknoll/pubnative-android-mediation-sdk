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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import net.pubnative.URLDriller;
import net.pubnative.library.network.PubnativeAPIRequest;
import net.pubnative.library.utils.SystemUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PubnativeAdTracker implements PubnativeAPIRequest.Listener,
                                           URLDriller.Listener {

    private static       String TAG                             = PubnativeAdTracker.class.getSimpleName();
    private static final float  VISIBILITY_PERCENTAGE_THRESHOLD = 0.50f;
    private static final long   VISIBILITY_TIME_THRESHOLD       = 1000;
    private static final long   VISIBILITY_CHECK_INTERVAL       = 200;
    protected     Listener                 mListener;
    private       View                     mView;
    private       View                     mClickableView;
    private       ViewTreeObserver         mViewTreeObserver;
    private final ScheduledExecutorService mExecutor;
    private boolean mIsTracked            = false;
    private boolean mIsTrackingInProgress = false;
    private boolean mTrackingShouldStop   = false;
    private Handler mHandler              = null;
    private String  mImpressionUrl        = null;
    private String  mClickUrl             = null;
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

        /**
         * Called when the click is detected
         *
         * @param view view where the click was detected
         */
        void onTrackerClick(View view);

        /**
         * Called when the tracker is opening the offer in another
         */
        void onTrackerOpenOffer();
    }
    //==============================================================================================
    // PUBLIC
    //==============================================================================================

    /**
     * Constructor
     *
     * @param view          ad view
     * @param clickableView clickable view
     * @param listener      listener for callbacks
     */
    public PubnativeAdTracker(View view, View clickableView, String impressionUrl, String clickUrl, Listener listener) {

        mExecutor = Executors.newScheduledThreadPool(1);
        mHandler = new Handler();
        mListener = listener;
        if (view == null) {
            Log.e(TAG, "PubnativeAdTracker: view is null");
            return;
        }
        if (clickableView == null) {
            Log.e(TAG, "PubnativeAdTracker: clickable view is null");
            return;
        }
        mView = view;
        mClickableView = clickableView;
        mImpressionUrl = impressionUrl;
        mClickUrl = clickUrl;
        mViewTreeObserver = mView.getViewTreeObserver();
    }

    /**
     * This method stops tracking of the configured view
     */
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        mExecutor.shutdownNow();
        mListener = null;
        mTrackingShouldStop = true;
        mIsTrackingInProgress = false;
        mClickableView.setOnClickListener(null);
    }

    /**
     * This method starts tracking of the configured view
     */
    public void startTracking() {

        Log.v(TAG, "startTracking");
        // Impression tracking
        if (TextUtils.isEmpty(mImpressionUrl)) {
            Log.e(TAG, "startImpressionRequest - Error: provided impressionURL is null or empty, won't track impression");
        } else {
            mViewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
            mViewTreeObserver.addOnScrollChangedListener(onScrollChangedListener);
        }
        // Click tracking
        if (TextUtils.isEmpty(mClickUrl)) {
            Log.e(TAG, "startImpressionRequest - Error: provided clickURL is null or empty, clicks won't be tracked");
        } else {
            mClickableView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    handleClickEvent();
                }
            });
        }
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

        if (mIsTrackingInProgress || mIsTracked || mTrackingShouldStop) {
            return;
        }
        if (SystemUtils.isVisibleOnScreen(mView, VISIBILITY_PERCENTAGE_THRESHOLD)) {
            mIsTrackingInProgress = true;
            mExecutor.schedule(new Runnable() {

                @Override
                // After VISIBILITY_CHECK_INTERVAL (i.e. 200ms) of view visible on screen (first time)
                // it would be invoked. It regularly checks for visibility of view on screen on interval
                // of 200ms (VISIBILITY_CHECK_INTERVAL) to ensure that view is visible on the screen at least for 1 sec.
                public void run() {
                    // note first visible time
                    long firstVisibleTime = System.currentTimeMillis() - VISIBILITY_CHECK_INTERVAL;
                    Log.v(TAG, "checkImpression(), first visible at: " + firstVisibleTime);
                    // loop to make sure view is visible on screen for at least 1sec
                    while (System.currentTimeMillis() - firstVisibleTime < VISIBILITY_TIME_THRESHOLD + VISIBILITY_CHECK_INTERVAL) {
                        Log.v(TAG, "checkImpression(), within time threshold checking. Current time is: " + System.currentTimeMillis());
                        // If view is already tracked or not visible it returns from the loop without confirming impression
                        if (mIsTracked || !SystemUtils.isVisibleOnScreen(mView, VISIBILITY_PERCENTAGE_THRESHOLD)) {
                            Log.v(TAG, "checkImpression(), either already tracked or not visible anymore. Already tracked is: " + mIsTracked + " & Current time is: " + System.currentTimeMillis());
                            mIsTrackingInProgress = false;
                            break;
                        }
                        if (System.currentTimeMillis() - firstVisibleTime >= VISIBILITY_TIME_THRESHOLD) {
                            Log.v(TAG, "checkImpression - , it's visible more than " + VISIBILITY_TIME_THRESHOLD + "ms Current time is: " + System.currentTimeMillis());
                            stopImpressionTracking();
                            PubnativeTrackingManager.track(mView.getContext(), mImpressionUrl);
                            break;
                        } else {
                            try {
                                Log.v(TAG, "checkImpression -, thread is sleeping for " + VISIBILITY_CHECK_INTERVAL + "ms Current time is: " + System.currentTimeMillis());
                                // pausing thread for 200ms (VISIBILITY_CHECK_INTERVAL)
                                Thread.sleep(VISIBILITY_CHECK_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, VISIBILITY_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopImpressionTracking() {

        mIsTrackingInProgress = false;
        mIsTracked = true;
        mViewTreeObserver.removeGlobalOnLayoutListener(onGlobalLayoutListener);
        mViewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener);
        mExecutor.shutdownNow();
    }

    protected void handleClickEvent() {

        Log.v(TAG, "handleClickEvent");
        if (TextUtils.isEmpty(mClickUrl)) {
            Log.e(TAG, "handleClickEvent - Error: click url is null or empty");
        } else {
            invokeOnTrackerClick();
            URLDriller driller = new URLDriller();
            driller.setListener(this);
            driller.drill(mView.getContext(), mClickUrl);
        }
    }
    //==============================================================================================
    // Listener helpers
    //==============================================================================================

    protected void invokeOnTrackerImpression() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onTrackerImpression(mView);
                }
            }
        });
    }

    protected void invokeOnTrackerClick() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onTrackerClick(mClickableView);
                }
            }
        });
    }

    protected void invokeOnTrackerOpenOffer() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onTrackerOpenOffer();
                }
            }
        });
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeAPIRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeAPIRequestResponse(String response) {

        Log.v(TAG, "onPubnativeAPIRequestResponse");
        invokeOnTrackerImpression();
    }

    @Override
    public void onPubnativeAPIRequestError(Exception error) {

        Log.e(TAG, "onPubnativeAPIRequestError: " + error);
    }

    // URLDriller.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onURLDrillerStart(String url) {

        Log.v(TAG, "onURLDrillerStart");
    }

    @Override
    public void onURLDrillerRedirect(String url) {

        Log.v(TAG, "onURLDrillerRedirect");
    }

    @Override
    public void onURLDrillerFinish(String url) {

        Log.v(TAG, "onURLDrillerFinish");
        invokeOnTrackerOpenOffer();
    }

    @Override
    public void onURLDrillerFail(String url, Exception exception) {

        Log.v(TAG, "onURLDrillerFail");
    }
}
