package net.pubnative.mediation.request;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PubnativeNetworkRequestTest
{
    @Test
    public void test_creationNotNull()
    {
        PubnativeNetworkRequest request = new PubnativeNetworkRequest();
        assertThat(request).isNotNull();
    }

    // TODO: Create tests
}
