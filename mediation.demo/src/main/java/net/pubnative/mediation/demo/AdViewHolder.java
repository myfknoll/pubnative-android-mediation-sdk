package net.pubnative.mediation.demo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.PubnativeNetworkRequestListener;

/**
 * A class that holds the reference to all views in a cell.
 * This helps us to avoid redundant calls to "findViewById" each
 * time we load values into the cell.
 */
public class AdViewHolder implements View.OnClickListener, PubnativeNetworkRequestListener
{
    private static final String LOG_TAG = "AdViewHolder";

    protected Context                 context;
    protected PubnativeNetworkRequest request;

    // Request data
    protected String placementID;
    protected String appToken;

    // Behaviour
    protected ProgressBar    ad_spinner;
    protected RelativeLayout ad_container;
    protected Button         request_button;

    // Ad info
    protected TextView  placement_id_text;
    protected TextView  adapter_name_text;
    protected TextView  ad_title_text;
    protected ImageView ad_icon_image;

    public AdViewHolder(Context context, String appToken)
    {
        this.appToken = appToken;
        this.context = context;

    }

    public void initialize(View convertView)
    {
        this.request_button = (Button) convertView.findViewById(R.id.request_button);
        this.request_button.setOnClickListener(this);

        this.ad_spinner = (ProgressBar) convertView.findViewById(R.id.ad_spinner);
        this.ad_container = (RelativeLayout) convertView.findViewById(R.id.ad_container);

        this.ad_title_text = (TextView) convertView.findViewById(R.id.ad_title_text);
        this.adapter_name_text = (TextView) convertView.findViewById(R.id.ad_adapter_name_text);
        this.placement_id_text = (TextView) convertView.findViewById(R.id.placement_id_text);
        this.ad_icon_image = (ImageView) convertView.findViewById(R.id.ad_icon_image);
    }

    public void cleanView()
    {
        this.ad_title_text.setText("");
        this.adapter_name_text.setText("");
        this.ad_icon_image.setImageDrawable(null);
        this.ad_spinner.setVisibility(View.GONE);
    }

    public void setPlacementID(String placementID)
    {
        this.placementID = placementID;

        if (this.placement_id_text != null && !TextUtils.isEmpty(placementID))
        {
            this.placement_id_text.setText("Placement ID: " + placementID);
        }
    }

    @Override
    public void onClick(View v)
    {
        Log.d(LOG_TAG, "onClick");
        if(request_button.equals(v))
        {
            this.cleanView();
            this.ad_spinner.setVisibility(View.VISIBLE);
            if (this.request == null)
            {
                this.request = new PubnativeNetworkRequest();
            }
            this.request.start(context, this.appToken, this.placementID, this);
        }
    }

    @Override
    public void onRequestStarted(PubnativeNetworkRequest request)
    {
        Log.d(LOG_TAG, "onRequestStarted");
    }

    @Override
    public void onRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad)
    {
        Log.d(LOG_TAG, "onRequestLoaded");

        this.ad_spinner.setVisibility(View.GONE);

        if (ad != null)
        {
            this.adapter_name_text.setText(ad.getClass().getSimpleName());
            this.ad_title_text.setText(ad.getTitle());
            new LoadImageAsyncTask().execute(ad.getIconUrl(), this.ad_icon_image);
            ad.startTracking(this.context, this.ad_container);
        }
    }

    @Override
    public void onRequestFailed(PubnativeNetworkRequest request, Exception exception)
    {
        Log.d(LOG_TAG, "onRequestFailed: " + exception);

        this.ad_spinner.setVisibility(View.GONE);
        this.cleanView();
    }
}
