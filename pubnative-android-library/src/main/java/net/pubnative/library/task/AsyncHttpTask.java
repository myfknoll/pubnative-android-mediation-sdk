package net.pubnative.library.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHttpTask extends AsyncTask<String, Void, String>
{
    private Context context;
    private String  httpUrl;
    private String  result;
    private boolean isErrror;

    public interface AsyncHttpTaskListener
    {
        void onAsyncHttpTaskFinished(AsyncHttpTask task, String result);

        void onAsyncHttpTaskFailed(AsyncHttpTask task, Exception e);
    }

    private AsyncHttpTaskListener listener;

    public void setListener(AsyncHttpTaskListener listener)
    {
        this.listener = listener;
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
        this.isErrror = false;
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
                        connection = (HttpURLConnection) new URL(this.httpUrl).openConnection();
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(1000);
                        connection.setInstanceFollowRedirects(true);
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (HttpURLConnection.HTTP_OK == responseCode || HttpURLConnection.HTTP_MOVED_TEMP == responseCode)
                        {
                            result = this.getStringFromInputStream(connection.getInputStream());
                        }
                        else
                        {
                            result = "Pubnative - connection error";
                            this.isErrror = true;
                        }
                    }
                    catch (Exception e)
                    {
                        result = e.toString();
                        this.isErrror = true;
                    }
                }
                else
                {
                    result = "Pubnative - Server not reachable";
                    this.isErrror = true;
                }
            }
            else
            {
                result = "Pubnative - URL not valid: " + this.httpUrl;
                this.isErrror = true;
            }
        }
        else
        {
            result = "Pubnative - URL not specified";
            this.isErrror = true;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(this.listener != null)
        {
            if (isErrror)
            {
                this.listener.onAsyncHttpTaskFailed(this, new Exception(result));
            }
            else
            {
                this.listener.onAsyncHttpTaskFinished(this, result);
            }
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
