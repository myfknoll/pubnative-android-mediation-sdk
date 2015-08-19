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
package net.pubnative.library.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;

import net.pubnative.library.task.AsyncHttpTask;
import net.pubnative.library.task.AsyncHttpTask.HttpAsyncJSONTaskListener;

import org.droidparts.util.intent.IntentHelper;

import static android.content.Intent.ACTION_VIEW;

public class WebRedirector implements OnCancelListener
{
    private static final String LOADING_TEXT     = "Loading...";
    private static final String MARKET_PREFIX    = "market://details?id=";
    private static final String PLAYSTORE_PREFIX = "https://play.google.com/store/apps/details?id=";
    private final Context context;
    private final String   pkgName;
    private final String   link;
    private       Dialog   loadingDialog;
    private boolean cancelled = false;

    public WebRedirector(Context context, String pkgName, String link)
    {
        this.context = context;
        this.pkgName = pkgName;
        this.link = link;
    }

    public void doBrowserRedirect()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        IntentHelper.startActivityOrWarn(this.context, intent);
    }

    public void doBackgroundRedirect(int timeout)
    {
        try
        {
            loadingDialog = ProgressDialog.show(this.context, null, LOADING_TEXT, true);
            AsyncHttpTask task = new AsyncHttpTask(this.context);
            task.setListener(new HttpAsyncJSONTaskListener()
            {
                @Override
                public void onHttpAsyncJsonFinished(AsyncHttpTask task, String result)
                {
                    loadingDialog.dismiss();
                    openInPlayStore(MARKET_PREFIX + pkgName);
                }

                @Override
                public void onHttpAsyncJsonFailed(AsyncHttpTask task, Exception e)
                {
                    loadingDialog.dismiss();
                    openInPlayStore(MARKET_PREFIX + pkgName);
                }
            });
            task.execute(link);
        }
        catch (Exception ignored)
        {
            if (loadingDialog != null)
            {
                loadingDialog.dismiss();
            }
        }
    }

    public void cancel()
    {
        cancelled = true;
        loadingDialog.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        cancel();
    }

    private void openInPlayStore(String url)
    {
        if (!cancelled)
        {
            cancel();
            Intent intent = new Intent(ACTION_VIEW, toPlayStoreUri(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            IntentHelper.startActivityOrWarn(this.context, intent);
        }
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
}
