package net.pubnative.mediation.adapter;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Created by root on 30/7/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeNetworkAdapterTest {

    private static final int TIMEOUT_HALF_SECOND = 500;

    @Test
    public void invokeCallbacksWithValidListener() {
        HashMap<String, Object>         adapterConfigMock = mock(HashMap.class);
        PubnativeNetworkAdapterListener listenerSpy       = spy(PubnativeNetworkAdapterListener.class);

        PubnativeNetworkAdapter adapterInstance = spy(new PubnativeNetworkAdapter(adapterConfigMock) {
            @Override
            public void request(Context context) {
                // Do nothing
            }
        });

        adapterInstance.listener = listenerSpy;

        // onRequestStarted
        adapterInstance.invokeStart();
        verify(listenerSpy, times(1)).onAdapterRequestStarted(adapterInstance);

        // onRequestLoaded
        PubnativeAdModel adMock = mock(PubnativeAdModel.class);
        adapterInstance.invokeLoaded(adMock);
        verify(listenerSpy, times(1)).onAdapterRequestLoaded(eq(adapterInstance), eq(adMock));

        // onRequestFailed
        Exception exceptionMock = mock(Exception.class);
        adapterInstance.invokeFailed(exceptionMock);
        verify(listenerSpy, times(1)).onAdapterRequestFailed(eq(adapterInstance), eq(exceptionMock));

    }

    @Test
    public void invokeCallbacksWithNullListener() {
        HashMap<String, Object> adapterConfigMock = mock(HashMap.class);
        PubnativeNetworkAdapter adapterSpy = spy(new PubnativeNetworkAdapter(adapterConfigMock) {
            @Override
            public void request(Context context) {
                // Do nothing
            }
        });

        adapterSpy.listener = null;
        adapterSpy.invokeStart();
        adapterSpy.invokeLoaded(mock(PubnativeAdModel.class));
        adapterSpy.invokeFailed(mock(Exception.class));
    }

    @Test
    public void adapterRequestsTimeoutCallsInvokeFailed() {
        PubnativeNetworkAdapterListener listenerSpy = spy(PubnativeNetworkAdapterListener.class);
        PubnativeNetworkAdapter adapterSpy = spy(new PubnativeNetworkAdapter(null) {
            @Override
            public void request(Context context) {
                // Do nothing, doRequest should timeout
            }
        });

        adapterSpy.doRequest(mock(Context.class), TIMEOUT_HALF_SECOND, listenerSpy);
        Robolectric.flushForegroundThreadScheduler();

        verify(listenerSpy, times(1)).onAdapterRequestStarted(eq(adapterSpy));
        verify(listenerSpy, after(2 * TIMEOUT_HALF_SECOND).times(1)).onAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
        verify(listenerSpy, after(2 * TIMEOUT_HALF_SECOND).never()).onAdapterRequestLoaded(eq(adapterSpy), any(PubnativeAdModel.class));
    }

    // TODO: Ensure no more callbacks after fail or load
}
