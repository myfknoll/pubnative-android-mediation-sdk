package net.pubnative.library;

import java.lang.ref.WeakReference;

import net.pubnative.library.managers.TaskManager;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivityListener;
import net.pubnative.library.predefined.game_list.PubnativeGameListDelegate;
import net.pubnative.library.predefined.interstitial.PubnativeInterstitialDelegate;
import net.pubnative.library.util.WebRedirector;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class Pubnative
{
    public interface FullScreen
    {
        String INTERSTITIAL = "interstitial";
        String GAME_LIST    = "game_list";
    }

    public static void onPause()
    {
        TaskManager.onPause();
    }

    public static void onResume()
    {
        TaskManager.onResume();
    }

    public static void onDestroy()
    {
        TaskManager.onDestroy();
    }

    public static void showInPlayStoreViaBrowser(Activity act, NativeAdModel ad)
    {
        new WebRedirector(act, ad.app_details.store_id, ad.click_url).doBrowserRedirect();
    }

    public static void showInPlayStoreViaDialog(Activity act, NativeAdModel ad)
    {
        showInPlayStoreViaDialog(act, ad, 3000);
    }

    public static void showInPlayStoreViaDialog(Activity act, NativeAdModel ad, int timeout)
    {
        new WebRedirector(act, ad.app_details.store_id, ad.click_url).doBackgroundRedirect(timeout);
    }

    public static void show(Context context, String type, String app_token, PubnativeActivityListener listener)
    {
        if (!Pubnative.isMainThread())
        {
            // If were not in the main thread, call this from the main thread
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new MainThreadRunnable(context, app_token, type, listener));
        }
        else
        {
            switch (type)
            {
                case FullScreen.INTERSTITIAL:
                    PubnativeInterstitialDelegate.Create(context, app_token, listener);
                    break;
                case FullScreen.GAME_LIST:
                    PubnativeGameListDelegate.Create(context, app_token, listener);
                    break;
                default:
                    // Nothing to do
                    break;
            }
        }
    }

    // HELPERS
    private static boolean isMainThread()
    {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * This runnable is used to contain the call data from the background thread
     * while waiting for the main thread execution
     */
    private static class MainThreadRunnable implements Runnable
    {
        Context                                  context;
        String                                   app_token;
        String                                   type;
        WeakReference<PubnativeActivityListener> listener;

        public MainThreadRunnable(final Context context, final String type, final String app_token, final PubnativeActivityListener listener)
        {
            this.context = context;
            this.app_token = app_token;
            this.type = type;
            this.listener = new WeakReference<PubnativeActivityListener>(listener);
        }

        @Override
        public void run()
        {
            Pubnative.show(this.context, this.type, this.app_token, this.listener.get());
        }
    }
}
