package net.pubnative.mediation.adapter.network;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by rahul on 11/8/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FacebookNetworkAdapterTest
{
    private Context applicationContext;

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void verifyCallbacksOnFacebookFailure()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "test_placement_id");

        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(data));
        // stubbing the createRequest method to simulate facebook error.
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                FacebookNetworkAdapter adapter = (FacebookNetworkAdapter) invocation.getMock();
                adapter.onError(null, null);
                return null;
            }
        }).when(adapterSpy).createRequest(any(Context.class), anyString());

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithValidParams()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "test_placement_id");

        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(data));
        stubCreateRequestMethod(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        adapterSpy.doRequest(this.applicationContext, 1, listenerMock);

        verify(listenerMock, times(1)).onAdapterRequestStarted(eq(adapterSpy));
        verify(listenerMock, times(1)).onAdapterRequestLoaded(eq(adapterSpy), any(List.class));
        verify(listenerMock, never()).onAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
    }

    @Test
    public void verifyCallbacksWithEmptyPlacementId()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, "");

        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(data));
        stubCreateRequestMethod(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullPlacementId()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(FacebookNetworkAdapter.KEY_PLACEMENT_ID, null);

        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(data));
        stubCreateRequestMethod(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    /**
     * This method tests the facebook adapter with no placement_id
     * added inside the non-null 'data' hashmap.
     */
    @Test
    public void verifyCallbacksWithNotNullDataButNoPlacementIdKey()
    {
        HashMap<String, String> data = new HashMap<>();

        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(data));
        stubCreateRequestMethod(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullData()
    {
        FacebookNetworkAdapter adapterSpy = spy(new FacebookNetworkAdapter(null));
        stubCreateRequestMethod(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        verifyCallbacksForFailureCase(adapterSpy, listenerMock);
    }

    private void verifyCallbacksForFailureCase(FacebookNetworkAdapter adapter, PubnativeNetworkAdapterListener listener)
    {
        adapter.doRequest(this.applicationContext, 1, listener);

        verify(listener, times(1)).onAdapterRequestStarted(eq(adapter));
        verify(listener, times(1)).onAdapterRequestFailed(eq(adapter), any(Exception.class));
        verify(listener, never()).onAdapterRequestLoaded(eq(adapter), any(List.class));
    }

    private void stubCreateRequestMethod(FacebookNetworkAdapter adapterMock)
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                FacebookNetworkAdapter adapter = (FacebookNetworkAdapter) invocation.getMock();
                adapter.onAdLoaded(null);
                return null;
            }
        }).when(adapterMock).createRequest(any(Context.class), anyString());
    }
}

