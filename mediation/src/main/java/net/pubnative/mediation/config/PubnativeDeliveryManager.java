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

package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PubnativeDeliveryManager {

    protected static final String                IMPRESSION_PREFERENCES_KEY    = "net.pubnative.mediation.frequency_manager";
    protected static final String                IMPRESSION_COUNT_DAY_APPEND   = "_impression_count_day";
    protected static final String                IMPRESSION_COUNT_HOUR_APPEND  = "_impression_count_hour";
    protected static final String                IMPRESSION_LAST_UPDATE_APPEND = "_impression_last_update";
    protected              Map<String, Calendar> currentPacing                 = new HashMap<String, Calendar>();

    protected static PubnativeDeliveryManager instance = null;

    private PubnativeDeliveryManager() {
    }

    protected static synchronized PubnativeDeliveryManager getInstance() {
        if (PubnativeDeliveryManager.instance == null) {
            PubnativeDeliveryManager.instance = new PubnativeDeliveryManager();
        }
        return PubnativeDeliveryManager.instance;
    }

    public static Calendar getPacingCalendar(String placementID) {
        Calendar result = null;
        if (PubnativeDeliveryManager.getInstance().currentPacing.containsKey(placementID)) {
            result = PubnativeDeliveryManager.getInstance().currentPacing.get(placementID);
        }
        return result;
    }

    public static void updatePacingCalendar(String placementID) {
        PubnativeDeliveryManager.getInstance().currentPacing.put(placementID, Calendar.getInstance());
    }

    public static void resetPacingCalendar(String placementId) {
        PubnativeDeliveryManager.getInstance().currentPacing.put(placementId, null);
    }

    public static void logImpression(Context context, String placementID) {
        int dayCount  = PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID);
        int hourCount = PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID);

        PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID, ++dayCount);
        PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, ++hourCount);
    }

    public static void resetDailyImpressionCount(Context context, String placementId) {
        PubnativeDeliveryManager.setImpressionCount(context, PubnativeDeliveryManager.IMPRESSION_COUNT_DAY_APPEND, placementId, 0);
    }

    public static void resetHourlyImpressionCount(Context context, String placementId) {
        PubnativeDeliveryManager.setImpressionCount(context, PubnativeDeliveryManager.IMPRESSION_COUNT_HOUR_APPEND, placementId, 0);
    }

    public static int getCurrentDailyCount(Context context, String placementID) {
        return PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID);
    }

    public static int getCurrentHourlyCount(Context context, String placementID) {
        return PubnativeDeliveryManager.getImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID);
    }

    protected static void updateImpressionCount(Context context, String placementID) {
        if (context != null && !TextUtils.isEmpty(placementID)) {
            Calendar storedCalendar = PubnativeDeliveryManager.getImpressionLastUpdate(context, placementID);
            if (storedCalendar != null) {
                Calendar dayCalendar = Calendar.getInstance();
                dayCalendar.set(Calendar.HOUR_OF_DAY, 0);
                dayCalendar.set(Calendar.MINUTE, 0);
                dayCalendar.set(Calendar.SECOND, 0);
                dayCalendar.set(Calendar.MILLISECOND, 0);
                if (storedCalendar.before(dayCalendar)) {
                    PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_DAY_APPEND, placementID, 0);
                    PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, 0);
                } else {
                    Calendar hourCalendar = Calendar.getInstance();
                    hourCalendar.set(Calendar.MINUTE, 0);
                    hourCalendar.set(Calendar.SECOND, 0);
                    hourCalendar.set(Calendar.MILLISECOND, 0);
                    if (storedCalendar.before(hourCalendar)) {
                        PubnativeDeliveryManager.setImpressionCount(context, IMPRESSION_COUNT_HOUR_APPEND, placementID, 0);
                    }
                }
            }
            PubnativeDeliveryManager.setImpressionLastUpdate(context, placementID, Calendar.getInstance());
        }
    }

    protected static void setImpressionCount(Context context, String impressionCapType, String placementID, int value) {
        if (context != null && !TextUtils.isEmpty(impressionCapType) && !TextUtils.isEmpty(placementID)) {
            SharedPreferences.Editor editor = PubnativeDeliveryManager.getPreferencesEditor(context);
            if (editor != null) {
                String placementTrackingKey = placementID.concat(impressionCapType);
                if (value == 0) {
                    editor.remove(placementTrackingKey);
                } else {
                    editor.putInt(placementTrackingKey, value);
                }
                editor.apply();
            }
        }
    }

    protected static int getImpressionCount(Context context, String impressionCapType, String placementID) {
        PubnativeDeliveryManager.updateImpressionCount(context, placementID);
        int result = 0;
        if (context != null && !TextUtils.isEmpty(impressionCapType) && !TextUtils.isEmpty(placementID)) {
            SharedPreferences preferences = PubnativeDeliveryManager.getPreferences(context);
            if (preferences != null) {
                String placementTrackingKey = placementID.concat(impressionCapType);
                result = preferences.getInt(placementTrackingKey, 0);
            }
        }
        return result;
    }

    /**
     * Records the timestamp when the impression tracking is confirmed
     *
     * @param context     valid Context object
     * @param placementID valid placement id provided by Pubnative
     * @param calendar    calendar object with the timestamp
     */
    public static void setImpressionLastUpdate(Context context, String placementID, Calendar calendar) {
        if (context != null && !TextUtils.isEmpty(placementID)) {
            SharedPreferences.Editor editor = PubnativeDeliveryManager.getPreferencesEditor(context);
            if (editor != null) {
                String placementLastUpdateKey = placementID.concat(IMPRESSION_LAST_UPDATE_APPEND);
                if (calendar == null) {
                    editor.remove(placementLastUpdateKey);
                } else {
                    editor.putLong(placementLastUpdateKey, calendar.getTimeInMillis());
                }
                editor.apply();
            }
        }
    }

    public static Calendar getImpressionLastUpdate(Context context, String placementID) {
        Calendar result = null;
        if (context != null && !TextUtils.isEmpty(placementID)) {
            SharedPreferences preferences = PubnativeDeliveryManager.getPreferences(context);
            if (preferences != null) {
                String placementLastUpdateKey = placementID.concat(IMPRESSION_LAST_UPDATE_APPEND);
                long frequencyMillis = preferences.getLong(placementLastUpdateKey, 0);
                if (frequencyMillis > 0) {
                    result = Calendar.getInstance();
                    result.setTimeInMillis(frequencyMillis);
                }
            }
        }
        return result;
    }

    protected static SharedPreferences.Editor getPreferencesEditor(Context context) {
        SharedPreferences.Editor result = null;
        if (context != null) {
            result = PubnativeDeliveryManager.getPreferences(context).edit();
        }
        return result;
    }

    protected static SharedPreferences getPreferences(Context context) {
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(IMPRESSION_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
        return result;
    }
}
