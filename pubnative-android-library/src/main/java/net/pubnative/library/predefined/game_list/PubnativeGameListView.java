package net.pubnative.library.predefined.game_list;

import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import net.pubnative.library.R;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivity;
import net.pubnative.library.predefined.PubnativeView;

import java.util.ArrayList;

public class PubnativeGameListView extends PubnativeView implements
        OnItemClickListener
{
    ListView itemsListView;

    public PubnativeGameListView(PubnativeActivity activity, ArrayList<NativeAdModel> ads)
    {
        super(activity, ads);
        this.ads = ads;
        this.getActivity().getLayoutInflater().inflate(R.layout.pubnative_game_list, this, true);
        this.itemsListView = (ListView) this.findViewById(R.id.pn_game_list_items);
        this.itemsListView.setAdapter(new PubnativeGameListAdapter(activity, R.layout.pubnative_game_list, ads));
        this.itemsListView.setOnItemClickListener(this);
    }

    @Override
    protected void onOrientationChanged(Configuration configuration)
    {
        // Do nothing, this actually resizes correctly
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Log.v("PubnativeGameListView", "onItemClick: position-" + position);
        NativeAdModel clickedObjectModel = this.ads.get(position);
        clickedObjectModel.open(this.getContext());
    }
}
