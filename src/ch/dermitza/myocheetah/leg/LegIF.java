package ch.dermitza.myocheetah.leg;

/**
 *
 * @author exe
 */
public interface LegIF {
    
    public static final int LEG_FL = 0;
    public static final int LEG_FR = 1;
    public static final int LEG_RL = 2;
    public static final int LEG_RR = 3;
    public static final int[] LEG_IDS = {LEG_FL, LEG_FR, LEG_RL, LEG_RR};
    /* Leg names */
    public static final String[] LEG_NAMES = {
        "Front left",
        "Front right",
        "Rear left",
        "Rear right"};
    // Corresponding to LEG IDs as INDICES
    public static final short[] KNEE_IDS = {6, 2, 5, 0};
    public static final short[] HIP_IDS = {4, 3, 7, 1};
    public static final short[] FLEXOR_IDS = {5, 7, 4, 1};
    public static final short[] EXTENSOR_IDS = {6, 8, 3, 2};
    
    public static final double DEG_QC = 108544./360;
    public static final double QC_DEG = 360./108544;
    public static final double QC_SECRPM = 1/53.;
    public static final double RPM_QCSEC = 53.;//6.360/108544
    public static final double HALL_DEG = 360/4096.; // UNUSED
    
    // GEOMETRY 
    
}
