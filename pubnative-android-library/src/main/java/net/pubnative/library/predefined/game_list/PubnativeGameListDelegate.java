package net.pubnative.library.predefined.game_list;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import java.util.ArrayList;

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
import android.content.Context;
import android.content.Intent;

public class PubnativeGameListDelegate extends PubnativeActivityDelegate implements
        AdRequestListener
{
    private int       LAUNCH_FLAGS = FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
    private AdRequest request;

    public static void Create(Context context, String app_token, PubnativeActivityListener listener)
    {
        PubnativeGameListDelegate delegate = new PubnativeGameListDelegate(context, app_token, listener);
        PubnativeActivityDelegateManager.addDelegate(delegate);
    }

    public PubnativeGameListDelegate(Context context, String app_token, PubnativeActivityListener listener)
    {
        super(context, app_token, listener);
        this.request = new AdRequest(this.context);
        this.request.setParameter(Request.APP_TOKEN, this.app_token);
        this.request.setParameter(Request.AD_COUNT, "10");
        this.request.start(Endpoint.NATIVE, this);
    }

    @Override
    public void onAdRequestStarted(AdRequest request)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads)
    {
        if (ads.size() > 0)
        {
            Intent adIntent = new Intent(context, PubnativeActivity.class);
            adIntent.setFlags(LAUNCH_FLAGS);
            adIntent.putExtra(PubnativeActivity.EXTRA_IDENTIFIER, this.identifier);
            adIntent.putExtra(PubnativeActivity.EXTRA_ADS, ads);
            adIntent.putExtra(PubnativeActivity.EXTRA_TYPE, Pubnative.FullScreen.GAME_LIST);
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
