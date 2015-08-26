package net.pubnative.mediation.config;

import net.pubnative.mediation.model.PubnativeConfigModel;

/**
 * Created by rahul on 24/8/15.
 */
public interface PubnativeConfigManagerListener
{
    void onConfigLoaded(PubnativeConfigModel configModel);
}
