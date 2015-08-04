package net.pubnative.mediation.model;

import java.util.List;
import java.util.Map;

public class PubnativeConfigModel
{
    public int                           conf_refresh;
    public Map<String, Object>           networks;
    public Map<String, Object>           ad_formats;
    public List<PubnativePlacementModel> placements;

    public interface NetworkContract
    {
        String ADAPTER = "adapter";
        String TIMEOUT = "timeout";
    }

    public interface AdFormatContract
    {
        String SETTINGS = "settings";
        String DELIVERY_RULE = "delivery_rule";
        public interface DeliveryRuleContract
        {
            String FREQUENCY_LIMIT = "frequency";
            String PACING_LIMIT = "pacing";

            public interface LimitContract
            {
                String TIME_UNIT = "time_unit";
                String MAX = "max";
            }
        }
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
