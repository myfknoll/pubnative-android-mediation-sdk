package net.pubnative.mediation.model;

/**
 * Created by davidmartin on 26/08/15.
 */
public class PubnativeTrackingInfoModel
{
    public String                     url;
    public PubnativeTrackingDataModel dataModel;

    public PubnativeTrackingInfoModel(String url, PubnativeTrackingDataModel dataModel)
    {
        this.url = url;
        this.dataModel = dataModel;
    }
}
