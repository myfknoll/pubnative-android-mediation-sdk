package net.pubnative.mediation.request.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davidmartin on 04/05/16.
 */
public class PubnativeAdTargetingModel {

    public Integer      age;
    public String       education;
    public List<String> interests;
    public String       gender;
    public Boolean      iap; // In app purchase enabled, Just open it for the user to fill
    public Float        iap_total; // In app purchase total spent, just open for the user to fill

    public void addInterest(String interest) {

        if (interests == null) {
            interests = new ArrayList<String>();
        }
        interests.add(interest);
    }

    public Map toDictionary() {

        Map result = new HashMap();
        if (age != null) {
            result.put("age", age);
        }
        if (!TextUtils.isEmpty(education)) {
            result.put("education", education);
        }
        if (interests != null && interests.size() > 0) {
            result.put("interests", TextUtils.join(",", interests.toArray()));
        }
        if (!TextUtils.isEmpty(gender)) {
            result.put("gender", gender);
        }
        if (iap != null) {
            result.put("iap", iap);
        }
        if (iap_total != null) {
            result.put("iap_total", iap_total);
        }
        return result;
    }
}
