package net.pubnative.mediation.config;

import android.content.Context;

import com.google.gson.Gson;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.model.PubnativeConfigModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
        PubnativeConfigManager.setConfig(applicationContext, null);
        assertThat(PubnativeConfigManager.hasConfig(applicationContext)).isFalse();
    }

    @Test
    public void configTestDefault()
    {
        // config method returns at least the default config
        PubnativeConfigModel model = PubnativeConfigManager.config(applicationContext);
        assertThat(model).isNotNull();
        assertThat(PubnativeConfigManager.hasConfig(applicationContext)).isTrue();
    }

    @Test
    public void configTestReturnsLastConfig()
    {
        Gson gson = new Gson();
        PubnativeConfigModel setModel = mock(PubnativeConfigModel.class);
        setModel.version = 33;
        setModel.fallback_network = "test_network";
        setModel.networks = new ArrayList<>();
        PubnativeConfigManager.setConfig(applicationContext, gson.toJson(setModel, PubnativeConfigModel.class));
        String getModelString = PubnativeConfigManager.getConfig(applicationContext);
        PubnativeConfigModel getModel = PubnativeConfigManager.config(applicationContext);
        assertThat(getModel.version).isEqualTo(setModel.version);
        assertThat(getModel.fallback_network).isEqualTo(setModel.fallback_network);
        assertThat(getModel.networks).isEqualTo(setModel.networks);
    }

    @Test
    public void getConfigTest()
    {
        // getConfig returns null when the config is not set
        String storedConfig = PubnativeConfigManager.getConfig(applicationContext);
        assertThat(storedConfig).isNull();

        // getConfig returns the latest setted config
        String config1String = "config1";
        String config2String = "config2";
        PubnativeConfigManager.setConfig(applicationContext, config1String);
        String stored_config_1 = PubnativeConfigManager.getConfig(applicationContext);
        PubnativeConfigManager.setConfig(applicationContext, config2String);
        String stored_config_2 = PubnativeConfigManager.getConfig(applicationContext);
        assertThat(stored_config_1).isNotEqualTo(stored_config_2);
    }

    @Test
    public void setConfigTest()
    {
        String configMockString = "config";
        // setConfig is able to set the config and sets the correct one
        PubnativeConfigManager.setConfig(applicationContext, configMockString);
        assertThat(PubnativeConfigManager.hasConfig(applicationContext)).isTrue();
        assertThat(PubnativeConfigManager.getConfig(applicationContext)).isEqualTo(configMockString);

        // setConfig is able to remove the current string when there is already one
        PubnativeConfigManager.setConfig(applicationContext, null);
        assertThat(PubnativeConfigManager.hasConfig(applicationContext)).isFalse();
    }

    // TODO: Do some multithreading tests over config
}
