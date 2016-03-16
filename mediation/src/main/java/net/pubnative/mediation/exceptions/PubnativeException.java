package net.pubnative.mediation.exceptions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PubnativeException extends Exception {

    public static final String TAG = PubnativeException.class.getSimpleName();

    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final int ERROR_CODE_INVALID_PARAMETERS             		= 1000;
    public static final int ERROR_CODE_NULL_INVALID_CONFIG            		= 1001;
    public static final int ERROR_CODE_PLACEMENT_NOT_FOUND            		= 1002;
    public static final int ERROR_CODE_NO_ELEMENT_FOR_PLACEMENT       		= 1003;
    public static final int ERROR_CODE_DISABLED_PLACEMENT             		= 1004;
    public static final int ERROR_CODE_NO_NETWORK_FOR_PLACEMENT       		= 1005;
    public static final int ERROR_CODE_FREQUENCY_CAP                  		= 1006;
    public static final int ERROR_CODE_PACING_CAP                     		= 1007;
    public static final int ERROR_CODE_NO_FILL                        		= 1008;

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
    public static final int ERROR_CODE_ADAPTER                        		= 2000;
    public static final int ERROR_CODE_FACEBOOK_ADAPTER               		= 2001;
    public static final int ERROR_CODE_YAHOO_ADAPTER                  		= 2002;
    public static final int ERROR_CODE_PUBNATIVE_ADAPTER              		= 2003;
    public static final int ERROR_CODE_PUBNATIVE_ADAPTER_NO_CONTEXT   		= 2301;
    public static final int ERROR_CODE_PUBNATIVE_ADAPTER_INVALID_REQUEST    = 2302;

    public static final PubnativeException ADAPTER_NULL_CONTEXT       		= new PubnativeException(ERROR_CODE_ADAPTER, "Null context provided");
    public static final PubnativeException FACEBOOK_ADAPTER           		= new PubnativeException(ERROR_CODE_FACEBOOK_ADAPTER, "Facebook adapter error");
    public static final PubnativeException YAHOO_ADAPTER              		= new PubnativeException(ERROR_CODE_YAHOO_ADAPTER, "Yahoo adapter error");
    public static final PubnativeException PUBNATIVE_ADAPTER          		= new PubnativeException(ERROR_CODE_PUBNATIVE_ADAPTER, "Pubnative adapter error");
    public static final PubnativeException PUBNATIVE_NO_CONTEXT_OR_ADAPTER  = new PubnativeException(ERROR_CODE_PUBNATIVE_ADAPTER_NO_CONTEXT, "No context or adapter data provided");
    public static final PubnativeException PUBNATIVE_INVALID_REQUEST 		= new PubnativeException(ERROR_CODE_PUBNATIVE_ADAPTER_INVALID_REQUEST, "Invalid request object on response");

    //==============================================================================================
    // Generic Exceptions
    //==============================================================================================
    public static final int ERROR_CODE_NO_NETWORK                     		= 3000;

    public static final PubnativeException NO_NETWORK                 		= new PubnativeException(ERROR_CODE_NO_NETWORK, "Internet connection is not available");

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
