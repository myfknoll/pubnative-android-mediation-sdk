// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.mediation.config.model;

import android.content.Context;

import net.pubnative.mediation.config.PubnativeDeliveryManager;

import java.util.Calendar;
import java.util.List;

public class PubnativeDeliveryRuleModel {

    public int           imp_cap_day;
    public int           imp_cap_hour;
    public int           pacing_cap_hour;
    public int           pacing_cap_minute;
    public boolean       no_ads;
    public List<Integer> segment_ids;

    public boolean isActive() {

        return !this.no_ads;
    }

    public boolean isDayImpressionCapActive() {

        return this.imp_cap_day > 0;
    }

    public boolean isHourImpressionCapActive() {

        return this.imp_cap_hour > 0;
    }

    public boolean isPacingCapActive() {

        return this.pacing_cap_hour > 0 || this.pacing_cap_minute > 0;
    }

    public Calendar getPacingOverdueCalendar() {

        Calendar result = null;
        if (this.isPacingCapActive()) {
            result = Calendar.getInstance();
            if (pacing_cap_minute > 0) {
                result.add(Calendar.MINUTE, -pacing_cap_minute);
            } else {
                result.add(Calendar.HOUR_OF_DAY, -pacing_cap_hour);
            }
        }
        return result;
    }

    public boolean isFrequencyCapReached(Context context, String placementID) {

        boolean frequencyCapReached = false;
        if (this.isDayImpressionCapActive()) {
            frequencyCapReached = this.imp_cap_day <= PubnativeDeliveryManager.getCurrentDailyCount(
                    context, placementID);
        }
        if (!frequencyCapReached && this.isHourImpressionCapActive()) {
            frequencyCapReached = this.imp_cap_hour <= PubnativeDeliveryManager.getCurrentHourlyCount(
                    context, placementID);
        }
        return frequencyCapReached;
    }
}
