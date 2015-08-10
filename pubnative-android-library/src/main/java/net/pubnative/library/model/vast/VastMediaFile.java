package net.pubnative.library.model.vast;

import net.pubnative.library.PubnativeContract.Response.VideoNativeAd.Vast;

import org.droidparts.annotation.serialize.XML;
import org.droidparts.model.Model;

public class VastMediaFile extends Model
{
    private static final long serialVersionUID = 2L;
    @XML(attribute = Vast.Ad.InLine.Creatives.Creative.Linear.MediaFiles.MediaFile.ATTR_DELIVERY)
    public String             delivery;
    @XML(attribute = Vast.Ad.InLine.Creatives.Creative.Linear.MediaFiles.MediaFile.ATTR_HEIGHT)
    public int                height;
    @XML(attribute = Vast.Ad.InLine.Creatives.Creative.Linear.MediaFiles.MediaFile.ATTR_SCALABLE)
    public boolean            scalable;
    @XML(attribute = Vast.Ad.InLine.Creatives.Creative.Linear.MediaFiles.MediaFile.ATTR_TYPE)
    public String             type;
    @XML(attribute = Vast.Ad.InLine.Creatives.Creative.Linear.MediaFiles.MediaFile.ATTR_WIDTH)
    public int                width;
    @XML
    public String             url;
}
