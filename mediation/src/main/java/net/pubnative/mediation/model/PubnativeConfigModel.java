package net.pubnative.mediation.model;

import java.util.HashMap;

public class PubnativeConfigModel
{
    public int                       conf_refresh;
    public HashMap<String, Object>   networks;
    public HashMap<String, Object>   ad_formats;
    public PubnativePlacementModel[] placements;
}
