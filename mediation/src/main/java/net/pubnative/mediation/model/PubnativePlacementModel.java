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
}
