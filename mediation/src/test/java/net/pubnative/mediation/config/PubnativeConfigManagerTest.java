package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.utils.PubnativeStringUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PubnativeConfigManagerTest
{
    Context applicationContext;

    @Before
    public void setUp()
    {
        applicationContext = RuntimeEnvironment.application.getApplicationContext();

        // Clean the manager on every test
        PubnativeConfigManager.setConfigString(applicationContext, null);
    }

    @Test
    public void test_returnedConfigIsTheLastOne()
    {
        InputStream configStream = this.getClass().getResourceAsStream("/test_config.json");
        assertThat(configStream).isNotNull();
        String configString = PubnativeStringUtils.readTextFromInputStream(configStream);
        assertThat(configString).isNotNull().isNotEmpty();

        // We can set a config and returns the same that has been set
        PubnativeConfigManager.setConfigString(applicationContext, configString);
        assertThat(PubnativeConfigManager.config(applicationContext)).isNotNull();
        assertThat(PubnativeConfigManager.getConfigString(applicationContext)).isEqualTo(configString);
    }

    @Test
    public void test_getConfigString()
    {
        // getConfigString returns null when the config is not set
        String storedConfig = PubnativeConfigManager.getConfigString(applicationContext);
        assertThat(storedConfig).isNull();

        // getConfigString returns the latest setted config
        String config1String = "config1";
        String config2String = "config2";
        PubnativeConfigManager.setConfigString(applicationContext, config1String);
        String stored_config_1 = PubnativeConfigManager.getConfigString(applicationContext);
        PubnativeConfigManager.setConfigString(applicationContext, config2String);
        String stored_config_2 = PubnativeConfigManager.getConfigString(applicationContext);
        assertThat(stored_config_1).isNotEqualTo(stored_config_2);
    }

    @Test
    public void test_setConfigString()
    {
        String configMockString = "config";
        // setConfigString is able to set the config and sets the correct one
        PubnativeConfigManager.setConfigString(applicationContext, configMockString);
        assertThat(PubnativeConfigManager.hasConfig(applicationContext)).isTrue();
        assertThat(PubnativeConfigManager.getConfigString(applicationContext)).isEqualTo(configMockString);

        // setConfigString is able to remove the current string when there is already one
        PubnativeConfigManager.setConfigString(applicationContext, null);
        assertThat(PubnativeConfigManager.hasConfig(applicationContext)).isFalse();
    }

    // TODO: Do some multithreading tests over config
}
