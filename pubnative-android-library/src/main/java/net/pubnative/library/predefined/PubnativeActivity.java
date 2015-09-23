package net.pubnative.library.predefined;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.library.Pubnative;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.game_list.PubnativeGameListView;
import net.pubnative.library.predefined.interstitial.PubnativeInterstitialView;

import java.util.ArrayList;

public class PubnativeActivity extends Activity
{
    public static final String                EXTRA_ADS              = "ads";
    public static final String                EXTRA_IDENTIFIER       = "identifier";
    public static final String                EXTRA_TYPE             = "view_type";
    public static final String                EVENT                  = "event";
    public static final String                DATA                   = "data";
    public static final String                EVENT_ACTIVITY_CREATE  = "activity_create";
    public static final String                EVENT_ACTIVITY_PAUSE   = "activity_pause";
    public static final String                EVENT_ACTIVITY_RESUME  = "activity_resume";
    public static final String                EVENT_ACTIVITY_STOP    = "activity_stop";
    public static final String                EVENT_ACTIVITY_DESTROY = "activity_destroy";
    public static final String                EVENT_LISTENER_START   = "listener_start";
    public static final String                EVENT_LISTENER_OPENED  = "listener_opened";
    public static final String                EVENT_LISTENER_FAILED  = "listener_failed";
    public static final String                EVENT_LISTENER_CLOSED  = "listener_closed";
    public ViewGroup                          contentView;
    public View                               adContainer;
    public ArrayList<? extends NativeAdModel> ads;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v("PubnativeActivity", "onCreate");
        this.streamMessageEvent(EVENT_ACTIVITY_CREATE);
        this.streamMessageEvent(EVENT_LISTENER_OPENED);
        ArrayList<? extends NativeAdModel> serializedAds = (ArrayList<? extends NativeAdModel>) getIntent().getSerializableExtra(PubnativeActivity.EXTRA_ADS);
        if (serializedAds != null)
        {
            this.ads = serializedAds;
            String type = getIntent().getStringExtra(EXTRA_TYPE);
            if(Pubnative.FullScreen.INTERSTITIAL.equals(type))
            {
                this.contentView = new PubnativeInterstitialView(this, (ArrayList<NativeAdModel>) this.ads);
            }
            else if(Pubnative.FullScreen.GAME_LIST.equals(type))
            {
                this.contentView = new PubnativeGameListView(this, (ArrayList<NativeAdModel>) this.ads);
            }

            if (this.contentView != null)
            {
                this.setContentView(this.contentView);
            }
            else
            {
                this.streamMessageEvent(EVENT_LISTENER_FAILED, new Exception("Pubnative - View for activity not available"));
                this.finish();
            }
        }
        else
        {
            this.streamMessageEvent(EVENT_LISTENER_FAILED, new Exception("Pubnative - Activity error: ads not found"));
            this.finish();
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        Log.v("PubnativeActivity", "finish");
        this.streamMessageEvent(EVENT_LISTENER_CLOSED);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v("PubnativeActivity", "onResume");
        this.streamMessageEvent(EVENT_ACTIVITY_RESUME);
    }

    @Override
    protected void onPause()
    {
        Log.v("PubnativeActivity", "onPause");
        super.onPause();
        this.streamMessageEvent(EVENT_ACTIVITY_PAUSE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v("PubnativeActivity", "onStop");
        this.streamMessageEvent(EVENT_ACTIVITY_STOP);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.v("PubnativeActivity", "onDestroy");
        this.streamMessageEvent(EVENT_ACTIVITY_DESTROY);
    }

    protected String getExtraString(String extraStringKey)
    {
        String result = null;
        Intent intent = this.getIntent();
        if (intent != null)
        {
            result = intent.getExtras().getString(extraStringKey);
        }
        return result;
    }

    public void streamMessageEvent(String event)
    {
        this.streamMessageEvent(event, null);
    }

    public void streamMessageEvent(String event, Exception extraObject)
    {
        Intent messageIntent = new Intent(this.getExtraString(EXTRA_IDENTIFIER));
        messageIntent.putExtra(EVENT, event);
        if (extraObject != null)
        {
            messageIntent.putExtra(DATA, extraObject);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
    }
}
