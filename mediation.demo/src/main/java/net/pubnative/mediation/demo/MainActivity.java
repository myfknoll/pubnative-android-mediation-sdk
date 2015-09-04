package net.pubnative.mediation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
    private final static String APP_TOKEN = "bed5dfea7feb694967a8755bfa7f67fdf1ceb9c291ddd7b8825983a103c8b266";

    private final static String PLACEMENT_WATERFALL_FACEBOOK_PUBNATIVE = "4";
    private final static String PLACEMENT_WATERFALL_PUBNATIVE_FACEBOOK = "5";
    private final static String PLACEMENT_DISABLED                     = "7";
    private final static String PLACEMENT_IMP_HOUR_CAP_1               = "8";
    private final static String PLACEMENT_PACING_CAP_MIN_1             = "9";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> placements = new ArrayList<>();
        placements.add(PLACEMENT_WATERFALL_FACEBOOK_PUBNATIVE);
        placements.add(PLACEMENT_WATERFALL_PUBNATIVE_FACEBOOK);
        placements.add(PLACEMENT_DISABLED);
        placements.add(PLACEMENT_IMP_HOUR_CAP_1);
        placements.add(PLACEMENT_PACING_CAP_MIN_1);

        AdListAdapter adListAdapter = new AdListAdapter(this, R.layout.ad_list_cell, placements, APP_TOKEN);

        ListView listView = (ListView) findViewById(R.id.ad_list);
        listView.setAdapter(adListAdapter);
    }
}
