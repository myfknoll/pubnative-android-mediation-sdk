package net.pubnative.mediation.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by davidmartin on 31/07/15.
 */
public class PubnativePlacementModel
{
    public String                                 placement_id;
    public String                                 ad_format;
    public ArrayList<PubnativePriorityRulesModel> priority_rules;
    public HashMap<String, Object>                delivery_rules;

    public interface DeliveryRuleContract
    {
        String FREQUENCY = "frequency";
        String PACING    = "pacing";

        interface Value
        {
            String TIME_UNIT = "time_unit";
            String MAX       = "max";
        }
    }
}
