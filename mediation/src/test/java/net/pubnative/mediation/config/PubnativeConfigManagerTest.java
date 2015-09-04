package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.config.model.PubnativeConfigModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(PubnativeConfigManager.class)
public class PubnativeConfigManagerTest
{
    protected Context applicationContext;
    protected static final String TEST_CONFIG_VALUE    = "testConfigValue";
    protected static final String TEST_APP_TOKEN_VALUE = "appTokenValue";
    protected static final String VALID_CONFIG_NAME    = "valid_config.json";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();

        // Clean the manager on every test
        PubnativeConfigManager.clean(this.applicationContext);
    }

    @Test
    public void configWithInvalidJSONs()
    {
        // Empty getConfig  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty_config.json", TEST_APP_TOKEN_VALUE);
        this.storedDataIsNull();

        // Empty JSON "{}"  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty.json", TEST_APP_TOKEN_VALUE);
        this.storedDataIsNull();

        // Invalid JSON "{..<useless_data>..}" >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "invalid.json", TEST_APP_TOKEN_VALUE);
        this.storedDataIsNull();

        // Broken JSON "{"
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "broken.json", TEST_APP_TOKEN_VALUE);
        this.storedDataIsNull();
    }

    @Test
    public void callbackWithValidJSON()
    {
        // Valid getConfig  >> Should return an initializedConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        PubnativeConfigRequestListener listenerSpy = spy(PubnativeConfigRequestListener.class);
        PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE, listenerSpy);
        verify(listenerSpy, times(1)).onConfigLoaded(any(PubnativeConfigModel.class));
    }

    private void storedDataIsNull()
    {
        assertThat(PubnativeConfigManager.getStoredConfigString(this.applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getStoredTimestamp(this.applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getStoredAppToken(this.applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getStoredRefresh(this.applicationContext)).isNull();
    }

    @Test
    public void configNullOnStart()
    {
        assertThat(PubnativeConfigManager.getStoredConfigString(this.applicationContext)).isNull();
    }

    @Test
    public void configSetCorrectly()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getStoredConfigString(this.applicationContext)).isNotNull();
    }

    @Test
    public void appTokenNullOnStart()
    {
        assertThat(PubnativeConfigManager.getStoredAppToken(this.applicationContext)).isNull();
    }

    @Test
    public void appTokenSetOnSetConfigString()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getStoredAppToken(this.applicationContext)).isEqualTo(TEST_APP_TOKEN_VALUE);
    }

    @Test
    public void timestampIsNullOnStart()
    {
        assertThat(PubnativeConfigManager.getStoredTimestamp(this.applicationContext)).isNull();
    }

    @Test
    public void timestampSetOnSetConfigString()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getStoredTimestamp(this.applicationContext)).isNotNull();
    }

    @Test
    public void configNotSetOnEmptyFields()
    {
        PubnativeConfigModel configMock = mock(PubnativeConfigModel.class);

        PubnativeConfigManager.updateConfig(null, TEST_APP_TOKEN_VALUE, configMock);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfig(this.applicationContext, null, null);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfig(this.applicationContext, "", null);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfig(this.applicationContext, null, configMock);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfig(this.applicationContext, "", configMock);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfig(this.applicationContext, TEST_APP_TOKEN_VALUE, null);
        this.storedDataIsNull();
    }

    @Test
    public void configNeedsUpdateForDifferentTimestamps()
    {
        // null timestamp
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, null);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        // timestamp is not set or set to zero.
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, (long) 0);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        // negative timestamp
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, (long) -1);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        // positive timestamp - should not update
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, System.currentTimeMillis());
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isFalse();
    }

    @Test
    public void configNeedsUpdateWithDifferentVales()
    {
        // At start, it needs update
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        // TRUE
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, "")).isTrue(); // empty app_token
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, null)).isTrue(); // null app_token
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, "sample_token_value")).isTrue(); //different app_token
        // FALSE
        assertThat(PubnativeConfigManager.configNeedsUpdate(null, TEST_APP_TOKEN_VALUE)).isFalse(); // null context
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isFalse(); //valid parameters
    }

    @Test
    public void configNeedsUpdateForTimedOutConfig()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        Long refreshTime = PubnativeConfigManager.getStoredRefresh(this.applicationContext);
        Long storedTime = PubnativeConfigManager.getStoredTimestamp(this.applicationContext);

        storedTime = storedTime - TimeUnit.MINUTES.toMillis(refreshTime);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, storedTime);

        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();
    }
}
