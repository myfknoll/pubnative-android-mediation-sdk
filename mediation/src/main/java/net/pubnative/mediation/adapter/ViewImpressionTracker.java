package net.pubnative.mediation.adapter;

import android.view.View;

public class ViewImpressionTracker
{
    public interface OnImpressionListener
    {
        void onAdImpressionConfirmed();
    }

    private static OnImpressionListener listener;

    public static void startTracking(OnImpressionListener impressionListener, View view)
    {
        listener = impressionListener;

        trackImpression(view, -1);
    }

    private static void trackImpression(View view, long firstAppeared)
    {
        new TrackImpressionTask(new TrackImpressionTask.Listener()
        {
            @Override
            public void onSuccess(View view)
            {
                if (listener != null)
                {
                    listener.onAdImpressionConfirmed();
                }
            }

            @Override
            public void onFailure(View view, long firstAppeared)
            {
                trackImpression(view, firstAppeared);
            }
        }).execute(view, firstAppeared);
    }
}
