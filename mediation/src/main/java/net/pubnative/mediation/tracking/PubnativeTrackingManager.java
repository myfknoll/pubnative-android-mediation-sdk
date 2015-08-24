package net.pubnative.mediation.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.pubnative.mediation.model.PubnativeTrackingDataModel;
import net.pubnative.mediation.model.PubnativeTrackingInfoModel;
import net.pubnative.mediation.model.PubnativeTrackingManagerCacheModel;
import net.pubnative.mediation.task.HttpTask;

import java.util.ArrayList;
import java.util.List;

public class PubnativeTrackingManager
{
    private static final String TRACKING_PREFERENCES  = "net.pubnative.mediation.tracking.PubnativeTrackingManager";
    private static final String TRACKING_PENDING_DATA = "pending_data";

    private static boolean isTracking = false;

    public synchronized static void trackData(Context context, String baseURL, PubnativeTrackingDataModel dataModel)
    {
        if (context != null && !TextUtils.isEmpty(baseURL) && dataModel != null)
        {
            PubnativeTrackingInfoModel model = new PubnativeTrackingInfoModel(baseURL, dataModel);
            PubnativeTrackingManager.enqueueTrackingItem(context, model);
            PubnativeTrackingManager.trackNext(context);
        }
    }

    protected synchronized static void trackNext(final Context context)
    {
        if (context != null && !PubnativeTrackingManager.isTracking)
        {
            PubnativeTrackingManager.isTracking = true;
            final PubnativeTrackingInfoModel model = PubnativeTrackingManager.dequeueTrackingItem(context);
            if (model != null)
            {
                String trackingDataString = new Gson().toJson(model.dataModel);
                if (!TextUtils.isEmpty(model.url) && !TextUtils.isEmpty(trackingDataString))
                {

                    HttpTask http = new HttpTask(context);
                    http.setPOSTData(trackingDataString);
                    http.setListener(new HttpTask.HttpTaskListener()
                    {
                        @Override
                        public void onHttpTaskFinished(HttpTask task, String result)
                        {
                            System.out.println("PubnativeTrackingManager - Finished with result: " + result);

                            boolean success = true;
                            // TODO: Check for error in the result string for re-schedule
                            if (success)
                            {
                                PubnativeTrackingManager.trackingFinished(context, model);
                            }
                            else
                            {
                                PubnativeTrackingManager.trackingFailed(context, model, "result");
                            }
                        }
                    });
                    http.execute(model.url);
                }
                else
                {
                    // Drop the call, tracking data is errored
                    PubnativeTrackingManager.trackingFinished(context, model);
                }
            }
        }
    }

    protected static void trackingFailed(Context context, PubnativeTrackingInfoModel model, String message)
    {
        PubnativeTrackingManager.enqueueTrackingItem(context, model);
        PubnativeTrackingManager.trackNext(context);
    }

    protected static void trackingFinished(Context context, PubnativeTrackingInfoModel model)
    {
        PubnativeTrackingManager.isTracking = false;
        PubnativeTrackingManager.trackNext(context);
    }

    protected static void enqueueTrackingItem(Context context, PubnativeTrackingInfoModel model)
    {
        if (context != null && model != null)
        {
            List<PubnativeTrackingInfoModel> pendingList = PubnativeTrackingManager.getTrackingPendingList(context);
            if (pendingList == null)
            {
                pendingList = new ArrayList<PubnativeTrackingInfoModel>();
            }
            pendingList.add(model);
            PubnativeTrackingManager.setTrackingPendingList(context, pendingList);
        }
    }

    protected static PubnativeTrackingInfoModel dequeueTrackingItem(Context context)
    {
        PubnativeTrackingInfoModel result = null;
        if (context != null)
        {
            List<PubnativeTrackingInfoModel> pendingList = PubnativeTrackingManager.getTrackingPendingList(context);
            if (pendingList != null && pendingList.size() > 0)
            {
                result = pendingList.get(0);
                pendingList.remove(0);
                PubnativeTrackingManager.setTrackingPendingList(context, pendingList);
            }
        }
        return result;
    }

    protected static List<PubnativeTrackingInfoModel> getTrackingPendingList(Context context)
    {
        List<PubnativeTrackingInfoModel> result = null;
        if (context != null)
        {
            SharedPreferences preferences = PubnativeTrackingManager.getSharedPreferences(context);
            if (preferences != null)
            {
                String pendingListString = preferences.getString(TRACKING_PENDING_DATA, null);
                if (!TextUtils.isEmpty(pendingListString))
                {
                    try
                    {
                        PubnativeTrackingManagerCacheModel cacheModel = new Gson().fromJson(pendingListString, PubnativeTrackingManagerCacheModel.class);

                        if (cacheModel != null && cacheModel.items != null && cacheModel.items.size() > 0)
                        {
                            result = cacheModel.items;
                        }
                    }
                    catch (JsonSyntaxException e)
                    {
                        // Do nothing
                    }
                }
            }
        }
        return result;
    }

    protected static void setTrackingPendingList(Context context, List<PubnativeTrackingInfoModel> pendingList)
    {
        if (context != null)
        {
            SharedPreferences.Editor editor = PubnativeTrackingManager.getSharedPreferencesEditor(context);
            if (editor != null)
            {
                if (pendingList == null || pendingList.size() == 0)
                {
                    editor.remove(TRACKING_PENDING_DATA);
                }
                else
                {
                    PubnativeTrackingManagerCacheModel cacheModel = new PubnativeTrackingManagerCacheModel(pendingList);
                    String cacheModelString = new Gson().toJson(cacheModel);
                    if (!TextUtils.isEmpty(cacheModelString))
                    {
                        editor.putString(TRACKING_PENDING_DATA, cacheModelString);
                    }
                }
                editor.apply();
            }
        }
    }

    protected static SharedPreferences.Editor getSharedPreferencesEditor(Context context)
    {
        SharedPreferences.Editor result = null;
        SharedPreferences preferences = PubnativeTrackingManager.getSharedPreferences(context);
        if (preferences != null)
        {
            result = preferences.edit();
        }
        return result;
    }

    protected static SharedPreferences getSharedPreferences(Context context)
    {
        SharedPreferences result = null;
        if (context != null)
        {
            result = context.getSharedPreferences(TRACKING_PREFERENCES, Context.MODE_PRIVATE);
        }
        return result;
    }
}
