package net.pubnative.mediation;

import net.pubnative.mediation.model.PubnativeAdModel;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class SampleTest extends AndroidTestCase
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
    public void testSampleFail()
    {
        PubnativeAdModel model = new PubnativeAdModel();
        assertNotNull("Model shoulr not be null", null);
    }
    
    @SmallTest
    public void testSampleSuccess()
    {
        assertTrue("This should always success", true);
    }
}
