package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.webkit.WebView;

import com.google.gson.Gson;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.model.LoopMeNativeAdModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;
import net.pubnative.mediation.task.PubnativeHttpTask;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import org.json.JSONObject;
import org.json.XML;

import java.util.Map;

public class LoopMeNetworkAdapter extends PubnativeNetworkAdapter implements PubnativeHttpTask.Listener
{
    private   static final String BASE_URL   = "http://loopme.me/api/s2s/ads?";
    protected static final String KEY_APP_ID = "app_id";

    public LoopMeNetworkAdapter(Map data)
    {
        super(data);
    }

    @Override
    public void request(Context context)
    {
        if (context != null && data != null)
        {
            String appId = (String) data.get(KEY_APP_ID);
            if (!TextUtils.isEmpty(appId))
            {
                this.createRequest(context, appId);
            }
            else
            {
                this.invokeFailed(new Exception("Invalid appId provided."));
            }
        }
        else
        {
            this.invokeFailed(new Exception("No appId provided."));
        }
    }

    protected void createRequest(final Context context, final String appId)
    {
        new AsyncTask<Void, Void, String>()
        {
            // We need to run this url creation in a non-ui thread
            // to ensure that the androidAdId is retrieved properly.
            @Override
            protected String doInBackground(Void... voids)
            {
                return BASE_URL
                        + "appId=" + appId
                        + "&xml=1"
                        + "&uid=" + PubnativeDeviceUtils.getAndroidAdvertisingID(context)
                        + "&ip=" + PubnativeDeviceUtils.getPublicIpAddress()
                        + "&ua=" + System.getProperty("http.agent");
            }

            @Override
            protected void onPostExecute(String url)
            {
                PubnativeHttpTask httpTask = new PubnativeHttpTask(context);
                httpTask.setListener(LoopMeNetworkAdapter.this);
                httpTask.execute(url);
            }
        }.execute();
    }

    @Override
    public void onHttpTaskFinished(PubnativeHttpTask task, String result)
    {
        PubnativeAdModel adModel = null;
        try
        {
            JSONObject jsonObject = XML.toJSONObject(result);
            if (jsonObject != null)
            {
                String responseJson = jsonObject.getString("response");
                if (!TextUtils.isEmpty(responseJson))
                {
                    JSONObject response = new JSONObject(responseJson);
                    String adJson = response.getString("ad");
                    if (!TextUtils.isEmpty(adJson))
                    {
                        adModel = new Gson().fromJson(adJson, LoopMeNativeAdModel.class);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (adModel != null)
        {
            this.invokeLoaded(adModel);
        }
        else
        {
            this.invokeFailed(new Exception("LoopMe - No Ad available"));
        }
    }

    @Override
    public void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage)
    {
        this.invokeFailed(new Exception(errorMessage));
    }
}
