package net.pubnative.mediation.model.network;

import android.content.Context;
import android.view.View;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by rahul on 26/8/15.
 */
public class FlurryNativeAdModelTest
{
    @Test
    public void methodsDoNotFailWhenNullAdPassesIn()
    {
        FlurryNativeAdModel adModel = spy(new FlurryNativeAdModel(null));

        // verify getter methods returns null
        assertThat(adModel.getTitle()).isNull();
        assertThat(adModel.getIconUrl()).isNull();
        assertThat(adModel.getBannerUrl()).isNull();
        assertThat(adModel.getDescription()).isNull();
        assertThat(adModel.getCallToAction()).isNotNull(); // it will give u "Read more" as output
        assertThat(adModel.getStarRating()).isZero();

        // check with mocked arguments.
        adModel.registerAdView(mock(Context.class), mock(View.class));
        adModel.unregisterAdView(mock(Context.class), mock(View.class));

        // check with null arguments.
        adModel.registerAdView(null, null);
        adModel.unregisterAdView(null, null);

        // check with combination of null and mocked arguments.
        // case #1
        adModel.registerAdView(null, mock(View.class));
        adModel.unregisterAdView(null, mock(View.class));
        // case #2
        adModel.registerAdView(mock(Context.class), null);
        adModel.unregisterAdView(mock(Context.class), null);
    }

    /**
     * No more tests possible by mock/spy FlurryAdNative.
     * Gives me error as Facebook's NativeAd
     */
}
