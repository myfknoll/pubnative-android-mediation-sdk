package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.model.PubnativeConfigModel;
import net.pubnative.mediation.utils.PubnativeConfigTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
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
        PubnativeConfigManager.updateConfigString(this.applicationContext, null, null);
    }

    @Test
    public void configWithDifferentJSONs()
    {
        // Valid getConfig  >> Should return an initializedConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getStoredConfigString(this.applicationContext)).isNotNull();
        assertThat(PubnativeConfigManager.getStoredTimestamp(this.applicationContext)).isNotNull();
        assertThat(PubnativeConfigManager.getStoredAppToken(this.applicationContext)).isNotNull();
        assertThat(PubnativeConfigManager.getStoredRefresh(this.applicationContext)).isNotNull();

        PubnativeConfigManagerListener listenerSpy = spy(PubnativeConfigManagerListener.class);
        PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE, listenerSpy);
        verify(listenerSpy, times(1)).onConfigLoaded(any(PubnativeConfigModel.class));

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
        String sampleConfig = "sampleConfigString";

        PubnativeConfigManager.updateConfigString(null, sampleConfig, TEST_APP_TOKEN_VALUE);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfigString(this.applicationContext, sampleConfig, null);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfigString(this.applicationContext, sampleConfig, "");
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfigString(this.applicationContext, "", TEST_APP_TOKEN_VALUE);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfigString(this.applicationContext, null, TEST_APP_TOKEN_VALUE);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfigString(this.applicationContext, null, null);
        this.storedDataIsNull();

        PubnativeConfigManager.updateConfigString(this.applicationContext, "", null);
        this.storedDataIsNull();
    }

    @Test
    public void verifyUpdateCasesForDifferentTimestamps()
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
    public void verifyUpdateCasesForDifferentRefreshValues()
    {
        // setting a valid config for testing purpose.
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        // refresh set to null
        PubnativeConfigManager.setStoredRefresh(this.applicationContext, null);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        // refresh not set or set as zero
        PubnativeConfigManager.setStoredRefresh(this.applicationContext, (long) 0);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        // negative refresh value
        PubnativeConfigManager.setStoredRefresh(this.applicationContext, (long) -1);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        // positive refresh value - should not update
        PubnativeConfigManager.setStoredRefresh(this.applicationContext, (long) 10);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isFalse();

    }

    @Test
    public void verifyUpdateCasesForDifferentAppTokens()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        // no valid app_token found.
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, "")).isTrue();
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, null)).isTrue();
        //no update for same app_token
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isFalse();

        //app_token empty at the time of setting a config
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, "");
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();
    }

    @Test
    public void noUpdateForNullContext()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.configNeedsUpdate(null, TEST_APP_TOKEN_VALUE)).isFalse();
    }

    @Test
    public void needsUpdateForEmptyConfig()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "", TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();

        PubnativeConfigManager.updateConfigString(this.applicationContext, null, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();
    }

    @Test
    public void verifyUpdateCasesForDifferentConfigStringValues()
    {
        //no update for valid config string
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isFalse();

        //update for invalid config string
        PubnativeConfigManager.updateConfigString(this.applicationContext, "config", TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();
    }

    @Test
    public void noDownloadForEmptyAppToken()
    {
        //app token empty at time of getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, "");
        PubnativeConfigManagerListener listenerSpy = spy(PubnativeConfigManagerListener.class);

        PowerMockito.spy(PubnativeConfigManager.class);
        PubnativeConfigManager.getConfig(this.applicationContext, "", listenerSpy);
        PubnativeConfigManager.getConfig(this.applicationContext, null, listenerSpy);

        PowerMockito.verifyStatic(never());
        PubnativeConfigManager.downloadConfig(any(Context.class), anyString());
    }

    @Test
    public void updateNeededForDifferentAppToken()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, "sample_token_value")).isTrue();
    }

    @Test
    public void updateNeededOnTimePassed()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        Long refreshTime = PubnativeConfigManager.getStoredRefresh(this.applicationContext);
        Long storedTime = PubnativeConfigManager.getStoredTimestamp(this.applicationContext);

        storedTime = storedTime - TimeUnit.MINUTES.toMillis(refreshTime);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, storedTime);

        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isTrue();
    }

    @Test
    public void updateNotNeededOnValidTimeWithinRefresh()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        Long refreshTime = PubnativeConfigManager.getStoredRefresh(this.applicationContext);
        Long storedTime = PubnativeConfigManager.getStoredTimestamp(this.applicationContext);

        storedTime = storedTime - TimeUnit.MINUTES.toMillis(refreshTime - 5);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, storedTime);

        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isFalse();
    }

    @Test
    public void updateNotNeededForValidValues()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.configNeedsUpdate(this.applicationContext, TEST_APP_TOKEN_VALUE)).isFalse();
    }

    @Test
    public void noDownloadForValidValues()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        PubnativeConfigManagerListener configListenerSpy = spy(PubnativeConfigManagerListener.class);
        PowerMockito.spy(PubnativeConfigManager.class);
        PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE, configListenerSpy);

        PowerMockito.verifyStatic(never());
        PubnativeConfigManager.downloadConfig(any(Context.class), anyString());
    }

    @Test
    public void downloadsNewConfigWithExpiredTimestamp()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        Long refresh = PubnativeConfigManager.getStoredRefresh(this.applicationContext);
        Long overdueTimestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(refresh);
        PubnativeConfigManager.setStoredTimestamp(this.applicationContext, overdueTimestamp);

        PubnativeConfigManagerListener configListenerSpy = spy(PubnativeConfigManagerListener.class);
        PowerMockito.spy(PubnativeConfigManager.class);
        try
        {
            // Stub the download method so we don't really download nothing
            // just want to verify that this is being called
            PowerMockito.doNothing().when(PubnativeConfigManager.class, "downloadConfig", any(Context.class), anyString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE, configListenerSpy);

        PowerMockito.verifyStatic(times(1));
        PubnativeConfigManager.downloadConfig(any(Context.class), anyString());
    }

    @Test
    public void downloadConfigWithDifferentAppToken()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        PubnativeConfigManagerListener listenerSpy = spy(PubnativeConfigManagerListener.class);

        PowerMockito.spy(PubnativeConfigManager.class);
        try
        {
            // Stub the download method so we don't really download nothing
            // just want to verify that this is being called
            PowerMockito.doNothing().when(PubnativeConfigManager.class, "downloadConfig", any(Context.class), anyString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        PubnativeConfigManager.getConfig(this.applicationContext, "sample_token", listenerSpy);

        PowerMockito.verifyStatic(times(1));
        PubnativeConfigManager.downloadConfig(any(Context.class), anyString());
    }
}
