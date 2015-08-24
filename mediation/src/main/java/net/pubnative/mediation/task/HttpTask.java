package net.pubnative.mediation.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTask extends AsyncTask<String, Void, String>
{
    public interface HttpTaskListener
    {
        void onHttpTaskFinished(HttpTask task, String result);
    }

    protected Context          context;
    protected String           url;
    protected String           postString;
    protected HttpTaskListener listener;

    public HttpTask(Context context)
    {
        this.context = context;
    }

    public void setListener(HttpTaskListener listener)
    {
        this.listener = listener;
    }

    public void setPOSTData(String postString)
    {
        this.postString = postString;
    }

    public String getPOSTData()
    {
        return this.postString;
    }

    public String getURL()
    {
        return this.url;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String result = null;
        if (params.length > 0)
        {
            this.url = params[0];

            if (!TextUtils.isEmpty(this.url))
            {
                ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected)
                {
                    try
                    {
                        HttpURLConnection connection;
                        connection = (HttpURLConnection) new URL(this.url).openConnection();
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(1000);
                        if (!TextUtils.isEmpty(this.postString))
                        {
                            connection.setRequestMethod("POST");
                            connection.setUseCaches(false);
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            OutputStream connectionOutputStream = connection.getOutputStream();
                            DataOutputStream wr = new DataOutputStream(connectionOutputStream);
                            wr.writeBytes(this.postString);
                            wr.flush();
                            wr.close();
                        }
                        connection.connect();

                        int responseCode = connection.getResponseCode();
                        if (HttpURLConnection.HTTP_OK == responseCode)
                        {
                            result = PubnativeStringUtils.readStringFromInputStream(connection.getInputStream());
                        }
                    }
                    catch (Exception e)
                    {
                        // Do nothing
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (this.listener != null)
        {
            this.listener.onHttpTaskFinished(this, result);
        }
    }
}
