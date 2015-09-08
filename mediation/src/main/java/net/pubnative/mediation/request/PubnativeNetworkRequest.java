package net.pubnative.mediation.request;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.config.PubnativeConfigRequestListener;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.Calendar;

public class PubnativeNetworkRequest implements PubnativeNetworkAdapterListener, PubnativeConfigRequestListener
{
    protected static final String APP_TOKEN_PARAMETER = "?app_token=";

    protected Context                         context;
    protected PubnativeNetworkRequestListener listener;
    protected PubnativeConfigModel            config;
    protected PubnativePlacementModel         placement;
    protected PubnativeAdModel                ad;
    protected PubnativeInsightDataModel       trackingModel;
    protected String                          appToken;
    protected String                          placementID;
    protected String                          currentNetworkID;
    protected int                             currentNetworkIndex;

    /**
     * This constructor should be called from the UI thread.
     */
    public PubnativeNetworkRequest()
    {
        this.trackingModel = new PubnativeInsightDataModel();
    }

    // TRACKING INFO
    public void setAge(int age)
    {
        this.trackingModel.age = age;
    }

    public void setEducation(String education)
    {
        this.trackingModel.education = education;
    }

    public void addInterest(String interest)
    {
        this.trackingModel.addInterest(interest);
    }

    public enum Gender
    {
        MALE, FEMALE
    }

    public void setGender(Gender gender)
    {
        this.trackingModel.gender = gender.name().toLowerCase();
    }

    public void setInAppPurchasesEnabled(boolean iap)
    {
        this.trackingModel.iap = iap;
    }

    public void setInAppPurchasesTotal(float iapTotal)
    {
        this.trackingModel.iap_total = iapTotal;
    }

