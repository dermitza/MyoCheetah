package ch.dermitza.myocheetah;

/**
 *
 * @author exe
 */
public interface StatusListener {
    
    public static final String[] STATUS_NAMES = {
        "DISCONNECTED",
        "CONNECTING",
        "DISCONNECTING",
        "CONNECTED",
        "DISABLED",
        "ENABLING",
        "DISABLING",
        "ENABLED",
        "LOGGING DISABLED",
        "LOGGING ENABLING",
        "LOGGING DISABLING",
        "LOGGING ENABLED",
        "CONTROLLING DISABLED",
        "CONTROLLING ENABLING",
        "CONTROLLING DISABLING",
        "CONTROLLING ENABLED",
        "PROFILE POSITION PARAMS SET SUCCESSFULLY",
        "PROFILE POSITION PARAMS SET ERROR",
        "PROFILE POSITION PARAMS READY"
    };
    
    public static final String[] REQUEST_NAMES = {
        "NO REQUEST",
        "CONNECT",
        "DISCONNECT",
        "ENABLE",
        "DISABLE",
        "START LOGGING",
        "STOP LOGGING",
        "START CONTROLLING",
        "STOP CONTROLLING",
        "START PDO",
        "STOP PDO"
    };
    
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_DISCONNECTING = 2;
    public static final int STATUS_CONNECTED = 3;
    
    public static final int STATUS_DISABLED = 4;
    public static final int STATUS_ENABLING = 5;
    public static final int STATUS_DISABLING = 6;
    public static final int STATUS_ENABLED = 7;
    
    public static final int STATUS_LOGGING_DISABLED = 8;
    public static final int STATUS_LOGGING_ENABLING = 9;
    public static final int STATUS_LOGGING_DISABLING = 10;
    public static final int STATUS_LOGGING_ENABLED = 11;
    
    public static final int STATUS_CONTROLLING_DISABLED = 12;
    public static final int STATUS_CONTROLLING_ENABLING = 13;
    public static final int STATUS_CONTROLLING_DISABLING = 14;
    public static final int STATUS_CONTROLLING_ENABLED = 15;
    
    public static final int STATUS_PROFILE_POSITION_PARAMS_SET_OK = 16;
    public static final int STATUS_PROFILE_POSITION_PARAMS_SET_ERR = 17;
    public static final int STATUS_PROFILE_POSITION_PARAMS_READY = 18;
    
    public static final int REQUEST_EMPTY = 0;
    public static final int REQUEST_CONNECT = 1;
    public static final int REQUEST_DISCONNECT = 2;
    public static final int REQUEST_ENABLE = 3;
    public static final int REQUEST_DISABLE = 4;
    public static final int REQUEST_START_LOGGING = 5;
    public static final int REQUEST_STOP_LOGGING = 6;
    public static final int REQUEST_START_CONTROLLING = 7;
    public static final int REQUEST_STOP_CONTROLLING = 8;
    public static final int REQUEST_START_PDO = 9;
    public static final int REQUEST_STOP_PDO = 10;
    public static final int REQUEST_SET_PROFILE_POSITION_PARAMS = 11;
    public static final int REQUEST_READ_PROFILE_POSITION_PARAMS = 12;
    
    public static final int STATE_IDLE = 0;
    public static final int STATE_LOGGING = 1;
    public static final int STATE_CONTROLLING = 2;
    
    public void statusChanged(int status);
    
}
