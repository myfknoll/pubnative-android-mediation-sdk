package net.pubnative.mediation.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by rahul on 3/9/15.
 */
public class AdListAdapter extends ArrayAdapter<String>
{
    private static final String LOG_TAG     = "AdListAdapter";

    public String                           appToken;

    public AdListAdapter(Context context, int resource, List<String> objects, String appToken)
    {
        super(context, resource, objects);
        this.appToken = appToken;
    }

    public void setPlacements(List<String> placements)
    {
        this.clear();
        this.addAll(placements);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        String placementID = this.getItem(position);
        AdViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.ad_list_cell, viewGroup, false);
            viewHolder = new AdViewHolder(this.getContext(), this.appToken);
            viewHolder.initialize(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (AdViewHolder) convertView.getTag();
        viewHolder.setPlacementID(placementID);

        return convertView;
    }
}
