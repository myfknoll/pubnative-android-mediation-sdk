package net.pubnative.library.model;

import net.pubnative.library.PubnativeContract.Response.VideoNativeAd;

import org.droidparts.annotation.serialize.JSON;
import org.droidparts.persist.serializer.XMLSerializer;
import org.droidparts.util.L;
import org.w3c.dom.Node;

import android.content.Context;

public class VideoAdModel extends NativeAdModel implements VideoNativeAd
{
    /**
     * 
     */
    private static final long serialVersionUID = 2L;
    //
    // FIELDS
    //
    @JSON(key = VAST)
    VastAdModel[]             vast;

    public VideoAd getVastAd(Context ctx)
    {
        if (this.vast.length == 1)
        {
            String txt = this.vast[0].ad;
            try
            {
                Node doc = XMLSerializer.parseDocument(txt).getFirstChild();
                return new XMLSerializer<VastAdModel>(VastAdModel.class, ctx).deserialize(doc);
            }
            catch (Exception e)
            {
                L.wtf(e);
            }
        }
        return null;
    }

    private VastAdModel getVastAdModel(int index)
    {
        VastAdModel result = null;
        if (this.vast.length > index)
        {
            result = this.vast[index];
        }
        return result;
    }

    public int getVideoSkipTime()
    {
        int result = -1;
        VastAdModel model = this.getVastAdModel(0);
        if (model != null)
        {
            result = model.video_skip_time;
        }
        return result;
    }

    public String getSkipVideoButton()
    {
        String result = "";
        VastAdModel model = this.getVastAdModel(0);
        if (model != null)
        {
            result = model.skip_video_button;
        }
        return result;
    }

    public String getMuteString()
    {
        String result = "";
        VastAdModel model = this.getVastAdModel(0);
        if (model != null)
        {
            result = model.mute;
        }
        return result;
    }

    public String getLearnMoreButton()
    {
        String result = "";
        VastAdModel model = this.getVastAdModel(0);
        if (model != null)
        {
            result = model.learn_more_button;
        }
        return result;
    }
}
