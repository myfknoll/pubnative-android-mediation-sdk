package net.pubnative.mediation.adapter;

import android.content.Context;

import net.pubnative.mediation.model.PubnativeAdModel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Created by root on 30/7/15.
 */
public class PubnativeNetworkAdapterTest
{

    @Test
    public void invokeCallbacksWithValidListener()
    {
        HashMap<String, Object> adapterConfigMock = mock(HashMap.class);
        PubnativeNetworkAdapterListener listenerSpy = spy(PubnativeNetworkAdapterListener.class);

        PubnativeNetworkAdapter adapterInstance = spy(new PubnativeNetworkAdapter(adapterConfigMock)
        {
            @Override
            public void request(Context context)
            {
                // Do nothing
            }
        });

        adapterInstance.listener = listenerSpy;

        // onRequestStarted
        adapterInstance.invokeStart();
        verify(listenerSpy, times(1)).onAdapterRequestStarted(adapterInstance);

        // onRequestLoaded
        ArrayList<PubnativeAdModel> adsMock = mock(ArrayList.class);
        adapterInstance.invokeLoaded(adsMock);
        verify(listenerSpy, times(1)).onAdapterRequestLoaded(eq(adapterInstance), eq(adsMock));

        // onRequestFailed
        Exception exceptionMock = mock(Exception.class);
        adapterInstance.invokeFailed(exceptionMock);
        verify(listenerSpy, times(1)).onAdapterRequestFailed(eq(adapterInstance), eq(exceptionMock));

    }

    @Test
    public void invokeCallbacksWithNullListener()
    {
        HashMap<String, Object> adapterConfigMock = mock(HashMap.class);
        PubnativeNetworkAdapter adapterSpy = spy(new PubnativeNetworkAdapter(adapterConfigMock)
        {
            @Override
            public void request(Context context)
            {
                // Do nothing
            }
        });

        adapterSpy.listener = null;
        adapterSpy.invokeStart();
        adapterSpy.invokeLoaded(mock(ArrayList.class));
        adapterSpy.invokeFailed(mock(Exception.class));
    }
}