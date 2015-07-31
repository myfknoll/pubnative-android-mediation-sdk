package net.pubnative.mediation.adapter;

import net.pubnative.mediation.model.PubnativeConfigModel;
import net.pubnative.mediation.utils.PubnativeTestUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeNetworkAdapterFactoryTest
{
    HashMap<String, Object> data;


    @Test
    public void test_empty()
    {
        assertThat(true).isTrue();
    }

    @Before
    public void setUp()
    {
        data = new HashMap<String, Object>();
    }

    @Test
    public void createsAdaptersForPresentAdapters()
    {
        List<String> adapters = PubnativeTestUtils.getClassesPackages(PubnativeNetworkAdapterFactory.NETWORK_PACKAGE);
        for(String adapterName : adapters)
        {
            HashMap<String, Object> adapterData = new HashMap<String, Object>();
            adapterData.put(PubnativeConfigModel.NetworkContract.ADAPTER, adapterName);
            PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(adapterData);
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
    public void createAdapterFromAdapterContractKey()
    {
        HashMap<String, Object> adapterSpy = spy(HashMap.class);
        adapterSpy.put(PubnativeConfigModel.NetworkContract.ADAPTER, "adapter_data");
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(adapterSpy);
        verify(adapterSpy,  atLeastOnce()).get(eq(PubnativeConfigModel.NetworkContract.ADAPTER));
    }

    @Test
    public void createAdapterWithValidString()
    {
        data.put(PubnativeConfigModel.NetworkContract.ADAPTER, "valid_string");
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(data);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void createAdapterWithEmptyString()
    {
        data.put(PubnativeConfigModel.NetworkContract.ADAPTER, "");
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(data);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void createAdapterWithNullString()
    {
        data.put(PubnativeConfigModel.NetworkContract.ADAPTER, null);
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(data);
        assertThat(adapterInstance).isNull();
    }
}
