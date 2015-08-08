package net.pubnative.mediation.model;

import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by davidmartin on 04/08/15.
 */

public class PubnativeAdFormatModel
{
    public static final String IMPRESSION_PERIOD_DAYS_VALUE  = "days";
    public static final String IMPRESSION_PERIOD_HOURS_VALUE = "hours";

    public static final String PACING_PERIOD_HOURS_VALUE   = "hours";
    public static final String PACING_PERIOD_MINUTES_VALUE = "minutes";

    public String impression_period;
    public String pacing_period;

    public Integer impression_cap;
    public Integer pacing_cap;

    public Calendar getImpressionOverdueCalendar()
    {
        Calendar result = null;

        if (this.impression_cap != null && !TextUtils.isEmpty(this.impression_period))
        {
            result = Calendar.getInstance();
            int affectedField = Calendar.HOUR_OF_DAY;
            if (PubnativeAdFormatModel.IMPRESSION_PERIOD_DAYS_VALUE.equals(this.impression_period))
            {
                affectedField = Calendar.DAY_OF_MONTH;
            }
            result.add(affectedField, -1 * this.impression_cap);
        }
        return result;
    }

    public Calendar getPacingOverdueCalendar()
    {
        Calendar result = null;

        if (this.pacing_cap != null && !TextUtils.isEmpty(this.pacing_period))
        {
            result = Calendar.getInstance();
            int affectedField = Calendar.HOUR_OF_DAY;
            if (PubnativeAdFormatModel.PACING_PERIOD_MINUTES_VALUE.equals(this.pacing_period))
            {
                affectedField = Calendar.MINUTE;
            }
            result.add(affectedField, -1 * this.pacing_cap);
        }
        return result;
    }
}