    // REQUEST
    public void start(Context context, String appToken, String placementID, PubnativeNetworkRequestListener listener)
    {
        this.appToken = appToken;
        this.placementID = placementID;
        this.context = context;
        this.currentNetworkIndex = 0;

        if (listener == null)
        {
            // Just drop the call
            System.out.println("PubnativeNetworkRequest.start - listener not specified, dropping the call");
            return;
        }
        this.listener = listener;

        this.invokeStart();
        if (this.context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementID))
        {
            this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - invalid start parameters"));
        }
        else
        {
            this.getConfig(context, appToken, this);
        }
    }

    protected void getConfig(Context context, String appToken, PubnativeConfigRequestListener listener)
    {
        // This method getConfig() here gets the stored/downloaded config and
        // continues to startRequest() in it's callback "onConfigLoaded()".
        PubnativeConfigManager.getConfig(context, appToken, listener);
    }

    @Override
    public void onConfigLoaded(PubnativeConfigModel configModel)
    {
        this.startRequest(configModel);
    }

    private void startRequest(PubnativeConfigModel configModel)
    {
        this.config = configModel;

        if (this.config == null || this.config.isNullOrEmpty())
        {
            this.invokeFail(new NetworkErrorException("PubnativeNetworkRequest.start - invalid config retrieved"));
        }
        else
        {
            if (this.config.placements.containsKey(placementID))
            {
                this.placement = this.config.placements.get(placementID);
                if (this.placement != null && this.placement.delivery_rule != null)
                {
                    if (this.placement.delivery_rule.isActive())
                    {
                        this.trackingModel.reset();
                        this.trackingModel.placement_id = placementID;
                        this.startRequest();
                    }
                    else
                    {
                        this.invokeFail(new Exception("PubnativeNetworkRequest.start - placement_id not active"));
                    }
                }
                else
                {
                    this.invokeFail(new Exception("PubnativeNetworkRequest.start - config error"));
                }
            }
            else
            {
                this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - placement_id not found"));
            }
        }
    }

    protected void startRequest()
    {
        PubnativeDeliveryRuleModel deliveryRuleModel = this.placement.delivery_rule;

        if (deliveryRuleModel.isFrequencyCapReached(context, this.placementID))
        {
            this.invokeFail(new Exception("Pubnative - start error: (frequecy_cap) too many ads"));
        }
        else
        {
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(this.placementID);
            if (overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar))
            {
                // Pacing cap deactivated or not reached
                this.doNextNetworkRequest();
            }
            else
            {
                // Pacing cap active and limit reached
                // return the same ad during the pacing cap amount of time
                if (this.ad == null)
                {
                    this.invokeFail(new Exception("Pubnative - start error: (pacing_cap) too many ads"));
                }
                else
                {
                    this.invokeLoad(this.ad);
                }
            }
        }
    }

    protected void doNextNetworkRequest()
    {
        if (this.placement.priority_rules != null && this.placement.priority_rules.size() > this.currentNetworkIndex)
        {
            this.currentNetworkID = this.placement.priority_rules.get(this.currentNetworkIndex).network_code;
            this.currentNetworkIndex++;
            if (!TextUtils.isEmpty(this.currentNetworkID) && this.config.networks.containsKey(this.currentNetworkID))
            {
                PubnativeNetworkModel network = this.config.networks.get(this.currentNetworkID);
                PubnativeNetworkAdapter adapter = PubnativeNetworkAdapterFactory.createAdapter(network);

                if (adapter == null)
                {
                    System.out.println(new Exception("PubnativeNetworkRequest.requestForPlacementRank - Error: " + network.adapter + " not found"));
                    this.trackNetworkAttempt(this.currentNetworkID);
                    this.doNextNetworkRequest();
                }
                else
                {
                    adapter.doRequest(this.context, network.timeout, this);
                }
            }
            else
            {
                System.out.println(new Exception("PubnativeNetworkRequest.requestForPlacementRank - Error: networkID " + currentNetworkID + " config not found"));
                this.trackNetworkAttempt(this.currentNetworkID);
                this.doNextNetworkRequest();
            }
        }
        else
        {
            this.invokeFail(new Exception("Pubnative - no fill"));
        }
    }

    // HELPERS
    protected void trackNetworkAttempt(String networkID)
    {
        this.trackingModel.addAttemptedNetwork(networkID);
    }

    protected void invokeStart()
    {
        if (this.listener != null)
        {
            this.listener.onRequestStarted(this);
        }
    }

    protected void invokeLoad(final PubnativeAdModel ad)
    {
        if (this.listener != null)
        {
            this.listener.onRequestLoaded(this, ad);
        }
    }

    protected void invokeFail(final Exception exception)
    {
        if (this.listener != null)
        {
            this.listener.onRequestFailed(this, exception);
        }
    }

    // CALLBACKS
    // PubnativeNetworkAdapterListener

    @Override
    public void onAdapterRequestStarted(PubnativeNetworkAdapter adapter)
    {
        // Do nothing
    }

    @Override
    public void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad)
    {
        String adFormatCode = this.placement.ad_format_code;
        String impressionURL = null;
        if (this.config.globals.containsKey(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON))
        {
            impressionURL = (String) this.config.globals.get(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON);
            impressionURL = impressionURL + APP_TOKEN_PARAMETER + this.appToken;
        }
        String clickURL = null;
        if (this.config.globals.containsKey(PubnativeConfigModel.ConfigContract.CLICK_BEACON))
        {
            clickURL = (String) this.config.globals.get(PubnativeConfigModel.ConfigContract.CLICK_BEACON);
            clickURL = clickURL + APP_TOKEN_PARAMETER + this.appToken;
        }
        this.trackingModel.fillDefaults(this.context);
        this.trackingModel.ad_format_code = adFormatCode;
        this.trackingModel.network = this.currentNetworkID;
        this.ad = ad;
        this.ad.setTrackingInfo(this.trackingModel, impressionURL, clickURL);
        PubnativeDeliveryManager.updatePacingCalendar(this.trackingModel.placement_id);
        this.invokeLoad(ad);
    }

    @Override
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception)
    {
        System.out.println("Pubnative - adapter start error: " + exception);
        // Waterfall to the next network
        this.trackNetworkAttempt(this.currentNetworkID);
        this.doNextNetworkRequest();
    }
}
