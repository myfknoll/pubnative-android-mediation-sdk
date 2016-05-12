package net.pubnative.mediation.adapter.network;

import android.content.Context;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.PubnativeNetworkBanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PubnativeNetworkBannerAdapterTest {

    @Mock private PubnativeNetworkBanner banner;

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
    public void startInvokeLoadFail_withNormalData_callOnAdapterLoadFailMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        bannerAdapter.mLoadListener = banner;

        bannerAdapter.invokeLoadFail(PubnativeException.BANNER_PARAMETERS_INVALID);
        verify(banner).onAdapterLoadFail(bannerAdapter, PubnativeException.BANNER_PARAMETERS_INVALID);
    }

    @Test
    public void startExecuteMethod_withNormalData_callLoadAndStartTimeout() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        PubnativeLibraryNetworkBannerAdapter spy = (PubnativeLibraryNetworkBannerAdapter) spy(bannerAdapter);
        Context context = mock(Context.class);
        spy.execute(context, 5);

        verify(spy, atLeastOnce()).load(context);
        verify(spy, atLeastOnce()).startTimeout(5);
    }
}