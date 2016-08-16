package net.pubnative.mediation.demo;

import android.app.Application;
import android.util.Log;

import net.pubnative.mediation.Pubnative;

public class MediationApplication extends Application {

    private static final String  TAG       = MediationApplication.class.getSimpleName();
    public  static final String  APP_TOKEN = "7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c";
    @Override
    public void onCreate() {

        Log.v(TAG, "onCreate");
        super.onCreate();
        Pubnative.init(this, APP_TOKEN);
    }
}
