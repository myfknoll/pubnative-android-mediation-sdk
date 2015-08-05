package net.pubnative.mediation.model;

import net.pubnative.mediation.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by davidmartin on 06/08/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class  PubnativeAdFormatModelTest
{
    @Test
    public void impressionCalendarValues()
    {
        PubnativeAdFormatModel modelSpy = spy(PubnativeAdFormatModel.class);
        Calendar currentCalendar = Calendar.getInstance();
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);

        // Positive value
        modelSpy.impression_cap = 1;

        modelSpy.impression_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_DAYS_VALUE;
        int overdueDayPositive = modelSpy.getImpressionOverdueCalendar().get(Calendar.DAY_OF_MONTH);
        assertThat(overdueDayPositive).isNotEqualTo(currentDay);
        assertThat(currentDay - overdueDayPositive).isEqualTo(1);

        modelSpy.impression_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_HOURS_VALUE;
        int overdueHourPositive = modelSpy.getImpressionOverdueCalendar().get(Calendar.HOUR_OF_DAY);
        assertThat(overdueHourPositive).isNotEqualTo(currentHour);
        assertThat(currentHour-overdueHourPositive).isEqualTo(1);

        modelSpy.impression_period = "";
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();

        modelSpy.impression_period = null;
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();

        // Zero value
        modelSpy.impression_cap = 0;
        modelSpy.impression_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_DAYS_VALUE;
        int overdueDayZero = modelSpy.getImpressionOverdueCalendar().get(Calendar.DAY_OF_MONTH);
        assertThat(overdueDayZero).isEqualTo(currentDay);
        assertThat(currentDay - overdueDayZero).isEqualTo(0);

        modelSpy.impression_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_HOURS_VALUE;
        int overdueHourZero = modelSpy.getImpressionOverdueCalendar().get(Calendar.HOUR_OF_DAY);
        assertThat(overdueHourZero).isEqualTo(currentHour);
        assertThat(currentHour - overdueHourZero).isEqualTo(0);

        modelSpy.impression_period = "";
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();

        modelSpy.impression_period = null;
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();

        // Null value
        modelSpy.impression_cap = null;

        modelSpy.impression_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_DAYS_VALUE;
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();
        modelSpy.impression_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_HOURS_VALUE;
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();
        modelSpy.impression_period = "";
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();
        modelSpy.impression_period = null;
        assertThat(modelSpy.getImpressionOverdueCalendar()).isNull();
    }

    @Test
    public void pacingCalendarValues()
    {
        PubnativeAdFormatModel modelSpy = spy(PubnativeAdFormatModel.class);
        Calendar currentCalendar = Calendar.getInstance();
        int currentMinute = currentCalendar.get(Calendar.MINUTE);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);

        // Positive value
        modelSpy.pacing_cap = 1;

        modelSpy.pacing_period = PubnativeAdFormatModel.PACING_PERIOD_MINUTES_VALUE;
        int overdueMinutePositive = modelSpy.getPacingOverdueCalendar().get(Calendar.MINUTE);
        assertThat(overdueMinutePositive ).isNotEqualTo(currentMinute);
        assertThat(currentMinute - overdueMinutePositive ).isEqualTo(1);

        modelSpy.pacing_period = PubnativeAdFormatModel.PACING_PERIOD_HOURS_VALUE;
        int overdueHourPositive = modelSpy.getPacingOverdueCalendar().get(Calendar.HOUR_OF_DAY);
        assertThat(overdueHourPositive).isNotEqualTo(currentHour);
        assertThat(currentHour-overdueHourPositive).isEqualTo(1);

        modelSpy.pacing_period = "";
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();

        modelSpy.pacing_period = null;
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();

        // Zero value
        modelSpy.pacing_cap = 0;
        modelSpy.pacing_period = PubnativeAdFormatModel.PACING_PERIOD_MINUTES_VALUE;
        int overdueMinuteZero = modelSpy.getPacingOverdueCalendar().get(Calendar.MINUTE);
        assertThat(overdueMinuteZero).isEqualTo(currentMinute);
        assertThat(currentMinute - overdueMinuteZero).isEqualTo(0);

        modelSpy.pacing_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_HOURS_VALUE;
        int overdueHourZero = modelSpy.getPacingOverdueCalendar().get(Calendar.HOUR_OF_DAY);
        assertThat(overdueHourZero).isEqualTo(currentHour);
        assertThat(currentHour - overdueHourZero).isEqualTo(0);

        modelSpy.pacing_period = "";
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();

        modelSpy.pacing_period = null;
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();

        // Null value
        modelSpy.pacing_cap = null;

        modelSpy.pacing_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_DAYS_VALUE;
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();
        modelSpy.pacing_period = PubnativeAdFormatModel.IMPRESSION_PERIOD_HOURS_VALUE;
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();
        modelSpy.pacing_period = "";
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();
        modelSpy.pacing_period = null;
        assertThat(modelSpy.getPacingOverdueCalendar()).isNull();
    }
}
