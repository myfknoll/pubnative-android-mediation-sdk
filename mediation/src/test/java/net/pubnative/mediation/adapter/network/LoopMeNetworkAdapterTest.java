package net.pubnative.mediation.adapter.network;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterTestUtils;
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

/**
 * Created by rahul on 17/9/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LoopMeNetworkAdapterTest
{
    private final static String VALID_APP_ID        = "sample_app_id";
    private final static int    TIMEOUT_DEACTIVATED = 0;

    private Context appContext    = null;
    private String  adResponseXML = null;

    @Before
    public void setUp()
    {
        this.appContext = RuntimeEnvironment.application.getApplicationContext();
        this.adResponseXML = PubnativeNetworkAdapterTestUtils.getSampleAdResponse("loopme_native_ad.xml");
    }

    private void stubCreateRequestWithOnFinishedCallback(LoopMeNetworkAdapter adapter)
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                LoopMeNetworkAdapter adapter = (LoopMeNetworkAdapter) invocation.getMock();
                adapter.onHttpTaskFinished(null, adResponseXML);
                return null;
            }
        }).when(adapter).createRequest(any(Context.class), anyString());
    }

    private void verifyCallbacksForFail(LoopMeNetworkAdapter adapter, PubnativeNetworkAdapterListener listener)
    {
        verify(listener, times(1)).onAdapterRequestStarted(eq(adapter));
        verify(listener, times(1)).onAdapterRequestFailed(eq(adapter), any(Exception.class));
        verify(listener, never()).onAdapterRequestLoaded(eq(adapter), any(PubnativeAdModel.class));
    }

    @Test
    public void successCallbacksWithValidAppId()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoopMeNetworkAdapter.KEY_APP_ID, VALID_APP_ID);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        LoopMeNetworkAdapter adapterSpy = spy(new LoopMeNetworkAdapter(data));

        this.stubCreateRequestWithOnFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.appContext, TIMEOUT_DEACTIVATED, listenerMock);
        verify(listenerMock, times(1)).onAdapterRequestStarted(eq(adapterSpy));
        verify(listenerMock, times(1)).onAdapterRequestLoaded(eq(adapterSpy), any(PubnativeAdModel.class));
        verify(listenerMock, never()).onAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
    }

    @Test
    public void failureCallbacksWithNullAppId()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoopMeNetworkAdapter.KEY_APP_ID, null);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        LoopMeNetworkAdapter adapterSpy = spy(new LoopMeNetworkAdapter(data));

        this.stubCreateRequestWithOnFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.appContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFail(adapterSpy, listenerMock);
    }

    @Test
    public void failureCallbacksWithEmptyAppId()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoopMeNetworkAdapter.KEY_APP_ID, "");

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        LoopMeNetworkAdapter adapterSpy = spy(new LoopMeNetworkAdapter(data));

        this.stubCreateRequestWithOnFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.appContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFail(adapterSpy, listenerMock);
    }

    @Test
    public void failureCallbacksWithNoAppId()
    {
        Map<String, String> data = new HashMap<String, String>();

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        LoopMeNetworkAdapter adapterSpy = spy(new LoopMeNetworkAdapter(data));

        this.stubCreateRequestWithOnFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.appContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFail(adapterSpy, listenerMock);
    }

    @Test
    public void failureCallbacksWithNullData()
    {
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        LoopMeNetworkAdapter adapterSpy = spy(new LoopMeNetworkAdapter(null));

        this.stubCreateRequestWithOnFinishedCallback(adapterSpy);

        adapterSpy.doRequest(this.appContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFail(adapterSpy, listenerMock);
    }

    @Test
    public void failureCallbacksWhenLoopMeFailsToGiveAd()
    {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoopMeNetworkAdapter.KEY_APP_ID, VALID_APP_ID);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        LoopMeNetworkAdapter adapterSpy = spy(new LoopMeNetworkAdapter(data));

        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                LoopMeNetworkAdapter adapter = (LoopMeNetworkAdapter) invocation.getMock();
                adapter.onHttpTaskFailed(null, null);
                return null;
            }
        }).when(adapterSpy).createRequest(any(Context.class), anyString());

        adapterSpy.doRequest(this.appContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFail(adapterSpy, listenerMock);
    }
}
