package net.pubnative.mediation.model;

import java.util.List;

/**
 * Created by davidmartin on 04/08/15.
 */
public class PubnativePlacementModel
{
    public String                            ad_format_code;
    public List<PubnativePriorityRulesModel> priority_rules;
    public PubnativeDeliveryRuleModel        delivery_rule;

    public interface AdFormatCode
    {
        String NATIVE_ICON   = "native_icon";
        String NATIVE_BANNER = "native_banner";
        String VIDEO         = "video";
    }
}
