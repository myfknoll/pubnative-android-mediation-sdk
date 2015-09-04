package net.pubnative.mediation.demo;

import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.model.PubnativeAdModel;

/**
 * Created by davidmartin on 04/09/15.
 */
public class CellRequestModel
{
    public PubnativeNetworkRequest request;
    public PubnativeAdModel        adModel;
    public String                  placementID;
    public String                  appToken;

    public CellRequestModel(String placementID, String appToken)
    {
        this.request = new PubnativeNetworkRequest();
        this.appToken = appToken;
        this.placementID = placementID;
    }
}
