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
        return imp_cap_day > 0;
    }

    public boolean isHourImpressionCapActive()
    {
        return imp_cap_hour > 0;
    }

    public boolean isHourPacingCapActive()
    {
        return pacing_cap_hour > 0;
    }

    public boolean isMinutePacingCapActive()
    {
        return pacing_cap_minute > 0;
    }
}
