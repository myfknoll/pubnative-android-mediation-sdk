package net.pubnative.mediation.config;

import net.pubnative.mediation.config.model.PubnativeConfigModel;

/**
 * Created by rahul on 24/8/15.
 */
public interface PubnativeConfigRequestListener
{
    /**
     * Invoked when config manager returns a config.
     * @param configModel PubnativeConfigModel object when cached/download config is available, else null.
     */
    void onConfigLoaded(PubnativeConfigModel configModel);
}
