package net.pubnative.mediation.adapter;

import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.InputStream;

/**
 * Created by rahul on 21/09/15.
 */
public class PubnativeNetworkAdapterTestUtils
{
    /**
     * This method reads the sample ad responses stored under resources/ads
     */
    public static String getSampleAdResponse(String filename)
    {
        InputStream configStream = PubnativeNetworkAdapterTestUtils.class.getResourceAsStream("/ads/" + filename);
        return PubnativeStringUtils.readStringFromInputStream(configStream);
    }
}
