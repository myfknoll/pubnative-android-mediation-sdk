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
package net.pubnative.mediation.insights.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import net.pubnative.mediation.R;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.ArrayList;
import java.util.List;

public class PubnativeInsightDataModel {

    protected static final String CONNECTION_TYPE_CELLULAR = "cellular";
    protected static final String CONNECTION_TYPE_WIFI     = "wifi";
    // Tracking info
    public String                             network;
    public List<String>                       attempted_networks;
    public List<String>                       unreachable_networks;
    public List<Integer>                      delivery_segment_ids;
    public List<PubnativeInsightNetworkModel> networks;
    public String                             placement_name;
    public String                             pub_app_version;
    public String                             pub_app_bundle_id;
    public String                             os_version;
    public String                             sdk_version;
    public String                             user_uid; // android advertiser id
    public String                             connection_type; //typ “wifi” or “cellular"
    public String                             device_name;
    public String                             ad_format_code;
    public String                             creative_url; // Creative selected from the ad_format_code value of the config
    public Boolean                            video_start;
    public Boolean                            video_complete;
    public Integer                            retry;
    // User info
    public Integer                            age;
    public String                             education;
    public List<String>                       interests;
    public String                             gender;
    public Boolean                            iap; // In app purchase enabled, Just open it for the user to fill
    public Float                              iap_total; // In app purchase total spent, just open for the user to fill

    /**
     * This method takes two Objects "first" and "second" as arguments and does a comparison.
     * Returns true if they are equal.
     * Returns false if they are not equal or not comparable.
     */
    private boolean isEqual(Object first, Object second) {

        return (first != null) ? first.equals(second) : second == null;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            // return true immediately if both objects are identical.
            return true;
        }
        if (!(object instanceof PubnativeInsightDataModel)) {
            // return immediately if the object is of another class,
            // this is to avoid possible class cast exception in next line.
            return false;
        }
        PubnativeInsightDataModel dataModel = (PubnativeInsightDataModel) object;
        boolean                   result    = isEqual(this.network, dataModel.network);
        if (result) {
            result = isEqual(this.attempted_networks, dataModel.attempted_networks);
        }
        if (result) {
            result = isEqual(this.placement_name, dataModel.placement_name);
        }
        if (result) {
            result = isEqual(this.pub_app_version, dataModel.pub_app_version);
        }
        if (result) {
            result = isEqual(this.pub_app_bundle_id, dataModel.pub_app_bundle_id);
        }
        if (result) {
            result = isEqual(this.os_version, dataModel.os_version);
        }
        if (result) {
            result = isEqual(this.sdk_version, dataModel.sdk_version);
        }
        if (result) {
            result = isEqual(this.user_uid, dataModel.user_uid);
        }
        if (result) {
            result = isEqual(this.connection_type, dataModel.connection_type);
        }
        if (result) {
            result = isEqual(this.device_name, dataModel.device_name);
        }
        if (result) {
            result = isEqual(this.ad_format_code, dataModel.ad_format_code);
        }
        if (result) {
            result = isEqual(this.creative_url, dataModel.creative_url);
        }
        if (result) {
            result = isEqual(this.video_start, dataModel.video_start);
        }
        if (result) {
            result = isEqual(this.video_complete, dataModel.video_complete);
        }
        // user info
        if (result) {
            result = isEqual(this.age, dataModel.age);
        }
        if (result) {
            result = isEqual(this.education, dataModel.education);
        }
        if (result) {
            result = isEqual(this.interests, dataModel.interests);
        }
        if (result) {
            result = isEqual(this.gender, dataModel.gender);
        }
        if (result) {
            result = isEqual(this.iap, dataModel.iap);
        }
        if (result) {
            result = isEqual(this.iap_total, dataModel.iap_total);
        }
        return result;
    }

    public void addInterest(String interest) {

        if (!TextUtils.isEmpty(interest)) {
            if (this.interests == null) {
                this.interests = new ArrayList();
            }
            this.interests.add(interest);
        }
    }

    public void addNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime, PubnativeInsightCrashModel crashModel) {

        if (priorityRuleModel != null) {
            if (this.networks == null) {
                this.networks = new ArrayList();
            }
            PubnativeInsightNetworkModel networkModel = new PubnativeInsightNetworkModel();
            networkModel.code = priorityRuleModel.network_code;
            networkModel.priority_rule_id = priorityRuleModel.id;
            networkModel.priority_segment_ids = priorityRuleModel.segment_ids;
            networkModel.response_time = responseTime;
            if (crashModel != null) {
                networkModel.crash_report = crashModel;
            }
            this.networks.add(networkModel);
        }
    }

    public void addAttemptedNetwork(String network) {

        if (!TextUtils.isEmpty(network)) {
            if (this.attempted_networks == null) {
                this.attempted_networks = new ArrayList();
            }
            this.attempted_networks.add(network);
        }
    }

    public void addUnreachableNetwork(String network) {

        if (!TextUtils.isEmpty(network)) {
            if (this.unreachable_networks == null) {
                this.unreachable_networks = new ArrayList();
            }
            this.unreachable_networks.add(network);
        }
    }

    public void reset() {

        this.retry = 0;
        this.network = null;
        this.networks = null;
        this.delivery_segment_ids = null;
        this.attempted_networks = null;
        this.unreachable_networks = null;
    }

    /**
     * Fills insight data model with default available data.
     *
     * @param context valid Context object
     */
    public void fillDefaults(Context context) {

        if (context != null) {
            PackageInfo info = PubnativeDeviceUtils.getPackageInfo(context);
            if (info != null) {
                this.pub_app_version = info.versionName;
                this.pub_app_bundle_id = info.packageName;
            }
            this.retry = 0;
            this.os_version = Build.VERSION.RELEASE;
            this.device_name = Build.MODEL;
            this.sdk_version = context.getResources().getString(R.string.version);
            // AAID
            String androidAdvertisingId = PubnativeDeviceUtils.getAndroidAdvertisingID(context);
            if (androidAdvertisingId != null) {
                this.user_uid = androidAdvertisingId;
            }
            // Connection type
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    String connectionType = CONNECTION_TYPE_CELLULAR;
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        connectionType = CONNECTION_TYPE_WIFI;
                    }
                    this.connection_type = connectionType;
                }
            }
        }
    }
}
