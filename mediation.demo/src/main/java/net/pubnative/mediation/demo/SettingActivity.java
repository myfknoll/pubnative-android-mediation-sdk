package net.pubnative.mediation.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class SettingActivity extends ActionBarActivity
{
    private PlacementListAdapter adapter;
    private ListView             listView;
    private EditText             appKeyEdit;
    private EditText             placementIdEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        listView = (ListView) findViewById(R.id.placement_id_list);
        appKeyEdit = (EditText) findViewById(R.id.app_key_edit);
        placementIdEdit = (EditText) findViewById(R.id.placement_id_edit);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.displayStoredValues();
    }

    @Override
    public void onBackPressed()
    {
        this.saveCredentials();

        super.onBackPressed();
    }

    public void onAddPlacementClicked(View view)
    {
        if (placementIdEdit != null && placementIdEdit.getText() != null)
        {
            String placementId = placementIdEdit.getText().toString();
            if (this.adapter != null && !TextUtils.isEmpty(placementId))
            {
                this.adapter.addPlacement(placementId);
                this.placementIdEdit.setText("");
            }
        }
    }

    private void saveCredentials()
    {
        if (this.appKeyEdit != null && this.appKeyEdit.getText() != null)
        {
            String appToken = this.appKeyEdit.getText().toString();
            PubnativeTestCredentials.setStoredAppToken(this, appToken);
        }

        if (this.adapter != null && this.adapter.getPlacements() != null)
        {
            ArrayList<String> placements = this.adapter.getPlacements();
            PubnativeTestCredentials.setStoredPlacements(this, placements);
        }
    }

    private void displayStoredValues()
    {
        if (this.appKeyEdit != null)
        {
            String appToken = PubnativeTestCredentials.getStoredAppToken(this);
            if (!TextUtils.isEmpty(appToken))
            {
                this.appKeyEdit.setText(appToken);
            }
        }

        if (this.listView != null)
        {
            ArrayList<String> placements = PubnativeTestCredentials.getStoredPlacements(this);
            if (placements == null)
            {
                placements = new ArrayList<String>();
            }
            this.adapter = new PlacementListAdapter(this, placements);
            this.listView.setAdapter(adapter);
        }
    }
}
