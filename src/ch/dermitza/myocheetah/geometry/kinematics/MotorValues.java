/**
 * This file is part of MyoCheetah. Copyright (C) 2014 K. Dermitzakis
 * <dermitza@gmail.com>
 *
 * MyoCheetah is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * MyoCheetah is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with MyoCheetah. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dermitza.myocheetah.geometry.kinematics;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
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
