package net.pubnative.mediation.model.network;

import android.content.Context;
import android.view.View;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by rahul on 13/8/15.
 */
public class FacebookNativeAdModelTest
{
    @Test
    public void memberFunctionsWithNullNativeAdDoNotFail()
    {
        FacebookNativeAdModel adModel = spy(new FacebookNativeAdModel(null));

        // verify getter methods returns null
        assertThat(adModel.getTitle()).isNull();
        assertThat(adModel.getIconUrl()).isNull();
        assertThat(adModel.getBannerUrl()).isNull();
        assertThat(adModel.getDescription()).isNull();
        assertThat(adModel.getCallToAction()).isNull();
        assertThat(adModel.getStarRating()).isZero();

        // check with mocked arguments.
        adModel.startTracking(mock(Context.class), mock(View.class));
        adModel.stopTracking(mock(Context.class), mock(View.class));

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
