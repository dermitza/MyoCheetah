package ch.dermitza.myocheetah.leg;

import ch.dermitza.epos2.EPOS2;
import ch.dermitza.myocheetah.canopen.SDOTransceiver;
import ch.dermitza.myocheetah.data.DataManager;
import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 *
 * @author exe
 */
public abstract class RawLeg implements LegIF{

    protected final int legID;
    protected final EPOS2 flexor;
    protected final EPOS2 extensor;
    protected final DataManager manager;

    protected double endpointX;
    protected double endpointY;

    public RawLeg(DataManager manager, int legID) {
        this.manager = manager;
        this.legID = legID;
        flexor = new EPOS2(FLEXOR_IDS[legID]);
        extensor = new EPOS2(EXTENSOR_IDS[legID]);
    }
    
    public int getLegID(){
        return this.legID;
    }

    public short getFlexorID() {
        return FLEXOR_IDS[legID];
    }

    public short getExtensorID() {
        return EXTENSOR_IDS[legID];
    }

    public short getKneeID() {
        return KNEE_IDS[legID];
         
    }

    public short getHipID() {
        return HIP_IDS[legID];
    }

    public double getFlexorTimestamp() {
        return manager.getTimestamp(FLEXOR_IDS[legID]);
    }

    public double getExtensorTimestamp() {
        return manager.getTimestamp(EXTENSOR_IDS[legID]);
    }

    public double getLastTimestamp() {
        return manager.getLatestTimestamp();
    }

    public abstract double getFlexorRotation();

    public abstract double getExtensorRotation();

    public abstract double getFlexorAngularSpeed();

    public abstract double getExtensorAngularSpeed();

    public abstract double getFlexorAmps();

    public abstract double getExtensorAmps();

    public abstract double getKneeAngle();

    public abstract double getHipAngle( );

    public abstract Point2D getEndpoint();

    public int getRawFlexorPos() {
        return manager.getRawPos(FLEXOR_IDS[legID]);
    }

    public int getRawExtensorPos() {
        return manager.getRawPos(EXTENSOR_IDS[legID]);
    }

    public int getRawFlexorSpd() {
        return manager.getRawSpeed(FLEXOR_IDS[legID]);
    }

    public int getRawExtensorSpd() {
        return manager.getRawSpeed(EXTENSOR_IDS[legID]);
    }

    public int getRawFlexorAmps() {
        return manager.getRawAmps(FLEXOR_IDS[legID]);
    }

    public int getRawExtensorAmps() {
        return manager.getRawAmps(EXTENSOR_IDS[legID]);
    }
    public int getRawFlexorAmpsAvg() {
        return manager.getRawAmpsAvg(FLEXOR_IDS[legID]);
    }
    public int getRawExtensorAmpsAvg() {
        return manager.getRawAmpsAvg(EXTENSOR_IDS[legID]);
    }
    public int getRawKneeAngle() {
        return manager.getRawAngle(KNEE_IDS[legID]);
    }

    public int getRawHipAngle() {
        return manager.getRawAngle(HIP_IDS[legID]);
    }

    public void setTransceiver(SDOTransceiver trans) {
        flexor.setTranceiver(trans);
        extensor.setTranceiver(trans);
    }

    public int enable() {
        int ret = -1;
        if(flexor.enableController()){
            ret += 1;
        }
        if(extensor.enableController()){
            ret += 2;
        }
        return ret;
    }

    public void disable() {
        flexor.disableController();
        extensor.disableController();
    }

    public void enableMotors(byte opMode) {
        flexor.enableMotor(opMode);
        extensor.enableMotor(opMode);
    }
    
    public abstract void setPositions(double flexorRotation, double extensorRotation);
    
    public void setRawPositions(int flexorPos, int extensorPos) {
        flexor.setTargetProfilePosition(flexorPos);
        extensor.setTargetProfilePosition(extensorPos);

        flexor.startProfilePosition(true);
        extensor.startProfilePosition(true);
    }

    public void setOperationMode(byte opMode) {
        flexor.setOperationMode(opMode);
        extensor.setOperationMode(opMode);
    }

    public int setProfileVelocity(boolean setFlexor, int velocity) {
        if (setFlexor) {
            return flexor.setProfileVelocity(velocity);
        } else {
            return extensor.setProfileVelocity(velocity);
        }
    }
    

    public int setProfileAcceleration(boolean setFlexor, int acceleration) {
        if (setFlexor) {
            return flexor.setProfileAcceleration(acceleration);
        } else{
            return extensor.setProfileAcceleration(acceleration);
        }
    }
    
    public int setProfileDeceleration(boolean setFlexor, int deceleration) {
        if (setFlexor) {
            return flexor.setProfileDeceleration(deceleration);
        } else{
            return extensor.setProfileDeceleration(deceleration);
        }
    }

    public int setMotionProfileType(boolean setFlexor, short profileType) {
        if(setFlexor){
            return flexor.setMotionProfileType(profileType);
        }else{
            return extensor.setMotionProfileType(profileType);
        }
    }
    
    public int getProfileVelocity(boolean getFlexor) {
        if (getFlexor) {
            return flexor.readProfileVelocity();
        } else{
            return extensor.readProfileVelocity();
        }
    }

    public int getProfileAcceleration(boolean getFlexor) {
        if (getFlexor) {
            return flexor.readProfileAcceleration();
        } else{
            return extensor.readProfileAcceleration();
        }
    }

    public int getProfileDeceleration(boolean getFlexor) {
        if (getFlexor) {
            return flexor.readProfileDeceleration();
        } else{
            return extensor.readProfileDeceleration();
        }
    }

    public int getMotionProfileType(boolean getFlexor) {
        if(getFlexor){
            return flexor.readMotionProfileType();
        }else{
            return extensor.readMotionProfileType();
        }
    }
    public int setVelocity(boolean setFlexor, double velocity) {
        double velSet = velocity*LegIF.RPM_QCSEC;
        if (setFlexor) {
            return flexor.setTargetVelocity((int)velSet);
        } else{
            return extensor.setTargetVelocity((int)velSet);
        }
    }
//    public int setVelocityMode(short id) {
//        if (id == flexorID) {
//            return flexor.setVelocityMode();
//        } else if (id == extensorID) {
//            return extensor.setVelocityMode();
//        }
//        return -1;
//    }
    

}
