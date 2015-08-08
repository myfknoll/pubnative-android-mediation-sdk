package net.pubnative.mediation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.PubnativeNetworkRequestListener;
import net.pubnative.mediation.request.PubnativeNetworkRequestParameters;
import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.InputStream;
import java.util.List;


public class MainActivity extends ActionBarActivity implements PubnativeNetworkRequestListener
{
    private final static String PUBNATIVE_TAG = "Pubnative";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setFakeConfig();

        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.ad_count = 5;
        parameters.app_token = "app_token";
        parameters.placement_id = "placement_id";

        PubnativeNetworkRequest request = new PubnativeNetworkRequest();
        request.request(this.getApplicationContext(), parameters, this);
    }

    // TODO: Remove this method
    private void setFakeConfig()
    {
        InputStream configStream = MainActivity.class.getResourceAsStream("/configs/test_config.json");
        String configString = PubnativeStringUtils.readStringFromInputStream(configStream);
        PubnativeConfigManager.updateConfigString(this.getApplicationContext(), configString, "app_token");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestStarted(PubnativeNetworkRequest request)
    {
        Log.d(PUBNATIVE_TAG, "MainActivity.onRequestStarted");
    }

    @Override
    public void onRequestLoaded(PubnativeNetworkRequest request, List<PubnativeAdModel> ads)
    {
        Log.d(PUBNATIVE_TAG, "MainActivity.onRequestLoaded: " + ads.size());
    }

    @Override
    public void onRequestFailed(PubnativeNetworkRequest request, Exception exception)
    {
        Log.d(PUBNATIVE_TAG, "MainActivity.onRequestFailed: " + exception);
    }
}
