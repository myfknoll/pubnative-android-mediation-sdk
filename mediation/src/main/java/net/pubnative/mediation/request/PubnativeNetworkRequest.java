package net.pubnative.mediation.request;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeConfigModel;
import net.pubnative.mediation.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.model.PubnativeNetworkModel;
import net.pubnative.mediation.model.PubnativePlacementModel;

import java.util.Calendar;

public class PubnativeNetworkRequest implements PubnativeNetworkAdapterListener
{
    protected Context                           context;
    protected PubnativeNetworkRequestListener   listener;
    protected PubnativeNetworkRequestParameters parameters;
    protected PubnativeConfigModel              config;
    protected PubnativePlacementModel           placement;
    protected PubnativeAdModel                  ad;
    protected int                               currentRankIndex;

    public PubnativeNetworkRequest()
    {
        // Do some initialization here
    }

    public void start(Context context, PubnativeNetworkRequestParameters parameters, PubnativeNetworkRequestListener listener)
    {
        this.context = context;
        this.currentRankIndex = 0;

        if (listener == null)
        {
            // Just drop the call
            System.out.println("PubnativeNetworkRequest.start - listener not specified, dropping the call");
            return;
        }
        this.listener = listener;

        this.invokeStart();
        if (this.context == null || parameters == null || TextUtils.isEmpty(parameters.app_token) || TextUtils.isEmpty(parameters.placement_id))
        {
            this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - invalid start parameters"));
        }
        else
        {
            this.parameters = parameters;
            this.config = PubnativeConfigManager.getConfig(this.context, this.parameters.app_token);
            if (this.config == null)
            {
                this.invokeFail(new NetworkErrorException("PubnativeNetworkRequest.start - invalid config retrieved"));
            }
            else
            {
                if (this.config.placements.containsKey(this.parameters.placement_id))
                {
                    this.placement = this.config.placements.get(this.parameters.placement_id);
                    if (this.placement != null && this.placement.delivery_rule != null)
                    {
                        if (this.placement.delivery_rule.isActive())
                        {
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
    }

    protected void startRequest()
    {
        PubnativeDeliveryRuleModel deliveryRuleModel = this.placement.delivery_rule;

        if (deliveryRuleModel.isFrequencyCapReached(context, this.parameters.placement_id))
        {
            this.invokeFail(new Exception("Pubnative - start error: (frequecy_cap) too many ads"));
        }
        else
        {
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(this.parameters.placement_id);
            if(overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar))
            {
                // Pacing cap deactivated or not reached
                this.doNextNetworkRequest();
            }
            else
            {
                // Pacing cap active and limit reached
                // return the same ad during the pacing cap amount of time
                if(this.ad == null)
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
                this.doNextNetworkRequest();
            }
            else
            {
                // TODO: Add timeout
                adapter.doRequest(this.context, this);
            }
        }
        else
        {
            System.out.println(new Exception("PubnativeNetworkRequest.requestForPlacementRank - Error: networkID " + networkIDString + " config not found"));
            this.doNextNetworkRequest();
        }
    }

    // HELPERS

    protected void invokeStart()
    {
        if (this.listener != null)
        {
            this.listener.onRequestStarted(this);
        }
    }

    protected void invokeLoad(PubnativeAdModel ad)
    {
        if (this.listener != null)
        {
            this.listener.onRequestLoaded(this, ad);
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
    public void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad)
    {
        PubnativeDeliveryManager.updatePacingCalendar(this.parameters.placement_id);
        this.ad = ad;
        this.invokeLoad(ad);
    }

    @Override
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception)
    {
        System.out.println("Pubnative - adapter start error: " + exception);
        // Waterfall to the next network
        this.doNextNetworkRequest();
    }
}
