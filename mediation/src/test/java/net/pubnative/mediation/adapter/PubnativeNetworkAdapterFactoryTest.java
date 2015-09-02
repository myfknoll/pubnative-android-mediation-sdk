package net.pubnative.mediation.adapter;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.model.PubnativeNetworkModel;
import net.pubnative.mediation.utils.PubnativeTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Created by davidmartin on 28/07/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PubnativeNetworkAdapterFactoryTest
{
    private PubnativeNetworkModel model;


    @Test
    public void test_empty()
    {
        assertThat(true).isTrue();
    }

    @Before
    public void setUp()
    {
        model = new PubnativeNetworkModel();
    }

    @Test
    public void createsAdaptersForPresentAdapters()
    {
        List<String> adapters = PubnativeTestUtils.getClassesPackages(PubnativeNetworkAdapterFactory.NETWORK_PACKAGE);
        for (String adapterName : adapters)
        {
            model = new PubnativeNetworkModel();
            model.adapter = adapterName;
            model.params = mock(HashMap.class);
            PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
            try
            {
                assertThat(adapterInstance).isInstanceOf(Class.forName(PubnativeNetworkAdapterFactory.getPackageName(adapterName)));
            }
            catch (ClassNotFoundException e)
            {
                fail("PubnativeNetworkAdapterFactory should be able to create all the network classes given the name");
            }
        }
    }

    @Test
    public void createAdapterWithInvalidClassString()
    {
        model.adapter = "invalid_class_string";
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void createAdapterWithEmptyString()
    {
        model.adapter = "";
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void createAdapterWithNullString()
    {
        model.adapter = null;
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(model);
        assertThat(adapterInstance).isNull();
    }
}
