package ch.dermitza.myocheetah.controller;

import ch.dermitza.myocheetah.Cheetah;
import static ch.dermitza.myocheetah.CheetahIF.LEGS_NUM;
import ch.dermitza.myocheetah.geometry.kinematics.IKine;
import ch.dermitza.myocheetah.geometry.kinematics.KineData;
import ch.dermitza.myocheetah.geometry.kinematics.MotorValues;
import ch.dermitza.myocheetah.leg.LegIF;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_IDS;
import ch.dermitza.myocheetah.ui.DataPanel;
import ch.dermitza.myocheetah.ui.EndpointPanel;
import static ch.dermitza.myocheetah.ui.EndpointPanel.SENSORS_NUM;

/**
 *
 * @author exe
 */
public class CalibrationController extends AbstractController {
    
    private static final double ANGLE_THRESH_EXT = 10d;
    private static final double ANGLE_THRESH_FLEX = 10d;
    private static final double EXTENSOR_STEP = -0.8;
    private static final double FLEXOR_STEP = -0.8;
    private static final int ANGLE_COUNTER_THRESH = 2000;
    
    private static final MotorValues[] INIT_STANCE = {
        IKine.motorsFromAngles(LEG_IDS[0], 100, 60),
        IKine.motorsFromAngles(LEG_IDS[1], 100, 60),
        IKine.motorsFromAngles(LEG_IDS[2], 100, 60),
        IKine.motorsFromAngles(LEG_IDS[3], 100, 60)
    };
    
    private int[] angleCounter; // Counting how many steps the angle has been
                                // greater than the threshold set
                                // (initial angle + threshold)
    private double[] initialAngles;
    private double extensorRotations[];
    private double flexorRotations[];
    private KineData[] kine;
    
    private boolean extensorCalibrated = false;
    private boolean flexorCalibrated = false;
    private boolean calibrated = false;
    
    public CalibrationController(EndpointPanel endpointPanel,
            DataPanel dataPanel, Cheetah cheetah, boolean constrain,
            long constrainPeriod) {
        super(endpointPanel, dataPanel, cheetah, constrain, constrainPeriod);
    }

    @Override
    protected void initController() {
        kine = new KineData[LEGS_NUM];
        angleCounter = new int[LEGS_NUM];
        initialAngles = new double[SENSORS_NUM];
        flexorRotations = new double[LEGS_NUM];
        extensorRotations = new double[LEGS_NUM];
        for (int i = 0; i < LEGS_NUM; i++) {
            angleCounter[i] = 0;
            flexorRotations[i] = 0;
            extensorRotations[i] = 0;
            initialAngles[LegIF.HIP_IDS[i]] = cheetah.legs[i].getHipAngle();
            initialAngles[LegIF.KNEE_IDS[i]] = cheetah.legs[i].getKneeAngle();
        }
        
        // Check if the robot has already been calibrated at a previous run of
        // the calibration controller
        if(cheetah.isCalibrated()){
            System.out.println("PRE-CALIBRATED");
            // In this case, no calibration needs to happen. Only to get to
            // intial stance and then shutdown
            setStance();
            stop();
        }
    }

    @Override
    protected void shutdown() {
        
    }

    @Override
    protected void controlLoop() {
        calibrate();
    }

    @Override
    public void errorReceived(int id, int type, String message) {
    }
    
