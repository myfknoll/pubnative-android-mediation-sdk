package net.pubnative.mediation.model;

import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by davidmartin on 20/08/15.
 */
public class PubnativeAdModelTest
{
    @Test
    public void callbackInvocationWithDifferentValues()
    {
        PubnativeAdModel modelSpy = spy(PubnativeAdModel.class);
        PubnativeAdModelListener listenerSpy = spy(PubnativeAdModelListener.class);

        // Does nothing with null listener
        modelSpy.setListener(null);
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();

        // Callbacks the setted up listener
        modelSpy.setListener(listenerSpy);
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();

        // Calling them again don't increment the callback
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();

        verify(listenerSpy, times(1)).onAdImpressionConfirmed(eq(modelSpy));
        verify(listenerSpy, times(1)).onAdClick(eq(modelSpy));

        // TODO: Verify that the tracking static method is called
    }
}