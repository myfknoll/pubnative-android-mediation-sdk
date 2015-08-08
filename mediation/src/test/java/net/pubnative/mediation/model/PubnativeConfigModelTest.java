package net.pubnative.mediation.model;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by davidmartin on 06/08/15.
 */
public class PubnativeConfigModelTest
{
    @Test
    public void startsWithNullValues()
    {
        PubnativeConfigModel model = spy(PubnativeConfigModel.class);

        assertThat(model.config).isNull();
        assertThat(model.networks).isNull();
        assertThat(model.ad_formats).isNull();
        assertThat(model.placements).isNull();
    }

    @Test
    public void isNullIsForDifferentValues()
    {
        PubnativeConfigModel model = spy(PubnativeConfigModel.class);
        Map mockMap = mock(Map.class);

        // Not null when all data is setted up
        model.networks = mockMap;
        model.ad_formats = mockMap;
        model.placements = mockMap;
        assertThat(model.isNullConfig()).isFalse();

        // isNull when one of them is null
        model.networks = null;
        model.ad_formats = mockMap;
        model.placements = mockMap;
        assertThat(model.isNullConfig()).isTrue();

        model.networks = mockMap;
        model.ad_formats = null;
        model.placements = mockMap;
        assertThat(model.isNullConfig()).isTrue();

        model.networks = mockMap;
        model.ad_formats = mockMap;
        model.placements = null;
        assertThat(model.isNullConfig()).isTrue();
    }

    @Test
    public void isEmptyForDifferentValues()
    {
        PubnativeConfigModel model = spy(PubnativeConfigModel.class);
        Map notEmptyMap = mock(Map.class);
        doReturn(1).when(notEmptyMap).size();

        Map emptyMap = mock(Map.class);
        doReturn(0).when(emptyMap).size();

        // is not empty if all the data is filled
        model.networks = notEmptyMap;
        model.ad_formats = notEmptyMap;
        model.placements = notEmptyMap;
        assertThat(model.isEmptyConfig()).isFalse();

        // is empty if at least one data is empty
        model.networks = emptyMap;
        model.ad_formats = notEmptyMap;
        model.placements = notEmptyMap;
        assertThat(model.isEmptyConfig()).isTrue();

        model.networks = notEmptyMap;
        model.ad_formats = emptyMap;
        model.placements = notEmptyMap;
        assertThat(model.isEmptyConfig()).isTrue();

        model.networks = notEmptyMap;
        model.ad_formats = notEmptyMap;
        model.placements = emptyMap;
        assertThat(model.isEmptyConfig()).isTrue();
    }
}