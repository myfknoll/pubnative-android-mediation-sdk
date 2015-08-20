package net.pubnative.mediation.config;

import android.content.Context;

import net.pubnative.mediation.BuildConfig;
import net.pubnative.mediation.model.PubnativeCacheAdModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by davidmartin on 15/08/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest(PubnativeConfigManager.class)
public class PubnativeAdCacheTest
{
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private Context applicationContext;

    @Before
    public void setUp()
    {
        applicationContext = RuntimeEnvironment.application.getApplicationContext();
    }

    @Test
    public void setLastUpdateWithDifferentValues()
    {
        // Sets positive values
        PubnativeAdCache.setLastUpdate(this.applicationContext, Long.valueOf(1));
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isEqualTo(1);

        // Does nothing when null context
        PubnativeAdCache.setLastUpdate(null, null);
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isEqualTo(1);

        // Deletes on
        // Negative value
        PubnativeAdCache.setLastUpdate(this.applicationContext, Long.valueOf(1));
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isEqualTo(1);
        PubnativeAdCache.setLastUpdate(this.applicationContext, Long.valueOf(-1));
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isNull();

        // Zero value
        PubnativeAdCache.setLastUpdate(this.applicationContext, Long.valueOf(1));
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isEqualTo(1);
        PubnativeAdCache.setLastUpdate(this.applicationContext, Long.valueOf(0));
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isNull();
        // Null value
        PubnativeAdCache.setLastUpdate(this.applicationContext, Long.valueOf(1));
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isEqualTo(1);
        PubnativeAdCache.setLastUpdate(this.applicationContext, null);
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isNull();
    }

    @Test
    public void getLastUpdateWithDifferentValues()
    {
        // Returns null first time
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isNull();

        // Returns last setted value
        PubnativeAdCache.setLastUpdate(this.applicationContext, Long.valueOf(1));
        assertThat(PubnativeAdCache.getLastUpdate(this.applicationContext)).isEqualTo(1);

        // Returns null with null parameters
        assertThat(PubnativeAdCache.getLastUpdate(null)).isNull();
    }

    @Test
    public void setsAdsCacheParameterValues()
    {
        List<PubnativeCacheAdModel> emptyList = mock(List.class);
        when(emptyList.size()).thenReturn(0);
        List<PubnativeCacheAdModel> adsList = new ArrayList();
        PubnativeCacheAdModel cacheModel = new PubnativeCacheAdModel(null);
        adsList.add(cacheModel);

        // Does nothing when passing null context
        PubnativeAdCache.setCachedAds(null, adsList);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();

        // Sets the cache list passed as parameter
        PubnativeAdCache.setCachedAds(this.applicationContext, adsList);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotNull();
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotEmpty();

        // Empty lists does nothing
        PubnativeAdCache.setCachedAds(this.applicationContext, emptyList);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();

        // Empty lists removes the current cache
        PubnativeAdCache.setCachedAds(this.applicationContext, adsList);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotNull();
        PubnativeAdCache.setCachedAds(this.applicationContext, emptyList);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();

        // Null list removes the current cache
        PubnativeAdCache.setCachedAds(this.applicationContext, adsList);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotNull();
        PubnativeAdCache.setCachedAds(this.applicationContext, null);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();
    }

    @Test
    public void getAdsCacheWithDifferentParameterValues()
    {
        List<PubnativeCacheAdModel> adsCache = new ArrayList<PubnativeCacheAdModel>();

        // Returns null when nothing has been setted up
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();

        // Does nothing when passing null context
        PubnativeAdCache.setCachedAds(null, adsCache);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();

        // Empty lists removes the current cache
        PubnativeAdCache.setCachedAds(this.applicationContext, adsCache);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();

        // Sets the cache list passed as parameter
        PubnativeCacheAdModel cacheModel = new PubnativeCacheAdModel(null);
        adsCache.add(cacheModel);
        PubnativeAdCache.setCachedAds(this.applicationContext, adsCache);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotNull();
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotEmpty();
    }

    @Test
    public void cacheAdWithDifferentParameterValues()
    {
        PubnativeCacheAdModel modelSpy = spy(new PubnativeCacheAdModel(null));

        // Does nothing with any null parameter
        PubnativeAdCache.cacheAd(null, null);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();
        PubnativeAdCache.cacheAd(this.applicationContext, null);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();
        PubnativeAdCache.cacheAd(null, modelSpy);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNull();

        // Caches the passed model
        PubnativeAdCache.cacheAd(this.applicationContext, modelSpy);
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotNull();
        assertThat(PubnativeAdCache.getCachedAds(this.applicationContext)).isNotEmpty();
    }

    // TODO: updateAdsCache removes cache when selected pacing time passed
}
