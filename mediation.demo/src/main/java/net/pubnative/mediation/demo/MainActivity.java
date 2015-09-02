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

import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeAdModelListener;
import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.PubnativeNetworkRequestListener;

// List of configured placements for testing
//-------------------------------


public class MainActivity extends ActionBarActivity implements PubnativeNetworkRequestListener, OnClickListener
{
    private final static String APP_TOKEN = "bed5dfea7feb694967a8755bfa7f67fdf1ceb9c291ddd7b8825983a103c8b266";

    private final static String PLACEMENT_WATERFALL_FACEBOOK_PUBNATIVE = "4";
    private final static String PLACEMENT_WATERFALL_PUBNATIVE_FACEBOOK = "5";
    private final static String PLACEMENT_DISABLED                     = "7";
    private final static String PLACEMENT_IMP_HOUR_CAP_1               = "8";
    private final static String PLACEMENT_PACING_CAP_MIN_1             = "9";

    private final static String PUBNATIVE_TAG = "Pubnative";

    private Button requestButton = null;

    View adView1 = null;
    View adView2 = null;
    View adView3 = null;
    View adView4 = null;

    ImageView adImage1 = null;
    ImageView adImage2 = null;
    ImageView adImage3 = null;
    ImageView adImage4 = null;

    TextView adText1 = null;
    TextView adText2 = null;
    TextView adText3 = null;
    TextView adText4 = null;

    PubnativeNetworkRequest request1 = new PubnativeNetworkRequest();
    PubnativeNetworkRequest request2 = new PubnativeNetworkRequest();
    PubnativeNetworkRequest request3 = new PubnativeNetworkRequest();
    PubnativeNetworkRequest request4 = new PubnativeNetworkRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.adView1 = this.findViewById(R.id.ad1_layout);
        this.adView2 = this.findViewById(R.id.ad2_layout);
        this.adView3 = this.findViewById(R.id.ad3_layout);
        this.adView4 = this.findViewById(R.id.ad4_layout);

        this.adImage1 = (ImageView) this.findViewById(R.id.ad1_image);
        this.adImage2 = (ImageView) this.findViewById(R.id.ad2_image);
        this.adImage3 = (ImageView) this.findViewById(R.id.ad3_image);
        this.adImage4 = (ImageView) this.findViewById(R.id.ad4_image);

        this.adText1 = (TextView) this.findViewById(R.id.ad1_text);
        this.adText2 = (TextView) this.findViewById(R.id.ad2_text);
        this.adText3 = (TextView) this.findViewById(R.id.ad3_text);
        this.adText4 = (TextView) this.findViewById(R.id.ad4_text);

        this.requestButton = (Button) this.findViewById(R.id.requestButton);
        this.requestButton.setOnClickListener(this);
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
        this.request1.start(this.getApplicationContext(), APP_TOKEN, PLACEMENT_WATERFALL_FACEBOOK_PUBNATIVE, this);
        this.request2.start(this.getApplicationContext(), APP_TOKEN, PLACEMENT_WATERFALL_PUBNATIVE_FACEBOOK, this);
        this.request3.start(this.getApplicationContext(), APP_TOKEN, PLACEMENT_PACING_CAP_MIN_1, this);
        this.request4.start(this.getApplicationContext(), APP_TOKEN, PLACEMENT_IMP_HOUR_CAP_1, this);
    }

    protected void updateAd(PubnativeAdModel ad, ImageView imageView, TextView textView, View adView)
    {
        ad.setListener(new PubnativeAdModelListener()
        {
            @Override
            public void onAdImpressionConfirmed(PubnativeAdModel model)
            {
                Log.d(PUBNATIVE_TAG, "Impression confirmed");
            }

            @Override
            public void onAdClick(PubnativeAdModel model)
            {
                Log.d(PUBNATIVE_TAG, "Ad clicked");
            }
        });

        textView.setText(ad.getClass().getSimpleName());
        new LoadImageAsyncTask().execute(ad.getIconUrl(), imageView);
        ad.startTracking(this, adView);
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
        else if (this.request4.equals(request))
        {
            this.updateAd(ad, this.adImage4, this.adText4, this.adView4);
        }
    }

    @Override
    public void onRequestFailed(PubnativeNetworkRequest request, Exception exception)
    {
        Log.d(PUBNATIVE_TAG, "MainActivity.onRequestFailed: " + exception);
    }
}
