package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.InputStream;

/**
 * Created by davidmartin on 31/07/15.
 */
public class PubnativeConfigTestUtils
{
    /**
     * This method should be used to set up a test getConfig file stored under /src/test/resources
     * @param context
     * @param filename
     */
    public static void setTestConfig(Context context, String filename)
    {
        InputStream configStream = PubnativeConfigTestUtils.class.getResourceAsStream("/configs/" + filename);
        String configString = PubnativeStringUtils.readStringFromInputStream(configStream);
        PubnativeConfigManager.setConfigString(context, configString);
    }
}
