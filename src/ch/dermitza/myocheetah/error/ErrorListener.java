package ch.dermitza.myocheetah.error;

/**
 *
 * @author exe
 */
public interface ErrorListener {
    
    public static final int ERROR_MAX_ANGLE = 0;
    public static final int ERROR_MIN_ANGLE = 1;
    public static final int ERROR_INVALID_ANGLE = 2;
    public static final int ERROR_MAX_CURRENT = 3;
    public static final int ERROR_EPOS = 4;
    
    public abstract void errorReceived(int id, int type, String message);
    
}
