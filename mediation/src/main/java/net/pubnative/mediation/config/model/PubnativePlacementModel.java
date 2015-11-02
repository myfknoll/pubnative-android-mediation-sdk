package net.pubnative.mediation.config.model;

import java.util.List;

/**
 * Created by davidmartin on 04/08/15.
 */
public class PubnativePlacementModel {

    public String                            ad_format_code;
    public List<PubnativePriorityRulesModel> priority_rules;
    public PubnativeDeliveryRuleModel        delivery_rule;

    public interface AdFormatCode {

        String NATIVE_ICON   = "icon";
        String NATIVE_BANNER = "banner";
        String VIDEO         = "video";
    }
}
