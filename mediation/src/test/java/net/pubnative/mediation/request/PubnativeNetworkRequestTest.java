package net.pubnative.mediation.request;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.model.PubnativeAdModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeNetworkRequestTest
{
    public Context applicationContext;

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void invokeCallbacksWorks()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        networkRequestSpy.listener = listenerMock;

        // onRequestStarted
        networkRequestSpy.invokeStart();
        verify(listenerMock, times(1)).onRequestStarted(networkRequestSpy);

        // onRequestLoaded
        ArrayList<PubnativeAdModel> adsMock = mock(ArrayList.class);
        networkRequestSpy.invokeLoad(adsMock);
        verify(listenerMock, times(1)).onRequestLoaded(networkRequestSpy, adsMock);

        // onRequestFailed
        Exception exceptionMock = mock(Exception.class);
        networkRequestSpy.invokeFail(exceptionMock);
        verify(listenerMock, times(1)).onRequestFailed(networkRequestSpy, exceptionMock);
    }

    @Test
    public void invokeCallbacksWithoutListenerWontCrash()
    {
        PubnativeNetworkRequest requestSpy = spy(PubnativeNetworkRequest.class);

        // invoking method with arguments when lister is null
        requestSpy.listener = null;
        requestSpy.invokeStart();

        ArrayList<PubnativeAdModel> ads = mock(ArrayList.class);
        requestSpy.invokeLoad(ads);

        Exception exception = mock(Exception.class);
        requestSpy.invokeFail(exception);
    }

    @Test
    public void requestWithCorrectParameters()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        // Valid parameters
        networkRequestSpy.request(this.applicationContext, "app_token", "placement_id", listenerMock);
        verify(listenerMock, times(1)).onRequestStarted(networkRequestSpy);

        // TODO: Ensure that the entire workflow works, callbacking an onRequestLoaded
    }

    @Test
    public void requestWithInvalidParametersCallbacksFail()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        // Invalid parameters
        networkRequestSpy.request(null, "ap_token", "placement_id", listenerMock);
        networkRequestSpy.request(this.applicationContext, null, "placement_id", listenerMock);
        networkRequestSpy.request(this.applicationContext, "app_token", null, listenerMock);
        verify(listenerMock, times(3)).onRequestFailed(eq(networkRequestSpy), any(IllegalArgumentException.class));

        // Invalid listener (just dropping the call, shouldn't crash)
        networkRequestSpy.request(this.applicationContext, "sample_token", "placement_id", null);
    }

    @Test
    public void requestWithoutConfigFails()
    {
        // TODO: write a test for ensuring that with a null config we get onRequestFailed();
    }
}
