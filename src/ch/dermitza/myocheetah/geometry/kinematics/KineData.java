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

import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 * Distances in millimeters, angles in degrees (UNLESS SPECIFIED).
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
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
