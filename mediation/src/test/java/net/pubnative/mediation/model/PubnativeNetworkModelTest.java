package net.pubnative.mediation.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PubnativeNetworkModelTest
{
    @Test
    public void test_creationNotNull()
    {
        PubnativeNetworkModel model = new PubnativeNetworkModel();
        assertThat(model).isNotNull();
    }

    // TODO: Create tests
}
