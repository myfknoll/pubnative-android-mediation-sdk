// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.view.View;

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.mediation.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PubnativeLibraryAdModelTest {

    @Test
    public void getMethodsDoNotFailWhenNullValueSetToVariables() {
        NativeAdModel nativeAdModelMock = spy(NativeAdModel.class);

        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModelMock));

        assertThat(adModelSpy.getTitle()).isNull();
        assertThat(adModelSpy.getIconUrl()).isNull();
        assertThat(adModelSpy.getBannerUrl()).isNull();
        assertThat(adModelSpy.getDescription()).isNull();
        assertThat(adModelSpy.getCallToAction()).isNull();
        assertThat(adModelSpy.getStarRating()).isZero();
    }

    @Test
    public void getMethodsRetrievesContentsWhenValidValueSetToVariables() {
        NativeAdModel nativeAdModelMock = spy(NativeAdModel.class);

        String sampleContent = "sample_content";
        nativeAdModelMock.title = sampleContent;
        nativeAdModelMock.iconUrl = sampleContent;
        nativeAdModelMock.bannerUrl = sampleContent;
        nativeAdModelMock.description = sampleContent;
        nativeAdModelMock.ctaText = sampleContent;
        nativeAdModelMock.store_rating = 1f;

        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModelMock));

        assertThat(adModelSpy.getTitle()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getIconUrl()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getBannerUrl()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getDescription()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getCallToAction()).isEqualTo(sampleContent);
        assertThat(adModelSpy.getStarRating()).isEqualTo(1f);
    }

    @Test
    public void getMethodsRetrievesContentsWhenEmptyValueSetToVariables() {
        NativeAdModel nativeAdModelMock = spy(NativeAdModel.class);

        String emptyString = "";
        nativeAdModelMock.title = emptyString;
        nativeAdModelMock.iconUrl = emptyString;
        nativeAdModelMock.bannerUrl = emptyString;
        nativeAdModelMock.description = emptyString;
        nativeAdModelMock.ctaText = emptyString;
        nativeAdModelMock.store_rating = 0f;

        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModelMock));

        assertThat(adModelSpy.getTitle()).isEqualTo(emptyString);
        assertThat(adModelSpy.getIconUrl()).isEqualTo(emptyString);
        assertThat(adModelSpy.getBannerUrl()).isEqualTo(emptyString);
        assertThat(adModelSpy.getDescription()).isEqualTo(emptyString);
        assertThat(adModelSpy.getCallToAction()).isEqualTo(emptyString);
        assertThat(adModelSpy.getStarRating()).isZero();
    }

    @Test
    public void viewRelatedMethodsDoNotFailWhenNotNullNativeAdObjectGiven() {
        // check with a mocked NativeAdModel object
        viewRelatedMethodsDoNotFailWithGivenNativeAdModel(mock(NativeAdModel.class));
    }

    @Test
    public void viewRelatedMethodsDoNotFailWhenNullNativeAdObjectGiven() {
        // check with a null NativeAdModel object
        viewRelatedMethodsDoNotFailWithGivenNativeAdModel(null);
    }

    public void viewRelatedMethodsDoNotFailWithGivenNativeAdModel(NativeAdModel nativeAdModel) {
        Context                 appContext = RuntimeEnvironment.application.getApplicationContext();
        PubnativeLibraryAdModel adModelSpy = spy(new PubnativeLibraryAdModel(nativeAdModel));

        if (nativeAdModel != null) {
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    // this mocks the api call to Pubnative SDK
                    return null;
                }
            }).when(nativeAdModel).confirmImpressionAutomatically(any(Context.class), any(View.class));
        }

        // check with mocked arguments.
        adModelSpy.startTracking(appContext, mock(View.class));
        adModelSpy.stopTracking(appContext, mock(View.class));

        // check with null arguments.
        adModelSpy.startTracking(null, null);
        adModelSpy.stopTracking(null, null);

        // check with combination of null and mocked arguments.
        // case #1
        adModelSpy.startTracking(null, mock(View.class));
        adModelSpy.stopTracking(null, mock(View.class));
        // case #2
        adModelSpy.startTracking(mock(Context.class), null);
        adModelSpy.stopTracking(mock(Context.class), null);
    }
}
