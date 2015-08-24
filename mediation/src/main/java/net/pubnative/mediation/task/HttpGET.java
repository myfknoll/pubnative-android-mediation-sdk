package net.pubnative.mediation.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGET extends AsyncTask<String, Void, String>
{
    public interface HttpGETListener
    {
        public void onHttpGETFinished(HttpGET task, String result);

        public void onHttpGETFailed(HttpGET task, Exception e);
    }

    private Context         context;
    private String          httpUrl;
    private HttpGETListener listener;

    public HttpGET(Context context)
    {
        this.context = context;
    }

    public HttpGET setListener(HttpGETListener listener)
    {
        this.listener = listener;
        return this;
    }

    @Override
    protected String doInBackground(String... params)
    {
        String result = null;
        if (params.length > 0)
        {
            this.httpUrl = params[0];
            if (!TextUtils.isEmpty(this.httpUrl))
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
                            result = PubnativeStringUtils.readStringFromInputStream(connection.getInputStream());
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
        if (this.listener != null)
        {
            this.listener.onHttpGETFinished(this, result);
        }
    }

    private void invokeFailed(Exception exception)
    {
        if (this.listener != null)
        {
            this.listener.onHttpGETFailed(this, exception);
        }
    }
}
