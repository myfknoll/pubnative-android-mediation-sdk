package net.pubnative.mediation.request;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.config.PubnativeAdCache;
import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.config.PubnativeFrequencyManager;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeCacheAdModel;
import net.pubnative.mediation.model.PubnativeConfigModel;
import net.pubnative.mediation.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.model.PubnativeNetworkModel;
import net.pubnative.mediation.model.PubnativePlacementModel;

import java.util.ArrayList;
import java.util.List;

public class PubnativeNetworkRequest implements PubnativeNetworkAdapterListener
{
    protected PubnativeNetworkRequestListener   listener;
    protected PubnativeNetworkRequestParameters parameters;
    protected PubnativeConfigModel              config;
    protected PubnativePlacementModel           placement;
    protected List<PubnativeAdModel>            ads;
    protected int                               currentRankIndex;
    protected Context                           context;

    public PubnativeNetworkRequest()
    {
        // Do some initialization here
    }

    public void request(Context context, PubnativeNetworkRequestParameters parameters,
                        PubnativeNetworkRequestListener listener)
    {
        this.context = context;
        this.currentRankIndex = 0;
        this.ads = new ArrayList();

        if (listener == null)
        {
            // Just drop the call
            System.out.println("PubnativeNetworkRequest.request - listener not specified, dropping the call");
            return;
        }
        this.listener = listener;

        this.invokeStart();
        if (this.context == null || parameters == null || TextUtils.isEmpty(parameters.app_token) || TextUtils.isEmpty(parameters.placement_id))
        {
            this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.request - invalid request parameters"));
        }
        else
        {
            this.parameters = parameters;
            this.config = PubnativeConfigManager.getConfig(this.context, this.parameters.app_token);
            if (this.config == null)
            {
                this.invokeFail(new NetworkErrorException("PubnativeNetworkRequest.request - invalid config retrieved"));
            }
            else
            {
                if (this.config.placements.containsKey(this.parameters.placement_id))
                {
                    this.placement = this.config.placements.get(this.parameters.placement_id);
                    if (this.placement.delivery_rule.isActive())
                    {
                        this.doRequest();
                    }
                    else
                    {
                        this.invokeFail(new Exception("PubnativeNetworkRequest.request - placement_id not active"));
                    }
                }
                else
                {
                    this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.request - placement_id not found"));
                }
            }
        }
    }

    protected void doRequest()
    {
        int adCount = this.getAdsLeft();
        if (adCount <= 0 || this.currentRankIndex >= this.placement.priority_rules.size())
        {
            // No more needed ads or possible networks to request
            this.invokeLoad(this.ads);
        }
        else
        {
            PubnativeDeliveryRuleModel deliveryRuleModel = this.placement.delivery_rule;
            int frequencyCap = this.getFrequencyCap(this.context, adCount, deliveryRuleModel);

            if (frequencyCap > 0)
            {
                int pacingCap = this.getPacingCap(this.context, adCount, deliveryRuleModel);

                // If requestable ads is less than the pacing limit
                // fill part of the request with cached ads
                if (adCount < pacingCap)
                {
                    List<PubnativeCacheAdModel> adsCache = PubnativeAdCache.getCachedAds(this.context);
                    int requiredCacheAds = pacingCap - adCount;
                    for (PubnativeCacheAdModel cacheModel : adsCache)
                    {
                        this.ads.add(cacheModel);
                        if (--requiredCacheAds == 0)
                        {
                            break;
                        }
                    }
                }

                this.doAdapterRequest(adCount);
            }
            else
            {
                if (this.ads != null && this.ads.size() > 0)
                {
                    this.invokeLoad(this.ads);
                }
                else
                {
                    this.invokeFail(new Exception("Pubnative - request error: frequecy cap"));
                }
            }
        }
    }

