package net.pubnative.mediation.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.demo.Settings;

import java.util.List;

public class StandardAdUnitActivity extends Activity {

    private static final String TAG                = StandardAdUnitActivity.class.getSimpleName();
    private Spinner             mPlacementSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_ad_unit);
        mPlacementSpinner = (Spinner) findViewById(R.id.spinner_standard_unit_placement);

        loadSpinnerData();
    }

    private void loadSpinnerData() {

        Log.v(TAG, "loadSpinnerData");
        List<String> placements = Settings.getPlacements(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, placements);
        mPlacementSpinner.setAdapter(adapter);
    }
}
