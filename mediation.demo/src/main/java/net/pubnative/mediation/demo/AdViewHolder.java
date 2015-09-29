package net.pubnative.mediation.demo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.PubnativeNetworkRequestListener;
import net.pubnative.mediation.request.model.PubnativeAdModel;

/**
 * A class that holds the reference to all views in a cell.
 * This helps us to avoid redundant calls to "findViewById" each
 * time we load values into the cell.
 */
public class AdViewHolder implements View.OnClickListener, PubnativeNetworkRequestListener
{
    private static final String LOG_TAG = "AdViewHolder";

    protected Context context;

    // Data
    protected CellRequestModel requestModel;

    // Behaviour
    protected ProgressBar    ad_spinner;
    protected RelativeLayout ad_container;
    protected Button         request_button;

    // Ad info
    protected TextView  placement_id_text;
    protected TextView  adapter_name_text;
    protected TextView  ad_description_text;
    protected TextView  ad_title_text;
    protected RatingBar ad_rating;
    protected ImageView ad_icon_image;
    protected ImageView ad_banner_image;

    public AdViewHolder(Context context)
    {
        this.context = context;
    }

    public void initialize(View convertView)
    {
        this.request_button = (Button) convertView.findViewById(R.id.request_button);
        this.request_button.setOnClickListener(this);

        this.ad_spinner = (ProgressBar) convertView.findViewById(R.id.ad_spinner);
        this.ad_container = (RelativeLayout) convertView.findViewById(R.id.ad_container);

        this.ad_title_text = (TextView) convertView.findViewById(R.id.ad_title_text);
        this.ad_description_text = (TextView) convertView.findViewById(R.id.ad_description_text);
        this.adapter_name_text = (TextView) convertView.findViewById(R.id.ad_adapter_name_text);
        this.placement_id_text = (TextView) convertView.findViewById(R.id.placement_id_text);
        this.ad_rating = (RatingBar) convertView.findViewById(R.id.ad_rating);
        this.ad_icon_image = (ImageView) convertView.findViewById(R.id.ad_icon_image);
        this.ad_banner_image = (ImageView) convertView.findViewById(R.id.ad_banner_image);
    }

    public void cleanView()
    {
        this.ad_title_text.setText("");
        this.ad_description_text.setText("");
        this.adapter_name_text.setText("");
        this.ad_rating.setRating(0f);
        this.ad_rating.setVisibility(View.GONE);
        this.ad_banner_image.setImageDrawable(null);
        this.ad_icon_image.setImageDrawable(null);
        this.ad_spinner.setVisibility(View.GONE);
    }

    public void setRequestModel(CellRequestModel requestModel)
    {
        this.requestModel = requestModel;
        this.cleanView();
        this.renderAd();
    }

    public void renderAd()
    {
        this.placement_id_text.setText("Placement ID: " + requestModel.placementID);

        if (this.requestModel.adModel != null)
        {
            this.adapter_name_text.setText(this.requestModel.adModel.getClass().getSimpleName());
            this.ad_title_text.setText(this.requestModel.adModel.getTitle());
            this.ad_description_text.setText(this.requestModel.adModel.getDescription());
            this.ad_rating.setRating(this.requestModel.adModel.getStarRating());
            this.ad_rating.setVisibility(View.VISIBLE);
            new LoadImageAsyncTask().execute(this.requestModel.adModel.getIconUrl(), this.ad_icon_image);
            new LoadImageAsyncTask().execute(this.requestModel.adModel.getBannerUrl(), this.ad_banner_image);
            this.requestModel.adModel.startTracking(this.context, this.ad_container);
        }
    }

    @Override
    public void onClick(View v)
    {
        Log.d(LOG_TAG, "onClick");
        if (request_button.equals(v))
        {
            this.cleanView();
            this.ad_spinner.setVisibility(View.VISIBLE);
            this.requestModel.request.start(context, this.requestModel.appToken, this.requestModel.placementID, this);
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
        this.requestModel.adModel = ad;
        this.renderAd();
    }

    @Override
    public void onRequestFailed(PubnativeNetworkRequest request, Exception exception)
    {
        Log.d(LOG_TAG, "onRequestFailed: " + exception);

        Toast.makeText(this.context, exception.getMessage(), Toast.LENGTH_LONG).show();

        this.ad_spinner.setVisibility(View.GONE);
        this.requestModel.adModel = null;
        this.cleanView();
    }
}
