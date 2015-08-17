package net.pubnative.mediation.model;

import java.util.Calendar;

/**
 * Created by davidmartin on 14/08/15.
 */
public class PubnativeDeliveryRuleModel
{
    int imp_cap_day;
    int imp_cap_hour;
    int pacing_cap_hour;
    int pacing_cap_minute;
    boolean no_ads;

    public boolean isActive()
    {
        return !this.no_ads;
    }

    public Calendar getOverdueImpressionDay()
    {
        return getOverdueCalendar(Calendar.DAY_OF_MONTH, this.imp_cap_day);
    }

    public Calendar getOverdueImpressionHour()
    {
        return getOverdueCalendar(Calendar.HOUR_OF_DAY, this.imp_cap_hour);
    }

    public Calendar getOverduePacingHour()
    {
        return getOverdueCalendar(Calendar.HOUR_OF_DAY, this.pacing_cap_hour);
    }

    public Calendar getOverduePacingMinute()
    {
        return getOverdueCalendar(Calendar.MINUTE, this.pacing_cap_minute);
    }

    private Calendar getOverdueCalendar(int field, int value)
    {
        Calendar result = null;
        if (value > 0)
        {
            result = Calendar.getInstance();
            result.add(field, -1 * value);
        }
        return result;
    }
}
