package net.pubnative.mediation;

import net.pubnative.mediation.model.PubnativeAdModel;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class SampleTest extends TestCase
{
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    @SmallTest
    public void testSampleNull()
    {
        PubnativeAdModel model = new PubnativeAdModel();
        assertNotNull("Model should not be null", model);
    }

    @SmallTest
    public void testSampleTrue()
    {
        assertTrue("This should always success", true);
    }
}
