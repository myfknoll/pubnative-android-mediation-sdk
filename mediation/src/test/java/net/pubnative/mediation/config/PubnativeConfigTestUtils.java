package net.pubnative.mediation.config;

import android.content.Context;

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.InputStream;

/**
 * Created by davidmartin on 31/07/15.
 */
public class PubnativeConfigTestUtils
{
    /**
     * This method should be used to set up a test getConfig file stored under /src/test/resources
     *
     * @param context   valid Context object
     * @param filename  name of the resource file
     * @param app_token valid app token obtained from pubnative
     */
    public static void setTestConfig(Context context, String filename, String app_token)
    {
        InputStream configStream = PubnativeConfigTestUtils.class.getResourceAsStream("/configs/" + filename);
        String configString = PubnativeStringUtils.readStringFromInputStream(configStream);
        try
        {
            PubnativeConfigModel model = new Gson().fromJson(configString, PubnativeConfigModel.class);
            PubnativeConfigManager.updateConfig(context, app_token, model);
        }
        catch(Exception e)
        {
            // Do nothing
        }
    }

    /**
     * This method simulates the config download using a stored config json file inside resources.
     * @param fileName name of the resource file that contains config json
     * @return valid JSON of the api response with a status "OK"
     */
    public static String getConfigApiResponseJsonFromResource(String fileName)
    {
        Gson gson = new Gson();
        InputStream configStream = PubnativeConfigTestUtils.class.getResourceAsStream("/configs/" + fileName);
        String configString = PubnativeStringUtils.readStringFromInputStream(configStream);

        PubnativeConfigAPIResponseModel apiResponseModel = new PubnativeConfigAPIResponseModel();
        apiResponseModel.status = PubnativeConfigAPIResponseModel.Status.OK;
        apiResponseModel.config = gson.fromJson(configString, PubnativeConfigModel.class);

        return gson.toJson(apiResponseModel);
    }
}
