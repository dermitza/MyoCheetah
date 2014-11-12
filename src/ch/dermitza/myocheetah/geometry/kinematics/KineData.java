package ch.dermitza.myocheetah.geometry.kinematics;

import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 * Distances in millimeters, angles in degrees (UNLESS SPECIFIED).
 * @author exe
 */
public class KineData {
    
    public Point2D pointI = new Point2D();
    
    public Point2D point2 = new Point2D();
    
    public double hipAngle;
    public double kneeAngle;
    
    public Point2D pointD = new Point2D();
    public double centerDist;
    public double theta1; // rads
    public double theta2; // rads
    
    public double thetaArcKnee;
    public double arcKnee;
    public double cableDist;
    public double flexorDist;
    
    public Point2D pointJP = new Point2D();
    
    public double extensorDist;
    
    public double flexorRotation;
    public double extensorRotation;
    
    public double flexorAngle;
    public double extensorAngle;
}
