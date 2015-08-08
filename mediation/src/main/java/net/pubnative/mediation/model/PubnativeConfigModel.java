package net.pubnative.mediation.model;

import java.util.Map;

public class PubnativeConfigModel
{
    public Map<String, Object>                  config;
    public Map<String, PubnativeNetworkModel>   networks;
    public Map<String, PubnativeAdFormatModel>  ad_formats;
    public Map<String, PubnativePlacementModel> placements;

    public interface ConfigContract
    {
        String REFRESH = "refresh";
    }

    public boolean isNullOrEmpty()
    {
        return this.isNullConfig() || this.isEmptyConfig();
    }

    protected boolean isNullConfig()
    {
        return this.networks == null ||
                this.ad_formats == null ||
                this.placements == null;
    }

    protected boolean isEmptyConfig()
    {
        return this.networks.size() == 0 ||
                this.ad_formats.size() == 0 ||
                this.placements.size() == 0;
    }
}
