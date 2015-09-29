package net.pubnative.mediation.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
    private final static String APP_TOKEN = "7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c";

    private static AdListAdapter adListAdapter = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.listView = (ListView) findViewById(R.id.ad_list);

        this.populateDefaultData();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        this.displayPlacementsList();
    }

    private void populateDefaultData()
    {
        String appToken = PubnativeTestCredentials.getStoredAppToken(this);
        if (TextUtils.isEmpty(appToken))
        {
            PubnativeTestCredentials.setStoredAppToken(this, APP_TOKEN);
        }

        ArrayList<String> placements = PubnativeTestCredentials.getStoredPlacements(this);
        if (placements == null)
        {
            placements = new ArrayList<String>();
            placements.add("facebook_only");
            placements.add("pubnative_only");
            placements.add("yahoo_only");
            placements.add("waterfall");
            placements.add("imp_day_cap_10");
            placements.add("imp_hour_cap_10");
            placements.add("pacing_cap_hour_1");
            placements.add("pacing_cap_min_1");
            placements.add("disabled");
            PubnativeTestCredentials.setStoredPlacements(this, placements);
        }
    }

    private void displayPlacementsList()
    {
        if (adListAdapter == null)
        {
            List<CellRequestModel> requests = new ArrayList<>();
            String appToken = PubnativeTestCredentials.getStoredAppToken(this);
            if (!TextUtils.isEmpty(appToken))
            {
                ArrayList<String> placements = PubnativeTestCredentials.getStoredPlacements(this);
                if (placements != null)
                {
                    for (String placementId : placements)
                    {
                        requests.add(new CellRequestModel(appToken, placementId));
                    }
                }
            }
            adListAdapter = new AdListAdapter(this, R.layout.ad_list_cell, requests);
        }

        if (this.listView != null)
        {
            this.listView.setAdapter(adListAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            // the following line clears the adapter to load next time propely
            adListAdapter = null;

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
