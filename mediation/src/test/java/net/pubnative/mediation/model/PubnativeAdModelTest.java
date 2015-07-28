package net.pubnative.mediation.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PubnativeAdModelTest
{
    @Test
    public void test_creationNotNull()
    {
        PubnativeAdModel model = new PubnativeAdModel();
        assertThat(model).isNotNull();
    }

    // TODO: Create tests
}
