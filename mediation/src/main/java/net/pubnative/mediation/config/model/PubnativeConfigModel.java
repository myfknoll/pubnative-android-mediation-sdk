package net.pubnative.mediation.config.model;

import java.util.Map;

public class PubnativeConfigModel
{
    public Map<String, Object>                  globals;
    public Map<String, PubnativeNetworkModel>   networks;
    public Map<String, PubnativePlacementModel> placements;

    public interface ConfigContract
    {
        String REFRESH           = "refresh";
        String IMPRESSION_BEACON = "impression_beacon";
        String CLICK_BEACON      = "click_beacon";
        String REQUEST_BEACON    = "request_beacon";
    }

    public boolean isNullOrEmpty()
    {
        return this.networks == null || this.placements == null || this.networks.size() == 0 || this.placements.size() == 0;
    }
}
