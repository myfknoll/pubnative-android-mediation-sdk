package net.pubnative.mediation.request;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeConfigModel;
import net.pubnative.mediation.model.PubnativePlacementModel;
import net.pubnative.mediation.model.PubnativePriorityRulesModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PubnativeNetworkRequest implements PubnativeNetworkAdapterListener
{
    protected PubnativeNetworkRequestListener listener;
    protected Map<String, String>             parameters;
    protected PubnativeConfigModel            config;
    protected PubnativePlacementModel         placement;
    protected List<PubnativeAdModel>          ads;
    protected int                             currentRankIndex;

    public interface Parameters
    {
        String APP_TOKEN       = "app_token";
        String PLACEMENT_ID    = "placement_id";
        String AD_COUNT        = "ad_count";
        String AGE             = "age";
        String APP_VERSION     = "app_version";
        String SDK_VERSION     = "sdk_version";
        String BIRTH_DATE      = "birth_date";
        String CONNECTION_TYPE = "connection_type";
        String DEVICE          = "device";
        String EDUCATION       = "education";
        String GENDER          = "gender";
        String INTERESTS       = "interests";
        String LAST_SESSION    = "last_session";
        String SESSION_NUMBER  = "session_number";
        String IAP_SPENDER     = "iap_spender";
        String IAP_TOTAL       = "iap_total";
        String USER_AGENT      = "user_agent";
    }

    public PubnativeNetworkRequest()
    {
        // Do some initialization here
    }

    public void request(Context context, Map<String, String> parameters,
                        PubnativeNetworkRequestListener listener)
    {
        if (listener == null)
        {
            // Just drop the call
            System.out.println("PubnativeNetworkRequest.request - listener not specified, dropping the call");
            return;
        }

        this.currentRankIndex = 0;
        this.ads = new ArrayList<>();
        this.listener = listener;
        this.parameters = parameters;

        String app_token = this.parameters.get(Parameters.APP_TOKEN);
        String placement_id = this.parameters.get(Parameters.PLACEMENT_ID);

        if (context != null && !TextUtils.isEmpty(app_token) && !TextUtils.isEmpty(placement_id))
        {
            this.invokeStart();

            this.config = PubnativeConfigManager.getConfig(context, app_token);
            if (this.config != null)
            {

                for (PubnativePlacementModel currentPlacement : this.config.placements)
                {
                    if (currentPlacement.placement_id != null)
                    {
                        if (placement_id.equals(currentPlacement.placement_id))
                        {
                            this.placement = currentPlacement;
                            break;
                        }
                    }
                }
                if (this.placement != null)
                {
                    // Sort priority rules
                    Collections.sort(placement.priority_rules, new Comparator<PubnativePriorityRulesModel>()
                    {
                        @Override
                        public int compare(PubnativePriorityRulesModel priorityRule1,
                                           PubnativePriorityRulesModel priorityRule2)
                        {
                            return priorityRule1.rank - priorityRule2.rank;
                        }
                    });

                    // Start requesting ads
                    this.doAdapterRequest();
                }
                else
                {
                    this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.request - placement_id not found"));
                }
            }
            else
            {
                this.invokeFail(new NetworkErrorException("PubnativeNetworkRequest.request - invalid server getConfig"));
            }
        }
        else
        {
            this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.request - invalid request parameters"));
        }
    }

    protected void doAdapterRequest()
    {
        Integer ad_count = 1; // Default value if not included
        if (this.parameters.containsKey(Parameters.AD_COUNT))
        {
            ad_count = Integer.parseInt(this.parameters.get(Parameters.AD_COUNT));
        }
        Integer ads_left = ad_count - this.ads.size();

        // Check if there are still ads to request and networks to request from
        if (ads_left > 0 && this.currentRankIndex < this.placement.priority_rules.size())
        {
            PubnativePriorityRulesModel priorityRule = this.placement.priority_rules.get(this.currentRankIndex);

            Map networkConfig = null;
            String networkName = null;
            if (priorityRule.network.containsKey(PubnativePriorityRulesModel.NetworkContract.NAME))
            {
                networkName = (String) priorityRule.network.get(PubnativePriorityRulesModel.NetworkContract.NAME);
                if (networkName != null)
                {
                    networkConfig = (Map) this.config.networks.get(networkName);
                }
            }

            if (networkConfig != null)
            {
                PubnativeNetworkAdapter adapter = PubnativeNetworkAdapterFactory.createAdapter(networkConfig);
                if (adapter != null)
                {
                    this.currentRankIndex++;
                    adapter.doRequest(ads_left, this);
                }
                else
                {
                    this.invokeFail(new Exception("PubnativeNetworkRequest.requestForPlacementRank - Error: " + networkName + " adapter not found"));
                }
            }
            else
            {
                this.invokeFail(new Exception("PubnativeNetworkRequest.requestForPlacementRank - Error: " + networkName + " network getConfig not found"));
            }

        }
        else
        {
            this.invokeLoad(this.ads);
        }
    }

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
        if (ads != null)
        {
            this.ads.addAll(ads);
        }
        this.doAdapterRequest();
    }

    @Override
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception)
    {
        System.out.println("Pubnative - adapter request error: " + exception);
        // Waterfall to the next network
        this.doAdapterRequest();
    }
}
