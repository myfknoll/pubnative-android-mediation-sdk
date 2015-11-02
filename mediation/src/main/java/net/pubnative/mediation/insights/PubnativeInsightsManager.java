package net.pubnative.mediation.insights;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.pubnative.mediation.insights.model.PubnativeInsightRequestModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.task.PubnativeHttpTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PubnativeInsightsManager {

    protected static final String INSIGHTS_PREFERENCES_KEY = "net.pubnative.mediation.tracking.PubnativeInsightsManager";
    protected static final String INSIGHTS_PENDING_DATA    = "pending_data";
    protected static final String INSIGHTS_FAILED_DATA     = "failed_data";

    protected static boolean idle = true;

    /**
     * Queues impression/click tracking data and sends it to pubnative server.
     *
     * @param context   valid Context object
     * @param baseURL   the base URL of the tracking server
     * @param dataModel PubnativeInsightDataModel object with values filled in.
     */
    public synchronized static void trackData(Context context, String baseURL, PubnativeInsightDataModel dataModel) {
        if (context != null && !TextUtils.isEmpty(baseURL) && dataModel != null) {
            PubnativeInsightRequestModel model = new PubnativeInsightRequestModel(baseURL, dataModel);

            // Enqueue failed
            List<PubnativeInsightRequestModel> failedList = PubnativeInsightsManager.getTrackingList(context, INSIGHTS_FAILED_DATA);
            PubnativeInsightsManager.enqueueInsightList(context, INSIGHTS_PENDING_DATA, failedList);
            PubnativeInsightsManager.setTrackingList(context, INSIGHTS_FAILED_DATA, null);

            // Enqueue current
            PubnativeInsightsManager.enqueueInsightItem(context, INSIGHTS_PENDING_DATA, model);

            // Start tracking
            PubnativeInsightsManager.trackNext(context);
        }
    }

    protected synchronized static void trackNext(final Context context) {
        if (context != null && PubnativeInsightsManager.idle) {
            final PubnativeInsightRequestModel model = PubnativeInsightsManager.dequeueInsightItem(context, INSIGHTS_PENDING_DATA);
            if (model != null) {
                PubnativeInsightsManager.idle = false;
                String trackingDataString = new Gson().toJson(model.dataModel);
                if (!TextUtils.isEmpty(model.url) && !TextUtils.isEmpty(trackingDataString)) {
                    PubnativeHttpTask.Listener listener = new PubnativeHttpTask.Listener() {

                        @Override
                        public void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage) {
                            PubnativeInsightsManager.trackingFailed(context, model, errorMessage);
                        }

                        @Override
                        public void onHttpTaskFinished(PubnativeHttpTask task, String result) {
                            System.out.println("Pubnative result: " + result);

                            if (TextUtils.isEmpty(result)) {
                                PubnativeInsightsManager.trackingFailed(context, model, "invalid insight response (empty or null)");
                            } else {
                                try {
                                    PubnativeInsightsAPIResponseModel response = new Gson().fromJson(result, PubnativeInsightsAPIResponseModel.class);

                                    if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                                        PubnativeInsightsManager.trackingFinished(context, model);
                                    } else {
                                        PubnativeInsightsManager.trackingFailed(context, model, response.error_message);
                                    }
                                } catch (Exception e) {
                                    PubnativeInsightsManager.trackingFailed(context, model, e.toString());
                                }
                            }
                        }
                    };

                    PubnativeInsightsManager.sendTrackingDataToServer(context, trackingDataString, model.url, listener);
                } else {
                    // Drop the call, tracking data is errored
                    PubnativeInsightsManager.trackingFinished(context, model);
                }
            }
        }
    }

    protected static void sendTrackingDataToServer(Context context, String trackingDataString, String url, PubnativeHttpTask.Listener listener) {
        PubnativeHttpTask http = new PubnativeHttpTask(context);
        http.setPOSTData(trackingDataString);
        http.setListener(listener);
        http.execute(url);
    }

    protected static void trackingFailed(Context context, PubnativeInsightRequestModel model, String message) {
        PubnativeInsightsManager.enqueueInsightItem(context, INSIGHTS_FAILED_DATA, model);
        PubnativeInsightsManager.idle = true;
        PubnativeInsightsManager.trackNext(context);
    }

    protected static void trackingFinished(Context context, PubnativeInsightRequestModel model) {
        PubnativeInsightsManager.idle = true;
        PubnativeInsightsManager.trackNext(context);
    }

    protected static void enqueueInsightItem(Context context, String listKey, PubnativeInsightRequestModel model) {
        if (context != null && model != null) {
            List<PubnativeInsightRequestModel> pendingList = PubnativeInsightsManager.getTrackingList(context, listKey);
            if (pendingList == null) {
                pendingList = new ArrayList<PubnativeInsightRequestModel>();
            }
            pendingList.add(model);
            PubnativeInsightsManager.setTrackingList(context, listKey, pendingList);
        }
    }

    protected static void enqueueInsightList(Context context, String listKey, List<PubnativeInsightRequestModel> list) {
        if (context != null && list != null) {
            List<PubnativeInsightRequestModel> insightList = PubnativeInsightsManager.getTrackingList(context, listKey);
            if (insightList == null) {
                insightList = new ArrayList<PubnativeInsightRequestModel>();
            }
            insightList.addAll(list);
            PubnativeInsightsManager.setTrackingList(context, listKey, insightList);
        }
    }

    protected static PubnativeInsightRequestModel dequeueInsightItem(Context context, String listKey) {
        PubnativeInsightRequestModel result = null;
        if (context != null) {
            List<PubnativeInsightRequestModel> pendingList = PubnativeInsightsManager.getTrackingList(context, listKey);
            if (pendingList != null && pendingList.size() > 0) {
                result = pendingList.get(0);
                pendingList.remove(0);
                PubnativeInsightsManager.setTrackingList(context, listKey, pendingList);
            }
        }
        return result;
    }

    protected static List<PubnativeInsightRequestModel> getTrackingList(Context context, String listKey) {
        List<PubnativeInsightRequestModel> result = null;
        if (context != null) {
            SharedPreferences preferences = PubnativeInsightsManager.getSharedPreferences(context);
            if (preferences != null) {
                String pendingListString = preferences.getString(listKey, null);
                if (!TextUtils.isEmpty(pendingListString)) {
                    try {
                        PubnativeInsightRequestModel[] cacheModel = new Gson().fromJson(pendingListString, PubnativeInsightRequestModel[].class);
                        if (cacheModel != null && cacheModel.length > 0) {
                            result = new ArrayList<PubnativeInsightRequestModel>(Arrays.asList(cacheModel));
                        }
                    } catch (JsonSyntaxException e) {
                        // Do nothing
                    }
                }
            }
        }
        return result;
    }

    protected static void setTrackingList(Context context, String listKey, List<PubnativeInsightRequestModel> pendingList) {
        if (context != null) {
            SharedPreferences.Editor editor = PubnativeInsightsManager.getSharedPreferencesEditor(context);
            if (editor != null) {
                if (pendingList == null || pendingList.size() == 0) {
                    editor.remove(listKey);
                } else {
                    String cacheModelString = new Gson().toJson(pendingList.toArray());
                    if (!TextUtils.isEmpty(cacheModelString)) {
                        editor.putString(listKey, cacheModelString);
                    }
                }
                editor.apply();
            }
        }
    }

    protected static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        SharedPreferences.Editor result      = null;
        SharedPreferences        preferences = PubnativeInsightsManager.getSharedPreferences(context);
        if (preferences != null) {
            result = preferences.edit();
        }
        return result;
    }

    protected static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(INSIGHTS_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
        return result;
    }
}
