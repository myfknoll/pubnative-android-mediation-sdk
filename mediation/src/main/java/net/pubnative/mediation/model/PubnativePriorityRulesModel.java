package net.pubnative.mediation.model;

import java.util.HashMap;

/**
 * Created by davidmartin on 31/07/15.
 */
public class PubnativePriorityRulesModel
{
    public int                     priority_rule_id;
    public int                     rank;
    public HashMap<String, Object> network;

    public interface NetworkContract
    {
        String NAME   = "name";
        String PARAMS = "params";
    }
}
