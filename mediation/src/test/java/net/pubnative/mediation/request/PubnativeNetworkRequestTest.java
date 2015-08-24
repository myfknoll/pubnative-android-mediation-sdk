package net.pubnative.mediation.request;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.config.PubnativeConfigTestUtils;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.model.PubnativeAdModel;
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
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({PubnativeNetworkRequest.class, PubnativeNetworkAdapterFactory.class})
public class PubnativeNetworkRequestTest
{
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    final static String TEST_PLACEMENT_ID       = "placement";
    final static String TEST_PLACEMENT_ID_VALID = "1";
    final static String TEST_APP_TOKEN          = "app_token";

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
    }

    @Test
    public void invokeCallbacksWithValidListener()
    {
        requestSpy.listener = listenerMock;

        // onRequestStarted
        requestSpy.invokeStart();
        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));

        // onRequestLoaded
        PubnativeAdModel adMock = spy(PubnativeAdModel.class);
        requestSpy.invokeLoad(adMock);
        verify(listenerMock, times(1)).onRequestLoaded(eq(requestSpy), eq(adMock));

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
        requestSpy.invokeLoad(spy(PubnativeAdModel.class));
        requestSpy.invokeFail(mock(Exception.class));
    }

    @Test
    public void requestWithCorrectParameters()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", TEST_APP_TOKEN);

        final PubnativeAdModel modelMock = spy(PubnativeAdModel.class);
        final PubnativeNetworkAdapter adapterMock = mock(PubnativeNetworkAdapter.class);
        // Stub Adapter doRequest to callback the listener directly
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                PubnativeNetworkAdapter adapterMock = (PubnativeNetworkAdapter) invocation.getMock();
                PubnativeNetworkAdapterListener adapterListener = (PubnativeNetworkAdapterListener) invocation.getArgumentAt(2, PubnativeNetworkAdapterListener.class);
                adapterListener.onAdapterRequestStarted(adapterMock);
                adapterListener.onAdapterRequestLoaded(adapterMock, modelMock);
                return null;
            }
        }).when(adapterMock).doRequest(any(Context.class), anyInt(), any(PubnativeNetworkAdapterListener.class));
        // Stub Factory create to return my adapter mock
        PowerMockito.mockStatic(PubnativeNetworkAdapterFactory.class);
        when(PubnativeNetworkAdapterFactory.createAdapter(any(PubnativeNetworkModel.class))).thenReturn(adapterMock);

        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID_VALID, listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, never()).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, times(1)).onRequestLoaded(eq(requestSpy), eq(modelMock));
    }

    @Test
    public void requestWithNullParameters()
    {
        requestSpy.start(null, TEST_APP_TOKEN, TEST_PLACEMENT_ID, this.listenerMock);
        requestSpy.start(this.applicationContext, null, TEST_PLACEMENT_ID, this.listenerMock);
        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, null, this.listenerMock);

        verify(listenerMock, times(3)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(3)).onRequestFailed(eq(requestSpy), any(IllegalArgumentException.class));
    }

    @Test
    public void requestWithInvalidAppToken()
    {
        // both null
        requestSpy.start(this.applicationContext, null, null, this.listenerMock);

        // null app_token
        requestSpy.start(this.applicationContext, null, TEST_PLACEMENT_ID, this.listenerMock);

        // null placement_id
        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, null, this.listenerMock);

        // empty app_token
        requestSpy.start(this.applicationContext, "", TEST_PLACEMENT_ID, this.listenerMock);

        // empty placement_id
        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, "", this.listenerMock);

        // both empty
        requestSpy.start(this.applicationContext, "", "", this.listenerMock);

        verify(listenerMock, times(6)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(6)).onRequestFailed(eq(requestSpy), any(IllegalArgumentException.class));
    }

    @Test
    public void requestWithNullListenerDrops()
    {
        // This should not crash
        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID, null);
    }

    @Test
    public void requestWithEmptyPlacementConfig()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty_config.json", TEST_APP_TOKEN);

        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID, this.listenerMock);
        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        /**
         * Request with empty config need not fail all the time.
         * When a config is nullOrEmpty, the config in disc gets cleared.
         * This leads to a config download next time. If the download fails,
         * then the onRequestFailed will get called.
         * // verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(IllegalArgumentException.class));
         */
    }

    @Test
    public void requestWithDeliverInactive()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "delivery_inactive.json", TEST_APP_TOKEN);
        PubnativeDeliveryManager.logImpression(this.applicationContext, "placement_id");

        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID, this.listenerMock);
        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, never()).onRequestLoaded(eq(requestSpy), any(PubnativeAdModel.class));
    }

    @Test
    public void requestWithFrequencyDayLimitReached()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "delivery_freq_day.json", TEST_APP_TOKEN);

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
        when(PubnativeNetworkAdapterFactory.createAdapter(any(PubnativeNetworkModel.class))).thenReturn(adapterMock);

        PubnativeDeliveryManager.logImpression(this.applicationContext, TEST_APP_TOKEN);
        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID, this.listenerMock);
        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, never()).onRequestLoaded(eq(requestSpy), any(PubnativeAdModel.class));
    }

    @Test
    public void requestWithFrequencyHourLimitReached()
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
        when(PubnativeNetworkAdapterFactory.createAdapter(any(PubnativeNetworkModel.class))).thenReturn(adapterMock);

        PubnativeDeliveryManager.logImpression(this.applicationContext, TEST_APP_TOKEN);
        requestSpy.start(this.applicationContext, TEST_APP_TOKEN, TEST_PLACEMENT_ID, this.listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(eq(requestSpy));
        verify(listenerMock, times(1)).onRequestFailed(eq(requestSpy), any(Exception.class));
        verify(listenerMock, never()).onRequestLoaded(eq(requestSpy), any(PubnativeAdModel.class));
    }
}
