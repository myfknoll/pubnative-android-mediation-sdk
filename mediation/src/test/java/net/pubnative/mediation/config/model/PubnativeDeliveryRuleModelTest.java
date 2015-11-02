package net.pubnative.mediation.config.model;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

/**
 * Created by davidmartin on 17/08/15.
 */
public class PubnativeDeliveryRuleModelTest {

    PubnativeDeliveryRuleModel modelSpy;

    @Before
    public void setUp() {
        modelSpy = spy(PubnativeDeliveryRuleModel.class);
    }

    @Test
    public void isActiveWithValues() {
        modelSpy.no_ads = false;
        assertThat(modelSpy.isActive()).isTrue();
        modelSpy.no_ads = true;
        assertThat(modelSpy.isActive()).isFalse();
    }

    @Test
    public void isDayImpressionCapActiveWithValues() {
        modelSpy.imp_cap_day = -1;
        assertThat(modelSpy.isDayImpressionCapActive()).isFalse();
        modelSpy.imp_cap_day = 0;
        assertThat(modelSpy.isDayImpressionCapActive()).isFalse();
        modelSpy.imp_cap_day = 1;
        assertThat(modelSpy.isDayImpressionCapActive()).isTrue();
    }

    @Test
    public void isHourImpressionCapActiveWithValues() {
        modelSpy.imp_cap_hour = -1;
        assertThat(modelSpy.isHourImpressionCapActive()).isFalse();
        modelSpy.imp_cap_hour = 0;
        assertThat(modelSpy.isHourImpressionCapActive()).isFalse();
        modelSpy.imp_cap_hour = 1;
        assertThat(modelSpy.isHourImpressionCapActive()).isTrue();
    }

    @Test
    public void isPacingCapActiveWithValues() {
        modelSpy.pacing_cap_minute = -1;
        modelSpy.pacing_cap_hour = -1;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = -1;
        modelSpy.pacing_cap_hour = 0;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = 0;
        modelSpy.pacing_cap_hour = -1;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = 0;
        modelSpy.pacing_cap_hour = 0;
        assertThat(modelSpy.isPacingCapActive()).isFalse();

        modelSpy.pacing_cap_minute = 1;
        modelSpy.pacing_cap_hour = 0;
        assertThat(modelSpy.isPacingCapActive()).isTrue();

        modelSpy.pacing_cap_minute = 0;
        modelSpy.pacing_cap_hour = 1;
        assertThat(modelSpy.isPacingCapActive()).isTrue();

        modelSpy.pacing_cap_minute = 1;
        modelSpy.pacing_cap_hour = 1;
        assertThat(modelSpy.isPacingCapActive()).isTrue();
    }
}
