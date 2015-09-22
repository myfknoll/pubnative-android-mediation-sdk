package net.pubnative.library.predefined;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.UUID;

public abstract class PubnativeActivityDelegate extends BroadcastReceiver
{
    public String identifier = UUID.randomUUID()
                                   .toString();
    protected WeakReference<PubnativeActivityListener> listener;
    protected Context                                  context;
    protected String                                   app_token;
    protected boolean registered = false;

    public static void Create(Context context, String app_token, PubnativeActivityListener listener)
    {
        // Do nothing
    }

    public PubnativeActivityDelegate(Context context, String app_token,
                                     PubnativeActivityListener listener)
    {
        this.listener = new WeakReference<PubnativeActivityListener>(listener);
        this.context = context;
        this.app_token = app_token;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj.getClass()
               .isInstance(PubnativeActivityDelegate.class))
        {
            PubnativeActivityDelegate delegate = (PubnativeActivityDelegate) obj;
            result = this.identifier == delegate.identifier;
        }
        return result;
    }

    private void unregisterBroadcastReceiver()
    {
        this.registered = false;
        LocalBroadcastManager.getInstance(this.context)
                             .unregisterReceiver(this);
    }

    private void registerBroadcastReceiver()
    {
        if (!this.registered)
        {
            this.registered = true;
            LocalBroadcastManager.getInstance(this.context)
                                 .registerReceiver(this, new IntentFilter(this.identifier));
        }
    }

    protected void invokeListenerStart()
    {
        if (this.listener.get() != null)
        {
            this.listener.get()
                         .onPubnativeActivityStarted(this.identifier);
        }
    }

    protected void invokeListenerOpened()
    {
        if (this.listener.get() != null)
        {
            this.listener.get()
                         .onPubnativeActivityOpened(this.identifier);
        }
    }

    protected void invokeListenerFailed(Exception exception)
    {
        PubnativeActivityDelegateManager.removeDelegate(this);
        if (this.listener.get() != null)
        {
            this.listener.get()
                         .onPubnativeActivityFailed(this.identifier, exception);
        }
    }

    protected void invokeListenerClosed()
    {
        PubnativeActivityDelegateManager.removeDelegate(this);
        if (this.listener.get() != null)
        {
            this.listener.get()
                         .onPubnativeActivityClosed(this.identifier);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String message = intent.getStringExtra(PubnativeActivity.EVENT);
        if (PubnativeActivity.EVENT_ACTIVITY_CREATE.equals(message))
        {
            // Do nothing
        }
        else if (PubnativeActivity.EVENT_ACTIVITY_PAUSE.equals(message) ||
                PubnativeActivity.EVENT_ACTIVITY_STOP.equals(message) ||
                PubnativeActivity.EVENT_ACTIVITY_DESTROY.equals(message))
        {
            this.unregisterBroadcastReceiver();
        }
        else if (PubnativeActivity.EVENT_ACTIVITY_RESUME.equals(message))
        {
            this.registerBroadcastReceiver();
        }
        else if (PubnativeActivity.EVENT_LISTENER_START.equals(message))
        {
            this.invokeListenerStart();
        }
        else if (PubnativeActivity.EVENT_LISTENER_OPENED.equals(message))
        {
            this.invokeListenerOpened();
        }
        else if (PubnativeActivity.EVENT_LISTENER_FAILED.equals(message))
        {
            Exception error = (Exception) intent.getExtras()
                                                .getSerializable(PubnativeActivity.DATA);
            this.invokeListenerFailed(error);
        }
        else if (PubnativeActivity.EVENT_LISTENER_CLOSED.equals(message))
        {
            this.invokeListenerClosed();
        }
    }
}
