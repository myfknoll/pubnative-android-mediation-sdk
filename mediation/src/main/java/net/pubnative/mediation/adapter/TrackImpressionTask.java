package net.pubnative.mediation.adapter;

import android.os.AsyncTask;
import android.view.View;

import net.pubnative.mediation.utils.PubnativeViewUtils;

/**
 * Created by rahul on 22/9/15.
 */
public class TrackImpressionTask extends AsyncTask<Object, Void, Boolean>
{
    private final int VIEW_MIN_SHOWN_TIME      = 1000;
    private final int VIEW_MIN_VISIBLE_PERCENT = 50;

    public interface Listener
    {
        void onSuccess(View view);
        void onFailure(View view, long firstAppeared);
    }

    private Listener listener;
    private View     checkedView;
    private long     firstAppeared = -1;

    public TrackImpressionTask(Listener listener)
    {
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Object[] objects)
    {
        Boolean isTracked = false;

        checkedView = (View) objects[0];
        firstAppeared = (Long) objects[1];

        long now = System.currentTimeMillis();
        boolean startedTracking = (firstAppeared > 0);
        float currentVisiblePercent = PubnativeViewUtils.getVisiblePercent(checkedView);
        if (startedTracking)
        {
            if (currentVisiblePercent >= VIEW_MIN_VISIBLE_PERCENT)
            {
                if (now - firstAppeared >= VIEW_MIN_SHOWN_TIME)
                {
                    isTracked = true;
                }
            }
            else
            {
                firstAppeared = -1;
            }
        }
        else if (currentVisiblePercent > VIEW_MIN_VISIBLE_PERCENT)
        {
            firstAppeared = now;
        }

        return isTracked;
    }

    @Override
    protected void onPostExecute(Boolean success)
    {
        if (this.listener != null)
        {
            if (success)
            {
                this.listener.onSuccess(checkedView);
            } else
            {
                this.listener.onFailure(checkedView, firstAppeared);
            }
        }
    }
}
