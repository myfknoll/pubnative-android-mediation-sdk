package net.pubnative.mediation.config;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeConfigRequestTest
{
    @Test
    public void test_creationNotNull()
    {
        PubnativeConfigRequest request = new PubnativeConfigRequest();
        assertThat(request).isNotNull();
    }

    // TODO: Create tests
}
