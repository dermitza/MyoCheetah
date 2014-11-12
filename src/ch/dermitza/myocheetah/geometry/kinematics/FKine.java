package ch.dermitza.myocheetah.geometry.kinematics;

import ch.dermitza.myocheetah.CheetahIF;
import ch.dermitza.myocheetah.leg.LegIF;
import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 *
 * @author exe
 */
public class FKine {
    
    public static final double[] X1 = {160, 159, 307, 306};
    public static final double[] X2 = {309, 307.5, 158.5, 159};
    public static final double[] X3 = {39, 40, 37, 36};
    public static final double[] SP12 = {15, 15, 20, 20};
    public static final double JXD = 22.5;
    public static final double JYD = 28.5 + 8;
    public static final double FYD = 63.65;// Foot y distance
    public static final double[] L1 = calcL1();
    public static final double[] L2 = calcL2();
    public static final double ANGLE_INVALID = Double.NaN;
    public static final double JOINT_ANGLE_OFFSET = 20; // degrees, from mechanics (not breaking the pulleys)
    public static final double JAMAX = 135; // Joint angle max, from mechanics (not breaking pulley)
    public static final double[] HAO = calcHAO(); // Hip angle offset (in kinematic model)
    public static final double[] KAO = calcKAO(); // Knee angle offset (in kinematic model)
    // Uncalibrated angle sensor IDs: 2, 3, 4
    public static final int[] ANGLES_MAX_RAW2 = {1456,940,4096,4096,4096,324,1800,25};
    public static final int[] ANGLES_FIX_RAW2 = {0,0,3091,3360,3815,0,0,0};
    public static final int[] ANGLES_MIN_RAW2 = {2980,2380,526,700,1150,1806,3195,1825};
    public static final double[] ANGLES_MAX = {JAMAX, JAMAX, JAMAX, JAMAX, JAMAX, JAMAX, JAMAX, JAMAX};
    public static final double[] ANGLES_MIN = calcAnglesMin();
    public static final double[] ANGLES_RANGE = calcAngleRange();
    public static final double[] ANGLES_RANGE_RAW = calcRawAngleRange();
    
    public static final double yFlexorPulley = (34.5-6-22.5); //mm
    public static final double[] XFP = calcXFP(); // xFlexorPulley mm
    public static final double[] FPD = calcFPD(); // flexor pulley distance, mm
    
    public static double[] calcXFP(){
        double[] xfp = new double[CheetahIF.LEGS_NUM];
        String print = "";
        for(int i=0; i < CheetahIF.LEGS_NUM; i++){
            xfp[i] = -(28.5+22+FKine.SP12[i]+FKine.X3[i]-6);
            print += "FLEXOR_PULLEY_X[" + i + "] = " + xfp[i] + " ";
        }
        System.out.println(print);
        return xfp;
    }
    
    public static double[] calcFPD(){
        double[] fpd = new double[CheetahIF.LEGS_NUM];
        String print = "";
        for(int i=0; i < CheetahIF.LEGS_NUM; i++){
            fpd[i] = Math.sqrt(XFP[i]*XFP[i] + yFlexorPulley*yFlexorPulley);
            print += "FLEXOR_PULLEY_DISTANCE[" + i + "] = " + fpd[i] + " ";
        }
        System.out.println(print);
        return fpd;
    }
    
    private static double[] calcAnglesMin(){
        double[] anglesMin = new double[CheetahIF.SENSORS_NUM];
        
        for(int i=0; i < CheetahIF.LEGS_NUM; i++){
            anglesMin[LegIF.KNEE_IDS[i]] = KAO[i];
            anglesMin[LegIF.HIP_IDS[i]] = HAO[i];
        }
        
        return anglesMin;
    }
    
    private static double[] calcAngleRange(){
        double[] range = new double[CheetahIF.SENSORS_NUM];
        String print = "";
        for(int i=0; i < CheetahIF.SENSORS_NUM; i++){
            range[i] = ANGLES_MAX[i]-ANGLES_MIN[i];
            print += "ANGLE_RANGE[" + i + "] = " + range[i] + " ";
        }
        System.out.println(print);
        return range;
    }
    
    private static double[] calcRawAngleRange(){
        double[] rawRange = new double[CheetahIF.SENSORS_NUM];
        String print = "";
        for(int i=0; i < CheetahIF.SENSORS_NUM; i++){
            if(ANGLES_FIX_RAW2[i] == 0){
                rawRange[i] = ANGLES_MAX_RAW2[i]-ANGLES_MIN_RAW2[i]; // negative
            } else{
                double tempRange1 = ANGLES_FIX_RAW2[i]-ANGLES_MAX_RAW2[i]; // negative
                double tempRange2 = - ANGLES_MIN_RAW2[i]; // negative
                rawRange[i] = tempRange1 + tempRange2;
            }
            print += "RAW_ANGLE_RANGE[" + i + "] = " + rawRange[i] + " ";
        }
        System.out.println(print);
        return rawRange;
    }
    
