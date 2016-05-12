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

package net.pubnative.mediation.insights;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeInsightsManagerTest {

    private Context                   appContext       = null;

    @Before
    public void setUp() {
        appContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void pendingAndFailedQueueIsEmptyAtBeginning() {
        // assert that the failed items queue is empty at the beginning
        assertThat(PubnativeInsightsManager.dequeueInsightItem(appContext, PubnativeInsightsManager.INSIGHTS_FAILED_DATA)).isNull();
        // assert that the pending items queue is empty at the beginning
        assertThat(PubnativeInsightsManager.dequeueInsightItem(appContext, PubnativeInsightsManager.INSIGHTS_PENDING_DATA)).isNull();
    }

    @Test
    public void trackDataWithNullContext() {
        PubnativeInsightsManager.trackData(null, null, null, null);
    }

    @Test
    public void trackDataWithInvalidUrl() {
        PubnativeInsightsManager.trackData(appContext, null, null, null);
    }

    @Test
    public void trackData() {
        PubnativeInsightDataModel dataModel = new PubnativeInsightDataModel();
        dataModel.fillDefaults(appContext);
        PubnativeInsightsManager.trackData(appContext, "http://www.google.com", null, dataModel);
        assertThat(PubnativeInsightsManager.dequeueInsightItem(appContext, PubnativeInsightsManager.INSIGHTS_PENDING_DATA)).isNull();
    }
}
