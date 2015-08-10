package net.pubnative.library.predefined;

import java.lang.ref.WeakReference;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public abstract class PubnativeActivityDelegate extends BroadcastReceiver
{
    public String                                      identifier = UUID.randomUUID().toString();
    protected WeakReference<PubnativeActivityListener> listener;
    protected Context                                  context;
    protected String                                   app_token;
    protected boolean                                  registered = false;

    public static void Create(Context context, String app_token, PubnativeActivityListener listener)
    {
        // Do nothing
    }

    public PubnativeActivityDelegate(Context context, String app_token, PubnativeActivityListener listener)
    {
        this.listener = new WeakReference<PubnativeActivityListener>(listener);
        this.context = context;
        this.app_token = app_token;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj.getClass().isInstance(PubnativeActivityDelegate.class))
        {
            PubnativeActivityDelegate delegate = (PubnativeActivityDelegate) obj;
            result = this.identifier == delegate.identifier;
        }
        return result;
    }

    private void unregisterBroadcastReceiver()
    {
        this.registered = false;
        LocalBroadcastManager.getInstance(this.context).unregisterReceiver(this);
    }

    private void registerBroadcastReceiver()
    {
        if (!this.registered)
        {
            this.registered = true;
            LocalBroadcastManager.getInstance(this.context).registerReceiver(this, new IntentFilter(this.identifier));
        }
    }

    protected void invokeListenerStart()
    {
        if (this.listener.get() != null)
        {
            this.listener.get().onPubnativeActivityStarted(this.identifier);
        }
    }

    protected void invokeListenerOpened()
    {
        if (this.listener.get() != null)
        {
            this.listener.get().onPubnativeActivityOpened(this.identifier);
        }
    }

    protected void invokeListenerFailed(Exception exception)
    {
        PubnativeActivityDelegateManager.removeDelegate(this);
        if (this.listener.get() != null)
        {
            this.listener.get().onPubnativeActivityFailed(this.identifier, exception);
        }
    }

    protected void invokeListenerClosed()
    {
        PubnativeActivityDelegateManager.removeDelegate(this);
        if (this.listener.get() != null)
        {
            this.listener.get().onPubnativeActivityClosed(this.identifier);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String message = intent.getStringExtra(PubnativeActivity.EVENT);
        switch (message)
        {
            case PubnativeActivity.EVENT_ACTIVITY_CREATE:
                break;
            case PubnativeActivity.EVENT_ACTIVITY_PAUSE:
            case PubnativeActivity.EVENT_ACTIVITY_STOP:
            case PubnativeActivity.EVENT_ACTIVITY_DESTROY:
            {
                this.unregisterBroadcastReceiver();
            }
                break;
            case PubnativeActivity.EVENT_ACTIVITY_RESUME:
            {
                this.registerBroadcastReceiver();
            }
                break;
            case PubnativeActivity.EVENT_LISTENER_START:
            {
                this.invokeListenerStart();
            }
                break;
            case PubnativeActivity.EVENT_LISTENER_OPENED:
            {
                this.invokeListenerOpened();
            }
                break;
            case PubnativeActivity.EVENT_LISTENER_FAILED:
            {
                Exception error = (Exception) intent.getExtras().getSerializable(PubnativeActivity.DATA);
                this.invokeListenerFailed(error);
            }
                break;
            case PubnativeActivity.EVENT_LISTENER_CLOSED:
            {
                this.invokeListenerClosed();
            }
                break;
        }
    }
}
