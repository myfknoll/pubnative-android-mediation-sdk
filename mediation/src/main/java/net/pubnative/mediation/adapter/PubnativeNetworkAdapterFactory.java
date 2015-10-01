package net.pubnative.mediation.adapter;

import net.pubnative.mediation.config.model.PubnativeNetworkModel;

import java.lang.reflect.Constructor;
import java.util.Map;

public class PubnativeNetworkAdapterFactory
{
    protected final static String NETWORK_PACKAGE = "net.pubnative.mediation.adapter.network";

    /**
     * Creates a new network adapter instance by using the values passed in using model
     *
     * @param model network model that contains the values needed for creating a network adapter
     * @return instance of PubnativeNetworkAdapter if created, else null
     */
    public static PubnativeNetworkAdapter createAdapter(PubnativeNetworkModel model)
    {
        PubnativeNetworkAdapter result = null;

        try
        {
            Class<?> networkClass = Class.forName(getPackageName(model.adapter));
            Constructor<?> constructor = networkClass.getConstructor(Map.class);
            result = (PubnativeNetworkAdapter) constructor.newInstance(model.params);
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
