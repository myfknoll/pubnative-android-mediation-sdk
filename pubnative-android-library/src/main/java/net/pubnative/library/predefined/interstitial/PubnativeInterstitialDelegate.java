package net.pubnative.library.predefined.interstitial;

import android.content.Context;
import android.content.Intent;

import net.pubnative.library.Pubnative;
import net.pubnative.library.PubnativeContract.Request;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivity;
import net.pubnative.library.predefined.PubnativeActivityDelegate;
import net.pubnative.library.predefined.PubnativeActivityDelegateManager;
import net.pubnative.library.predefined.PubnativeActivityListener;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.AdRequest.Endpoint;
import net.pubnative.library.request.AdRequestListener;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class PubnativeInterstitialDelegate extends PubnativeActivityDelegate implements
        AdRequestListener
{
    private int       LAUNCH_FLAGS = FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
    private AdRequest request;

    public static void Create(Context context, String app_token, PubnativeActivityListener listener)
    {
        PubnativeInterstitialDelegate delegate = new PubnativeInterstitialDelegate(context, app_token, listener);
        PubnativeActivityDelegateManager.addDelegate(delegate);
    }

    public PubnativeInterstitialDelegate(Context context, String app_token, PubnativeActivityListener listener)
    {
        super(context, app_token, listener);
        this.request = new AdRequest(this.context);
        this.request.setParameter(Request.APP_TOKEN, this.app_token);
        this.request.setParameter(Request.AD_COUNT, "1");
        this.request.setParameter(Request.ICON_SIZE, "200x200");
        this.request.setParameter(Request.BANNER_SIZE, "1200x627");
        this.request.start(Endpoint.NATIVE, this);
    }

    @Override
    public void onAdRequestStarted(AdRequest request)
    {
        // Do nothing
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads)
    {
        // Start the activity
        if (ads.size() > 0)
        {
            Intent adIntent = new Intent(context, PubnativeActivity.class);
            adIntent.setFlags(LAUNCH_FLAGS);
            adIntent.putExtra(PubnativeActivity.EXTRA_IDENTIFIER, this.identifier);
            adIntent.putExtra(PubnativeActivity.EXTRA_ADS, ads);
            adIntent.putExtra(PubnativeActivity.EXTRA_TYPE, Pubnative.FullScreen.INTERSTITIAL);
            this.context.startActivity(adIntent);
        }
        else
        {
            this.invokeListenerFailed(new Exception("Pubnative - NO FILL ERROR"));
        }
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception exception)
    {
        this.invokeListenerFailed(exception);
    }
}
