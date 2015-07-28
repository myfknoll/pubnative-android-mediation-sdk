package net.pubnative.mediation.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PubnativeConfigModelTest
{
    @Test
    public void test_creationNotNull()
    {
        PubnativeConfigModel model = new PubnativeConfigModel();
        assertThat(model).isNotNull();
    }

    // TODO: Create tests
}
