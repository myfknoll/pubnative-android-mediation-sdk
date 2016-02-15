package net.pubnative.mediation.insights.model;

import java.util.List;

/**
 * Created by davidmartin on 02/02/16.
 */
public class PubnativeInsightNetworkModel {

    public String                     code;
    public int                        priority_rule_id;
    public List<Integer>              priority_segment_ids;
    public long                       response_time;
    public PubnativeInsightCrashModel crash_report;
}
