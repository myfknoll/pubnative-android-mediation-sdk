package net.pubnative.mediation.insights.model;

/**
 * Created by davidmartin on 20/01/16.
 */
public class PubnativeInsightCrashModel {

    public static final String ERROR_NO_FILL = "no_fill";
    public static final String ERROR_TIMEOUT = "timeout";
    public static final String ERROR_CONFIG  = "configuration";
    public static final String ERROR_ADAPTER = "adapter";

    public String error;
    public String details;
    public long   start;
    public long   end;
}
