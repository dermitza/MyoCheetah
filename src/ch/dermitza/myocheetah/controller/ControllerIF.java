package ch.dermitza.myocheetah.controller;

/**
 *
 * @author exe
 */
public interface ControllerIF {
    
    
    public static final String[] CONTROLLER_NAMES = 
    {
        "CalibrationController",
        "TrajectoryOne",
        "TrajectoryFour",
        "EmptyController",
        "SkataController"
    };
    
    public static final int ID_INVALID_CONTROLLER = -1;
    public static final int ID_CALIBRATION_CONTROLLER = 0;
    public static final int ID_TRAJECTORY_ONE = 1;
    public static final int ID_TRAJECTORY_FOUR = 2;
    public static final int ID_EMPTY_CONTROLLER = 3;
    public static final int ID_SKATA_CONTROLLER = 4;
    
}
