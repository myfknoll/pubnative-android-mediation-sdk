package net.pubnative.library.model.vast;

import net.pubnative.library.PubnativeContract.Response.VideoNativeAd.Vast;

import org.droidparts.annotation.serialize.XML;
import org.droidparts.model.Model;

public class VastTrackingEvent extends Model
{
    public enum EventType
    {
        CREATE_VIEW("creativeView"), START("start"), FIRST_QUARTILE(
                "firstQuartile"), MIDPOINT("midpoint"), THIRD_QUARTILE(
                "thirdQuartile"), COMPLETE("complete"), MUTE("mute"), UNMUTE(
                "unmute"), PAUSE("pause"), FULL_SCREEN("fullscreen");
        public final String key;

        private EventType(String key)
        {
            this.key = key;
        }
    }

    private static final long serialVersionUID = 2L;
    @XML(attribute = Vast.Ad.InLine.Creatives.Creative.Linear.TrackingEvents.Tracking.ATTR_EVENT)
    public String             event;
    @XML
    public String             url;
}
