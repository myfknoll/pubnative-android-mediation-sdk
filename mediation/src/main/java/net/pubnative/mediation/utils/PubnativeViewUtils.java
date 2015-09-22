package net.pubnative.mediation.utils;

import android.graphics.Rect;
import android.view.View;

public class PubnativeViewUtils
{
    public static int getVisiblePercent(View v)
    {
        if (v.isShown())
        {
            Rect r = new Rect();
            v.getGlobalVisibleRect(r);
            double sVisible = r.width() * r.height();
            double sTotal = v.getWidth() * v.getHeight();
            return (int) (100 * sVisible / sTotal);
        }
        else
        {
            return -1;
        }
    }
}
