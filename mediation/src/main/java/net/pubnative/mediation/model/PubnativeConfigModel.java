package net.pubnative.mediation.model;

import java.util.ArrayList;
import java.util.HashMap;

public class PubnativeConfigModel
{
    public int                                conf_refresh;
    public HashMap<String, Object>            networks;
    public HashMap<String, Object>            ad_formats;
    public ArrayList<PubnativePlacementModel> placements;

    public interface NetworkContract
    {
        String ADAPTER = "adapter";
        String TIMEOUT = "timeout";
    }

    public interface AdFormatContract
    {
        String SETTINGS = "settings";
    }

    public boolean isNullOrEmpty()
    {
        return this.isNullConfig() || this.isEmptyConfig();
    }

    protected boolean isNullConfig()
    {
        return this.networks == null &&
                this.ad_formats == null &&
                this.placements == null;
    }

    protected boolean isEmptyConfig()
    {
        return this.networks.size() == 0 &&
                this.ad_formats.size() == 0 &&
                this.placements.size() == 0;
    }
}
