package net.pubnative.library;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import net.pubnative.library.managers.TaskManager;
import net.pubnative.library.predefined.PubnativeActivityListener;
import net.pubnative.library.predefined.game_list.PubnativeGameListDelegate;
import net.pubnative.library.predefined.interstitial.PubnativeInterstitialDelegate;

import java.lang.ref.WeakReference;

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
            if(FullScreen.INTERSTITIAL.equals(type))
            {
                PubnativeInterstitialDelegate.Create(context, app_token, listener);
            }
            else if(FullScreen.GAME_LIST.equals(type))
            {
                PubnativeGameListDelegate.Create(context, app_token, listener);
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
