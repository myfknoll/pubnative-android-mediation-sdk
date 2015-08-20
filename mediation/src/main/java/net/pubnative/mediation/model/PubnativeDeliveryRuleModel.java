package net.pubnative.mediation.model;

/**
 * Created by davidmartin on 14/08/15.
 */
public class PubnativeDeliveryRuleModel
{
    public int imp_cap_day;
    public int imp_cap_hour;
    public int pacing_cap_hour;
    public int pacing_cap_minute;
    public boolean no_ads;

    public boolean isActive()
    {
        return !this.no_ads;
    }

    public boolean isDayImpressionCapActive()
    {
        return this.imp_cap_day > 0;
    }

    public boolean isHourImpressionCapActive()
    {
        return this.imp_cap_hour > 0;
    }

    public boolean isPacingCapActive()
    {
        return this.pacing_cap_hour > 0 || this.pacing_cap_minute > 0;
    }
}
