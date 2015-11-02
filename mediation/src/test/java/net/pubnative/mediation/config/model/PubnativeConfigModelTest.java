package net.pubnative.mediation.config.model;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by davidmartin on 06/08/15.
 */
public class PubnativeConfigModelTest {

    @Test
    public void startsWithNullValues() {
        PubnativeConfigModel model = spy(PubnativeConfigModel.class);

        assertThat(model.globals).isNull();
        assertThat(model.networks).isNull();
        assertThat(model.placements).isNull();
    }

    @Test
    public void isNullOrEmptyForDifferentValues() {
        PubnativeConfigModel model = spy(PubnativeConfigModel.class);

        Map notEmptyMap = mock(Map.class);
        doReturn(1).when(notEmptyMap).size();

        Map emptyMap = mock(Map.class);
        doReturn(0).when(emptyMap).size();

        // isNullOrEmpty when any of them is null
        model.networks = null;
        model.placements = null;
        assertThat(model.isNullOrEmpty()).isTrue();

        model.networks = null;
        model.placements = emptyMap;
        assertThat(model.isNullOrEmpty()).isTrue();

        model.networks = emptyMap;
        model.placements = null;
        assertThat(model.isNullOrEmpty()).isTrue();

        model.networks = null;
        model.placements = notEmptyMap;
        assertThat(model.isNullOrEmpty()).isTrue();

        model.networks = notEmptyMap;
        model.placements = null;
        assertThat(model.isNullOrEmpty()).isTrue();

        // isNullOrEmpty when any of them is empty
        model.networks = emptyMap;
        model.placements = emptyMap;
        assertThat(model.isNullOrEmpty()).isTrue();

        model.networks = emptyMap;
        model.placements = notEmptyMap;
        assertThat(model.isNullOrEmpty()).isTrue();

        model.networks = notEmptyMap;
        model.placements = emptyMap;
        assertThat(model.isNullOrEmpty()).isTrue();

        // isNullOrEmpty false when all data is setted up
        model.networks = notEmptyMap;
        model.placements = notEmptyMap;
        assertThat(model.isNullOrEmpty()).isFalse();
    }

}