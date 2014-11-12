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

import ch.dermitza.myocheetah.leg.LegIF;
import ch.dermitza.myocheetah.util.geometry.Circle;
import ch.dermitza.myocheetah.util.geometry.CircleCircleIntersection;
import ch.dermitza.myocheetah.util.geometry.Point2D;
import ch.dermitza.myocheetah.util.geometry.Vector2;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class IKine {
    
    //private static final double ANGLE_THRESH_EXT = 5d; // degrees
    //private static final double ANGLE_THRESH_FLEX = 30d; // degrees
     // arc length = theta*pi*r/180
    private static final double KNEE_PULLEY_R = 68d; // mm
    private static final double ARC_KNEE_CONSTANT = Math.PI*KNEE_PULLEY_R/180d; // degrees
    
    
    
    
    private static final double HIP_ARC_DEGREES = 105; // degrees
    private static final double xExtensorPulley = -(28.5+8+2); // mm
    private static final double yExtensorPulley = -(22.5+(46/2)+6.05+2.5); //mm
    
    private static final double SHAFT_RADIUS = 5.2d; // mm (approximation to also account for the cable wrapping on itself sometimes)
    private static final double SHAFT_CIRCUMFERENCE = 2*Math.PI*SHAFT_RADIUS;
    
    // CABLE LENGTHS AT FULL EXTENSION
    private static final double[] EXTENSOR_NO_ROTATION = {128.07802268173666,128.043404259679,132.96025966891682,128.043404259679};
    private static final double[] FLEXOR_NO_ROTATION = {553.1373968620952,548.8244143230758,685.0809197875822,730.9569409397407};
   
    private static Circle circ2;
    private static Vector2 endPt;
   
    public static final Circle[] HOMES = {new Circle(new Vector2(0,0), FKine.L1[0]),
                                        new Circle(new Vector2(0,0), FKine.L1[1]),
                                        new Circle(new Vector2(0,0), FKine.L1[2]),
                                        new Circle(new Vector2(0,0), FKine.L1[3])};
    
    public static MotorValues motorsFromAngles(int legID, double hipAngle, double kneeAngle){
        if(FKine.ANGLES_MAX[LegIF.HIP_IDS[legID]] < hipAngle || hipAngle < 0){
            System.out.println("Hip angle " + hipAngle + " outside requested range [0-" + FKine.ANGLES_MAX[LegIF.HIP_IDS[legID]] + "]");
            return new MotorValues(false, 0,0);
        }
        if(FKine.ANGLES_MAX[LegIF.KNEE_IDS[legID]] < kneeAngle || kneeAngle < 0){
            System.out.println("Knee angle " + kneeAngle + " outside requested range [0-" + FKine.ANGLES_MAX[LegIF.KNEE_IDS[legID]] + "]");
            return new MotorValues(false, 0,0);
        }
        
        Point2D endPoint = FKine.getEndPoint(legID, hipAngle, kneeAngle);
        
        KineData d = calculateKneeLoc(legID, endPoint);
        
        if(d == null){ // No solution exists for the calculated endpoint
            System.out.println("No solution exists for the calculated endpoint " + endPoint.getX() + ":" + endPoint.getY());
            return new MotorValues(false, 0,0);
        }
        
        if(FKine.ANGLES_MAX[LegIF.HIP_IDS[legID]] < d.hipAngle || d.hipAngle < 0){
            System.out.println("Hip angle " + d.hipAngle + " outside requested range [0-" + FKine.ANGLES_MAX[LegIF.HIP_IDS[legID]] + "]");
            return new MotorValues(false, 0,0);
        }
        if(FKine.ANGLES_MAX[LegIF.KNEE_IDS[legID]] < d.kneeAngle || d.kneeAngle < 0){
            System.out.println("Knee angle " + d.kneeAngle + " outside requested range [0-" + FKine.ANGLES_MAX[LegIF.KNEE_IDS[legID]] + "]");
            return new MotorValues(false, 0,0);
        }
                
        return new MotorValues(true, d.flexorAngle, d.extensorAngle);
    }
    
    public static MotorValues motorsFromEndpoint(int legID, Point2D endpoint){
        KineData d = calculateKneeLoc(legID, endpoint);
        
        if(d == null){ // No solution exists for the given endpoint
            System.out.println("No solution exists for the given endpoint " + endpoint.getX() + ":" + endpoint.getY());
            return new MotorValues(false, 0,0);
        }
        if(FKine.ANGLES_MAX[LegIF.HIP_IDS[legID]] < d.hipAngle || d.hipAngle < 0){
            System.out.println("Hip angle " + d.hipAngle + " outside requested range [0-" + FKine.ANGLES_MAX[LegIF.HIP_IDS[legID]] + "]");
            return new MotorValues(false, 0,0);
        }
        if(FKine.ANGLES_MAX[LegIF.KNEE_IDS[legID]] < d.kneeAngle || d.kneeAngle < 0){
            System.out.println("Knee angle " + d.kneeAngle + " outside requested range [0-" + FKine.ANGLES_MAX[LegIF.KNEE_IDS[legID]] + "]");
            return new MotorValues(false, 0,0);
        }
        
        return new MotorValues(true, d.flexorAngle, d.extensorAngle);
    }
    
    public static KineData calculateKneeLoc(int legID, Point2D endpoint){
        
        KineData d = new KineData();
        
        // Find knee location (x, y) from circle to circle intersection
        endPt = new Vector2(endpoint.getX(), endpoint.getY());
        circ2 = new Circle(endPt, FKine.L2[legID]);
        CircleCircleIntersection i = new CircleCircleIntersection(HOMES[legID], circ2);
        
        // Create triangle distances to calculate inverse knee angle
        // NEED TO CHECK ALSO FOR NON-INTERSECTING BUT TANGENT CIRCLES
        if(i.intersectionPoint1 == null){
            if(i.intersectionPoint != null){
            d.pointI.setX(i.intersectionPoint.x);
            d.pointI.setY(i.intersectionPoint.y);
            d.point2.setX(endPt.x - d.pointI.getX()); // mm
            d.point2.setY(endPt.y - d.pointI.getY()); // mm
            } else {
                return null;
            }
        }else{
            d.pointI.setX(i.intersectionPoint1.x);
            d.pointI.setY(i.intersectionPoint1.y);
            d.point2.setX(endPt.x - d.pointI.getX()); // mm
            d.point2.setY(endPt.y - d.pointI.getY()); // mm
        }
        
        // Find the inverse hip and knee angle
        d.hipAngle = 180-Math.toDegrees(Math.acos(-d.pointI.getX()/FKine.L1[legID])); // degrees
        d.kneeAngle = -Math.toDegrees(Math.asin(d.point2.getY()/FKine.L2[legID]))+d.hipAngle; // degrees
        
        // Find motor rotation to get to these angles
        // Circle - circle center distance
        d.pointD.setX(FKine.XFP[legID]-d.pointI.getX());
        d.pointD.setY(FKine.yFlexorPulley-d.pointI.getY());
        d.centerDist = Math.sqrt(d.pointD.getX()*d.pointD.getX() + d.pointD.getY()*d.pointD.getY());
        double cDistSq = d.centerDist*d.centerDist;
        
        // Calculate the angles themselves
        d.theta1 = Math.acos(KNEE_PULLEY_R/d.centerDist); // rads
        d.theta2 = Math.acos((FKine.L1[legID]*FKine.L1[legID] + cDistSq - FKine.FPD[legID]*FKine.FPD[legID])/(2*FKine.L1[legID]*d.centerDist)); // rads
        
        // Find the flexor cable distance
        d.thetaArcKnee = d.kneeAngle + 180 - Math.toDegrees(d.theta1 + d.theta2); // degrees
        d.arcKnee = d.thetaArcKnee*ARC_KNEE_CONSTANT;
        d.cableDist = Math.sqrt(cDistSq - KNEE_PULLEY_R*KNEE_PULLEY_R);
        d.flexorDist = d.arcKnee + d.cableDist;
        
        // Find the extensor cable distance
        d.pointJP.setX(KNEE_PULLEY_R*Math.cos(Math.toRadians(d.hipAngle-HIP_ARC_DEGREES)));
        d.pointJP.setY(KNEE_PULLEY_R*Math.sin(Math.toRadians(d.hipAngle-HIP_ARC_DEGREES)));
        
        d.extensorDist = Math.sqrt((d.pointJP.getX()-xExtensorPulley)*(d.pointJP.getX()-xExtensorPulley) + (d.pointJP.getY()-yExtensorPulley)*(d.pointJP.getY()-yExtensorPulley));
        
        d.flexorRotation = (FLEXOR_NO_ROTATION[legID] - d.flexorDist)/SHAFT_CIRCUMFERENCE; // number of turns
        d.extensorRotation = (EXTENSOR_NO_ROTATION[legID] - d.extensorDist)/SHAFT_CIRCUMFERENCE; // number of turns
        
        d.flexorAngle = d.flexorRotation*360; // degrees (negative to coincide with epos)
        d.extensorAngle = d.extensorRotation*360; // degrees (negative to coincide with epos)
        
        return d;
    }
}
