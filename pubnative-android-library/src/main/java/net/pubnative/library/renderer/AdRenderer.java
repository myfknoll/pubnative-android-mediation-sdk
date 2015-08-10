/**
 * Copyright 2014 PubNative GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pubnative.library.renderer;

import java.lang.ref.WeakReference;

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.util.ImageFetcher;
import android.content.Context;
import android.view.View;

public abstract class AdRenderer<T extends NativeAdModel>
{
    public T                                    ad;
    protected View                              containerView;
    protected ImageFetcher                      imageFetcher;
    protected Context                           context;
    protected WeakReference<AdRendererListener> rendererListener;

    public AdRenderer(Context context)
    {
        this.context = context;
        this.imageFetcher = new ImageFetcher(context);
    }

    public void render(T ad, AdRendererListener listener)
    {
        this.ad = ad;
        this.rendererListener = new WeakReference<AdRendererListener>(listener);
    }

    protected void invokeStarted(AdRenderer<T> renderer)
    {
        if (this.rendererListener.get() != null)
        {
            this.rendererListener.get().onAdRenderStarted(this);
        }
    }

    protected void invokeFailed(AdRenderer<T> renderer, Exception e)
    {
        if (this.rendererListener.get() != null)
        {
            this.rendererListener.get().onAdRenderFailed(this, e);
        }
    }

    protected void invokeFinished(AdRenderer<T> renderer)
    {
        if (this.rendererListener.get() != null)
        {
            this.rendererListener.get().onAdRenderFinished(renderer);
        }
    }
}
