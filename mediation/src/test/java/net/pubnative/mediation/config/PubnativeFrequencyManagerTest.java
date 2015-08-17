package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by davidmartin on 15/08/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(PubnativeFrequencyManager.class)
public class PubnativeFrequencyManagerTest
{
    private Context applicationContext;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp()
    {
        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void getlastUpdateWithAllValues()
    {
        // Returns null when not setted up or bad parameters
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext)).isNull();

        // Returns previous setted value and nothing
        Calendar calendar = this.getMockedCalendar();
        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, calendar);
        assertThat(PubnativeFrequencyManager.getLastUpdate(null)).isNull();
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext)
                                            .getTimeInMillis()).isEqualTo(calendar.getTimeInMillis());
    }

    @Test
    public void setLastUpdateWithAllValues()
    {
        Calendar calendar = this.getMockedCalendar();

        PubnativeFrequencyManager.setLastUpdate(null, null);
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext)).isNull();

        PubnativeFrequencyManager.setLastUpdate(null, calendar);
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext)).isNull();

        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, null);
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext)).isNull();

        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, calendar);
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext)).isNotNull();
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext).getTimeInMillis()).isEqualTo(calendar.getTimeInMillis());

        // Using a null removes the last setted up value
        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, null);
        assertThat(PubnativeFrequencyManager.getLastUpdate(this.applicationContext)).isNull();
    }

    private Calendar getMockedCalendar()
    {
        Calendar calendar = mock(Calendar.class);
        Long currentMillis = System.currentTimeMillis();
        when(calendar.getTimeInMillis()).thenReturn(currentMillis);
        return calendar;
    }

    @Test
    public void getImpressionCountWithAllValues()
    {
        String trackingKeyString = "trackingKey";

        // Returns 0 when not setted up
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString )).isZero();

        // Returns previous setted value and 0 when wrong parameters
        PubnativeFrequencyManager.setImpressionCount(this.applicationContext, trackingKeyString, 10);
        assertThat(PubnativeFrequencyManager.getImpressionCount(null, null)).isZero();
        assertThat(PubnativeFrequencyManager.getImpressionCount(null, "")).isZero();
        assertThat(PubnativeFrequencyManager.getImpressionCount(null, trackingKeyString )).isZero();
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, null)).isZero();
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, "")).isZero();
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isEqualTo(10);
    }

    @Test
    public void setImpressionCountWithAllValues()
    {
        String trackingKeyString = "trackingKeyString";

        // Invalid arguments
        PubnativeFrequencyManager.setImpressionCount(null, null, 0);
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isZero();

        PubnativeFrequencyManager.setImpressionCount(null, "", 0);
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isZero();

        PubnativeFrequencyManager.setImpressionCount(null, trackingKeyString, 0);
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isZero();

        PubnativeFrequencyManager.setImpressionCount(this.applicationContext, null, 0);
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isZero();

        PubnativeFrequencyManager.setImpressionCount(this.applicationContext, "", 0);
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isZero();

        // Valid arguments
        PubnativeFrequencyManager.setImpressionCount(this.applicationContext, trackingKeyString, 10);
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isEqualTo(10);

        PubnativeFrequencyManager.setImpressionCount(this.applicationContext, trackingKeyString, 0);
        assertThat(PubnativeFrequencyManager.getImpressionCount(this.applicationContext, trackingKeyString)).isZero();
    }

    @Test
    public void publicMethodsUpdatesData()
    {
        PowerMockito.spy(PubnativeFrequencyManager.class);
        PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext);
        PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext);
        PubnativeFrequencyManager.logImpression(this.applicationContext);
        PowerMockito.verifyStatic(times(3));
        PubnativeFrequencyManager.updateImpressionCount(any(Context.class));
    }

    @Test
    public void getCurrentCountsReturnsZeroWithNullParameters()
    {
        PubnativeFrequencyManager.logImpression(this.applicationContext);

        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(null)).isZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(null)).isZero();
    }

    @Test
    public void logImpressionIncrementsCount()
    {
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isZero();
        PubnativeFrequencyManager.logImpression(this.applicationContext);
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isEqualTo(1);
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isEqualTo(1);
    }

    @Test
    public void logImpressionDoesNothingWithNullParameters()
    {
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isZero();
        PubnativeFrequencyManager.logImpression(null);
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isZero();
    }

    @Test
    public void updateImpressionUpdatesCounts()
    {
        PubnativeFrequencyManager.logImpression(this.applicationContext);
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isNotZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isNotZero();

        Calendar calendar = mock(Calendar.class);
        when(calendar.getTimeInMillis()).thenReturn((long) 10);
        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, calendar);
        // Null
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(null)).isZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(null)).isZero();
        // Valid
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneDay()
    {
        PubnativeFrequencyManager.logImpression(this.applicationContext);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, calendar);
        PubnativeFrequencyManager.updateImpressionCount(this.applicationContext);
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isZero();
    }

    @Test
    public void updateImpressionUpdatesWithMoreThanOneHour()
    {
        PubnativeFrequencyManager.logImpression(this.applicationContext);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, calendar);
        PubnativeFrequencyManager.updateImpressionCount(this.applicationContext);
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isNotZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isZero();
    }

    @Test
    public void updateImpressionDontUpdatesWithValid()
    {
        PubnativeFrequencyManager.logImpression(this.applicationContext);
        Calendar calendar = Calendar.getInstance();
        PubnativeFrequencyManager.setLastUpdate(this.applicationContext, calendar);
        PubnativeFrequencyManager.updateImpressionCount(this.applicationContext);
        assertThat(PubnativeFrequencyManager.getCurrentDailyCount(this.applicationContext)).isNotZero();
        assertThat(PubnativeFrequencyManager.getCurrentHourlyCount(this.applicationContext)).isNotZero();
    }
}
