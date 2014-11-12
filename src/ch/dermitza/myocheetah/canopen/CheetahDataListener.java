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
package ch.dermitza.myocheetah.canopen;

import ch.dermitza.jcanopen.canopen.async.PDODataListener;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public interface CheetahDataListener extends PDODataListener{
    
    public static final int DATA_MOT_POS      = 0;
    public static final int DATA_MOT_SPD      = 1;
    public static final int DATA_MOT_CURR     = 2;
    public static final int DATA_MOT_CURR_AVG = 3;
    public static final int DATA_MOT_DIN      = 4;
    public static final int DATA_JOINT_ANGLE  = 5;
    
    
    //public void dataArrived(int dataType, int sensorNum, double timestamp, int value);
    
    public void dataPDO1(int sensorNum, double timestamp, int pos, int spd);
    public void dataPDO2(int sensorNum, double timestamp, int curr, int currAvg, int din);
    
}
