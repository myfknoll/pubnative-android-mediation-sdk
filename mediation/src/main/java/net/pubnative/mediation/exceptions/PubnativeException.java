package net.pubnative.mediation.exceptions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PubnativeException extends Exception {

    public static final String TAG = PubnativeException.class.getSimpleName();

    //==============================================================================================
    // Generic Exceptions
    //==============================================================================================
    public static final int ERROR_CODE_NO_NETWORK                     		= 1000;

    public static final PubnativeException NO_NETWORK                 		= new PubnativeException(ERROR_CODE_NO_NETWORK, "Internet connection is not available");

    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final int ERROR_CODE_INVALID_PARAMETERS             		= 2000;
    public static final int ERROR_CODE_NULL_INVALID_CONFIG            		= 2001;
    public static final int ERROR_CODE_PLACEMENT_NOT_FOUND            		= 2002;
    public static final int ERROR_CODE_NO_ELEMENT_FOR_PLACEMENT       		= 2003;
    public static final int ERROR_CODE_DISABLED_PLACEMENT             		= 2004;
    public static final int ERROR_CODE_NO_NETWORK_FOR_PLACEMENT       		= 2005;
    public static final int ERROR_CODE_FREQUENCY_CAP                  		= 2006;
    public static final int ERROR_CODE_PACING_CAP                     		= 2007;
    public static final int ERROR_CODE_NO_FILL                        		= 2008;

    public static final PubnativeException INVALID_PARAMETERS         		= new PubnativeException(ERROR_CODE_INVALID_PARAMETERS, "Invalid start parameters");
    public static final PubnativeException NULL_INVALID_CONFIG        		= new PubnativeException(ERROR_CODE_NULL_INVALID_CONFIG, "Null or invalid config");
    public static final PubnativeException PLACEMENT_NOT_FOUND        		= new PubnativeException(ERROR_CODE_PLACEMENT_NOT_FOUND, "Placement not found");
    public static final PubnativeException NO_ELEMENT_FOR_PLACEMENT   		= new PubnativeException(ERROR_CODE_NO_ELEMENT_FOR_PLACEMENT, "Retrieved config contains null element");
    public static final PubnativeException DISABLED_PLACEMENT         		= new PubnativeException(ERROR_CODE_DISABLED_PLACEMENT, "Placement is disabled");
    public static final PubnativeException NO_NETWORK_FOR_PLACEMENT   		= new PubnativeException(ERROR_CODE_NO_NETWORK_FOR_PLACEMENT, "No network is configured for placement");
    public static final PubnativeException FREQUENCY_CAP              		= new PubnativeException(ERROR_CODE_FREQUENCY_CAP, "(frequency_cap) too many ads");
    public static final PubnativeException PACING_CAP                 		= new PubnativeException(ERROR_CODE_PACING_CAP, "(pacing_cap) too many ads");
    public static final PubnativeException NO_FILL                    		= new PubnativeException(ERROR_CODE_NO_FILL, "No fill available");

    //==============================================================================================
    // Adapter Exceptions
    //==============================================================================================
    public static final int ERROR_CODE_ADAPTER                        		= 3000;
    public static final int ERROR_CODE_FACEBOOK_ADAPTER_UNKNOWN             = 3100;
    public static final int ERROR_CODE_FACEBOOK_ADAPTER_PLACEMENT           = 3101;
    public static final int ERROR_CODE_FACEBOOK_ADAPTER_NO_CONTEXT   		= 3102;
    public static final int ERROR_CODE_YAHOO_ADAPTER_UNKNOWN                = 3200;
    public static final int ERROR_CODE_YAHOO_ADAPTER_AD_SPACE_NAME          = 3201;
    public static final int ERROR_CODE_YAHOO_ADAPTER_API_KEY                = 3202;
    public static final int ERROR_CODE_YAHOO_ADAPTER_NO_CONTEXT   		    = 3203;
    public static final int ERROR_CODE_PUBNATIVE_ADAPTER_UNKNOWN            = 3300;
    public static final int ERROR_CODE_PUBNATIVE_ADAPTER_NO_CONTEXT   		= 3301;
    public static final int ERROR_CODE_PUBNATIVE_ADAPTER_INVALID_REQUEST    = 3302;

    public static final PubnativeException ADAPTER_NULL_CONTEXT       		= new PubnativeException(ERROR_CODE_ADAPTER, "Null context provided");
    public static final PubnativeException FACEBOOK_ADAPTER_UNKNOWN         = new PubnativeException(ERROR_CODE_FACEBOOK_ADAPTER_UNKNOWN, "Facebook adapter unknown error");
    public static final PubnativeException FACEBOOK_INVALID_PLACEMENT  		= new PubnativeException(ERROR_CODE_FACEBOOK_ADAPTER_PLACEMENT, "Invalid placement_id provided");
    public static final PubnativeException FACEBOOK_NO_CONTEXT_OR_ADAPTER   = new PubnativeException(ERROR_CODE_FACEBOOK_ADAPTER_NO_CONTEXT, "No context or adapter data provided");
    public static final PubnativeException YAHOO_ADAPTER_UNKNOWN            = new PubnativeException(ERROR_CODE_YAHOO_ADAPTER_UNKNOWN, "Yahoo adapter unknown error");
    public static final PubnativeException YAHOO_INVALID_AD_SPACE_NAME 		= new PubnativeException(ERROR_CODE_YAHOO_ADAPTER_AD_SPACE_NAME, "Invalid ad_space_name provided");
    public static final PubnativeException YAHOO_INVALID_API_KEY     		= new PubnativeException(ERROR_CODE_YAHOO_ADAPTER_API_KEY, "Invalid api_key provided");
    public static final PubnativeException YAHOO_NO_CONTEXT_OR_ADAPTER      = new PubnativeException(ERROR_CODE_YAHOO_ADAPTER_NO_CONTEXT, "No context or adapter data provided");
    public static final PubnativeException PUBNATIVE_ADAPTER          		= new PubnativeException(ERROR_CODE_PUBNATIVE_ADAPTER_UNKNOWN, "Pubnative adapter unknown error");
    public static final PubnativeException PUBNATIVE_NO_CONTEXT_OR_ADAPTER  = new PubnativeException(ERROR_CODE_PUBNATIVE_ADAPTER_NO_CONTEXT, "No context or adapter data provided");
    public static final PubnativeException PUBNATIVE_INVALID_REQUEST 		= new PubnativeException(ERROR_CODE_PUBNATIVE_ADAPTER_INVALID_REQUEST, "Invalid request object on response");

    /**
     * Adds a key value pair
     * @param key name of the key
     * @param value value of key
     */
    public void addParameter(String key, String value) {
        if(mKeyValueMap == null) {
            mKeyValueMap = new HashMap<String, String>();
        }

        mKeyValueMap.put(key, value);
    }

    /**
     * Set the complete map of key value pairs at once
     * @param keyValueMap map of key value pairs
     */
    public void setParameters(HashMap<String, String> keyValueMap) {
        mKeyValueMap = keyValueMap;
    }

    /**
     * Returns map of key value pairs
     * @return <code>HashMap<String, String></code> key value pairs
     */
    public HashMap<String, String> getParameters() {
        return mKeyValueMap;
    }

    /**
     * Constructor
     * @param errorCode Error code
     * @param message Error message
     */
    public PubnativeException(int errorCode, String message) {
        super(message);
        mErrorCode = errorCode;
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();

        try {
            result.put("message", getMessage());
            result.put("errorCode", mErrorCode);

            if(getParameters() != null) {
                JSONObject parameters = new JSONObject();
                for (String name: getParameters().keySet()){
                    parameters.put(name, mKeyValueMap.get(name));
                }
                result.put("parameters", parameters);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return result.toString();
    }

    private HashMap<String, String> mKeyValueMap;
    private int mErrorCode;
}
