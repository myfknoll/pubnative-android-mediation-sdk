package net.pubnative.mediation.model;

/**
 * Created by davidmartin on 14/08/15.
 */
public class PubnativeAPIResponseModel
{
    public String status;
    public PubnativeConfigModel config;
    public String error_message;

    public interface Status
    {
        String OK = "ok";
        String ERROR = "error";
    }
}
