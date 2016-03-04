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

package net.pubnative.library.request;

import android.content.Context;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.PubnativeTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeRequestTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testWithValidListenerForSuccess() {

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = listener;
        request.invokeOnPubnativeRequestSuccess(mock(ArrayList.class));
        verify(listener, times(1)).onPubnativeRequestSuccess(eq(request), any(List.class));
    }

    @Test
    public void testWithValidListenerForFailure() {

        Exception error = mock(Exception.class);
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = listener;
        request.invokeOnPubnativeRequestFailure(error);
        verify(listener, times(1)).onPubnativeRequestFailed(eq(request), eq(error));
    }

    @Test
    public void testWithNoListenerForSuccess() {

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = null;
        request.invokeOnPubnativeRequestSuccess(mock(ArrayList.class));
    }

    @Test
    public void testWithNoListenerForFailure() {

        PubnativeRequest request = spy(PubnativeRequest.class);
        Exception error = mock(Exception.class);
        request.mListener = null;
        request.invokeOnPubnativeRequestFailure(error);
    }

    @Test
    public void testParameterIsSet() {

        String testKey = "testKey";
        String testValue = "testValue";
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.setParameter(testKey, testValue);
        assertThat(request.mRequestParameters.get(testKey)).isEqualTo(testValue);
    }

    @Test
    public void testWithNullParameters() {

        PubnativeRequest request = spy(PubnativeRequest.class);
        String testKey = "testKey";
        request.setParameter(testKey, null);
        assertThat(request.mRequestParameters.containsKey(testKey)).isFalse();
    }

    @Test
    public void testParameterSize() {

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.setParameter("test1", "1");
        request.setParameter("test2", "2");
        assertThat(request.mRequestParameters.size() == 2).isTrue();
    }

    @Test
    public void testDuplicateParametersOverridesValue() {

        String testKey = "testKey";
        String testValue1 = "value1";
        String testValue2 = "value2";
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.setParameter(testKey, testValue1);
        request.setParameter(testKey, testValue2);
        assertThat(request.mRequestParameters.size()).isEqualTo(1);
        assertThat(request.mRequestParameters.get(testKey)).isEqualTo(testValue2);
    }

    @Test
    public void testNetworkRequestInitiatedOnStart() {

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = listener;
        request.mEndpoint = PubnativeRequest.Endpoint.NATIVE;
        request.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "test");
        request.start(this.applicationContext, PubnativeRequest.Endpoint.NATIVE, listener);
        verify(request, times(1)).setDefaultParameters();
        verify(request, times(1)).sendNetworkRequest();
    }

    @Test
    public void testStartWithNullEndpointFails() {

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "test");
        request.start(this.applicationContext, null, listener);
        verify(request, times(1)).invokeOnPubnativeRequestFailure(any(Exception.class));
    }

    @Test
    public void testStartWithNullContextFails() {

        PubnativeRequest.Listener listener = mock(PubnativeRequest.Listener.class);
        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.setParameter(PubnativeRequest.Parameters.ANDROID_ADVERTISER_ID, "test");
        pubnativeRequest.start(null, PubnativeRequest.Endpoint.NATIVE, listener);
        verify(pubnativeRequest, times(1)).invokeOnPubnativeRequestFailure(any(Exception.class));
    }

    @Test
    public void testSetsUpDefaultParametersAutomatically() {

        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mContext = this.applicationContext;
        request.setDefaultParameters();
        assertThat(request.mRequestParameters.containsKey(PubnativeRequest.Parameters.BUNDLE_ID)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PubnativeRequest.Parameters.OS)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PubnativeRequest.Parameters.OS_VERSION)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PubnativeRequest.Parameters.DEVICE_MODEL)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PubnativeRequest.Parameters.DEVICE_RESOLUTION)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PubnativeRequest.Parameters.DEVICE_TYPE)).isTrue();
        assertThat(request.mRequestParameters.containsKey(PubnativeRequest.Parameters.LOCALE)).isTrue();
    }

    @Test
    public void testRequestUrlValidity() {

        String testKey = "testKey";
        String testValue = "testValue";
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mEndpoint = PubnativeRequest.Endpoint.NATIVE;
        request.setParameter(testKey, testValue);
        String url = request.getRequestURL();
        assertThat(url).isNotNull();
        assertThat(url).isNotEmpty();
        assertThat(url).startsWith(PubnativeRequest.BASE_URL);
        assertThat(url).contains(testKey);
    }

    @Test
    public void testInvalidEndpointReturnsNullURL() {

        PubnativeRequest pubnativeRequest = spy(PubnativeRequest.class);
        pubnativeRequest.mEndpoint = null;
        String url = pubnativeRequest.getRequestURL();
        assertThat(url).isNull();
    }

    @Test
    public void testOnResponseSuccess() {

        String response = PubnativeTestUtils.getResponseJSON("success.json");
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = listener;
        request.onPubnativeAPIRequestResponse(response);
        verify(listener, times(1)).onPubnativeRequestSuccess(eq(request), any(List.class));
    }

    @Test
    public void testOnResponseWithInvalidData() {

        String response = PubnativeTestUtils.getResponseJSON("failure.json");
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = listener;
        request.onPubnativeAPIRequestResponse(response);
        verify(listener, times(1)).onPubnativeRequestFailed(eq(request), any(Exception.class));
    }

    @Test
    public void testOnResponseWithNullData() {

        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = listener;
        request.onPubnativeAPIRequestResponse(null);
        verify(listener, times(1)).onPubnativeRequestFailed(eq(request), any(Exception.class));
    }

    @Test
    public void testOnErrorResponseFromRequestManager() {

        Exception error = mock(Exception.class);
        PubnativeRequest.Listener listener = spy(PubnativeRequest.Listener.class);
        PubnativeRequest request = spy(PubnativeRequest.class);
        request.mListener = listener;
        request.onPubnativeAPIRequestError(error);
        verify(listener, times(1)).onPubnativeRequestFailed(eq(request), eq(error));
    }
}
