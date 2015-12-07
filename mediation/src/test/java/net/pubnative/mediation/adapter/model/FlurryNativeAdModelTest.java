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

import net.pubnative.mediation.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FlurryNativeAdModelTest {

    @Test
    public void methodsDoNotFailWhenNullAdPassesIn() {
        Context             appContext = RuntimeEnvironment.application.getApplicationContext();
        FlurryNativeAdModel adModel    = spy(new FlurryNativeAdModel(null));

        // verify getter methods returns null
        assertThat(adModel.getTitle()).isNull();
        assertThat(adModel.getIconUrl()).isNull();
        assertThat(adModel.getBannerUrl()).isNull();
        assertThat(adModel.getDescription()).isNull();
        assertThat(adModel.getCallToAction()).isNotNull(); // it will give u "Read more" as output
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

    /**
     * No more tests possible by mock/spy FlurryAdNative.
     * Gives me error as Facebook's NativeAd
     */
}
