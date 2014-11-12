package ch.dermitza.myocheetah.geometry.kinematics;

/**
 *
 * @author exe
 */
public class MotorValues {
    
    private final boolean valid;
    private final double flexorAngle;
    private final double extensorAngle;
    
    /* ALWAYS SHOULD RECEIVE POSITIVE VALUES */
    public MotorValues(boolean valid, double flexorAngle, double extensorAngle){
        this.valid = valid;
        //System.out.println("MV BEFORE: " + extensorAngle + ":" + flexorAngle);
        if(flexorAngle >= 0){
            // Valid angles come in positive from inverse kinematics, and they should go to EPOS negative
            // So the translation happens here
            this.flexorAngle = -flexorAngle;
        }else{
            // If an angle is negative, it is over the limit (OVER-EXTENDED) and should be 0 to account for 0 epos value = FULL EXTENSION
            this.flexorAngle = 0;
        }
        
        if(extensorAngle >= 0){
            this.extensorAngle = -extensorAngle;
        }else{
            this.extensorAngle = 0;
        }
        
        //System.out.println("MV AFTER: " + this.extensorAngle + ":" + this.flexorAngle);
    }
    
    public boolean isValid(){
        return this.valid;
    }
    
    public double getFlexorAngle(){
        return this.flexorAngle;
    }
    
    public double getExtensorAngle(){
        return this.extensorAngle;
    }
}
