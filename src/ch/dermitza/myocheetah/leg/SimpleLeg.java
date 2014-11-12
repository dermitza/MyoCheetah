package ch.dermitza.myocheetah.leg;

import ch.dermitza.myocheetah.data.DataManager;
import ch.dermitza.myocheetah.geometry.kinematics.FKine;
import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 *
 * @author exe
 */
public class SimpleLeg extends RawLeg {
    
    private boolean legCalibrated = false;
    private double flexorOffset = 0;
    private double extensorOffset = 0;
    
    public void setCalibrationOffsets(double flexorOffset, double extensorOffset){
        this.flexorOffset = flexorOffset;
        this.extensorOffset = extensorOffset;
        this.legCalibrated = true;
    }
    
    public boolean isCalibrated(){
        return this.legCalibrated;
    }

    public SimpleLeg(DataManager manager, int id) {
        super(manager, id);
    }

    @Override
    public double getFlexorRotation() {
        return LegIF.QC_DEG * (double) getRawFlexorPos();

    }

    @Override
    public double getExtensorRotation() {
        return LegIF.QC_DEG * (double) getRawExtensorPos();
    }

    @Override
    public double getFlexorAngularSpeed() {
        return LegIF.QC_DEG * (double) getRawFlexorSpd() * LegIF.QC_SECRPM;
    }

    @Override
    public double getExtensorAngularSpeed() {
        return LegIF.QC_DEG * (double) getRawExtensorSpd() * LegIF.QC_SECRPM;
    }

    @Override
    public double getFlexorAmps() {
        return (double) getRawFlexorAmpsAvg();
    }

    @Override
    public double getExtensorAmps() {
        return (double) getRawFlexorAmpsAvg();
    }

    @Override
    public double getKneeAngle() {
        return manager.getAngle(KNEE_IDS[legID]);
    }

    @Override
    public double getHipAngle() {
        return manager.getAngle(HIP_IDS[legID]);
    }
    
    @Override
    public Point2D getEndpoint(){
        return FKine.getEndPoint(getLegID(), getHipAngle(), getKneeAngle());
    }
    
    public void setPositionsCalibration(double flexorRotation, double extensorRotation){
        if (flexorRotation > 0 || extensorRotation > 0) {
            System.out.println("CRAP ANGLES");
            return;
        }

        int flexPosition = (int) ((int) flexorRotation * LegIF.DEG_QC);
        int extensorPosition = (int) ((int) extensorRotation * LegIF.DEG_QC);

        setRawPositions(flexPosition, extensorPosition);
    }

    @Override
    public void setPositions(double flexorRotation, double extensorRotation) {
        
        int flexPosition = (int)((flexorRotation + flexorOffset) * LegIF.DEG_QC);
        int extensorPosition = (int)((extensorRotation + extensorOffset) * LegIF.DEG_QC);

        setRawPositions(flexPosition, extensorPosition);
    }

}
