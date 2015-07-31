package net.pubnative.mediation.adapter;

import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeNetworkModel;

import org.junit.Test;

import java.util.ArrayList;

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
    public void test_callbacksWithValidListener()
    {
        PubnativeNetworkModel testModel = mock(PubnativeNetworkModel.class);
        PubnativeNetworkAdapterListener testListener = mock(PubnativeNetworkAdapterListener.class);

        PubnativeNetworkAdapter adapterInstance = spy(new PubnativeNetworkAdapter(testModel)
        {
            @Override
            public void request()
            {

            }
        });

        adapterInstance.listener = testListener;
        adapterInstance.invokeStart();

        ArrayList<PubnativeAdModel> mockAdModelArrayList = mock(ArrayList.class);
        adapterInstance.invokeLoaded(mockAdModelArrayList);

        Exception mockException = mock(Exception.class);
        adapterInstance.invokeFailed(mockException);

        verify(testListener,times(1)).onAdapterRequestStarted(adapterInstance);
        verify(testListener,times(1)).onAdapterRequestLoaded(adapterInstance, mockAdModelArrayList);
        verify(testListener,times(1)).onAdapterRequestFailed(adapterInstance,mockException);

    }

    @Test
    public void test_callbacksWithNullListener()
    {
        PubnativeNetworkModel mockModel = mock(PubnativeNetworkModel.class);
        PubnativeNetworkAdapter adapterSpy = spy(new PubnativeNetworkAdapter(mockModel)
        {
            @Override
            public void request()
            {
                // Do nothing
            }
        });

        adapterSpy.listener = null;
        adapterSpy.invokeStart();
        adapterSpy.invokeLoaded(null);
        adapterSpy.invokeFailed(null);
    }

    @Test
    public void test_callbacksWithInvalidListener()
    {
        PubnativeNetworkModel mockModel = mock(PubnativeNetworkModel.class);
        PubnativeNetworkAdapterListener mockListener = mock(PubnativeNetworkAdapterListener.class);

        PubnativeNetworkAdapter adapterSpy = spy(new PubnativeNetworkAdapter(mockModel)
        {
            @Override
            public void request()
            {
                // Do nothing
            }
        });

        adapterSpy.listener = mockListener;
        adapterSpy.invokeStart();

        ArrayList<PubnativeAdModel> mockAdModelArrayList = mock(ArrayList.class);
        adapterSpy.invokeLoaded(mockAdModelArrayList);

        Exception mockException = mock(Exception.class);
        adapterSpy.invokeFailed(mockException);

    }
}