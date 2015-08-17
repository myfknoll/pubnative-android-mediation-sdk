package net.pubnative.mediation.adapter.network;

import android.content.Context;

import net.pubnative.library.request.AdRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by rahul on 13/8/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PubnativeLibraryNetworkAdapterTest
{
    private Context applicationContext;

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void verifyCallbacksOnPubnativeLibraryFailure()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(PubnativeLibraryNetworkAdapter.APP_TOKEN_KEY, "test_placement_id");

        PubnativeLibraryNetworkAdapter adapterSpy = spy(new PubnativeLibraryNetworkAdapter(data));
        // stubbing the createRequest method to simulate facebook error.
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                PubnativeLibraryNetworkAdapter adapter = (PubnativeLibraryNetworkAdapter) invocation.getMock();
                adapter.onAdRequestFailed(mock(AdRequest.class), mock(Exception.class));
                return null;
            }
        }).when(adapterSpy).createRequest(any(Context.class), anyString());

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);

        adapterSpy.doRequest(this.applicationContext, 1, listenerMock);
        failCallbacksWhenInvalidDataProvided(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithValidParams()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(PubnativeLibraryNetworkAdapter.APP_TOKEN_KEY, "test_app_token");

        PubnativeLibraryNetworkAdapter adapterSpy = spy(new PubnativeLibraryNetworkAdapter(data));
        stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);
        adapterSpy.doRequest(this.applicationContext, 1, listenerMock);

        verify(listenerMock, times(1)).onAdapterRequestStarted(eq(adapterSpy));
        verify(listenerMock, times(1)).onAdapterRequestLoaded(eq(adapterSpy), any(List.class));
        verify(listenerMock, never()).onAdapterRequestFailed(eq(adapterSpy), any(Exception.class));
    }

    @Test
    public void verifyCallbacksWithEmptyAppTocken()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(PubnativeLibraryNetworkAdapter.APP_TOKEN_KEY, "");

        PubnativeLibraryNetworkAdapter adapterSpy = spy(new PubnativeLibraryNetworkAdapter(data));
        stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);

        adapterSpy.doRequest(this.applicationContext, 1, listenerMock);
        failCallbacksWhenInvalidDataProvided(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullAppToken()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put(PubnativeLibraryNetworkAdapter.APP_TOKEN_KEY, null);

        PubnativeLibraryNetworkAdapter adapterSpy = spy(new PubnativeLibraryNetworkAdapter(data));
        stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);

        adapterSpy.doRequest(this.applicationContext, 1, listenerMock);
        failCallbacksWhenInvalidDataProvided(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNotNullDataButNoAppToken()
    {
        HashMap<String, String> data = new HashMap<>();

        PubnativeLibraryNetworkAdapter adapterSpy = spy(new PubnativeLibraryNetworkAdapter(data));
        stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);

        adapterSpy.doRequest(this.applicationContext, 1, listenerMock);
        failCallbacksWhenInvalidDataProvided(adapterSpy, listenerMock);
    }

    @Test
    public void verifyCallbacksWithNullData()
    {
        PubnativeLibraryNetworkAdapter adapterSpy = spy(new PubnativeLibraryNetworkAdapter(null));
        stubCreateRequestMethodWithRequestFinishedCallback(adapterSpy);

        PubnativeNetworkAdapterListener listenerMock = mock(PubnativeNetworkAdapterListener.class);

        adapterSpy.doRequest(this.applicationContext, 1, listenerMock);
        failCallbacksWhenInvalidDataProvided(adapterSpy, listenerMock);
    }

    private void failCallbacksWhenInvalidDataProvided(PubnativeLibraryNetworkAdapter adapter, PubnativeNetworkAdapterListener listener)
    {
        verify(listener, times(1)).onAdapterRequestStarted(eq(adapter));
        verify(listener, times(1)).onAdapterRequestFailed(eq(adapter), any(Exception.class));
        verify(listener, never()).onAdapterRequestLoaded(eq(adapter), any(List.class));
    }

    private void stubCreateRequestMethodWithRequestFinishedCallback(PubnativeLibraryNetworkAdapter adapterMock)
    {
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                PubnativeLibraryNetworkAdapter adapter = (PubnativeLibraryNetworkAdapter) invocation.getMock();
                adapter.onAdRequestFinished(mock(AdRequest.class), mock(ArrayList.class));
                return null;
            }
        }).when(adapterMock).createRequest(any(Context.class), anyString());
    }
}
