/**
 * Copyright 2014 PubNative GmbH
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pubnative.library.model;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.pubnative.library.PubnativeContract.Response;
import net.pubnative.library.PubnativeContract.Response.NativeAd;
import net.pubnative.library.managers.TaskManager;
import net.pubnative.library.managers.TrackingManager;
import net.pubnative.library.managers.task.TaskItem;
import net.pubnative.library.managers.task.TaskItem.TaskItemListener;
import net.pubnative.library.managers.task.TrackViewImpressionTask;
import net.pubnative.library.task.AsyncHttpTask;

import org.droidparts.annotation.serialize.JSON;
import org.droidparts.model.Model;
import org.droidparts.util.intent.IntentHelper;

import java.util.ArrayList;

import static android.content.Intent.ACTION_VIEW;

public class NativeAdModel extends Model implements NativeAd, TaskItemListener
{
    private static final String LOADING_TEXT     = "Loading...";
    private static final String MARKET_PREFIX    = "market://details?id=";
    private static final String PLAYSTORE_PREFIX = "https://play.google.com/store/apps/details?id=";


    public interface Listener
    {
        void onAdImpression(NativeAdModel model);
    }

    /**
     *
     */
    private static final long serialVersionUID = 2L;
    //
    // FIELDS
    //
    @JSON(key = TYPE)
    public  String                 type;
    @JSON(key = TITLE)
    public  String                 title;
    @JSON(key = DESCRIPTION)
    public  String                 description;
    @JSON(key = CTA_TEXT)
    public  String                 ctaText;
    @JSON(key = ICON_URL)
    public  String                 iconUrl;
    @JSON(key = BANNER_URL)
    public  String                 bannerUrl;
    @JSON(key = CLICK_URL)
    public  String                 click_url;
    @JSON(key = STORE_RATING, optional = true)
    public  float                  store_rating;
    @JSON(key = BEACONS)
    public  ArrayList<BeaconModel> beacons;
    @JSON(key = REVENUE_MODEL)
    public  String                 revenue_model;
    @JSON(key = POINTS)
    public  String                 points;
    @JSON(key = PORTRAIT_BANNER_URL, optional = true)
    public  String                 portraitBannerUrl;
    @JSON(key = APP_DETAILS, optional = true)
    public  AppDetailsModel        app_details;
    private Context                context;
    private Listener               listener;
    private Dialog                 loadingDialog;

    public float getStoreRating()
    {
        float result = 0;
        if (this.app_details != null)
        {
            result = this.app_details.store_rating;
        }
        else
        {
            result = this.store_rating;
        }
        return result;
    }

    public String getBeaconURL(String beaconType)
    {
        String result = null;
        for (BeaconModel beacon : this.beacons)
        {
            if (beacon.type.equals(beaconType))
            {
                result = beacon.url;
                break;
            }
        }
        return result;
    }

    public void open(Context context)
    {
        if (this.click_url != null)
        {
            if (this.app_details != null && this.app_details.store_id != null)
            {
                this.doBackgroundRedirect();
            }
            else
            {
                this.doBrowserRedirect();
            }
        }
    }

    private void doBrowserRedirect()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.click_url));
        IntentHelper.startActivityOrWarn(this.context, intent);
    }

    private void doBackgroundRedirect()
    {
        try
        {
            loadingDialog = ProgressDialog.show(this.context, null, LOADING_TEXT, true);
            AsyncHttpTask task = new AsyncHttpTask(this.context);
            task.setListener(new AsyncHttpTask.AsyncHttpTaskListener()
            {
                @Override
                public void onAsyncHttpTaskFinished(AsyncHttpTask task, String result)
                {
                    loadingDialog.dismiss();
                    openInPlayStore(MARKET_PREFIX + NativeAdModel.this.app_details.store_id);
                }

                @Override
                public void onAsyncHttpTaskFailed(AsyncHttpTask task, Exception e)
                {
                    loadingDialog.dismiss();
                    openInPlayStore(MARKET_PREFIX + NativeAdModel.this.app_details.store_id);
                }
            });
            task.execute(this.click_url);
        }
        catch (Exception ignored)
        {
            Toast.makeText(context, "Couldn't open the ad", Toast.LENGTH_SHORT).show();
            if (loadingDialog != null)
            {
                loadingDialog.dismiss();
            }
        }
    }

    private void openInPlayStore(String url)
    {
        Intent intent = new Intent(ACTION_VIEW, toPlayStoreUri(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentHelper.startActivityOrWarn(this.context, intent);
    }

    private static Uri toPlayStoreUri(String url)
    {
        if (url.startsWith(PLAYSTORE_PREFIX))
        {
            String pkgName = url.substring(PLAYSTORE_PREFIX.length());
            url = MARKET_PREFIX + pkgName;
        }
        return Uri.parse(url);
    }

    public void confirmBeacon(Context context, String beacon)
    {
        if (!TrackingManager.isTrackedBeacon(context, this, beacon))
        {
            TrackingManager.TrackBeacon(context, this, beacon);
        }
    }

    public void confirmImpressionAutomatically(Context context, View view)
    {
        this.confirmImpressionAutomatically(context, view, null);
    }

    public void confirmImpressionAutomatically(Context context, View view, Listener listener)
    {
        this.listener = listener;
        this.context = context;
        TrackViewImpressionTask task = new TrackViewImpressionTask(this, view);
        TaskManager.addLooperTask(task);
    }

    @Override
    public void onTaskItemListenerFinished(TaskItem item)
    {
        Log.v("NativeAdModel", "onTaskItemListenerFinished");
        this.confirmBeacon(this.context, Response.NativeAd.Beacon.TYPE_IMPRESSION);
        if (this.listener != null)
        {
            this.listener.onAdImpression(this);
        }
    }

    @Override
    public void onTaskItemListenerFailed(TaskItem item, Exception e)
    {
        Log.v("NativeAdModel", "onTaskItemListenerFailed: " + e);
        // No errors should arrive here
    }
}
