package net.pubnative.mediation.exceptions;

public class PubnativeException extends Exception {

    public static final String TAG = PubnativeException.class.getSimpleName();

    //==============================================================================================
    // Request Exceptions
    //==============================================================================================
    public static final int ERROR_CODE_NO_NETWORK                           = 1000;
    public static final int ERROR_CODE_INVALID_PARAMETERS                   = 1000;
    public static final int ERROR_CODE_NULL_INVALID_CONFIG                  = 1001;
    public static final int ERROR_CODE_PLACEMENT_NOT_FOUND                  = 1002;
    public static final int ERROR_CODE_NO_ELEMENT_FOR_PLACEMENT             = 1003;
    public static final int ERROR_CODE_DISABLED_PLACEMENT                   = 1004;
    public static final int ERROR_CODE_NO_NETWORK_FOR_PLACEMENT             = 1005;
    public static final int ERROR_CODE_FREQUENCY_CAP                        = 1006;
    public static final int ERROR_CODE_PACING_CAP                           = 1007;
    public static final int ERROR_CODE_NO_FILL                              = 1008;

    public static final PubnativeException REQUEST_NO_NETWORK               = new PubnativeException(ERROR_CODE_NO_NETWORK, "Internet connection is not available");
    public static final PubnativeException REQUEST_INVALID_PARAMETERS       = new PubnativeException(ERROR_CODE_INVALID_PARAMETERS, "Invalid start parameters");
    public static final PubnativeException REQUEST_NULL_INVALID_CONFIG      = new PubnativeException(ERROR_CODE_NULL_INVALID_CONFIG, "Null or invalid config");
    public static final PubnativeException REQUEST_PLACEMENT_NOT_FOUND      = new PubnativeException(ERROR_CODE_PLACEMENT_NOT_FOUND, "Placement not found");
    public static final PubnativeException REQUEST_NO_ELEMENT_FOR_PLACEMENT = new PubnativeException(ERROR_CODE_NO_ELEMENT_FOR_PLACEMENT, "Retrieved config contains null element");
    public static final PubnativeException REQUEST_DISABLED_PLACEMENT       = new PubnativeException(ERROR_CODE_DISABLED_PLACEMENT, "Placement is disabled");
    public static final PubnativeException REQUEST_NO_NETWORK_FOR_PLACEMENT = new PubnativeException(ERROR_CODE_NO_NETWORK_FOR_PLACEMENT, "No network is configured for placement");
    public static final PubnativeException REQUEST_FREQUENCY_CAP            = new PubnativeException(ERROR_CODE_FREQUENCY_CAP, "(frequency_cap) too many ads");
    public static final PubnativeException REQUEST_PACING_CAP               = new PubnativeException(ERROR_CODE_PACING_CAP, "(pacing_cap) too many ads");
    public static final PubnativeException REQUEST_NO_FILL                  = new PubnativeException(ERROR_CODE_NO_FILL, "No fill available");

    //==============================================================================================
    // Adapter Exceptions
    //==============================================================================================
    public static final int ERROR_CODE_ADAPTER_UNKNOWN_ERROR                = 2000;
    public static final int ERROR_CODE_ADAPTER_MISSING_DATA                 = 2001;
    public static final int ERROR_CODE_ADAPTER_ILLEGAL_ARGUMENTS            = 2002;

    public static final PubnativeException ADAPTER_UNKNOWN_ERROR            = new PubnativeException(ERROR_CODE_ADAPTER_UNKNOWN_ERROR, "Unknown error");
    public static final PubnativeException ADAPTER_MISSING_DATA             = new PubnativeException(ERROR_CODE_ADAPTER_MISSING_DATA, "Null context or adapter data provided");
    public static final PubnativeException ADAPTER_ILLEGAL_ARGUMENTS        = new PubnativeException(ERROR_CODE_ADAPTER_ILLEGAL_ARGUMENTS, "Invalid data provided");

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
        return "PubnativeException{" +
                "errorCode=" + mErrorCode +
                "message=" + getMessage() +
                '}';
    }

    private int mErrorCode;
}
