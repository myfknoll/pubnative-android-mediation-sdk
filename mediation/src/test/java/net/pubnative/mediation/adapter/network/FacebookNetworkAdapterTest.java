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
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FacebookNetworkAdapterTest {

    private final static int TIMEOUT_DEACTIVATED = 0;
    private Context applicationContext;

    @Before
    public void setUp() {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void verifyCallbacksWithValidData() {
        Map<String, String> data = new HashMap();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "test_placement_id");
        PubnativeNetworkAdapter.Listener listenerMock = mock(PubnativeNetworkAdapter.Listener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);

        // Check that
        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        verify(listenerMock, times(1)).onPubnativeNetworkAdapterRequestStarted(eq(adapterSpy));
        verify(listenerMock, times(1)).onPubnativeNetworkAdapterRequestLoaded(eq(adapterSpy), any(PubnativeAdModel.class));
        verify(listenerMock, never()).onPubnativeNetworkAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
    }

    @Test
    public void verifyCallbacksOnFacebookError() {
        Map<String, String> data = new HashMap();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "test_placement_id");
        PubnativeNetworkAdapter.Listener listenerMock = mock(PubnativeNetworkAdapter.Listener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        // stubbing the createRequest method to simulate facebook error.
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                FacebookNetworkAdapter adapter = (FacebookNetworkAdapter) invocation.getMock();
                adapter.onError(null, null);
                return null;
            }
        }).when(adapterSpy).createRequest(any(Context.class), anyString());

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithEmptyPlacementId() {
        Map<String, String> data = new HashMap();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "");
        PubnativeNetworkAdapter.Listener listenerMock = mock(PubnativeNetworkAdapter.Listener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullPlacementId() {
        Map<String, String> data = new HashMap();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, null);
        PubnativeNetworkAdapter.Listener listenerMock = mock(PubnativeNetworkAdapter.Listener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    /**
     * This method tests the facebook adapter with no placement_id
     * added inside the non-null 'data' hashmap.
     */
    @Test
    public void verifyCallbacksWithNotNullDataButNoPlacementIdKey() {
        Map<String, String>             data         = new HashMap();
        PubnativeNetworkAdapter.Listener listenerMock = mock(PubnativeNetworkAdapter.Listener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullData() {
        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(null));
        this.stubCreateRequestMethod(adapterSpy);

        PubnativeNetworkAdapter.Listener listenerMock = mock(PubnativeNetworkAdapter.Listener.class);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    private void verifyCallbacksForFailureCase(FacebookNetworkAdapter adapter, PubnativeNetworkAdapter.Listener listener) {
        verify(listener, times(1)).onPubnativeNetworkAdapterRequestStarted(eq(adapter));
        verify(listener, times(1)).onPubnativeNetworkAdapterRequestFailed(eq(adapter), any(Exception.class));
        verify(listener, never()).onPubnativeNetworkAdapterRequestLoaded(eq(adapter), any(PubnativeAdModel.class));
    }

    private void stubCreateRequestMethod(FacebookNetworkAdapter adapter) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                FacebookNetworkAdapter adapter = (FacebookNetworkAdapter) invocation.getMock();
                adapter.onAdLoaded(null);
                return null;
            }
        }).when(adapter).createRequest(any(Context.class), anyString());
    }
}

