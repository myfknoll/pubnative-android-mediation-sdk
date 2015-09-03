package net.pubnative.mediation.config.model;

/**
 * Created by davidmartin on 14/08/15.
 */
public class PubnativeConfigAPIResponseModel
{
    public String               status;
    public PubnativeConfigModel config;
    public String               error_message;

    public interface Status
    {
        String OK    = "ok";
        String ERROR = "error";
    }
}
