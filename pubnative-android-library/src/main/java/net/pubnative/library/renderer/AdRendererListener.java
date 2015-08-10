package net.pubnative.library.renderer;

public interface AdRendererListener
{
    void onAdRenderStarted(AdRenderer renderer);

    void onAdRenderFailed(AdRenderer renderer, Exception e);

    void onAdRenderFinished(AdRenderer renderer);
}
