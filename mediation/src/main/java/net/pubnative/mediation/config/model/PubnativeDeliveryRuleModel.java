package net.pubnative.mediation.config.model;

import android.content.Context;

import net.pubnative.mediation.config.PubnativeDeliveryManager;

import java.util.Calendar;

/**
 * Created by davidmartin on 14/08/15.
 */
public class PubnativeDeliveryRuleModel
{
    public int     imp_cap_day;
    public int     imp_cap_hour;
    public int     pacing_cap_hour;
    public int     pacing_cap_minute;
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

    public Calendar getPacingOverdueCalendar()
    {
        Calendar result = null;
        if (this.isPacingCapActive())
        {
            result = Calendar.getInstance();
            if (pacing_cap_minute > 0)
            {
                result.add(Calendar.MINUTE, -pacing_cap_minute);
            }
            else
            {
                result.add(Calendar.HOUR_OF_DAY, -pacing_cap_hour);
            }
        }
        return result;
    }

    public boolean isFrequencyCapReached(Context context, String placementID)
    {
        boolean frequencyCapReached = false;
        if (this.isDayImpressionCapActive())
        {
            frequencyCapReached = this.imp_cap_day <= PubnativeDeliveryManager.getCurrentDailyCount(context, placementID);
        }
        if (!frequencyCapReached && this.isHourImpressionCapActive())
        {
            frequencyCapReached = this.imp_cap_hour <= PubnativeDeliveryManager.getCurrentHourlyCount(context, placementID);
        }
        return frequencyCapReached;
    }

}
