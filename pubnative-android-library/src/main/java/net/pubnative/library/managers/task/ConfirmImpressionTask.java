package net.pubnative.library.managers.task;

import net.pubnative.library.util.ViewUtil;

import org.apache.http.HttpResponse;

import android.view.View;

public class ConfirmImpressionTask extends TaskItem
{
    private final int   VIEW_MIN_SHOWN_TIME      = 1000;
    private final int   VIEW_MIN_VISIBLE_PERCENT = 50;
    private View        checkedView              = null;
    private long        firstAppeared            = -1;
    public HttpResponse response;

    public ConfirmImpressionTask(TaskItemListener listener, View view)
    {
        super(listener);
        this.checkedView = view;
    }

    @Override
    public void onExecute()
    {
        long now = System.currentTimeMillis();
        boolean startedTracking = (this.firstAppeared > 0);
        float currentVisiblePercent = ViewUtil.getVisiblePercent(this.checkedView);
        if (startedTracking)
        {
            if (currentVisiblePercent >= VIEW_MIN_VISIBLE_PERCENT)
            {
                if (now - this.firstAppeared >= VIEW_MIN_SHOWN_TIME)
                {
                    this.invokeOnTaskItemListenerFinished();
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
    }
}
