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
package ch.dermitza.myocheetah.leg;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public interface LegIF {
    
    public static final int LEG_FL = 0;
    public static final int LEG_FR = 1;
    public static final int LEG_RL = 2;
    public static final int LEG_RR = 3;
    public static final int[] LEG_IDS = {LEG_FL, LEG_FR, LEG_RL, LEG_RR};
    /* Leg names */
    public static final String[] LEG_NAMES = {
        "Front left",
        "Front right",
        "Rear left",
        "Rear right"};
    // Corresponding to LEG IDs as INDICES
    public static final short[] KNEE_IDS = {6, 2, 5, 0};
    public static final short[] HIP_IDS = {4, 3, 7, 1};
    public static final short[] FLEXOR_IDS = {5, 7, 4, 1};
    public static final short[] EXTENSOR_IDS = {6, 8, 3, 2};
    
    public static final double DEG_QC = 108544./360;
    public static final double QC_DEG = 360./108544;
    public static final double QC_SECRPM = 1/53.;
    public static final double RPM_QCSEC = 53.;//6.360/108544
    public static final double HALL_DEG = 360/4096.; // UNUSED
    
    // GEOMETRY 
    
}
