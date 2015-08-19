package net.pubnative.library.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHttpTask extends AsyncTask<String, Void, String>
{
    private Context context;
    private String  httpUrl;

    public interface HttpAsyncJSONTaskListener
    {
        public void onHttpAsyncJsonFinished(AsyncHttpTask task, String result);

        public void onHttpAsyncJsonFailed(AsyncHttpTask task, Exception e);
    }

    private WeakReference<HttpAsyncJSONTaskListener> listener;

    public void setListener(HttpAsyncJSONTaskListener httpAsyncJSONTask)
    {
        this.listener = new WeakReference<HttpAsyncJSONTaskListener>(httpAsyncJSONTask);
    }

    public Context getContext()
    {
        return this.context;
    }
    public String getHttpUrl()
    {
        return this.httpUrl;
    }

    public AsyncHttpTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String result = null;
        if (params.length > 0)
        {
            this.httpUrl = params[0];
            if (this.httpUrl != null)
            {
                ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected)
                {
                    try
                    {
                        HttpURLConnection connection;
                        connection = (HttpURLConnection) new URL(this.httpUrl ).openConnection();
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(1000);
                        connection.setInstanceFollowRedirects(true);
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (HttpURLConnection.HTTP_OK == responseCode || HttpURLConnection.HTTP_MOVED_TEMP == responseCode)
                        {
                            result = this.getStringFromInputStream(connection.getInputStream());
                            this.invokeFinished(result);
                        }
                    }
                    catch (Exception e)
                    {
                        this.invokeFailed(e);
                    }
                }
                else
                {
                    this.invokeFailed(new Exception("Pubnative - Server not reachable"));
                }
            }
            else
            {
                this.invokeFailed(new Exception("Pubnative - URL not valid: " + this.httpUrl ));
            }
        }
        else
        {
            this.invokeFailed(new Exception("Pubnative - URL not specified"));
        }
        return result;
    }

    private void invokeFinished(String result)
    {
        if (this.listener != null && this.listener.get() != null)
        {
            this.listener.get()
                         .onHttpAsyncJsonFinished(this, result);
        }
    }

    private void invokeFailed(Exception exception)
    {
        if (this.listener != null && this.listener.get() != null)
        {
            this.listener.get()
                         .onHttpAsyncJsonFailed(this, exception);
        }
    }

    private String getStringFromInputStream(InputStream is)
    {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try
        {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
