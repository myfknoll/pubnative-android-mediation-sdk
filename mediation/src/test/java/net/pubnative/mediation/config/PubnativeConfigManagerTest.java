package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeConfigManagerTest
{
    Context applicationContext;

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();

        // Clean the manager on every test
        PubnativeConfigManager.setConfigString(this.applicationContext, null);
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
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "valid_config.json");
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, "app_token")).isNotNull();

        // Empty getConfig  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty_config.json");
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, "app_token")).isNull();

        // Empty JSON "{}"  >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "empty.json");
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, "app_token")).isNull();

        // Invalid JSON "{..<useless_data>..}" >> Should return an empty getConfig
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "invalid.json");
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, "app_token")).isNull();

        // Broken JSON "{"
        PubnativeConfigTestUtils.setTestConfig(this.applicationContext, "broken.json");
        assertThat(PubnativeConfigManager.getConfig(this.applicationContext, "app_token")).isNull();
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
        PubnativeConfigManager.setConfigString(this.applicationContext, config1String);
        String stored_config_1 = PubnativeConfigManager.getConfigString(this.applicationContext);
        PubnativeConfigManager.setConfigString(this.applicationContext, config2String);
        String stored_config_2 = PubnativeConfigManager.getConfigString(this.applicationContext);
        assertThat(stored_config_1).isNotEqualTo(stored_config_2);
    }

    @Test
    public void setConfigWorks()
    {
        String configMockString = "getConfig";
        // setConfigString is able to set the getConfig and sets the correct one
        PubnativeConfigManager.setConfigString(this.applicationContext, configMockString);
        assertThat(PubnativeConfigManager.hasConfig(this.applicationContext)).isTrue();
        assertThat(PubnativeConfigManager.getConfigString(this.applicationContext)).isEqualTo(configMockString);

        // setConfigString is able to remove the current string when there is already one
        PubnativeConfigManager.setConfigString(this.applicationContext, "");
        assertThat(PubnativeConfigManager.hasConfig(this.applicationContext)).isFalse();

        // setConfigString is able to remove the current string when there is already one
        PubnativeConfigManager.setConfigString(this.applicationContext, null);
        assertThat(PubnativeConfigManager.hasConfig(this.applicationContext)).isFalse();
    }

    // TODO: Do some multithreading tests over getConfig
}
