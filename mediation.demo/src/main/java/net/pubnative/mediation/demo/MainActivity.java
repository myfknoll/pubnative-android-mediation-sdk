package net.pubnative.mediation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
    private final static String APP_TOKEN = "bed5dfea7feb694967a8755bfa7f67fdf1ceb9c291ddd7b8825983a103c8b266";

    private final static String PLACEMENT_FACEBOOK_ONLY     = "4";
    private final static String PLACEMENT_PUBNATIVE_ONLY    = "5";
    private final static String PLACEMENT_YAHOO_ONLY        = "10";
    private final static String PLACEMENT_LOOPME_ONLY       = "33";
    private final static String PLACEMENT_WATERFALL         = "6";
    private final static String PLACEMENT_IMP_DAY_CAP_10    = "11";
    private final static String PLACEMENT_IMP_HOUR_CAP_10   = "8";
    private final static String PLACEMENT_PACING_CAP_HOUR_1 = "9";
    private final static String PLACEMENT_PACING_CAP_MIN_1  = "12";
    private final static String PLACEMENT_DISABLED          = "7";

    private static AdListAdapter adListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (adListAdapter == null)
        {
            List<CellRequestModel> requests = new ArrayList<>();
            requests.add(new CellRequestModel(PLACEMENT_FACEBOOK_ONLY, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_PUBNATIVE_ONLY, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_YAHOO_ONLY, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_LOOPME_ONLY, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_WATERFALL, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_IMP_DAY_CAP_10, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_IMP_HOUR_CAP_10, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_PACING_CAP_HOUR_1, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_PACING_CAP_MIN_1, APP_TOKEN));
            requests.add(new CellRequestModel(PLACEMENT_DISABLED, APP_TOKEN));

            adListAdapter = new AdListAdapter(this, R.layout.ad_list_cell, requests);
        }

        ListView listView = (ListView) findViewById(R.id.ad_list);
        if (listView != null)
        {
            listView.setAdapter(adListAdapter);
        }
    }
}
