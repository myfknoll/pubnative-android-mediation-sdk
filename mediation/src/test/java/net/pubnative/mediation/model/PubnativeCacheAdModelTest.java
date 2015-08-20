package net.pubnative.mediation.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by davidmartin on 20/08/15.
 */
public class PubnativeCacheAdModelTest
{
    private static final String TITLE_VALUE          = "title";
    private static final String DESCRIPTION_VALUE    = "description";
    private static final String ICON_URL_VALUE       = "icon_url";
    private static final String BANNER_URL_VALUE     = "banner_url";
    private static final String CALL_TO_ACTION_VALUE = "call_to_action";
    private static final Float  STAR_RATING_VALUE    = 5.0f;


    @Test
    public void wrapsNullModel()
    {
        PubnativeCacheAdModel modelSpy = spy(new PubnativeCacheAdModel(null));
        assertThat(modelSpy.getTitle()).isNull();
        assertThat(modelSpy.getDescription()).isNull();
        assertThat(modelSpy.getIconUrl()).isNull();
        assertThat(modelSpy.getBannerUrl()).isNull();
        assertThat(modelSpy.getCallToAction()).isNull();
        assertThat(modelSpy.getStarRating()).isZero();
    }

    @Test
    public void wrapsModelValues()
    {
        PubnativeAdModel modelMock = mock(PubnativeAdModel.class);
        when(modelMock.getTitle()).thenReturn(TITLE_VALUE);
        when(modelMock.getDescription()).thenReturn(DESCRIPTION_VALUE);
        when(modelMock.getIconUrl()).thenReturn(ICON_URL_VALUE);
        when(modelMock.getBannerUrl()).thenReturn(BANNER_URL_VALUE);
        when(modelMock.getCallToAction()).thenReturn(CALL_TO_ACTION_VALUE);
        when(modelMock.getStarRating()).thenReturn(STAR_RATING_VALUE);

        PubnativeCacheAdModel modelSpy = spy(new PubnativeCacheAdModel(modelMock));
        assertThat(modelSpy.getTitle()).isEqualTo(TITLE_VALUE);
        assertThat(modelSpy.getDescription()).isEqualTo(DESCRIPTION_VALUE);
        assertThat(modelSpy.getIconUrl()).isEqualTo(ICON_URL_VALUE);
        assertThat(modelSpy.getBannerUrl()).isEqualTo(BANNER_URL_VALUE);
        assertThat(modelSpy.getCallToAction()).isEqualTo(CALL_TO_ACTION_VALUE);
        assertThat(modelSpy.getStarRating()).isEqualTo(STAR_RATING_VALUE);
    }
}
