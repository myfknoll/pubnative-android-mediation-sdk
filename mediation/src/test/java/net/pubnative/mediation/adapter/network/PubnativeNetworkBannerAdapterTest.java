package net.pubnative.mediation.adapter.network;

import android.content.Context;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.PubnativeNetworkBanner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
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
    public void invokeFail_withNullData_returnsNull() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        bannerAdapter.invokeLoadFail(null);
        assertThat(bannerAdapter.mLoadListener).isNull();
    }

    @Test
    public void invokeLoadFail_withNormalData_callOnAdapterLoadFailMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        bannerAdapter.mLoadListener = banner;

        bannerAdapter.invokeLoadFail(PubnativeException.BANNER_PARAMETERS_INVALID);
        verify(banner).onAdapterLoadFail(eq(bannerAdapter), eq(PubnativeException.BANNER_PARAMETERS_INVALID));
    }

    @Test
    public void invokeLoadFinish_withNormalData_callOnAdapterLoadFinishMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        bannerAdapter.mLoadListener = banner;

        bannerAdapter.invokeLoadFinish(bannerAdapter);
        verify(banner).onAdapterLoadFinish(eq(bannerAdapter));
    }

    @Test
    public void invokeShow_withNormalData_callOnAdapterShowMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        PubnativeNetworkBannerAdapter.AdListener listener = mock(PubnativeNetworkBannerAdapter.AdListener.class);
        bannerAdapter.mAdListener = listener;

        bannerAdapter.invokeShow();
        verify(listener).onAdapterShow(eq(bannerAdapter));
    }

    @Test
    public void invokeHide_withNormalData_callOnAdapterHideMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        PubnativeNetworkBannerAdapter.AdListener listener = mock(PubnativeNetworkBannerAdapter.AdListener.class);
        bannerAdapter.mAdListener = listener;

        bannerAdapter.invokeHide();
        verify(listener).onAdapterHide(eq(bannerAdapter));
    }

    @Test
    public void invokeClick_withNormalData_callOnAdapterClickMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        PubnativeNetworkBannerAdapter.AdListener listener = mock(PubnativeNetworkBannerAdapter.AdListener.class);
        bannerAdapter.mAdListener = listener;

        bannerAdapter.invokeClick();
        verify(listener).onAdapterClick(eq(bannerAdapter));
    }

    @Test
    public void invokeImpressionConfirmed_withNormalData_callOnAdapterImpressionConfirmedMethod() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        PubnativeNetworkBannerAdapter.AdListener listener = mock(PubnativeNetworkBannerAdapter.AdListener.class);
        bannerAdapter.mAdListener = listener;

        bannerAdapter.invokeImpressionConfirmed();
        verify(listener).onAdapterImpressionConfirmed(eq(bannerAdapter));
    }

    @Test
    public void startExecuteMethod_withNormalData_callLoadAndStartTimeout() {
        PubnativeNetworkBannerAdapter bannerAdapter = new PubnativeLibraryNetworkBannerAdapter(null);
        PubnativeLibraryNetworkBannerAdapter spy = (PubnativeLibraryNetworkBannerAdapter) spy(bannerAdapter);
        Context context = mock(Context.class);
        spy.execute(context, 5);

        verify(spy, atLeastOnce()).load(eq(context));
        verify(spy, atLeastOnce()).startTimeout(5);
    }
}