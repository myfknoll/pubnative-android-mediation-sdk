package net.pubnative.library.model;

import net.pubnative.library.PubnativeContract.Response.NativeAd.Beacon;

import org.droidparts.annotation.serialize.JSON;
import org.droidparts.model.Model;

public class BeaconModel extends Model implements Beacon
{
    /**
     * 
     */
    private static final long serialVersionUID = 2L;
    //
    // FIELDS
    //
    @JSON(key = TYPE)
    public String             type;
    @JSON(key = URL)
    public String             url;
}
