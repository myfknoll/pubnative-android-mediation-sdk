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

import android.content.Context;
import android.graphics.Bitmap;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import net.pubnative.library.model.NativeAdModel;

import org.droidparts.net.image.ImageFetchListener;
import org.droidparts.net.image.ImageReshaper;

import static org.droidparts.util.Strings.isEmpty;
import static org.droidparts.util.ui.ViewUtils.setInvisible;

public class NativeAdRenderer extends AdRenderer<NativeAdModel> implements
        ImageFetchListener
{
    private int          imageCounter     = 0;
    private boolean      finishedInvoked;
    public ImageReshaper iconReshaper;
    public ImageReshaper bannerReshaper;
    public ImageReshaper portraitBannerReshaper;
    public ImageView     iconView;
    public ImageView     bannerView;
    public ImageView     portraitBannerView;
    public RatingBar     ratingView;
    public TextView      titleView;
    public TextView      subTitleView;
    public TextView      descriptionView;
    public TextView      categoryView;
    public TextView      downloadView;
    public boolean       enableAnimations = false;

    public NativeAdRenderer(Context context)
    {
        super(context);
    }

    @Override
    public void render(NativeAdModel ad, AdRendererListener listener)
    {
        super.render(ad, listener);
        this.invokeStarted(this);
        this.finishedInvoked = false;
        this.imageCounter = 3;
        if (titleView != null)
        {
            titleView.setText(this.ad.title);
        }
        if (subTitleView != null)
        {
            subTitleView.setText(this.ad.app_details.category);
        }
        if (ratingView != null)
        {
            ratingView.setRating(this.ad.app_details.store_rating);
        }
        if (descriptionView != null)
        {
            descriptionView.setText(this.ad.description);
            setInvisible(isEmpty(this.ad.description), descriptionView);
        }
        if (categoryView != null)
        {
            categoryView.setText(this.ad.app_details.category);
        }
        if (downloadView != null)
        {
            downloadView.setText(this.ad.ctaText);
        }
        if (iconView != null)
        {
            this.imageFetcher.attachImage(this.ad.iconUrl, iconView, this.iconReshaper, 0, this);
        }
        else
        {
            this.countDown();
        }
        if (bannerView != null)
        {
            this.imageFetcher.attachImage(this.ad.bannerUrl, bannerView, this.bannerReshaper, 0, this);
        }
        else
        {
            this.countDown();
        }
        if (portraitBannerView != null)
        {
            this.imageFetcher.attachImage(this.ad.portraitBannerUrl, portraitBannerView, this.portraitBannerReshaper, 0, this);
        }
        else
        {
            this.countDown();
        }
    }

    private void countDown()
    {
        if (this.imageCounter > 0)
        {
            this.imageCounter--;
        }
    }

    private void checkFinished()
    {
        if (imageCounter <= 0 && !this.finishedInvoked)
        {
            this.invokeFinished(this);
            this.finishedInvoked = true;
        }
    }

    @Override
    public void onFetchAdded(ImageView imageView, String imgUrl)
    {
        setInvisible(true, imageView);
    }

    @Override
    public void onFetchCompleted(ImageView imageView, String imgUrl, Bitmap bm)
    {
        if (this.enableAnimations)
        {
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setInterpolator(new AccelerateInterpolator());
            fadeIn.setDuration(250);
            imageView.startAnimation(fadeIn);
        }
        setInvisible(false, imageView);
        this.countDown();
        this.checkFinished();
    }

    @Override
    public void onFetchFailed(ImageView imageView, String imgUrl, Exception e)
    {
        this.countDown();
        this.invokeFailed(this, e);
        this.checkFinished();
    }

    @Override
    public void onFetchProgressChanged(ImageView imageView, String imgUrl, int kBTotal, int kBReceived)
    {
        // Do nothing
    }
}
