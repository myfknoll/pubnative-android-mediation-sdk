package net.pubnative.mediation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.PubnativeNetworkRequestListener;
import net.pubnative.mediation.request.PubnativeNetworkRequestParameters;
import net.pubnative.mediation.utils.PubnativeStringUtils;

import java.io.InputStream;

// List of configured placements for testing
//-------------------------------
// facebook_only
// pubnative_only
// waterfall_facebook_pubnative
// disabled
// 1_imp_cap
// 1_min_pacing_cap

public class MainActivity extends ActionBarActivity implements PubnativeNetworkRequestListener,
                                                               OnClickListener
{
    private final static String PUBNATIVE_TAG = "Pubnative";

    private Button requestButton = null;

    View adView1 = null;
    View adView2 = null;
    View adView3 = null;

    ImageView adImage1 = null;
    ImageView adImage2 = null;
    ImageView adImage3 = null;

    TextView adText1 = null;
    TextView adText2 = null;
    TextView adText3 = null;

    PubnativeNetworkRequest request1 = new PubnativeNetworkRequest();
    PubnativeNetworkRequest request2 = new PubnativeNetworkRequest();
    PubnativeNetworkRequest request3 = new PubnativeNetworkRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setFakeConfig();

        this.adView1 = this.findViewById(R.id.ad1_layout);
        this.adView2 = this.findViewById(R.id.ad2_layout);
        this.adView3 = this.findViewById(R.id.ad3_layout);

        this.adImage1 = (ImageView) this.findViewById(R.id.ad1_image);
        this.adImage2 = (ImageView) this.findViewById(R.id.ad2_image);
        this.adImage3 = (ImageView) this.findViewById(R.id.ad3_image);

        this.adText1 = (TextView) this.findViewById(R.id.ad1_text);
        this.adText2 = (TextView) this.findViewById(R.id.ad2_text);
        this.adText3 = (TextView) this.findViewById(R.id.ad3_text);

        this.requestButton = (Button) this.findViewById(R.id.requestButton);
        this.requestButton.setOnClickListener(this);
    }

    // TODO: Remove this method
    private void setFakeConfig()
    {
        InputStream configStream = MainActivity.class.getResourceAsStream("/configs/test_config.json");
        String configString = PubnativeStringUtils.readStringFromInputStream(configStream);
        PubnativeConfigManager.updateConfigString(this.getApplicationContext(), "app_token", configString);
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

    protected void requestAds()
    {
        this.requestAd(this.request1, "facebook_only");
        this.requestAd(this.request2, "1_imp_cap");
        this.requestAd(this.request3, "1_min_pacing_cap");
    }

    protected void updateAd(PubnativeAdModel ad, ImageView imageView, TextView textView, View adView)
    {
        textView.setText(ad.getClass().getSimpleName());
        new LoadImageAsyncTask().execute(ad.getIconUrl(), imageView);
        ad.registerAdView(this, adView);
    }

    protected void requestAd(PubnativeNetworkRequest request, String placementID)
    {
        PubnativeNetworkRequestParameters parameters = new PubnativeNetworkRequestParameters();
        parameters.app_token = "app_token";
        parameters.placement_id = placementID;
        request.start(this.getApplicationContext(), parameters, this);
    }

    // OnClickListener
    @Override
    public void onClick(View v)
    {
        if (this.requestButton == v)
        {
            // Request new ads
            this.requestAds();
        }
    }

    // PubnativeNetworkRequestListener
    @Override
    public void onRequestStarted(PubnativeNetworkRequest request)
    {
        Log.d(PUBNATIVE_TAG, "MainActivity.onRequestStarted");
    }

    @Override
    public void onRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad)
    {
        Log.d(PUBNATIVE_TAG, "MainActivity.onRequestLoaded");
        if (this.request1.equals(request))
        {
            this.updateAd(ad, this.adImage1, this.adText1, this.adView1);
        }
        else if (this.request2.equals(request))
        {
            this.updateAd(ad, this.adImage2, this.adText2, this.adView2);
        }
        else if (this.request3.equals(request))
        {
            this.updateAd(ad, this.adImage3, this.adText3, this.adView3);
        }
    }

    @Override
    public void onRequestFailed(PubnativeNetworkRequest request, Exception exception)
    {
        Log.d(PUBNATIVE_TAG, "MainActivity.onRequestFailed: " + exception);
    }
}
