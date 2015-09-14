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
package net.pubnative.library.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;

import org.droidparts.util.L;

public class IdUtil
{
    public static boolean isPackageInstalled(Context context, String pkgName)
    {
        boolean result = false;
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(pkgName, 0);
            result = true;
        }
        catch (Exception e)
        {
            // Do nothing
        }
        return result;
    }

    public static String getPackageName(Context ctx)
    {
        PackageInfo pInfo = getPackageInfo(ctx);
        return (pInfo != null) ? pInfo.packageName : "";
    }

    private static PackageInfo getPackageInfo(Context ctx)
    {
        try
        {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
        }
        catch (NameNotFoundException e)
        {
            L.w("Error getting package info.");
            return null;
        }
    }

    public static boolean isTablet(Context context)
    {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static void getAndroidAdvertisingID(Context context, AndroidAdvertisingIDTask.AndroidAdvertisingIDTaskListener listener)
    {
        new AndroidAdvertisingIDTask().setListener(listener).execute(context);
    }

    public static class AndroidAdvertisingIDTask extends AsyncTask<Context, Void, String>
    {
        private AndroidAdvertisingIDTaskListener listener;
        public interface AndroidAdvertisingIDTaskListener
        {
            void onAndroidAdvertisingIDTaskFinished(String result);
        }

        public AndroidAdvertisingIDTask setListener(AndroidAdvertisingIDTaskListener listener)
        {
            this.listener = listener;
            return this;
        }

        @Override
        protected String doInBackground(Context... contexts)
        {
            String result = null;
            Context context = contexts[0];
            if(context != null)
            {
                Info adInfo = null;
                try
                {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    if (adInfo != null)
                    {
                        result = adInfo.getId();
                    }
                }
                catch (Exception e)
                {
                    Log.e("Pubnative", "Error retrieving androidAdvertisingID: " + e.toString());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(this.listener != null)
            {
                this.listener.onAndroidAdvertisingIDTaskFinished(result);
            }
        }
    }

    public static Location getLastLocation(Context ctx)
    {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        Location loc = null;
        for (String prov : lm.getProviders(true))
        {
            loc = lm.getLastKnownLocation(prov);
            if (loc != null)
            {
                break;
            }
        }
        return loc;
    }

    private IdUtil()
    {
    }
}
