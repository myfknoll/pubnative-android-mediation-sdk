package net.pubnative.mediation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.network.PubnativeLibraryCPICache;
import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.config.model.PubnativeConfigModel;

import java.util.HashMap;

public class Pubnative {

    private static final String TAG = Pubnative.class.getSimpleName();

    public static void init(Context context, String appToken) {
        init(context, appToken, null);
    }

    public static void init(Context context, String appToken, HashMap<String, String> requestParams) {

        Log.v(TAG, "init");
        if (context == null) {
            Log.v(TAG, "init - warning: invalid context");
        } else if (TextUtils.isEmpty(appToken)) {
            Log.v(TAG, "init - warning: invalid apptoken");
        } else {
            // cache config
            PubnativeConfigManager.getConfig(context, appToken, requestParams, new PubnativeConfigManager.Listener() {
                @Override
                public void onConfigLoaded(PubnativeConfigModel configModel) {
                    // do nothing
                }
            });

            // cache ads
            PubnativeLibraryCPICache.init(context, appToken);
        }
    }
}
