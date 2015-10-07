package net.pubnative.mediation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.pubnative.mediation.config.PubnativeConfigManager;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends Activity
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

    public void onResetConfigClicked(View view)
    {
        PubnativeConfigManager.clean(this);
        Toast.makeText(this, "Stored config reset!", Toast.LENGTH_SHORT).show();
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
            List<String> placements = this.adapter.getPlacements();
            PubnativeTestCredentials.setStoredPlacements(this, placements);
        }

        // reset the stored config when credentials are saved.
        this.onResetConfigClicked(null);
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
            List<String> placements = PubnativeTestCredentials.getStoredPlacements(this);
            if (placements == null)
            {
                placements = new ArrayList<>();
            }
            this.adapter = new PlacementListAdapter(this, placements);
            this.listView.setAdapter(adapter);
        }
    }
}
