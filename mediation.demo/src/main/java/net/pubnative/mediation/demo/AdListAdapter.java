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
public class AdListAdapter extends ArrayAdapter<CellRequestModel>
{
    private static final String LOG_TAG     = "AdListAdapter";

    public AdListAdapter(Context context, int resource, List<CellRequestModel> objects)
    {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        CellRequestModel requestModel = this.getItem(position);
        AdViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.ad_list_cell, viewGroup, false);
            viewHolder = new AdViewHolder(this.getContext());
            viewHolder.initialize(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (AdViewHolder) convertView.getTag();
        viewHolder.setRequestModel(requestModel);

        return convertView;
    }
}
