package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;

import net.pubnative.mediation.model.PubnativeConfigModel;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;


import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeConfigManagerTest
{
    Context applicationContext;
    static final String TEST_APP_TOKEN = "app_token_value";

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();

        // Clean the manager on every test
        PubnativeConfigManager.setConfigString(this.applicationContext, null, null);
    }

    @Test
    public void configWithNullString()
    {
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, "app_token")).isNull();
    }

    @Test
    public void configWithDifferentJSONs()
    {
        // Valid getConfig  >> Should return an initializedConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN)).isNotNull();

        // Empty getConfig  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty_config.json", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN)).isNull();

        // Empty JSON "{}"  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty.json", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN)).isNull();

        // Invalid JSON "{..<useless_data>..}" >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "invalid.json", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN)).isNull();

        // Broken JSON "{"
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "broken.json", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN)).isNull();
        // We can set a config and returns the same that has been set

    }

    @Test
    public void getConfigWorks()
    {
        // getConfigString returns null when the getConfig is not set
        String storedConfig = PubnativeConfigManager.getConfigString(this.applicationContext);
        assertThat(storedConfig).isNull();

        // getConfigString returns the latest setted getConfig
        String config1String = "config1";
        String config2String = "config2";

        PubnativeConfigManager.setConfigString(this.applicationContext, config1String, TEST_APP_TOKEN);
        String stored_config_1 = PubnativeConfigManager.getConfigString(this.applicationContext);
        PubnativeConfigManager.setConfigString(this.applicationContext, config2String, TEST_APP_TOKEN);
        String stored_config_2 = PubnativeConfigManager.getConfigString(this.applicationContext);


        assertThat(stored_config_1).isNotEqualTo(stored_config_2);
    }

    @Test
    public void setConfigWorks()
    {
        String configMockString = "getConfig";
        // setConfigString is able to set the getConfig and sets the correct one

        PubnativeConfigManager.setConfigString(this.applicationContext, configMockString, TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.hasConfig(this.applicationContext)).isTrue();
        assertThat(PubnativeConfigManager.getConfigString(this.applicationContext)).isEqualTo(configMockString);

        // setConfigString is able to set the config and sets the correct one
        PubnativeConfigManager.setConfigString(this.applicationContext, configMockString, TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.hasConfig(this.applicationContext)).isTrue();
        assertThat(PubnativeConfigManager.getConfigString(this.applicationContext)).isEqualTo(configMockString);

        // setConfigString is able to remove the current string when there is already one

        PubnativeConfigManager.setConfigString(this.applicationContext, "", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.hasConfig(this.applicationContext)).isFalse();

        // setConfigString is able to remove the current string when there is already one
        PubnativeConfigManager.setConfigString(this.applicationContext, null, TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.hasConfig(this.applicationContext)).isFalse();

    }

    // TODO: Do some multithreading tests over config

    @Test
    public void getAppTokenWorks()
    {
        PubnativeConfigManager.setConfigString(this.applicationContext, "config", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.getAppToken(this.applicationContext)).isEqualTo(TEST_APP_TOKEN);

    }

    @Test
    public void getTimeStampWorks()
    {
        PubnativeConfigManager.setConfigString(this.applicationContext, "config", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.getLastStoredTimestamp(applicationContext)).isNotNull();
    }

    @Test
    public void checkNullAndEmptyAppToken()
    {
        String sampleConfig = "config1";
        PubnativeConfigManager.setConfigString(this.applicationContext, sampleConfig, null);
        assertThat(PubnativeConfigManager.getAppToken(applicationContext)).isNull();

        PubnativeConfigManager.setConfigString(this.applicationContext, sampleConfig, "");
        assertThat(PubnativeConfigManager.getAppToken(this.applicationContext)).isNull();
    }

    @Test
    public void checkTimeStampExpiredWorks()
    {
        Long currentTime = null;
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json", TEST_APP_TOKEN);
        PubnativeConfigModel configModel = PubnativeConfigManager.getConfig(this.applicationContext, TEST_APP_TOKEN);
        if (configModel != null) {
            //setting current time to sum of current time and refresh period
            currentTime = TimeUnit.MINUTES.toMillis((long)(double) configModel.config.get("refresh")) + System.currentTimeMillis();
            assertThat(PubnativeConfigManager.hasTimeStampExpired(this.applicationContext, currentTime)).isTrue();

            //setting current time to 5 minutes less than refresh period
            currentTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis((long) (double) configModel.config.get("refresh") - 5);
            assertThat(PubnativeConfigManager.hasTimeStampExpired(this.applicationContext, currentTime)).isFalse();
        }

    }

    @Test
    public void checkAppTokenComparisonWorks()
    {
        PubnativeConfigManager.setConfigString(this.applicationContext, "config", TEST_APP_TOKEN);

        assertThat(PubnativeConfigManager.hasStoredAppToken(this.applicationContext, TEST_APP_TOKEN)).isTrue();

        assertThat(PubnativeConfigManager.hasStoredAppToken(this.applicationContext, "app")).isFalse();

    }

    @Test
    public void checkAppTokenComparisonEmptyToken()
    {
        PubnativeConfigManager.setConfigString(this.applicationContext, "config1", TEST_APP_TOKEN);
        assertThat(PubnativeConfigManager.hasStoredAppToken(this.applicationContext, "")).isFalse();

        assertThat(PubnativeConfigManager.hasStoredAppToken(this.applicationContext, null)).isFalse();
    }

    // TODO: Do some multithreading tests over getConfig
}
