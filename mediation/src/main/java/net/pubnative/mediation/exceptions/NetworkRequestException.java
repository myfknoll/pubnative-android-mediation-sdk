package net.pubnative.mediation.exceptions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NetworkRequestException extends Exception {

    public static final String TAG = NetworkRequestException.class.getSimpleName();

    public static final int INVALID_PARAMETERS_ERROR_CODE          			= 1000;
    public static final int NULL_INVALID_CONFIG_ERROR_CODE         			= 1001;
    public static final int PLACEMENT_NOT_FOUND_ERROR_CODE         			= 1002;
    public static final int NO_ELEMENT_FOR_PLACEMENT_ERROR_CODE    			= 1003;
    public static final int DISABLED_PLACEMENT_ERROR_CODE          			= 1004;
    public static final int NO_NETWORK_FOR_PLACEMENT_ERROR_CODE    			= 1005;
    public static final int FREQUENCY_CAP_ERROR_CODE               			= 1006;
    public static final int PACING_CAP_ERROR_CODE                  			= 1007;
    public static final int NO_FILL_ERROR_CODE                     			= 1008;

    public static final NetworkRequestException INVALID_PARAMETERS          = new NetworkRequestException(EXCEPTION_TYPE.INVALID_PARAMETERS);
    public static final NetworkRequestException NULL_INVALID_CONFIG         = new NetworkRequestException(EXCEPTION_TYPE.NULL_INVALID_CONFIG);
    public static final NetworkRequestException PLACEMENT_NOT_FOUND         = new NetworkRequestException(EXCEPTION_TYPE.PLACEMENT_NOT_FOUND);
    public static final NetworkRequestException NO_ELEMENT_FOR_PLACEMENT    = new NetworkRequestException(EXCEPTION_TYPE.NO_ELEMENT_FOR_PLACEMENT);
    public static final NetworkRequestException DISABLED_PLACEMENT          = new NetworkRequestException(EXCEPTION_TYPE.DISABLED_PLACEMENT);
    public static final NetworkRequestException NO_NETWORK_FOR_PLACEMENT    = new NetworkRequestException(EXCEPTION_TYPE.NO_NETWORK_FOR_PLACEMENT);
    public static final NetworkRequestException FREQUENCY_CAP               = new NetworkRequestException(EXCEPTION_TYPE.FREQUENCY_CAP);
    public static final NetworkRequestException PACING_CAP                  = new NetworkRequestException(EXCEPTION_TYPE.PACING_CAP);
    public static final NetworkRequestException NO_FILL                     = new NetworkRequestException(EXCEPTION_TYPE.NO_FILL);

    public void addParameter(String key, String value) {
        if(mKeyValueMap == null) {
            mKeyValueMap = new HashMap<String, String>();
        }

        mKeyValueMap.put(key, value);
    }

    public void setParameters(HashMap<String, String> keyValueMap) {
        mKeyValueMap = keyValueMap;
    }

    public HashMap<String, String> getParameters() {
        return mKeyValueMap;
    }

    public NetworkRequestException(EXCEPTION_TYPE exception_type) {
        super(exception_type.getReadableName());
        mErrorCode = exception_type.getErrorCode();
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

    private enum EXCEPTION_TYPE {
        INVALID_PARAMETERS {
            @Override
            String getReadableName() {
                return "Invalid start parameters";
            }

            @Override
            int getErrorCode() {
                return INVALID_PARAMETERS_ERROR_CODE;
            }
        },
        NULL_INVALID_CONFIG {
            @Override
            String getReadableName() {
                return "Null or invalid config";
            }

            @Override
            int getErrorCode() {
                return NULL_INVALID_CONFIG_ERROR_CODE;
            }
        },
        PLACEMENT_NOT_FOUND {
            @Override
            String getReadableName() {
                return "Placement not found";
            }

            @Override
            int getErrorCode() {
                return PLACEMENT_NOT_FOUND_ERROR_CODE;
            }
        },
        NO_ELEMENT_FOR_PLACEMENT {
            @Override
            String getReadableName() {
                return "Retrieved config contains null element.";
            }

            @Override
            int getErrorCode() {
                return NO_ELEMENT_FOR_PLACEMENT_ERROR_CODE;
            }
        },
        DISABLED_PLACEMENT {
            @Override
            String getReadableName() {
                return "Placement is disabled";
            }

            @Override
            int getErrorCode() {
                return DISABLED_PLACEMENT_ERROR_CODE;
            }
        },
        NO_NETWORK_FOR_PLACEMENT {
            @Override
            String getReadableName() {
                return "No network is configured for placement";
            }

            @Override
            int getErrorCode() {
                return NO_NETWORK_FOR_PLACEMENT_ERROR_CODE;
            }
        },
        FREQUENCY_CAP {
            @Override
            String getReadableName() {
                return "(frequency_cap) too many ads";
            }

            @Override
            int getErrorCode() {
                return FREQUENCY_CAP_ERROR_CODE;
            }
        },
        PACING_CAP {
            @Override
            String getReadableName() {
                return "(pacing_cap) too many ads";
            }

            @Override
            int getErrorCode() {
                return PACING_CAP_ERROR_CODE;
            }
        },
        NO_FILL {
            @Override
            String getReadableName() {
                return "No fill available";
            }

            @Override
            int getErrorCode() {
                return NO_FILL_ERROR_CODE;
            }
        };

        abstract String getReadableName();
        abstract int    getErrorCode();
    }
}
