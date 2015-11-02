package net.pubnative.mediation.config.model;

import android.content.Context;

import net.pubnative.mediation.config.PubnativeConfigRequestListener;

/**
 * Created by davidmartin on 03/09/15.
 */
public class PubnativeConfigRequestModel {

    public Context                        context;
    public String                         appToken;
    public PubnativeConfigRequestListener listener;
}
