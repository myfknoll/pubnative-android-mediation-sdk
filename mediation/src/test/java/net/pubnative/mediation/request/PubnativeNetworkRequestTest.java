package net.pubnative.mediation.request;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.config.PubnativeConfigTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeNetworkRequestTest
{
    public Context             applicationContext;
    public Map<String, String> parameters;

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        this.parameters = new HashMap<>();
    }

    @Test
    public void invokeCallbacksWithValidListener()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        networkRequestSpy.listener = listenerMock;

        // onRequestStarted
        networkRequestSpy.invokeStart();
        verify(listenerMock, times(1)).onRequestStarted(networkRequestSpy);

        // onRequestLoaded
        ArrayList adsMock = mock(ArrayList.class);
        networkRequestSpy.invokeLoad(adsMock);
        verify(listenerMock, times(1)).onRequestLoaded(eq(networkRequestSpy), eq(adsMock));

        // onRequestFailed
        Exception exceptionMock = mock(Exception.class);
        networkRequestSpy.invokeFail(exceptionMock);
        verify(listenerMock, times(1)).onRequestFailed(eq(networkRequestSpy), eq(exceptionMock));
    }

    @Test
    public void invokeCallbacksWithNullListener()
    {
        PubnativeNetworkRequest requestSpy = spy(PubnativeNetworkRequest.class);

        // invoking method with arguments when lister is null
        requestSpy.listener = null;
        requestSpy.invokeStart();
        requestSpy.invokeLoad(mock(ArrayList.class));
        requestSpy.invokeFail(mock(Exception.class));
    }

    @Test
    public void requestWithCorrectParameters()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json");
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        // Valid parameters
        this.parameters.put(PubnativeNetworkRequest.Parameters.AD_COUNT, "10");
        this.parameters.put(PubnativeNetworkRequest.Parameters.APP_TOKEN, "app_token");
        this.parameters.put(PubnativeNetworkRequest.Parameters.PLACEMENT_ID, "placement_id");

        networkRequestSpy.request(this.applicationContext, this.parameters, listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(networkRequestSpy));
        verify(listenerMock, never()).onRequestFailed(eq(networkRequestSpy), any(Exception.class));
        verify(listenerMock, times(1)).onRequestLoaded(eq(networkRequestSpy), any(List.class));
    }

    // TODO: Create tests for doAdapterRequest pathways

    @Test
    public void requestWithInvalidParametersCallbacksFail()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        // Invalid parameters
        this.parameters.clear();
        this.parameters.put(PubnativeNetworkRequest.Parameters.APP_TOKEN, "app_token");
        this.parameters.put(PubnativeNetworkRequest.Parameters.PLACEMENT_ID, "placement_id");
        networkRequestSpy.request(null, this.parameters, listenerMock);
        this.parameters.clear();
        this.parameters.put(PubnativeNetworkRequest.Parameters.APP_TOKEN, "app_token");
        networkRequestSpy.request(this.applicationContext, this.parameters, listenerMock);
        this.parameters.clear();
        this.parameters.put(PubnativeNetworkRequest.Parameters.PLACEMENT_ID, "placement_id");
        networkRequestSpy.request(this.applicationContext, this.parameters, listenerMock);
        verify(listenerMock, times(3)).onRequestFailed(eq(networkRequestSpy), any(IllegalArgumentException.class));

        // Invalid listener (just dropping the call, shouldn't crash)
        this.parameters.clear();
        this.parameters.put(PubnativeNetworkRequest.Parameters.APP_TOKEN, "app_token");
        this.parameters.put(PubnativeNetworkRequest.Parameters.PLACEMENT_ID, "placement_id");
        networkRequestSpy.request(this.applicationContext, this.parameters, null);
    }

    // TODO: write test for null getConfig path

    @Test
    public void requestWithEmptyPlacementConfig()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty_config.json");
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = spy(PubnativeNetworkRequestListener.class);

        this.parameters.put(PubnativeNetworkRequest.Parameters.APP_TOKEN, "app_token");
        this.parameters.put(PubnativeNetworkRequest.Parameters.PLACEMENT_ID, "placement_id");
        networkRequestSpy.request(this.applicationContext, this.parameters, listenerMock);

        verify(listenerMock, times(1)).onRequestFailed(eq(networkRequestSpy), any(IllegalArgumentException.class));
    }
}
