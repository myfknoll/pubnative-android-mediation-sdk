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

    public CellRequestModel(String appToken, String placementID)
    {
        this.request = new PubnativeNetworkRequest();
        this.appToken = appToken;
        this.placementID = placementID;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean same = false;
        if(this == object)
        {
            same = true;
        }
        if(object != null)
        {
            if(object instanceof CellRequestModel)
            {
                CellRequestModel cell = (CellRequestModel) object;
                same = cell.placementID == this.placementID;
            }
            else if(object instanceof String)
            {
                String objectPlacement = (String) object;
                same = objectPlacement == this.placementID;
            }
        }
        return same;
    }
}
