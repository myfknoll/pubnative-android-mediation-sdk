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
package net.pubnative.library.managers.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InvokeLinkTask
{
    public String                  link;
    public Context                 context;
    private InvokeLinkTaskListener listener;

    public interface InvokeLinkTaskListener
    {
        void onInvokeLinkTaskFinished(InvokeLinkTask task);
    }

    public InvokeLinkTask(Context context, InvokeLinkTaskListener listener, String link)
    {
        this.link = link;
        this.context = context;
        this.listener = listener;
    }

    public void execute()
    {
        new Handler(Looper.getMainLooper()).post(new MainThreadRunnable(this.context, this.listener, this.link, this));
    }

    private class MainThreadRunnable implements Runnable
    {
        InvokeLinkTask         task;
        InvokeLinkTaskListener listener;
        Context                context;
        WebView                webView;
        String                 link;

        public MainThreadRunnable(Context context, InvokeLinkTaskListener listener, String link, InvokeLinkTask task)
        {
            this.context = context;
            this.task = task;
            this.listener = listener;
            this.link = link;
        }

        @Override
        public void run()
        {
            this.webView = makeWebView();
            this.webView.loadUrl(this.link);
            this.listener.onInvokeLinkTaskFinished(this.task);
        }

        private WebView makeWebView()
        {
            WebViewClient wvc = new WebViewClient()
            {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url)
                {
                    return true;
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
                {
                    // Do nothing
                }
            };
            WebView wv = new WebView(this.context);
            wv.setWebChromeClient(new WebChromeClient());
            wv.clearCache(true);
            wv.clearHistory();
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv.setWebViewClient(wvc);
            return wv;
        }
    }
}
