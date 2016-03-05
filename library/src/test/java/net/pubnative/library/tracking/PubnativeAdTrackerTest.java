// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.library.tracking;

import android.content.Context;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdTrackerTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void testImpressionSuccessForNullListener() {

        View                        adView      = spy(new View(applicationContext));
        PubnativeImpressionTracker tracker     = spy(new PubnativeImpressionTracker(adView, adView, "http://www.google.com", "http://www.google.com", null));

        tracker.invokeOnTrackerImpression();
    }

    @Test
    public void testOnImpressionConfirmed() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));
        PubnativeImpressionTracker.Listener listener        = mock(PubnativeImpressionTracker.Listener.class);

        PubnativeImpressionTracker tracker         = spy(new PubnativeImpressionTracker(adView, clickableView, "http://www.google.com", "http://www.google.com", listener));

        tracker.invokeOnTrackerImpression();
        verify(listener, times(1)).onTrackerImpression(eq(adView));
    }

    @Test
    public void testInvalidClickUrl() {

        View                        adView      = spy(new View(applicationContext));
        PubnativeImpressionTracker.Listener listener    = mock(PubnativeImpressionTracker.Listener.class);

        PubnativeImpressionTracker tracker     = spy(new PubnativeImpressionTracker(adView, adView, "http://www.google.com", "", listener));

        tracker.handleClickEvent();
    }

    @Test
    public void testValidClickListener() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));

        PubnativeImpressionTracker tracker         = spy(new PubnativeImpressionTracker(adView, clickableView, "http://www.google.com", "http://www.google.com", null));
        tracker.startTracking();
        verify(clickableView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testInValidClickListener() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));

        PubnativeImpressionTracker tracker         = spy(new PubnativeImpressionTracker(adView, clickableView, "http://www.google.com", "http://www.google.com", null));
        tracker.startTracking();
        verify(adView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testOnClickConfirmed() {

        View                        adView          = spy(new View(applicationContext));
        View                        clickableView   = spy(new View(applicationContext));
        PubnativeImpressionTracker.Listener listener        = mock(PubnativeImpressionTracker.Listener.class);

        PubnativeImpressionTracker tracker         = spy(new PubnativeImpressionTracker(adView, clickableView, "http://www.google.com", "http://www.google.com", listener));

        tracker.invokeOnTrackerClick();
        verify(listener, times(1)).onTrackerClick(eq(clickableView));
    }
}
