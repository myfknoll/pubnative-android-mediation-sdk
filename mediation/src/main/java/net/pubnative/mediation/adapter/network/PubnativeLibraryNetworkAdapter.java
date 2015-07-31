package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;

import java.util.Map;

public class PubnativeLibraryNetworkAdapter extends PubnativeNetworkAdapter
{

    public PubnativeLibraryNetworkAdapter(Map data)
    {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void request()
    {
        // TODO Auto-generated method stub
        this.invokeLoaded(null);
    }
}
