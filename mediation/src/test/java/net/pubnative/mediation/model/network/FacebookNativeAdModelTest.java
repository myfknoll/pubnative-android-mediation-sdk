package net.pubnative.mediation.model.network;

import android.content.Context;
import android.view.View;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;

/**
 * Created by rahul on 13/8/15.
 */
public class FacebookNativeAdModelTest {

    @Test
    public void checkWithNullNativeAdDoNotCrash()
    {
        FacebookNativeAdModel model = spy(new FacebookNativeAdModel(null));

        assertThat(model.getTitle()).isNull();
        assertThat(model.getIconUrl()).isNull();
        assertThat(model.getBannerUrl()).isNull();
        assertThat(model.getDescription()).isNull();
        assertThat(model.getCallToAction()).isNull();
        assertThat(model.getStarRating()).isEqualTo(0.0f);

        // check with mocked arguments.
        model.registerAdView(mock(Context.class), mock(View.class));
        model.unregisterAdView(mock(Context.class), mock(View.class));

        // check with null arguments.
        model.registerAdView(null, null);
        model.unregisterAdView(null, null);

        // check with combination of null and mocked arguments.
        // case #1
        model.registerAdView(null, mock(View.class));
        model.unregisterAdView(null, mock(View.class));
        // case #2
        model.registerAdView(mock(Context.class), null);
        model.unregisterAdView(mock(Context.class), null);
    }
}
