package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.view.View;

import net.pubnative.mediation.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by rahul on 13/8/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FacebookNativeAdModelTest
{
    @Test
    public void memberFunctionsWithNullNativeAdDoNotFail()
    {
        Context appContext = RuntimeEnvironment.application.getApplicationContext();
        FacebookNativeAdModel adModel = spy(new FacebookNativeAdModel(null));

        // verify getter methods returns null
        assertThat(adModel.getTitle()).isNull();
        assertThat(adModel.getIconUrl()).isNull();
        assertThat(adModel.getBannerUrl()).isNull();
        assertThat(adModel.getDescription()).isNull();
        assertThat(adModel.getCallToAction()).isNull();
        assertThat(adModel.getStarRating()).isZero();

        // check with mocked arguments.
        adModel.startTracking(appContext, mock(View.class));
        adModel.stopTracking(appContext, mock(View.class));

        // check with null arguments.
        adModel.startTracking(null, null);
        adModel.stopTracking(null, null);

        // check with combination of null and mocked arguments.
        // case #1
        adModel.startTracking(null, mock(View.class));
        adModel.stopTracking(null, mock(View.class));
        // case #2
        adModel.startTracking(mock(Context.class), null);
        adModel.stopTracking(mock(Context.class), null);
    }
}