    private void calibrate() {
        if(calibrated){
            System.out.println("CALIBRATED");
            // Set calibration offsets, initial leg positions, then shutdown the
            // controller
            setCalibrationOffsets();
            setStance();
            stop();
        }else{
            if (!extensorCalibrated) {
                for (int i = 0; i < 4; i++) {
                    if (cheetah.legs[i].getHipAngle() >= initialAngles[LegIF.HIP_IDS[i]] + ANGLE_THRESH_EXT
                            || cheetah.legs[i].getHipAngle() <= initialAngles[LegIF.HIP_IDS[i]] - ANGLE_THRESH_EXT) {
                        extensorRotations[i] = cheetah.legs[i].getExtensorRotation();
                        angleCounter[i]++;

                    } else {
                        angleCounter[i] = 0;
                        extensorRotations[i] += EXTENSOR_STEP;
                        cheetah.legs[i].setPositionsCalibration(0, extensorRotations[i]);
                    }
                }
                System.out.println("EXT: " + angleCounter[0] + ":" + angleCounter[1] + ":" + angleCounter[2] + ":" + angleCounter[3]);
                if (angleCounter[0] > ANGLE_COUNTER_THRESH && angleCounter[1] > ANGLE_COUNTER_THRESH && angleCounter[2] > ANGLE_COUNTER_THRESH && angleCounter[3] > ANGLE_COUNTER_THRESH) {
                    extensorCalibrated = true;
                    System.out.println("EXTENSOR CALIBRATED");
                }
            }

            if (extensorCalibrated && !flexorCalibrated) {
                for (int i = 0; i < 4; i++) {
                    if (cheetah.legs[i].getKneeAngle() >= initialAngles[LegIF.KNEE_IDS[i]] + ANGLE_THRESH_FLEX
                            || cheetah.legs[i].getKneeAngle() <= initialAngles[LegIF.KNEE_IDS[i]] - ANGLE_THRESH_FLEX) {
                        angleCounter[i]++;
                    } else {
                        angleCounter[i] = 0;
                        flexorRotations[i] += FLEXOR_STEP;
                        cheetah.legs[i].setPositionsCalibration(flexorRotations[i], extensorRotations[i]);
                    }
                }
                System.out.println("FLEX: " + angleCounter[0] + ":" + angleCounter[1] + ":" + angleCounter[2] + ":" + angleCounter[3]);
                if (angleCounter[0] > ANGLE_COUNTER_THRESH && angleCounter[1] > ANGLE_COUNTER_THRESH && angleCounter[2] > ANGLE_COUNTER_THRESH && angleCounter[3] > ANGLE_COUNTER_THRESH) {
                    flexorCalibrated = true;
                    calibrated = true;
                    System.out.println("FLEXOR CALIBRATED");
                }
            }
        }
    }
    
    // Called at the end of the calibration step, before setting positions and 
    // shutting down the calibration controller
    private void setCalibrationOffsets(){
        System.out.println("SETTING OFFSETS");
        kine[0] = IKine.calculateKneeLoc(LEG_IDS[0], cheetah.legs[0].getEndpoint());
        kine[1] = IKine.calculateKneeLoc(LEG_IDS[1], cheetah.legs[1].getEndpoint());
        kine[2] = IKine.calculateKneeLoc(LEG_IDS[2], cheetah.legs[2].getEndpoint());
        kine[3] = IKine.calculateKneeLoc(LEG_IDS[3], cheetah.legs[3].getEndpoint());
        
        /* NOT ALWAYS POSITIVE */
        cheetah.legs[0].setCalibrationOffsets(kine[0].flexorAngle + cheetah.legs[0].getFlexorRotation(), kine[0].extensorAngle + cheetah.legs[0].getExtensorRotation());
        cheetah.legs[1].setCalibrationOffsets(kine[1].flexorAngle + cheetah.legs[1].getFlexorRotation(), kine[1].extensorAngle + cheetah.legs[1].getExtensorRotation());
        cheetah.legs[2].setCalibrationOffsets(kine[2].flexorAngle + cheetah.legs[2].getFlexorRotation(), kine[2].extensorAngle + cheetah.legs[2].getExtensorRotation());
        cheetah.legs[3].setCalibrationOffsets(kine[3].flexorAngle + cheetah.legs[3].getFlexorRotation(), kine[3].extensorAngle + cheetah.legs[3].getExtensorRotation());
    }
    
    private void setStance(){
        System.out.println("SETTING STANCE");
        if(stanceValid()){
            System.out.println("STANCE VALID");
            cheetah.legs[0].setPositions(INIT_STANCE[0].getFlexorAngle(), INIT_STANCE[0].getExtensorAngle());
            cheetah.legs[1].setPositions(INIT_STANCE[1].getFlexorAngle(), INIT_STANCE[1].getExtensorAngle());
            cheetah.legs[2].setPositions(INIT_STANCE[2].getFlexorAngle(), INIT_STANCE[2].getExtensorAngle());
            cheetah.legs[3].setPositions(INIT_STANCE[3].getFlexorAngle(), INIT_STANCE[3].getExtensorAngle());
        }
    }
    
    private boolean stanceValid(){
        if(INIT_STANCE[0].isValid()
                && INIT_STANCE[1].isValid()
                && INIT_STANCE[2].isValid()
                && INIT_STANCE[3].isValid()){
            return true;
        }
        return false;
    }
    
}
