package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.PubnativeNetworkBanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PubnativeNetworkBannerAdapterTest {

    @Mock private PubnativeNetworkBanner banner;
    @Mock private PubnativeNetworkAdapter networkAdapter;

    @Test
    public void setListeners_withNullData_returnsNull() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        bannerAdapter.setLoadListener(null);
        assertThat(bannerAdapter.mLoadListener).isNull();

        bannerAdapter.setAdListener(null);
        assertThat(bannerAdapter.mLoadListener).isNull();
    }

    @Test
    public void startInvokeFail_withNullData_returnsNull() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        bannerAdapter.invokeLoadFail(null);
        assertThat(bannerAdapter.mLoadListener).isNull();
    }

    @Test
    public void startInvokeFail_withRightData_callOnAdapterLoadFailMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        bannerAdapter.mLoadListener = banner;

        bannerAdapter.invokeLoadFail(PubnativeException.BANNER_PARAMETERS_INVALID);
        verify(banner).onAdapterLoadFail(bannerAdapter, PubnativeException.BANNER_PARAMETERS_INVALID);
    }
}