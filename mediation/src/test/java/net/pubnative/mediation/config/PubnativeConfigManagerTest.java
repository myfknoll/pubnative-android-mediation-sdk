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

package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
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
@Config(constants = BuildConfig.class, sdk = 21)
public class PubnativeConfigManagerTest {

    protected Context applicationContext;
    protected static final String TEST_APP_TOKEN_VALUE = "appTokenValue";
    protected static final String VALID_CONFIG_NAME    = "valid_config.json";

    @Before
    public void setUp() {
        applicationContext = RuntimeEnvironment.application.getApplicationContext();

        // Clean the manager on every test
        PubnativeConfigManager.clean(applicationContext);
    }

    @Test
    public void configWithInvalidJSONs() {
        // Empty getConfig  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(applicationContext, "empty_config.json", TEST_APP_TOKEN_VALUE);
        storedDataIsNull();

        // Empty JSON "{}"  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(applicationContext, "empty.json", TEST_APP_TOKEN_VALUE);
        storedDataIsNull();

        // Invalid JSON "{..<useless_data>..}" >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(applicationContext, "invalid.json", TEST_APP_TOKEN_VALUE);
        storedDataIsNull();

        // Broken JSON "{"
        PubnativeConfigTestUtils.setTestConfig(applicationContext, "broken.json", TEST_APP_TOKEN_VALUE);
        storedDataIsNull();
    }


    private void storedDataIsNull() {
        assertThat(PubnativeConfigManager.getStoredConfigString(applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getStoredTimestamp(applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getStoredAppToken(applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getStoredRefresh(applicationContext)).isNull();
    }

    @Test
    public void configSetCorrectly() {
        PubnativeConfigTestUtils.setTestConfig(applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getStoredConfigString(applicationContext)).isNotNull();
    }

    @Test
    public void appTokenSetOnSetConfigString() {
        PubnativeConfigTestUtils.setTestConfig(applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getStoredAppToken(applicationContext)).isEqualTo(TEST_APP_TOKEN_VALUE);
    }

    @Test
    public void timestampSetOnSetConfigString() {
        PubnativeConfigTestUtils.setTestConfig(applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getStoredTimestamp(applicationContext)).isNotNull();
    }

    @Test
    public void configNotSetOnEmptyFields() {
        PubnativeConfigModel configMock = mock(PubnativeConfigModel.class);

        PubnativeConfigManager.updateConfig(null, TEST_APP_TOKEN_VALUE, configMock);
        storedDataIsNull();

        PubnativeConfigManager.updateConfig(applicationContext, null, null);
        storedDataIsNull();

        PubnativeConfigManager.updateConfig(applicationContext, "", null);
        storedDataIsNull();

        PubnativeConfigManager.updateConfig(applicationContext, null, configMock);
        storedDataIsNull();

        PubnativeConfigManager.updateConfig(applicationContext, "", configMock);
        storedDataIsNull();

        PubnativeConfigManager.updateConfig(applicationContext, TEST_APP_TOKEN_VALUE, null);
        storedDataIsNull();
    }
}
