package net.pubnative.mediation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.config.PubnativeConfigService;

import java.util.HashMap;

public abstract class Pubnative {
    private static final String TAG           = Pubnative.class.getSimpleName();
    public  static final String APP_TOKEN_KEY = "app_token";
    public  static final String EXTRAS        = "extras";

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
            Intent intent = new Intent(context, PubnativeConfigService.class);
            Bundle bundle = new Bundle();
            bundle.putString(APP_TOKEN_KEY, appToken);
            // put extras, not necessary
            if (requestParams != null && !requestParams.isEmpty()) {
                bundle.putSerializable(EXTRAS, requestParams);
            }
            intent.putExtras(bundle);
            context.startService(intent);
        }
    }
}
