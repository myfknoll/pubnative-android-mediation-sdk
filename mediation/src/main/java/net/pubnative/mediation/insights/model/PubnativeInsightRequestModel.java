package net.pubnative.mediation.insights.model;

/**
 * Created by davidmartin on 26/08/15.
 */
public class PubnativeInsightRequestModel {

    public String                    url;
    public PubnativeInsightDataModel dataModel;

    public PubnativeInsightRequestModel(String url, PubnativeInsightDataModel dataModel) {
        this.url = url;
        this.dataModel = dataModel;
    }
}
