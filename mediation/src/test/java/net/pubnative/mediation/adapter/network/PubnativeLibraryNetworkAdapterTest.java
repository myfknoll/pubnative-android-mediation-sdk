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

import net.pubnative.library.request.AdRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PubnativeLibraryNetworkAdapterTest {

    private final static int TIMEOUT_DEACTIVATED = 0;

    private Context applicationContext;

    @Before
    public void setUp() {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void verifyCallbacksWithValidParams() {
        Map<String, String> data = mock(HashMap.class);
        PubnativeNetworkAdapterListener listenerSpy = spy(PubnativeNetworkAdapterListener.class);
        PubnativeLibraryNetworkAdapter  adapterSpy  = spy(new PubnativeLibraryNetworkAdapter(data));
        this.stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerSpy, null);
        verify(listenerSpy, times(1)).onAdapterRequestStarted(eq(adapterSpy));
        verify(listenerSpy, times(1)).onAdapterRequestLoaded(eq(adapterSpy), any(PubnativeAdModel.class));
        verify(listenerSpy, never()).onAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
    }

    @Test
    public void verifyCallbacksOnPubnativeLibraryFailure() {
        Map<String, String> data = mock(HashMap.class);
        PubnativeNetworkAdapterListener listenerSpy = spy(PubnativeNetworkAdapterListener.class);
        PubnativeLibraryNetworkAdapter  adapterSpy  = spy(new PubnativeLibraryNetworkAdapter(data));
        // stubbing the createRequest method to simulate facebook error.
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PubnativeLibraryNetworkAdapter adapter = (PubnativeLibraryNetworkAdapter) invocation.getMock();
                adapter.onAdRequestFailed(mock(AdRequest.class), mock(Exception.class));
                return null;
            }
        }).when(adapterSpy).createRequest(any(Context.class), any(Map.class));

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerSpy, null);
        this.failCallbacksWhenInvalidDataProvided(adapterSpy, listenerSpy);
    }

    @Test
    public void verifyCallbacksWithNotNullDataButNoAppToken() {
        PubnativeNetworkAdapterListener listenerSpy = spy(PubnativeNetworkAdapterListener.class);
        PubnativeLibraryNetworkAdapter  adapterSpy  = spy(new PubnativeLibraryNetworkAdapter(null));
        this.stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerSpy, null);
        this.failCallbacksWhenInvalidDataProvided(adapterSpy, listenerSpy);
    }

    @Test
    public void verifyCallbacksWithNullData() {
        PubnativeNetworkAdapterListener listenerSpy = spy(PubnativeNetworkAdapterListener.class);
        PubnativeLibraryNetworkAdapter  adapterSpy  = spy(new PubnativeLibraryNetworkAdapter(null));
        this.stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerSpy, null);
        this.failCallbacksWhenInvalidDataProvided(adapterSpy, listenerSpy);
    }

    private void failCallbacksWhenInvalidDataProvided(PubnativeLibraryNetworkAdapter adapter, PubnativeNetworkAdapterListener listener) {
        verify(listener, times(1)).onAdapterRequestStarted(eq(adapter));
        verify(listener, times(1)).onAdapterRequestFailed(eq(adapter), any(Exception.class));
        verify(listener, never()).onAdapterRequestLoaded(eq(adapter), any(PubnativeAdModel.class));
    }

    private void stubCreateRequestMethodWithRequestFinishedCallback(PubnativeLibraryNetworkAdapter adapterMock) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PubnativeLibraryNetworkAdapter adapter = (PubnativeLibraryNetworkAdapter) invocation.getMock();
                adapter.onAdRequestFinished(mock(AdRequest.class), mock(ArrayList.class));
                return null;
            }
        }).when(adapterMock).createRequest(any(Context.class), any(Map.class));
    }
}
