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

public class PubnativeHttpTask extends AsyncTask<String, Void, String>
{
    protected final static String ERROR_CONNECTION = "conection error, internet not reacbable";
    protected final static String ERROR_URL_FORMAT = "request url not provided or is null or empty";
    protected final static String ERROR_CANCELLED  = "request cancelled";

    public interface Listener
    {
        void onHttpTaskFinished(PubnativeHttpTask task, String result);

        void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage);
    }

    protected Context  context;
    protected String   url;
    protected String   postString;
    protected Listener listener;
    protected boolean  success;

    public PubnativeHttpTask(Context context)
    {
        this.context = context;
        this.success = false;
    }

    public void setListener(Listener listener)
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

            if (TextUtils.isEmpty(this.url))
            {
                result = ERROR_URL_FORMAT;
            }
            else
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
                            success = true;
                        }
                    }
                    catch (Exception e)
                    {
                        result = e.toString();
                    }
                }
                else
                {
                    result = ERROR_CONNECTION;
                }
            }
        }
        else
        {
            result = ERROR_URL_FORMAT;
        }
        return result;
    }

    @Override
    protected void onCancelled()
    {
        this.invokeFailed(ERROR_CANCELLED);
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (success)
        {
            this.invokeFinished(result);
        }
        else
        {
            this.invokeFailed(result);
        }
    }

    protected void invokeFinished(String result)
    {
        if (this.listener != null)
        {
            this.listener.onHttpTaskFinished(this, result);
        }
    }

    protected void invokeFailed(String errorMessage)
    {
        if (this.listener != null)
        {
            this.listener.onHttpTaskFailed(this, errorMessage);
        }
    }
}
