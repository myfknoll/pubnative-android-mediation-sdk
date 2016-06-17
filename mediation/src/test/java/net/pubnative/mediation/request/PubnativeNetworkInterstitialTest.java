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

package net.pubnative.mediation.request;

import android.os.Handler;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.exceptions.PubnativeException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeNetworkInterstitialTest {

    final static String TEST_PLACEMENT_ID_INVALID = "mPlacementName";
    final static String TEST_APP_TOKEN            = "app_token";

    @Test
    public void invokeLoadFinish_withNullListener_pass() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        request.mHandler = new Handler();
        request.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFail_withNullListener_pass() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        request.mHandler = new Handler();
        request.invokeLoadFail(any(Exception.class));
    }

    @Test
    public void invokeShow_withNullListener_pass() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        request.mHandler = new Handler();
        request.invokeShow();
    }

    @Test
    public void invokeImpressionConfirmed_withNullListener_pass() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        request.mHandler = new Handler();
        request.invokeImpressionConfirmed();
    }

    @Test
    public void invokeClick_withNullListener_pass() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        request.mHandler = new Handler();
        request.invokeClick();
    }

    @Test
    public void invokeHide_withNullListener_pass() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        request.mHandler = new Handler();
        request.invokeHide();
    }

    @Test
    public void invokeLoadFinish_withValidListener_callbackLoadFinish() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        PubnativeNetworkInterstitial.Listener listener = spy(PubnativeNetworkInterstitial.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeLoadFinish();

        verify(listener).onPubnativeNetworkInterstitialLoadFinish(eq(request));
    }

    @Test
    public void invokeLoadFail_withValidListener_callbackLoadFail() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        PubnativeNetworkInterstitial.Listener listener = spy(PubnativeNetworkInterstitial.Listener.class);
        Exception exception = mock(Exception.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeLoadFail(exception);

        verify(listener).onPubnativeNetworkInterstitialLoadFail(eq(request), eq(exception));
    }

    @Test
    public void invokeShow_withValidListener_callbackShow() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        PubnativeNetworkInterstitial.Listener listener = spy(PubnativeNetworkInterstitial.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeShow();

        verify(listener).onPubnativeNetworkInterstitialShow(eq(request));
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_callbackImpressionConfirmed() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        PubnativeNetworkInterstitial.Listener listener = spy(PubnativeNetworkInterstitial.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeImpressionConfirmed();

        verify(listener).onPubnativeNetworkInterstitialImpressionConfirmed(eq(request));
    }

    @Test
    public void invokeClick_withValidListener_callbackClick() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        PubnativeNetworkInterstitial.Listener listener = spy(PubnativeNetworkInterstitial.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.invokeClick();

        verify(listener).onPubnativeNetworkInterstitialClick(eq(request));
    }

    @Test
    public void load_withNullContext_callBackLoadFail() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        PubnativeNetworkInterstitial.Listener listener = spy(PubnativeNetworkInterstitial.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(null, TEST_APP_TOKEN, TEST_PLACEMENT_ID_INVALID);

        verify(listener).onPubnativeNetworkInterstitialLoadFail(eq(request), eq(PubnativeException.INTERSTITIAL_PARAMETERS_INVALID));
    }

    @Test
    public void load_withInvalidParams_callBackLoadFail() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        PubnativeNetworkInterstitial.Listener listener = spy(PubnativeNetworkInterstitial.Listener.class);
        request.mHandler = new Handler();
        request.mListener = listener;

        request.load(RuntimeEnvironment.application.getApplicationContext(), "", "");

        verify(listener).onPubnativeNetworkInterstitialLoadFail(eq(request), eq(PubnativeException.INTERSTITIAL_PARAMETERS_INVALID));
    }

    @Test
    public void load_withNullContextNullListener_Pass() {

        PubnativeNetworkInterstitial request = spy(PubnativeNetworkInterstitial.class);
        request.mHandler = new Handler();

        request.load(null, TEST_APP_TOKEN, TEST_PLACEMENT_ID_INVALID);
    }
}
