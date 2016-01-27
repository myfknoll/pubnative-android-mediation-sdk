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

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class YahooNetworkAdapterTest {

    private final static int TIMEOUT_DEACTIVATED = 0;
    private Context applicationContext;

    @Before
    public void setUp() {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void successCallbacksWithValidData() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, "ad_space_name");
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, "api_key");
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        YahooNetworkAdapter             adapterSpy   = spy(new YahooNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);
        this.stubEndFlurrySessionMethod(adapterSpy);

        // Check that
        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock, null);
        verify(listenerMock, times(1)).onAdapterRequestStarted(eq(adapterSpy));
        verify(listenerMock, times(1)).onAdapterRequestLoaded(eq(adapterSpy), any(PubnativeAdModel.class));
        verify(listenerMock, never()).onAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
    }

    @Test
    public void failureCallbacksOnFlurryError() {
        Map<String, String> data = new HashMap();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "test_placement_id");
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        YahooNetworkAdapter             adapterSpy   = spy(new YahooNetworkAdapter(data));
        // stubbing the createRequest method to simulate facebook error.
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                YahooNetworkAdapter adapter = (YahooNetworkAdapter) invocation.getMock();
                adapter.onError(null, null, 1);
                return null;
            }
        }).when(adapterSpy).createRequest(any(Context.class), anyString(), anyString());

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock, null);
        this.callbacksRunsForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void failureCallbacksWithNullData() {
        this.invokeFailureCallbacksWithInvalidData(null);
    }

    @Test
    public void failureCallbacksWithEmptyAdSpace() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, "");
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, "api_key");

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    @Test
    public void failureCallbacksWithNullAdSpace() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, null);
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, "api_key");

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    @Test
    public void failureCallbacksWithEmptyApiKey() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, "ad_space_name");
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, "");

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    @Test
    public void failureCallbacksWithNullApiKey() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, "ad_space_name");
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, null);

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    @Test
    public void failureCallbacksWithEmptyApiKeyAndAdSpaceName() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, "");
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, "");

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    @Test
    public void failureCallbacksWithNullApiKeyAndAdSpaceName() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, null);
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, null);

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    @Test
    public void failureCallbacksWithNoApiKey() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_AD_SPACE_NAME, "ad_space_name");

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    @Test
    public void failureCallbacksWithNoAdSpaceName() {
        Map<String, String> data = new HashMap();
        data.put(YahooNetworkAdapter.KEY_FLURRY_API_KEY, "api_key");

        this.invokeFailureCallbacksWithInvalidData(data);
    }

    /**
     * Method to check failure callbacks when invalid data passed in.
     *
     * @param data Map of adapter config items. This data should never be valid
     */
    private void invokeFailureCallbacksWithInvalidData(Map<String, String> data) {
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        YahooNetworkAdapter             adapterSpy   = spy(new YahooNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);
        this.stubEndFlurrySessionMethod(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock, null);
        this.callbacksRunsForFailureCase(adapterSpy, listenerMock);
    }

    private void callbacksRunsForFailureCase(YahooNetworkAdapter adapter, PubnativeNetworkAdapterListener listener) {
        verify(listener, times(1)).onAdapterRequestStarted(eq(adapter));
        verify(listener, times(1)).onAdapterRequestFailed(eq(adapter), any(Exception.class));
        verify(listener, never()).onAdapterRequestLoaded(eq(adapter), any(PubnativeAdModel.class));
    }

    private void stubCreateRequestMethod(YahooNetworkAdapter adapter) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                YahooNetworkAdapter adapter = (YahooNetworkAdapter) invocation.getMock();
                adapter.onFetched(null);
                return null;
            }
        }).when(adapter).createRequest(any(Context.class), anyString(), anyString());
    }

    private void stubEndFlurrySessionMethod(YahooNetworkAdapter adapter) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(adapter).endFlurrySession(any(Context.class));
    }
}
