package net.pubnative.mediation.adapter;

import net.pubnative.mediation.model.PubnativeConfigModel;

import java.lang.reflect.Constructor;
import java.util.Map;

public class PubnativeNetworkAdapterFactory
{
    protected final static String NETWORK_PACKAGE = "net.pubnative.mediation.adapter.network";

    public static PubnativeNetworkAdapter createAdapter(Map adapterConfig)
    {
        PubnativeNetworkAdapter result = null;

        try
        {
            String adapterName = (String) adapterConfig.get(PubnativeConfigModel.NetworkContract.ADAPTER);
            Class<?> networkClass = Class.forName(getPackageName(adapterName));
            Constructor<?> constructor = networkClass.getConstructor(Map.class);
            result = (PubnativeNetworkAdapter) constructor.newInstance(adapterConfig);
        }
        catch (Exception e)
        {
            // Don't crash, just return null, log error and return null
            System.out.println("Pubnative - Error creating adapter: " + e);
        }

        return result;
    }

    protected static String getPackageName(String classSimpleName)
    {
        String result = null;
        if (classSimpleName != null)
        {
            result = NETWORK_PACKAGE + "." + classSimpleName;
        }
        return result;
    }
}
