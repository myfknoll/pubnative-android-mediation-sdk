package net.pubnative.mediation.config;

import net.pubnative.mediation.config.model.PubnativeConfigModel;

/**
 * Created by rahul on 24/8/15.
 */
public interface PubnativeConfigRequestListener
{
    void onConfigLoaded(PubnativeConfigModel configModel);
}
