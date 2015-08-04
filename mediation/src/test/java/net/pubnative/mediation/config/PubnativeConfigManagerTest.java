package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;


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
    public void configWithNullString()
    {
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE)).isNull();
    }

    @Test
    public void configWithDifferentJSONs()
    {
        // Valid getConfig  >> Should return an initializedConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getConfigString(this.applicationContext)).isNotNull();
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE)).isNotNull();
        assertThat(PubnativeConfigManager.getTimestamp(this.applicationContext)).isNotNull();
        assertThat(PubnativeConfigManager.getAppToken(this.applicationContext)).isNotNull();
        assertThat(PubnativeConfigManager.getRefresh(this.applicationContext)).isNotNull();

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
        assertThat(PubnativeConfigManager.getConfigString(this.applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getTimestamp(this.applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getAppToken(this.applicationContext)).isNull();
        assertThat(PubnativeConfigManager.getRefresh(this.applicationContext)).isNull();
    }

    @Test
    public void configNullOnStart()
    {
        assertThat(PubnativeConfigManager.getConfigString(this.applicationContext)).isNull();
    }

    @Test
    public void configSetCorrectly()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getConfigString(this.applicationContext)).isNotNull();
    }

    @Test
    public void appTokenNullOnStart()
    {
        assertThat(PubnativeConfigManager.getAppToken(this.applicationContext)).isNull();
    }

    @Test
    public void appTokenSetOnSetConfigString()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getAppToken(this.applicationContext)).isEqualTo(TEST_APP_TOKEN_VALUE);
    }

    @Test
    public void timestampIsNullOnStart()
    {
        assertThat(PubnativeConfigManager.getTimestamp(this.applicationContext)).isNull();
    }

    @Test
    public void timestampSetOnSetConfigString()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        assertThat(PubnativeConfigManager.getTimestamp(this.applicationContext)).isNotNull();
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
    public void needsUpdateForAllCases()
    {
        // TODO: Write tests
        // 1. empty configString
        // 2. empty app token
        // 3. different app token
        // 4. empty refresh
        // 5. empty timestamp
        // 6. elapsedTime is bigger than refresh
    }

    @Test
    public void noDownloadForValidValues()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        PowerMockito.spy(PubnativeConfigManager.class);
        PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE);

        PowerMockito.verifyStatic(never());
        PubnativeConfigManager.downloadConfig();
    }

    @Test
    public void downloadsNewConfigWithExpiredTimestamp()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);
        Long refresh = PubnativeConfigManager.getRefresh(this.applicationContext);
        Long overdueTimestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(refresh);
        PubnativeConfigManager.setTimestamp(this.applicationContext, overdueTimestamp);

        PowerMockito.spy(PubnativeConfigManager.class);
        PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN_VALUE);

        PowerMockito.verifyStatic(times(1));
        PubnativeConfigManager.downloadConfig();
    }

    @Test
    public void downloadConfigWithDifferentAppToken()
    {
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, VALID_CONFIG_NAME, TEST_APP_TOKEN_VALUE);

        PowerMockito.spy(PubnativeConfigManager.class);
        PubnativeConfigManager.getConfig(this.applicationContext, "sample_token");

        PowerMockito.verifyStatic(times(1));
        PubnativeConfigManager.downloadConfig();
    }


    // TODO: Do some multithreading tests over getConfig
}
