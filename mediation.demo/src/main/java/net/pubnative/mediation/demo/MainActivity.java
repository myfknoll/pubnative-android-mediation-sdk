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

package net.pubnative.mediation.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private final static String APP_TOKEN = "7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c";

    private Button                 settingsButton  = null;
    private AdListAdapter          requestsAdapter = null;
    private List<CellRequestModel> requests        = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.settingsButton = (Button) this.findViewById(R.id.button_settings);
        this.settingsButton.setOnClickListener(this);

        List<String> placements = new ArrayList();
        placements.add("facebook_only");
        placements.add("pubnative_only");
        placements.add("yahoo_only");
        placements.add("waterfall");
        placements.add("imp_day_cap_10");
        placements.add("imp_hour_cap_10");
        placements.add("pacing_cap_hour_1");
        placements.add("pacing_cap_min_1");
        placements.add("disabled");
        PubnativeTestCredentials.setStoredAppToken(this, APP_TOKEN);
        PubnativeTestCredentials.setStoredPlacements(this, placements);

        this.requests = new ArrayList();
        for (String placementID : placements) {
            this.requests.add(new CellRequestModel(APP_TOKEN, placementID));
        }

        this.requestsAdapter = new AdListAdapter(this, R.layout.ad_list_cell, this.requests);
        ListView listView = (ListView) this.findViewById(R.id.ad_list);
        listView.setAdapter(this.requestsAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.displayPlacementsList();
    }

    private void displayPlacementsList() {
        // Add default data if needed
        String       appToken   = PubnativeTestCredentials.getStoredAppToken(this);
        List<String> placements = PubnativeTestCredentials.getStoredPlacements(this);

        if (!TextUtils.isEmpty(appToken) && placements != null) {
            this.requestsAdapter.clear();
            List<CellRequestModel> newRequests = new ArrayList();
            for (CellRequestModel request : this.requests) {
                if (placements.contains(request.placementID)) {
                    newRequests.add(request);
                    placements.remove(request.placementID);
                }
            }
            for (String placementID : placements) {
                newRequests.add(new CellRequestModel(appToken, placementID));
            }
            this.requests = newRequests;
            for (CellRequestModel request : this.requests) {
                this.requestsAdapter.add(request);
            }

            this.requestsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (this.settingsButton.equals(v)) {
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
        }
    }
}
