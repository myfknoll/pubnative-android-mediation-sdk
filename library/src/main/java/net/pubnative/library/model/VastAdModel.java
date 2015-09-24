package net.pubnative.library.model;

import net.pubnative.library.PubnativeContract.Response.VideoNativeAd.VideoAd;

import org.droidparts.annotation.serialize.JSON;
import org.droidparts.model.Model;

public class VastAdModel extends Model implements VideoAd
{
    /**
     * 
     */
    private static final long serialVersionUID = 2L;
    //
    // FIELDS
    //
    @JSON(key = AD)
    public String             ad;
    @JSON(key = VIDEO_SKIP_TIME)
    public int                video_skip_time;
    @JSON(key = SKIP_VIDEO_BUTTON)
    public String             skip_video_button;
    @JSON(key = MUTE)
    public String             mute;
    @JSON(key = LEARN_MORE_BUTTON)
    public String             learn_more_button;
}
