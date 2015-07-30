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

import static org.assertj.core.api.Assertions.assertThat;
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
    public void test_creationNotNull()
    {
        PubnativeNetworkRequest request = new PubnativeNetworkRequest();
        assertThat(request).isNotNull();
    }

    @Test
    public void test_callbacksWithValidListener()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        networkRequestSpy.listener = listenerMock;
        networkRequestSpy.invokeStart();

        ArrayList<PubnativeAdModel> adsMock = mock(ArrayList.class);
        networkRequestSpy.invokeLoad(adsMock);

        Exception exceptionMock = mock(Exception.class);
        networkRequestSpy.invokeFail(exceptionMock);

        verify(listenerMock, times(1)).onRequestStarted(networkRequestSpy);
        verify(listenerMock, times(1)).onRequestLoaded(networkRequestSpy, adsMock);
        verify(listenerMock, times(1)).onRequestFailed(networkRequestSpy, exceptionMock);
    }

    @Test
    public void test_callbacksWithNullListener() throws NullPointerException
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);

        // invoking method with arguments when lister is null
        networkRequestSpy.invokeStart();

        ArrayList<PubnativeAdModel> ads = mock(ArrayList.class);
        networkRequestSpy.invokeLoad(ads);

        Exception exception = mock(Exception.class);
        networkRequestSpy.invokeFail(exception);
    }

    @Test
    public void test_requestWithValidParams()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        networkRequestSpy.request(this.applicationContext, "sample_token", listenerMock);

        verify(listenerMock, times(1)).onRequestStarted(networkRequestSpy);
    }

    @Test
    public void test_requestWithNullToken()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        PubnativeNetworkRequestListener listenerMock = mock(PubnativeNetworkRequestListener.class);

        networkRequestSpy.request(this.applicationContext, null, listenerMock);

        verify(listenerMock, times(1)).onRequestFailed(eq(networkRequestSpy), any(Exception.class));
    }

    @Test(expected = RuntimeException.class)
    public void test_requestWithNullListener()
    {
        PubnativeNetworkRequest networkRequestSpy = spy(PubnativeNetworkRequest.class);
        networkRequestSpy.request(this.applicationContext, "sample_token", null);
    }

    // TODO: Create tests
}
