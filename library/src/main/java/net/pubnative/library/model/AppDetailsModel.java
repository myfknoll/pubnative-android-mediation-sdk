package net.pubnative.library.model;

import net.pubnative.library.PubnativeContract.Response.NativeAd.AppDetails;

import org.droidparts.annotation.serialize.JSON;
import org.droidparts.model.Model;

public class AppDetailsModel extends Model implements AppDetails
{
    /**
     * 
     */
    private static final long serialVersionUID = 2L;
    //
    // FIELDS
    //
    @JSON(key = REVIEW, optional = true)
    public String             review;
    @JSON(key = REVIEW_URL, optional = true)
    public String             review_url;
    @JSON(key = REVIEW_PROS, optional = true)
    public String[]           review_pros;
    @JSON(key = REVIEW_CONS, optional = true)
    public String[]           review_cons;
    @JSON(key = CATEGORY, optional = true)
    public String             category;
    @JSON(key = SUB_CATEGORY, optional = true)
    public String             sub_category;
    @JSON(key = STORE_RATING, optional = true)
    public float              store_rating;
    @JSON(key = STORE_CATEGORIES, optional = true)
    public String[]           store_categories;
    @JSON(key = PLATFORM, optional = true)
    public String             platform;
    @JSON(key = NAME, optional = true)
    public String             name;
    @JSON(key = PUBLISHER, optional = true)
    public String             publisher;
    @JSON(key = DEVELOPER, optional = true)
    public String             developer;
    @JSON(key = VERSION, optional = true)
    public String             version;
    @JSON(key = SIZE, optional = true)
    public String             size;
    @JSON(key = AGE_RATING, optional = true)
    public String             age_rating;
    @JSON(key = STORE_DESCRIPTION, optional = true)
    public String             store_description;
    @JSON(key = STORE_URL, optional = true)
    public String             store_url;
    @JSON(key = STORE_ID, optional = true)
    public String             store_id;
    @JSON(key = URL_SCHEME, optional = true)
    public String             url_scheme;
    @JSON(key = RELEASE_DATE, optional = true)
    public String             release_date;
    @JSON(key = TOTAL_RATINGS, optional = true)
    public float              total_ratings;
    @JSON(key = INSTALLS, optional = true)
    public String             installs;
}
