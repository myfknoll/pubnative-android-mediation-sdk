package net.pubnative.mediation;

import net.pubnative.mediation.model.PubnativeAdModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SampleTest
{
    @Before
    public void before_each_test()
    {
        // Do nothing
    }

    @After
    public void after_each_test()
    {
        // Do nothing
    }

    /**
     * Mockito usage sample
     */
    @Test
    public void test_mockito()
    {

        List mockedList = mock(List.class);
        // operate normally as if it where a List object
        mockedList.add("one");
        mockedList.clear();

        // post-operation verification
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }

    /**
     * AssertJ usage sample
     */
    @Test
    public void test_assertj()
    {
        assertThat(true).isTrue();
    }

    /**
     * Random test
     */
    @Test
    public void test_constructor()
    {
        PubnativeAdModel model = new PubnativeAdModel();
        assertThat(model).isNotNull();
    }
}
