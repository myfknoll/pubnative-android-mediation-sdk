package net.pubnative.mediation.request;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.config.PubnativeConfigTestUtils;
import net.pubnative.mediation.config.PubnativeFrequencyManager;
import net.pubnative.mediation.model.PubnativeNetworkModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest({PubnativeNetworkRequest.class, PubnativeNetworkAdapterFactory.class})
public class PubnativeNetworkRequestTest
{
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    final static String TEST_PLACEMENT_ID = "1";
    final static String TEST_APP_TOKEN    = "af117147db28ef258bfd6d042c718b537bc6a2b0760aca3d073a1c80865545f9";

    Context applicationContext;

    // Mocks
    PubnativeNetworkRequest         requestSpy;
    PubnativeNetworkRequestListener listenerMock;

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();

        this.requestSpy = spy(PubnativeNetworkRequest.class);
        this.listenerMock = mock(PubnativeNetworkRequestListener.class);

        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.app_token = TEST_PLACEMENT_ID;
    }

    @Test
    public void invokeCallbacksWithValidListener()
    {
        requestSpy.listener = listenerMock;

        // onRequestStarted
        requestSpy.invokeStart();
        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));

        // onRequestLoaded
        ArrayList adsMock = mock(ArrayList.class);
        requestSpy.invokeLoad(adsMock);
        verify(listenerMock, times(1)).onRequestLoaded(eq(requestSpy), eq(adsMock));

        // onRequestFailed
        Exception exceptionMock = mock(Exception.class);
        requestSpy.invokeFail(exceptionMock);
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), eq(exceptionMock));
    }

    @Test
    public void invokeCallbacksWithNullListener()
    {
        // invoking method with a rguments when lister is null should not crash
        requestSpy.listener = null;
        requestSpy.invokeStart();
        requestSpy.invokeLoad(mock(ArrayList.class));
        requestSpy.invokeFail(mock(Exception.class));
    }

    @Test
    public void requestWithCorrectParameters()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json",TEST_APP_TOKEN);
        final PubnativeNetworkAdapter adapterMock = mock(PubnativeNetworkAdapter.class);

        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = TEST_PLACEMENT_ID;

        PowerMockito.mockStatic(PubnativeNetworkAdapterFactory.class);
        // Stub Factory create to return my adapter mock
        when(PubnativeNetworkAdapterFactory.createAdapter(any(PubnativeNetworkModel.class))).thenReturn(adapterMock);
        // Stub Adapter doRequest to callback the listener directly
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                PubnativeNetworkAdapter adapterMock = (PubnativeNetworkAdapter) invocation.getMock();
                PubnativeNetworkAdapterListener adapterListener = (PubnativeNetworkAdapterListener) invocation.getArgumentAt(2, PubnativeNetworkAdapterListener.class);
                adapterListener.onAdapterRequestStarted(adapterMock);
                adapterListener.onAdapterRequestLoaded(adapterMock, null);
                return null;
            }
        }).when(adapterMock).doRequest(any(Context.class), anyInt(), any(PubnativeNetworkAdapterListener.class));

        requestSpy.request(this.applicationContext, parameters, listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, never()).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, times(1)).onRequestLoaded(eq(requestSpy), any(List.class));
    }

    @Test
    public void requestWithNullParameters()
    {
        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.app_token = TEST_PLACEMENT_ID;

        requestSpy.request(null, parameters, this.listenerMock);
        requestSpy.request(this.applicationContext, null, this.listenerMock);

        verify(listenerMock, times(2)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(2)).onRequestFailed(eq(requestSpy), any(IllegalArgumentException.class));
    }

    @Test
    public void requestWithInvalidAppToken()
    {
        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();

        // null app_token
        parameters.app_token = null;
        parameters.placement_id = TEST_PLACEMENT_ID;
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        // empty app_token
        parameters.app_token = "";
        parameters.placement_id = TEST_PLACEMENT_ID;
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        // null placement_id
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = null;
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        // empty placement_id
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = "";
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        // both null
        parameters.app_token = null;
        parameters.placement_id = null;
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        // both empty
        parameters.app_token = "";
        parameters.placement_id = "";
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        verify(listenerMock, times(6)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(6)).onRequestFailed(eq(requestSpy), any(IllegalArgumentException.class));
    }

    @Test
    public void requestWithNullListenerDrops()
    {
        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();

        // This should not crash
        requestSpy.request(null, parameters, null);
        requestSpy.request(this.applicationContext, null, null);
        requestSpy.request(this.applicationContext, parameters, null);
    }

    @Test
    public void requestWithEmptyPlacementConfig()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty_config.json",TEST_APP_TOKEN);
        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = TEST_PLACEMENT_ID;

        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(IllegalArgumentException.class));
    }

    @Test
    public void requestWithDeliverInactive()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "delivery_inactive.json", TEST_APP_TOKEN);
        PubnativeFrequencyManager.logImpression(this.applicationContext);

        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = TEST_PLACEMENT_ID;
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, never()).onRequestLoaded(eq(requestSpy), any(List.class));
    }

    @Test
    public void requestWithFrequencyDayLimitReached()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "delivery_freq_day.json", TEST_APP_TOKEN);
        PubnativeFrequencyManager.logImpression(this.applicationContext);

        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = TEST_PLACEMENT_ID;
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, never()).onRequestLoaded(eq(requestSpy), any(List.class));
    }

    @Test
    public void requestWithFrequencyHourLimitReached()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "delivery_freq_hour.json", TEST_APP_TOKEN);
        PubnativeFrequencyManager.logImpression(this.applicationContext);

        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = TEST_PLACEMENT_ID;
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, never()).onRequestLoaded(eq(requestSpy), any(List.class));
    }

    @Test
    public void requestWithFrequencyLimitsOpen()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "delivery_freq_hour.json", TEST_APP_TOKEN);

        final PubnativeNetworkAdapter adapterMock = mock(PubnativeNetworkAdapter.class);
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return null;
            }
        }).when(adapterMock).doRequest(any(Context.class), anyInt(), any(PubnativeNetworkAdapterListener.class));

        PowerMockito.mockStatic(PubnativeNetworkAdapterFactory.class);
        // Stub Factory create to return my adapter mock
        when(PubnativeNetworkAdapterFactory.createAdapter(any(PubnativeNetworkModel.class))).thenReturn(adapterMock);
        // Stub Adapter doRequest to callback the listener directly

        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = TEST_APP_TOKEN;
        parameters.placement_id = TEST_PLACEMENT_ID;

        // First request should work
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);
        PubnativeFrequencyManager.logImpression(applicationContext);
        // Second request should fail with frequency_cap before creating the adapter
        requestSpy.request(this.applicationContext, parameters, this.listenerMock);

        verify(listenerMock, times(2)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(Exception.class));
    }

    // TODO: Test for null getConfig path
}
