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
package net.pubnative.library.model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import net.pubnative.library.PubnativeContract.Response;
import net.pubnative.library.PubnativeContract.Response.NativeAd;
import net.pubnative.library.managers.TaskManager;
import net.pubnative.library.managers.TrackingManager;
import net.pubnative.library.managers.task.TaskItem;
import net.pubnative.library.managers.task.TaskItem.TaskItemListener;
import net.pubnative.library.managers.task.TrackViewImpressionTask;

import org.droidparts.annotation.serialize.JSON;
import org.droidparts.model.Model;

import java.util.ArrayList;

public class NativeAdModel extends Model implements NativeAd, TaskItemListener
{
    /**
     * 
     */
    private static final long     serialVersionUID = 2L;
    //
    // FIELDS
    //
    @JSON(key = TYPE)
    public String                 type;
    @JSON(key = TITLE)
    public String                 title;
    @JSON(key = DESCRIPTION)
    public String                 description;
    @JSON(key = CTA_TEXT)
    public String                 ctaText;
    @JSON(key = ICON_URL)
    public String                 iconUrl;
    @JSON(key = BANNER_URL)
    public String                 bannerUrl;
    @JSON(key = CLICK_URL)
    public String                 click_url;
    @JSON(key = BEACONS)
    public ArrayList<BeaconModel> beacons;
    @JSON(key = REVENUE_MODEL)
    public String                 revenue_model;
    @JSON(key = POINTS)
    public String                 points;
    @JSON(key = PORTRAIT_BANNER_URL)
    public String                 portraitBannerUrl;
    @JSON(key = APP_DETAILS, optional = true)
    public AppDetailsModel        app_details;
    private Context               context;

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

    public void confirmBeacon(Context context, String beacon)
    {
        if (!TrackingManager.isTrackedBeacon(context, this, beacon))
        {
            TrackingManager.TrackBeacon(context, this, beacon);
        }
    }

    public void confirmImpressionAutomatically(Context context, View view)
    {
        this.context = context;
        TrackViewImpressionTask task = new TrackViewImpressionTask(this, view);
        TaskManager.addLooperTask(task);
    }

    @Override
    public void onTaskItemListenerFinished(TaskItem item)
    {
        Log.v("NativeAdModel", "onTaskItemListenerFinished");
        this.confirmBeacon(this.context, Response.NativeAd.Beacon.TYPE_IMPRESSION);
    }

    @Override
    public void onTaskItemListenerFailed(TaskItem item, Exception e)
    {
        Log.v("NativeAdModel", "onTaskItemListenerFailed: " + e);
        // No errors should arrive here
    }
}
