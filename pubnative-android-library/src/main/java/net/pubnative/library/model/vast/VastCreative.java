/**
 * Copyright 2014 PubNative GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pubnative.library.model.vast;

import java.util.ArrayList;

import net.pubnative.library.PubnativeContract.Response.VideoNativeAd.Vast;

import org.droidparts.annotation.serialize.XML;
import org.droidparts.model.Model;

public class VastCreative extends Model
{
    private static final long           serialVersionUID = 2L;
    private final String                VAST_LINEAR      = Vast.Ad.InLine.Creatives.Creative.LINEAR + XML.SUB;
    @XML(tag = VAST_LINEAR + Vast.Ad.InLine.Creatives.Creative.Linear.DURATION)
    public String                       duration;
    @XML(tag = VAST_LINEAR + Vast.Ad.InLine.Creatives.Creative.Linear.TRACKING_EVENTS,
         attribute = Vast.Ad.InLine.Creatives.Creative.Linear.TrackingEvents.TRACKING)
    public ArrayList<VastTrackingEvent> trackingEvents;
    @XML(tag = VAST_LINEAR + Vast.Ad.InLine.Creatives.Creative.Linear.MEDIA_FILES,
         attribute = Vast.Ad.InLine.Creatives.Creative.Linear.MediaFiles.MEDIA_FILE)
    public ArrayList<VastMediaFile>     mediaFiles;

    public String getEventUrl(VastTrackingEvent.EventType eventType)
    {
        for (VastTrackingEvent trackingEvent : this.trackingEvents)
        {
            if (eventType.key.equals(trackingEvent.event))
            {
                return trackingEvent.url;
            }
        }
        return null;
    }
}
