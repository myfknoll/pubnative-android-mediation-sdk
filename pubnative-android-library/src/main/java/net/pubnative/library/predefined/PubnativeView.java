package net.pubnative.library.predefined;

import java.util.ArrayList;

import net.pubnative.library.model.NativeAdModel;
import android.content.res.Configuration;
import android.widget.RelativeLayout;

public abstract class PubnativeView extends RelativeLayout
{
    public ArrayList<NativeAdModel> ads;

    public PubnativeView(PubnativeActivity activity, ArrayList<NativeAdModel> ads)
    {
        super(activity);
        this.ads = ads;
    }

    protected PubnativeActivity getActivity()
    {
        return (PubnativeActivity) this.getContext();
    }

    protected void invokeMessage(String eventName)
    {
        this.invokeMessage(eventName, null);
    }

    protected void invokeMessage(String eventName, Exception exception)
    {
        this.getActivity().streamMessageEvent(eventName, exception);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        this.onOrientationChanged(getContext().getResources().getConfiguration());
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        this.onOrientationChanged(newConfig);
    }

    protected abstract void onOrientationChanged(Configuration configuration);
}
