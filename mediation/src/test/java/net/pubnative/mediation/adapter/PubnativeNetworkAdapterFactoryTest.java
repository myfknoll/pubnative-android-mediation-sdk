package net.pubnative.mediation.adapter;

import net.pubnative.mediation.adapter.network.FacebookNetworkAdapter;
import net.pubnative.mediation.adapter.network.LoopmeNetworkAdapter;
import net.pubnative.mediation.adapter.network.PubnativeLibraryNetworkAdapter;
import net.pubnative.mediation.adapter.network.YahooNetworkAdapter;
import net.pubnative.mediation.model.PubnativeNetworkModel;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeNetworkAdapterFactoryTest
{
    PubnativeNetworkModel testModel;
    @Test
    public void test_empty()
    {
        assertThat(true).isTrue();
    }

    @Before
    public void setUp()
    {
        testModel = new PubnativeNetworkModel();
    }

    // TODO: Create test

    @Test
    public void test_pubnativeLibraryNetwork()
    {
        testModel.adapter = "net.pubnative.mediation.adapter.network.PubnativeLibraryNetworkAdapter";
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(testModel);
        assertThat(adapterInstance).isInstanceOf(PubnativeLibraryNetworkAdapter.class);
    }

    @Test
    public void test_facebookNetwork()
    {
        testModel.adapter = "net.pubnative.mediation.adapter.network.FacebookNetworkAdapter";
        PubnativeNetworkAdapter facebookNetworkAdapterInstance = PubnativeNetworkAdapterFactory.createAdapter(testModel);
        assertThat(facebookNetworkAdapterInstance).isInstanceOf(FacebookNetworkAdapter.class);
    }

    @Test
    public void test_loopMeNetwork()
    {
        testModel.adapter = "net.pubnative.mediation.adapter.network.LoopmeNetworkAdapter";
        PubnativeNetworkAdapter loopMeNetworkAdapterInstance = PubnativeNetworkAdapterFactory.createAdapter(testModel);
        assertThat(loopMeNetworkAdapterInstance).isInstanceOf(LoopmeNetworkAdapter.class);
    }

    @Test
    public void test_yahooNetwork()
    {
        testModel.adapter = "net.pubnative.mediation.adapter.network.YahooNetworkAdapter";
        PubnativeNetworkAdapter yahooNetworkAdapterInstance = PubnativeNetworkAdapterFactory.createAdapter(testModel);
        assertThat(yahooNetworkAdapterInstance).isInstanceOf(YahooNetworkAdapter.class);
    }

    @Test
    public void test_invalidStringInput()
    {
        testModel.adapter = "HelloWorld";
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(testModel);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void test_nullInput()
    {
        testModel.adapter = null;
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(testModel);
        assertThat(adapterInstance).isNull();
    }

    @Test
    public void test_emptyInput()
    {
        testModel.adapter = "";
        PubnativeNetworkAdapter adapterInstance = PubnativeNetworkAdapterFactory.createAdapter(testModel);
        assertThat(adapterInstance).isNull();
    }

}
