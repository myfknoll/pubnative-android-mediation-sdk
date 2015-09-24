package net.pubnative.library.predefined.game_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import net.pubnative.library.R;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.renderer.NativeAdRenderer;

import java.util.List;

public class PubnativeGameListAdapter extends ArrayAdapter<NativeAdModel>
{
    public PubnativeGameListAdapter(Context context, int resource, List<NativeAdModel> objects)
    {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View result = convertView;
        NativeAdModel model = this.getItem(position);
        if (model != null)
        {
            if (result == null)
            {
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                result = inflater.inflate(R.layout.pubnative_game_list_item, null);
            }
            NativeAdRenderer renderer = new NativeAdRenderer(this.getContext());
            renderer.iconView = (ImageView) result.findViewById(R.id.pn_game_list_item_icon);
            renderer.titleView = (TextView) result.findViewById(R.id.pn_game_list_item_title);
            renderer.ratingView = (RatingBar) result.findViewById(R.id.pn_game_list_item_rating);
            renderer.downloadView = (TextView) result.findViewById(R.id.pn_game_list_item_cta);
            renderer.render(model, null);
            model.confirmImpressionAutomatically(this.getContext(), result);
        }
        return result;
    }
}