    public static double[] calcHAO(){
        double[] hao = new double[CheetahIF.LEGS_NUM];
        String print = "";
        for(int i=0; i < CheetahIF.LEGS_NUM; i++){
            double a2 = Math.toDegrees(Math.acos(JXD*2/L1[i]));
            hao[i] = a2 + JOINT_ANGLE_OFFSET - 90;
            print += "HIP_ANGLE_OFFSET[" + i + "] = " + hao[i] + " ";
        }
        System.out.println(print);
        return hao;
    }
    
    public static double[] calcKAO(){
        double[] kao = new double[CheetahIF.LEGS_NUM];
        String print = "";
        for(int i=0; i < CheetahIF.LEGS_NUM; i++){
            double a2 = Math.toDegrees(Math.acos(JXD*2/L1[i]));
            double a3 = Math.toDegrees(Math.asin(JXD/L2[i]));
            kao[i] = 90 - (180 - JOINT_ANGLE_OFFSET - a2) - a3;
            print += "KNEE_ANGLE_OFFSET[" + i + "] = " + kao[i] + " ";
        }
        System.out.println(print);
        return kao;
    }
    
    public static double getKneeAngleDegrees(int legID, int angleRaw){
        return getAngleDegrees(LegIF.KNEE_IDS[legID], angleRaw);
    }
    
    public static double getHipAngleDegrees(int legID, int angleRaw){
        return getAngleDegrees(LegIF.HIP_IDS[legID], angleRaw);
    }
    
    public static double getAngleDegrees(int legID, boolean hipAngle, int angleRaw){
        if(hipAngle){
            return getHipAngleDegrees(legID, angleRaw);
        }else{
            return getKneeAngleDegrees(legID, angleRaw);
        }
    }
    
    public static double getAngleDegrees(int sensorNum, int angleRaw){
        double angleDegrees = Double.NaN;
        
        if(ANGLES_FIX_RAW2[sensorNum] != 0){
            
            if(angleRaw >= ANGLES_FIX_RAW2[sensorNum]){
                double angleDegrees1 = ((angleRaw - ANGLES_MAX_RAW2[sensorNum])*ANGLES_RANGE[sensorNum]/ANGLES_RANGE_RAW[sensorNum]) + ANGLES_MIN[sensorNum];
                double angleDegrees2 = ((- ANGLES_MIN_RAW2[sensorNum])*ANGLES_RANGE[sensorNum]/ANGLES_RANGE_RAW[sensorNum]) + ANGLES_MIN[sensorNum];
                angleDegrees = angleDegrees1+angleDegrees2;
            } else if(angleRaw <= ANGLES_MIN_RAW2[sensorNum] && angleRaw >= 0){
                angleDegrees = ((angleRaw - ANGLES_MIN_RAW2[sensorNum])*ANGLES_RANGE[sensorNum]/ANGLES_RANGE_RAW[sensorNum]) + ANGLES_MIN[sensorNum];
            } else{
                //System.out.println("INVALID ANGLE");
            }
        }else{
            if(angleRaw > ANGLES_MIN_RAW2[sensorNum] || angleRaw < ANGLES_MAX_RAW2[sensorNum]){
                // EMERGENCY STOP, angle is too much in either direction
                //System.out.println("EMERGENCY STOP, ANGLE OVER LIMIT, ID: " + sensorNum + " angle: " + angleRaw);
                //fireErrorEvent(sensorNum, angleRaw);
            } else {
                angleDegrees = ((angleRaw - ANGLES_MIN_RAW2[sensorNum])*ANGLES_RANGE[sensorNum]/ANGLES_RANGE_RAW[sensorNum]) + ANGLES_MIN[sensorNum];
            }
        }
                
        return angleDegrees;
    }

    public static double[] calcL1() {
        double[] l1 = new double[CheetahIF.LEGS_NUM];
        String print = "";
        for (int i = 0; i < CheetahIF.LEGS_NUM; i++) {
            l1[i] = Math.sqrt(((JXD * 2) * (JXD * 2)) + ((JYD * 2) + X1[i]) * ((JYD * 2) + X1[i]));
            print += "L1[" + i + "] = " + l1[i] + " ";
        }
        System.out.println(print);
        return l1;
    }

    public static double[] calcL2() {
        double[] l2 = new double[CheetahIF.LEGS_NUM];
        String print = "";
        for (int i = 0; i < CheetahIF.LEGS_NUM; i++) {
            l2[i] = Math.sqrt(((JXD) * (JXD)) + (JYD + FYD + X2[i]) * (JYD + FYD + X2[i]));
            print += "L2[" + i + "] = " + l2[i] + " ";
        }
        System.out.println(print);
        return l2;
    }
    
    public static Point2D getEndPoint(int legID, double hipAngle, double kneeAngle){

        double phi1 = Math.toRadians(180-hipAngle); // rads
        double phi2 = Math.toRadians(hipAngle-kneeAngle); // rads
        
        double x1 = -L1[legID]*Math.cos(phi1); // mm
        double x2 = L2[legID]*Math.cos(phi2); // mm
        double y1 = L1[legID]*Math.sin(phi1); // mm
        double y2 = L2[legID]*Math.sin(phi2); // mm
        
        return new Point2D(x1 + x2, y1 + y2);
    }
    
    public static void main(String[] args){
        
    }
    
}
