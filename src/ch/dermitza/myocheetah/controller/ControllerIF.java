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
package ch.dermitza.myocheetah.controller;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public interface ControllerIF {
    
    
    public static final String[] CONTROLLER_NAMES = 
    {
        "CalibrationController",
        "TrajectoryOne",
        "TrajectoryFour",
        "EmptyController",
        "SkataController"
    };
    
    public static final int ID_INVALID_CONTROLLER = -1;
    public static final int ID_CALIBRATION_CONTROLLER = 0;
    public static final int ID_TRAJECTORY_ONE = 1;
    public static final int ID_TRAJECTORY_FOUR = 2;
    public static final int ID_EMPTY_CONTROLLER = 3;
    public static final int ID_SKATA_CONTROLLER = 4;
    
}
