package net.pubnative.mediation.model.network;

import android.content.Context;
import android.view.View;

import net.pubnative.library.model.AppDetailsModel;
import net.pubnative.library.model.NativeAdModel;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;

/**
 * Created by rahul on 13/8/15.
 */
public class PubnativeLibraryAdModelTest {

    @Test
    public void getMethodsDoNotFailWhenNullValueSetToVariables()
    {
        NativeAdModel nativeAdModelMock = mock(NativeAdModel.class);

        nativeAdModelMock.title = null;
        nativeAdModelMock.iconUrl = null;
        nativeAdModelMock.bannerUrl = null;
        nativeAdModelMock.description = null;
        nativeAdModelMock.ctaText = null;
        nativeAdModelMock.app_details = null;

        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModelMock));

        assertThat(adModelSpy.getTitle()).isNull();
        assertThat(adModelSpy.getIconUrl()).isNull();
        assertThat(adModelSpy.getBannerUrl()).isNull();
        assertThat(adModelSpy.getDescription()).isNull();
        assertThat(adModelSpy.getCallToAction()).isNull();
        assertThat(adModelSpy.getStarRating()).isZero();
    }

    @Test
    public void getMethodsRetrievesContentsWhenValidValueSetToVariables()
    {
        NativeAdModel nativeAdModelMock = mock(NativeAdModel.class);

        String sampleContent = "sample_content";
        nativeAdModelMock.title = sampleContent;
        nativeAdModelMock.iconUrl = sampleContent;
        nativeAdModelMock.bannerUrl = sampleContent;
        nativeAdModelMock.description = sampleContent;
        nativeAdModelMock.ctaText = sampleContent;
        nativeAdModelMock.app_details = mock(AppDetailsModel.class);
        nativeAdModelMock.app_details.store_rating = 1;

        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModelMock));

        assertThat(adModelSpy.getTitle()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getIconUrl()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getBannerUrl()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getDescription()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getCallToAction()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getStarRating()).isEqualTo(1);
    }

    @Test
    public void getMethodsRetrievesContentsWhenEmptyValueSetToVariables()
    {
        NativeAdModel nativeAdModelMock = mock(NativeAdModel.class);

        String emptyContent = "";
        nativeAdModelMock.title = emptyContent;
        nativeAdModelMock.iconUrl = emptyContent;
        nativeAdModelMock.bannerUrl = emptyContent;
        nativeAdModelMock.description = emptyContent;
        nativeAdModelMock.ctaText = emptyContent;
        nativeAdModelMock.app_details = mock(AppDetailsModel.class);

        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModelMock));

        assertThat(adModelSpy.getTitle()).isEqualTo(emptyContent);
        assertThat(adModelSpy.getIconUrl()).isEqualTo(emptyContent);
        assertThat(adModelSpy.getBannerUrl()).isEqualTo(emptyContent);
        assertThat(adModelSpy.getDescription()).isEqualTo(emptyContent);
        assertThat(adModelSpy.getCallToAction()).isEqualTo(emptyContent);
        assertThat(adModelSpy.getStarRating()).isZero();
    }

    @Test
    public void viewRelatedMethodsDoNotFailWhenNotNullNativeAdObjectGiven()
    {
        // check with a mocked NativeAdModel object
        viewRelatedMethodsDoNotFailWithGivenNativeAdModel(mock(NativeAdModel.class));
    }

    @Test
    public void viewRelatedMethodsDoNotFailWhenNullNativeAdObjectGiven()
    {
        // check with a null NativeAdModel object
        viewRelatedMethodsDoNotFailWithGivenNativeAdModel(null);
    }

    public void viewRelatedMethodsDoNotFailWithGivenNativeAdModel(NativeAdModel nativeAdModel)
    {
        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModel));

        if (nativeAdModel != null)
        {
            doAnswer(new Answer()
            {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable
                {
                    // this mocks the api call to Pubnative SDK
                    return null;
                }
            }).when(nativeAdModel).confirmImpressionAutomatically(any(Context.class), any(View.class));
        }

        // check with mocked arguments.
        adModelSpy.registerAdView(mock(Context.class), mock(View.class));
        adModelSpy.unregisterAdView(mock(Context.class), mock(View.class));

        // check with null arguments.
        adModelSpy.registerAdView(null, null);
        adModelSpy.unregisterAdView(null, null);

        // check with combination of null and mocked arguments.
        // case #1
        adModelSpy.registerAdView(null, mock(View.class));
        adModelSpy.unregisterAdView(null, mock(View.class));
        // case #2
        adModelSpy.registerAdView(mock(Context.class), null);
        adModelSpy.unregisterAdView(mock(Context.class), null);
    }
}
