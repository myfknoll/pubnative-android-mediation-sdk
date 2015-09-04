package net.pubnative.mediation.insights.model;

/**
 * Created by davidmartin on 03/09/15.
 */
public class PubnativeInsightsAPIResponseModel
{
    public String               status;
    public String               error_message;

    public interface Status
    {
        String OK    = "ok";
        String ERROR = "error";
    }
}