    protected void doAdapterRequest(int adCount)
    {
        String networkIDString = null;
        if (this.placement.priority_rules != null && this.placement.priority_rules.size() > this.currentRankIndex)
        {
            networkIDString = this.placement.priority_rules.get(this.currentRankIndex).network_code;
        }
        this.currentRankIndex++;
        if (!TextUtils.isEmpty(networkIDString) && this.config.networks.containsKey(networkIDString))
        {
            PubnativeNetworkModel network = this.config.networks.get(networkIDString);
            PubnativeNetworkAdapter adapter = PubnativeNetworkAdapterFactory.createAdapter(network);

            if (adapter == null)
            {
                System.out.println(new Exception("PubnativeNetworkRequest.requestForPlacementRank - Error: " + network.adapter + " not found"));
                this.doRequest();
            }
            else
            {
                // TODO: Add timeout
                adapter.doRequest(this.context, adCount, this);
            }
        }
        else
        {
            System.out.println(new Exception("PubnativeNetworkRequest.requestForPlacementRank - Error: networkID " + networkIDString + " config not found"));
            this.doRequest();
        }
    }

    protected int getAdsLeft()
    {
        int requestedAds = 0; // Default value if not included
        int foundAds = 0;
        if (this.parameters != null)
        {
            requestedAds = this.parameters.ad_count;
        }
        if (this.ads != null)
        {
            foundAds = this.ads.size();
        }
        return requestedAds - foundAds;
    }

    protected int getFrequencyCap(Context context, int adCount, PubnativeDeliveryRuleModel deliveryRuleModel)
    {
        int result = adCount;

        if (context != null && deliveryRuleModel != null)
        {
            // Cap adCount value by frequency count and caps

            if (deliveryRuleModel.isDayImpressionCapActive())
            {
                int leftDailyAds = deliveryRuleModel.imp_cap_day - PubnativeFrequencyManager.getCurrentDailyCount(context);
                result = Math.min(result, leftDailyAds);
            }

            if (deliveryRuleModel.isHourImpressionCapActive())
            {
                int leftHourlyAds = deliveryRuleModel.imp_cap_hour - PubnativeFrequencyManager.getCurrentHourlyCount(context);
                result = Math.min(result, leftHourlyAds);
            }
        }
        return result;
    }

    protected int getPacingCap(Context context, int adCount, PubnativeDeliveryRuleModel deliveryRuleModel)
    {
        int result = adCount;
        if (deliveryRuleModel.isPacingCapActive())
        {
            PubnativeAdCache.updateAdsCache(this.context, deliveryRuleModel);
            List<PubnativeCacheAdModel> adsCache = PubnativeAdCache.getCachedAds(this.context);
            if (adsCache != null)
            {
                // Set pacing cap
                int pacingCap = deliveryRuleModel.pacing_cap_hour;
                if (deliveryRuleModel.pacing_cap_minute > 0)
                {
                    pacingCap = deliveryRuleModel.pacing_cap_minute;
                }

                result = pacingCap - adsCache.size();
            }
        }
        return result;
    }

    // HELPERS

    protected void invokeStart()
    {
        if (this.listener != null)
        {
            this.listener.onRequestStarted(this);
        }
    }

    protected void invokeLoad(List<PubnativeAdModel> ads)
    {
        if (this.listener != null)
        {
            this.listener.onRequestLoaded(this, ads);
        }
    }

    protected void invokeFail(Exception exception)
    {
        if (this.listener != null)
        {
            this.listener.onRequestFailed(this, exception);
        }
    }

    // PubnativeNetworkAdapterListener methods

    @Override
    public void onAdapterRequestStarted(PubnativeNetworkAdapter adapter)
    {
        // Do nothing
    }

    @Override
    public void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, List<PubnativeAdModel> ads)
    {
        // save returned ads and waterfall to the next network
        if (ads != null && ads.size() > 0)
        {
            this.ads.addAll(ads);
        }
        this.doRequest();
    }

    @Override
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception)
    {
        System.out.println("Pubnative - adapter request error: " + exception);
        // Waterfall to the next network
        this.doRequest();
    }
}
