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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by rahul on 11/8/15.
 */
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
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);

        // Check that
        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        verify(listenerMock, times(1)).onAdapterRequestStarted(eq(adapterSpy));
        verify(listenerMock, times(1)).onAdapterRequestLoaded(eq(adapterSpy), any(PubnativeAdModel.class));
        verify(listenerMock, never()).onAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
    }

    @Test
    public void verifyCallbacksOnFacebookError() {
        Map<String, String> data = new HashMap();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "test_placement_id");
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
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
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullPlacementId() {
        Map<String, String> data = new HashMap();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, null);
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
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
        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        FacebookNetworkAdapter          adapterSpy   = spy(new FacebookNetworkAdapter(data));
        this.stubCreateRequestMethod(adapterSpy);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullData() {
        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(null));
        this.stubCreateRequestMethod(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);

        adapterSpy.doRequest(this.applicationContext, TIMEOUT_DEACTIVATED, listenerMock);
        this.verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    private void verifyCallbacksForFailureCase(FacebookNetworkAdapter adapter, PubnativeNetworkAdapterListener listener) {
        verify(listener, times(1)).onAdapterRequestStarted(eq(adapter));
        verify(listener, times(1)).onAdapterRequestFailed(eq(adapter), any(Exception.class));
        verify(listener, never()).onAdapterRequestLoaded(eq(adapter), any(PubnativeAdModel.class));
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

