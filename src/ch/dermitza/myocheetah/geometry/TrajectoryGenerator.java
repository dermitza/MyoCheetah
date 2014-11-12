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
package ch.dermitza.myocheetah.geometry;

import ch.dermitza.myocheetah.geometry.kinematics.IKine;
import ch.dermitza.myocheetah.geometry.kinematics.MotorValues;
import ch.dermitza.myocheetah.leg.LegIF;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_FR;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_RR;
import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class TrajectoryGenerator {
    
    
    // When xRadius == yRadius it is a circle
    public static Point2D[] genEllipse(Point2D center, double xRadius, double yRadius, int pointNumber){
        Point2D[] traj = new Point2D[pointNumber];
        double radStep = 2*Math.PI/pointNumber;
        for(int i=0; i < pointNumber; i++){
            traj[i] = new Point2D(center.getX()+ xRadius*Math.cos(i*radStep), center.getY()+ yRadius*Math.sin(i*radStep));
            System.out.println(traj[i].getX() + " " + traj[i].getY());       
        }
        return traj;
    }
    
    public static MotorValues[] motorValuesFromTrajectory(int legID, Point2D[] trajectory, boolean invert){
        MotorValues[] v = new MotorValues[trajectory.length];
        for(int i=0; i < trajectory.length; i++){
            if(invert && (legID == LEG_RR || legID == LegIF.LEG_RL)){
                v[i] = IKine.motorsFromEndpoint(legID, new Point2D(-trajectory[i].getX(), trajectory[i].getY()));
            }else{
                v[i] = IKine.motorsFromEndpoint(legID, trajectory[i]);
            }
            System.out.println(v[i].getFlexorAngle() + ":" + v[i].getExtensorAngle());
            if(!v[i].isValid()){
                // Potentially return an invalid trajectory
                System.out.println("INVALID MOTOR VALUE");
            }
        }
        return v;
    }
    
    
    public static void main(String[] args){
        motorValuesFromTrajectory(LEG_FR, genEllipse(new Point2D(50, 600), 0, 40, 50), false);
    }
}
