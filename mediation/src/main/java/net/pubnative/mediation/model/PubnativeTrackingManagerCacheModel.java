package net.pubnative.mediation.model;

import java.util.List;

/**
 * Created by davidmartin on 26/08/15.
 */
public class PubnativeTrackingManagerCacheModel
{
    public List<PubnativeTrackingInfoModel> items;

    public PubnativeTrackingManagerCacheModel(List<PubnativeTrackingInfoModel> items)
    {
        this.items = items;
    }
}
