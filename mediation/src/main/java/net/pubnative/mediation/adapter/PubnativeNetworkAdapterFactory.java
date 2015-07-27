package net.pubnative.mediation.adapter;

import java.lang.reflect.Constructor;

import net.pubnative.mediation.model.PubnativeNetworkModel;

public class PubnativeNetworkAdapterFactory
{
    public static PubnativeNetworkAdapter createAdapter(PubnativeNetworkModel networkModel)
    {
        PubnativeNetworkAdapter result = null;

        try
        {
            Class<?> networkClass = Class.forName(networkModel.name);
            Constructor<?> constructor= networkClass.getConstructor(PubnativeNetworkModel.class);
            result = (PubnativeNetworkAdapter) constructor.newInstance(networkModel);
        }
        catch (Exception e)
        {
            // Do nothing, if fails, this network shoyldn't be used by returning null
        }
        
        return result;
    }
}
