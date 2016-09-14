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

package net.pubnative.mediation.utils;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.config.PubnativeConfigTestUtils;
import net.pubnative.mediation.config.model.PubnativeConfigModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeConfigUtilsTest {

    protected static final String TEST_APP_TOKEN_VALUE = "appTokenValue";

    @Before
    public void setUp() {
        // Clean the manager on every test
        PubnativeConfigUtils.clean(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void updateConfig_withEmptyConfigModel_doNotSetsConfig() {

        PubnativeConfigModel model = PubnativeConfigTestUtils.getTestConfig("empty_config.json");
        PubnativeConfigUtils.updateConfig(RuntimeEnvironment.application.getApplicationContext(), TEST_APP_TOKEN_VALUE, model);
        assertThat(PubnativeConfigUtils.getStoredConfigString(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredTimestamp(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredAppToken(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredRefresh(RuntimeEnvironment.application.getApplicationContext())).isNull();
    }

    @Test
    public void updateConfig_withValidConfig_setsConfigAppTokenRefreshAndTimestamp() {

        PubnativeConfigModel model = PubnativeConfigTestUtils.getTestConfig("valid_config.json");
        PubnativeConfigUtils.updateConfig(RuntimeEnvironment.application.getApplicationContext(), TEST_APP_TOKEN_VALUE, model);
        assertThat(PubnativeConfigUtils.getStoredConfigString(RuntimeEnvironment.application.getApplicationContext())).isNotNull();
        assertThat(PubnativeConfigUtils.getStoredAppToken(RuntimeEnvironment.application.getApplicationContext())).isEqualTo(TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigUtils.getStoredTimestamp(RuntimeEnvironment.application.getApplicationContext())).isNotNull();
        assertThat(PubnativeConfigUtils.getStoredRefresh(RuntimeEnvironment.application.getApplicationContext())).isNotNull();
    }

    @Test
    public void updateConfig_withNullContext_doNotSetsConfig() {

        PubnativeConfigUtils.updateConfig(null, TEST_APP_TOKEN_VALUE, mock(PubnativeConfigModel.class));
        assertThat(PubnativeConfigUtils.getStoredConfigString(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredTimestamp(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredAppToken(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredRefresh(RuntimeEnvironment.application.getApplicationContext())).isNull();
    }

    @Test
    public void updateConfig_withNullAppToken_doNotSetsConfig() {

        PubnativeConfigUtils.updateConfig(RuntimeEnvironment.application.getApplicationContext(), null, mock(PubnativeConfigModel.class));
        assertThat(PubnativeConfigUtils.getStoredConfigString(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredTimestamp(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredAppToken(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredRefresh(RuntimeEnvironment.application.getApplicationContext())).isNull();
    }

    @Test
    public void updateConfig_withEmptyAppToken_doNotSetsConfig() {

        PubnativeConfigUtils.updateConfig(RuntimeEnvironment.application.getApplicationContext(), "", mock(PubnativeConfigModel.class));
        assertThat(PubnativeConfigUtils.getStoredConfigString(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredTimestamp(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredAppToken(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredRefresh(RuntimeEnvironment.application.getApplicationContext())).isNull();
    }

    @Test
    public void updateConfig_withNullConfig_doNotSetsConfig() {

        PubnativeConfigUtils.updateConfig(RuntimeEnvironment.application.getApplicationContext(), "", null);
        assertThat(PubnativeConfigUtils.getStoredConfigString(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredTimestamp(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredAppToken(RuntimeEnvironment.application.getApplicationContext())).isNull();
        assertThat(PubnativeConfigUtils.getStoredRefresh(RuntimeEnvironment.application.getApplicationContext())).isNull();
    }

    @Test
    public void getStoredConfig_withValidConfig_pass() {

        PubnativeConfigModel model = PubnativeConfigTestUtils.getTestConfig("valid_config.json");
        PubnativeConfigUtils.updateConfig(RuntimeEnvironment.application.getApplicationContext(), TEST_APP_TOKEN_VALUE, model);
        PubnativeConfigModel result = PubnativeConfigUtils.getStoredConfig(RuntimeEnvironment.application.getApplicationContext());
        assertThat(result).isNotNull();
    }

    @Test
    public void getStoredConfig_withNullConfig_isNull() {

        PubnativeConfigUtils.updateConfig(RuntimeEnvironment.application.getApplicationContext(), "", null);
        PubnativeConfigModel result = PubnativeConfigUtils.getStoredConfig(RuntimeEnvironment.application.getApplicationContext());
        assertThat(result).isNull();
    }
}
