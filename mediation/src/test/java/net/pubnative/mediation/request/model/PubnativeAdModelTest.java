package net.pubnative.mediation.request.model;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.insights.PubnativeInsightsManager;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;

/**
 * Created by davidmartin on 20/08/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({PubnativeAdModel.class, PubnativeInsightsManager.class, PubnativeDeliveryManager.class})
public class PubnativeAdModelTest
{
    private static final String SAMPLE_URL = "http://pubnative.net";

    private Context appContext = null;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp()
    {
        this.appContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void callbacksOnlyFiresOnce() throws Exception
    {
        PowerMockito.spy(PubnativeInsightsManager.class);
        // stub the method trackNext to nothing.
        PowerMockito.doNothing().when(PubnativeInsightsManager.class, "trackData", any(Context.class), anyString(), any(PubnativeInsightDataModel.class));

        PowerMockito.spy(PubnativeDeliveryManager.class);
        PowerMockito.doNothing().when(PubnativeDeliveryManager.class, "logImpression", any(Context.class), anyString());

        PubnativeAdModel modelSpy = spy(PubnativeAdModel.class);
        modelSpy.context = this.appContext;

        PubnativeInsightDataModel dataModel = new PubnativeInsightDataModel();
        modelSpy.setTrackingInfo(dataModel, SAMPLE_URL, SAMPLE_URL);

        // Callbacks the setted up listener
        PubnativeAdModelListener listenerSpy = spy(PubnativeAdModelListener.class);
        modelSpy.setListener(listenerSpy);

        // Calling invoke methods
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();

        // Calling them again don't increment the callback
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();

        verify(listenerSpy, times(1)).onAdImpressionConfirmed(eq(modelSpy));
        verify(listenerSpy, times(1)).onAdClick(eq(modelSpy));

        // track data called twice. once for each impression and click.
        PowerMockito.verifyStatic(times(2));
        PubnativeInsightsManager.trackData(eq(appContext), eq(SAMPLE_URL), eq(dataModel));

        // log impression called only once.
        PowerMockito.verifyStatic(times(1));
        PubnativeDeliveryManager.logImpression(eq(appContext), anyString());
    }

    @Test
    public void callbackWithNullListenerDoesNothing()
    {
        PubnativeAdModel modelSpy = spy(PubnativeAdModel.class);

        // Does nothing with null listener
        modelSpy.invokeOnAdImpressionConfirmed();
        modelSpy.invokeOnAdClick();
    }
}
