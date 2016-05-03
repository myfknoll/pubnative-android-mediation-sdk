package net.pubnative.mediation.exceptions;

import org.json.JSONException;
import org.json.JSONObject;

public class PubnativeException extends Exception {

    public static final String TAG = PubnativeException.class.getSimpleName();
    //==============================================================================================
    // Private fields
    //==============================================================================================
    protected int mErrorCode;
    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final PubnativeException REQUEST_NO_INTERNET             = new PubnativeException(1000, "Internet connection is not available");
    public static final PubnativeException REQUEST_PARAMETERS_INVALID      = new PubnativeException(1001, "Invalid start parameters");
    public static final PubnativeException REQUEST_NO_FILL                 = new PubnativeException(1008, "No fill");
    public static final PubnativeException REQUEST_ADAPTER_CREATION        = new PubnativeException(1009, "Network adapter couldn't be created");
    //==============================================================================================
    // Adapter Exceptions
    //==============================================================================================
    public static final PubnativeException ADAPTER_UNKNOWN_ERROR           = new PubnativeException(2000, "Unknown error");
    public static final PubnativeException ADAPTER_MISSING_DATA            = new PubnativeException(2001, "Null context or adapter data provided");
    public static final PubnativeException ADAPTER_ILLEGAL_ARGUMENTS       = new PubnativeException(2002, "Invalid data provided");
    public static final PubnativeException ADAPTER_TIMEOUT                 = new PubnativeException(2003, "adapter timeout");
    //==============================================================================================
    // Interstitial Exceptions
    //==============================================================================================
    public static final PubnativeException INTERSTITIAL_PARAMETERS_INVALID = new PubnativeException(3000, "parameters configuring the interstitial are invalid");
    public static final PubnativeException INTERSTITIAL_LOADING            = new PubnativeException(3001, "interstitial is currently loading");
    public static final PubnativeException INTERSTITIAL_SHOWN              = new PubnativeException(3002, "interstitial is already shown");
    public static final PubnativeException INTERSTITIAL_NO_FILL            = new PubnativeException(3004, "interstitial cannot fill the ad");
    public static final PubnativeException INTERSTITIAL_ADAPTER_CREATION   = new PubnativeException(3005, "interstitial network adapter cannot be created");
    //==============================================================================================
    // Placement Exceptions
    //==============================================================================================
    public static final PubnativeException PLACEMENT_FREQUENCY_CAP         = new PubnativeException(4000, "(frequency_cap) too many ads");
    public static final PubnativeException PLACEMENT_PACING_CAP            = new PubnativeException(4001, "(pacing_cap) too many ads");
    public static final PubnativeException PLACEMENT_DISABLED              = new PubnativeException(4002, "Placement is disabled");
    public static final PubnativeException PLACEMENT_CONFIG_INVALID        = new PubnativeException(4003, "Null or invalid config");
    public static final PubnativeException PLACEMENT_NOT_FOUND             = new PubnativeException(4004, "Placement not found");
    public static final PubnativeException PLACEMENT_EMPTY                 = new PubnativeException(4005, "Retrieved config contains null element");
    public static final PubnativeException PLACEMENT_PARAMETERS_INVALID    = new PubnativeException(4006, "Parameters invalid");

    /**
     * Constructor
     *
     * @param errorCode Error code
     * @param message   Error message
     */
    public PubnativeException(int errorCode, String message) {

        super(message);
        mErrorCode = errorCode;
    }

    /**
     * This will return this exception error code number
     *
     * @return valid int representing the error code
     */
    public int getErrorCode() {

        return mErrorCode;
    }

    @Override
    public String getMessage() {

        return String.valueOf("PubnativeException (" + getErrorCode() + "): " + super.getMessage());
    }

    @Override
    public String toString() {

        String result;
        try {
            JSONObject json = new JSONObject();
            json.put("code", getErrorCode());
            json.put("message", super.getMessage());
            StackTraceElement[] stack = getStackTrace();
            if (stack != null && stack.length > 0) {
                StringBuilder stackTraceBuilder = new StringBuilder();
                for (StackTraceElement element : getStackTrace()) {
                    stackTraceBuilder.append(element.toString());
                    stackTraceBuilder.append('\n');
                }
                json.put("stackTrace", stackTraceBuilder.toString());
            }
            result = json.toString();
        } catch (JSONException e) {
            result = getMessage();
        }
        return result;
    }
}
