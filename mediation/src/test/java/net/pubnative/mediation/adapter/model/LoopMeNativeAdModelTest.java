package net.pubnative.mediation.adapter.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by rahul on 17/9/15.
 */
public class LoopMeNativeAdModelTest
{
    @Test
    public void methodsDoNotFailWithEmptyAdModel()
    {
        LoopMeNativeAdModel adModel = spy(LoopMeNativeAdModel.class);

        // verify getter methods returns null
        assertThat(adModel.getTitle()).isNull();
        assertThat(adModel.getIconUrl()).isNull();
        assertThat(adModel.getBannerUrl()).isNull();
        assertThat(adModel.getDescription()).isNull();
        assertThat(adModel.getCallToAction()).isNull();
        assertThat(adModel.getStarRating()).isZero();

        // check with null arguments.
        adModel.startTracking(null, null);
        adModel.stopTracking(null, null);

        // testing startTracking with a mocked View crashes the tests
        // since impression tracking on a mocked view is impossible.
    }
}
