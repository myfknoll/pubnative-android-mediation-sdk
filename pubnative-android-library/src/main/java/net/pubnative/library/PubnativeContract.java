/**
 * Copyright 2014 PubNative GmbH
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pubnative.library;

public interface PubnativeContract
{
    interface Request
    {
        String APP_TOKEN                  = "app_token";
        String BUNDLE_ID                  = "bundle_id";
        String APPLE_IDFA                 = "apple_idfa";
        String APPLE_IDFA_SHA1            = "apple_idfa_sha1";
        String APPLE_IDFA_MD5             = "apple_idfa_md5";
        String ANDROID_ADVERTISER_ID      = "android_advertiser_id";
        String ANDROID_ADVERTISER_ID_SHA1 = "android_advertiser_id_sha1";
        String ANDROID_ADVERTISER_ID_MD5  = "android_advertiser_id_md5";
        String ICON_SIZE                  = "icon_size";
        String BANNER_SIZE                = "banner_size";
        String OS                         = "os";
        String DEVICE_MODEL               = "device_model";
        String OS_VERSION                 = "os_version";
        String NO_USER_ID                 = "no_user_id";
        String PARTNER                    = "partner";
        String LOCALE                     = "locale";
        String PORTRAIT_BANNER_SIZE       = "portrait_banner_size";
        String DEVICE_RESOLUTION          = "device_resolution";
        String DEVICE_TYPE                = "device_type";
        String AD_COUNT                   = "ad_count";
        String ZONE_ID                    = "zone_id";
        String LAT                        = "lat";
        String LONG                       = "long";
        String GENDER                     = "gender";
        String AGE                        = "age";
        String KEYWORDS                   = "keywords";
    }

    interface Response
    {
        String STATUS        = "status";
        String STATUS_ok     = "ok";
        String STATUS_error  = "error";
        String ERROR_MESSAGE = "error_message";
        String ADS           = "ads";

        interface NativeAd
        {
            String TYPE                = "type";
            String TITLE               = "title";
            String DESCRIPTION         = "description";
            String CTA_TEXT            = "cta_text";
            String ICON_URL            = "icon_url";
            String BANNER_URL          = "banner_url";
            String CLICK_URL           = "click_url";
            String STORE_RATING        = "store_rating";
            String BEACONS             = "beacons";
            String REVENUE_MODEL       = "revenue_model";
            String POINTS              = "points";
            String PORTRAIT_BANNER_URL = "portrait_banner_url";
            String APP_DETAILS         = "app_details";

            interface Beacon
            {
                String TYPE            = "type";
                String TYPE_IMPRESSION = "impression";
                String URL             = "url";
            }

            interface AppDetails
            {
                String REVIEW            = "review";
                String REVIEW_URL        = "review_url";
                String REVIEW_PROS       = "review_pros";
                String REVIEW_CONS       = "review_cons";
                String CATEGORY          = "category";
                String SUB_CATEGORY      = "sub_category";
                String STORE_RATING      = "store_rating";
                String STORE_CATEGORIES  = "store_categories";
                String PLATFORM          = "platform";
                String NAME              = "name";
                String PUBLISHER         = "publisher";
                String DEVELOPER         = "developer";
                String VERSION           = "version";
                String SIZE              = "size";
                String AGE_RATING        = "age_rating";
                String STORE_DESCRIPTION = "store_description";
                String STORE_URL         = "store_url";
                String STORE_ID          = "store_id";
                String URL_SCHEME        = "url_scheme";
                String RELEASE_DATE      = "release_date";
                String TOTAL_RATINGS     = "total_ratings";
                String INSTALLS          = "installs";
            }
        }

        interface VideoNativeAd extends NativeAd
        {
            String VAST = "vast";

            interface VideoAd
            {
                String AD                = "ad";
                String VIDEO_SKIP_TIME   = "video_skip_time";
                String SKIP_VIDEO_BUTTON = "skip_video_button";
                String MUTE              = "mute";
                String LEARN_MORE_BUTTON = "learn_more_button";
            }

            interface Vast
            {
                String AD = "Ad";

                interface Ad
                {
                    String ATTR_ID = "id";
                    String INLINE  = "InLine";

                    interface InLine
                    {
                        String AD_SYSTEM   = "AdSystem";
                        String AD_TITLE    = "AdTitle";
                        String DESCRIPTION = "Description";
                        String IMPRESSION  = "Impression";
                        String CREATIVES   = "Creatives";

                        interface Creatives
                        {
                            String CREATIVE      = "Creative";
                            String COMPANION_ADS = "CompanionAds";

                            interface Creative
                            {
                                String LINEAR        = "Linear";
                                String COMPANION_ADS = "CompanionAds";

                                interface Linear
                                {
                                    String DURATION        = "Duration";
                                    String TRACKING_EVENTS = "TrackingEvents";
                                    String MEDIA_FILES     = "MediaFiles";

                                    interface TrackingEvents
                                    {
                                        String TRACKING = "Tracking";

                                        interface Tracking
                                        {
                                            String ATTR_EVENT = "event";
                                        }
                                    }

                                    interface MediaFiles
                                    {
                                        String MEDIA_FILE = "MediaFile";

                                        interface MediaFile
                                        {
                                            String ATTR_DELIVERY = "delivery";
                                            String ATTR_SCALABLE = "scalable";
                                            String ATTR_TYPE     = "type";
                                            String ATTR_WIDTH    = "width";
                                            String ATTR_HEIGHT   = "height";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
