package net.pubnative.mediation.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.pubnative.mediation.model.PubnativeAdModel;

import java.util.List;

/**
 * Created by davidmartin on 10/08/15.
 */
public class PubnativeAdsListAdapter extends ArrayAdapter<PubnativeAdModel>
{
    public PubnativeAdsListAdapter(Context context, int resource, List<PubnativeAdModel> objects)
    {
        super(context, resource, objects);
    }

    public void clean()
    {
        this.clear();
        this.notifyDataSetChanged();
    }

    public void setAds(List<PubnativeAdModel> ads)
    {
        this.clear();
        for (PubnativeAdModel ad : ads)
        {
            this.add(ad);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        PubnativeAdModel model = this.getItem(position);
        if (model != null)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item, null);
            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
            TextView text = (TextView) convertView.findViewById(R.id.textView);

            text.setText(model.getClass().getSimpleName());
            new LoadImageAsyncTask().execute(model.getIconUrl(), icon);
            model.registerAdView(this.getContext(), convertView);
        }
        return convertView;
    }
}
