package net.pubnative.mediation.adapter.network;

import android.content.Context;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;

import java.util.Map;

public class YahooNetworkAdapter extends PubnativeNetworkAdapter
{

    public YahooNetworkAdapter(Map data)
    {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void request(Context context)
    {
        // TODO Auto-generated method stub
        this.invokeLoaded(null);
    }
}
