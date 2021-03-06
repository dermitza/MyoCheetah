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
package ch.dermitza.myocheetah;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public interface CheetahIF {
    
    /* Leg IDs */
    public static final short SINUSOIDAL = 1;
    public static final short TRAPEZOIDAL = 0;
    public static final int POSITIONPRIFLE_ACCE_CALI = 2000;
    public static final int POSITIONPRIFLE_CONSSPEED_CALI = 20000;
    public static final int POSITIONPRIFLE_DECCE_CALI = 20000;
    
    public static final int POSITIONPRIFLE_ACCE_NORMAL = 8000;
    public static final int POSITIONPRIFLE_CONSSPEED_NORMAL = 100000;
    public static final int POSITIONPRIFLE_DECCE_NORMAL = 100000;
    
    public static final int SENSORS_NUM = 8;
    public static final int LEGS_NUM = 4;
    
    /* GENERIC INVALID ID */
    public static final short ID_INVALID = -1;
    
    /* EPOS2 CONTROLLER IDS */
    public static final short ID_ALL         = 0;
    public static final short ID_FLEXOR_FL   = 5;
    public static final short ID_EXTENSOR_FL = 6;
    public static final short ID_FLEXOR_FR   = 7;
    public static final short ID_EXTENSOR_FR = 8;
    public static final short ID_FLEXOR_RL   = 4;
    public static final short ID_EXTENSOR_RL = 3;
    public static final short ID_FLEXOR_RR   = 1;
    public static final short ID_EXTENSOR_RR = 2;
    
    /* EPOS2 INDICES, used for controller name autoindexing */
    public static final short[] CONTROLLER_INDICES = {
        ID_ALL,
        ID_FLEXOR_FL,
        ID_EXTENSOR_FL,
        ID_FLEXOR_FR,
        ID_EXTENSOR_FR,
        ID_FLEXOR_RL,
        ID_EXTENSOR_RL,
        ID_FLEXOR_RR,
        ID_EXTENSOR_RR,
    };
    
    /* EPOS2 CONTROLLER NAMES */
    public static final String[] CONTROLLER_NAMES = {
        "ALL",
        "FLEXOR FL",
        "EXTENSOR FL",
        "FLEXOR FR",
        "EXTENSOR FR",
        "FLEXOR RL",
        "EXTENSOR RL",
        "FLEXOR RR",
        "EXTENSOR RR",};
    
    /* JOINT SENSOR IDS */
    public static final short ID_JOINT_BASE = 0x50;
    public static final short ID_JOINT_FL_HIP = 4;
    public static final short ID_JOINT_FL_KNEE = 6;
    public static final short ID_JOINT_FR_HIP = 3;
    public static final short ID_JOINT_FR_KNEE = 2;
    public static final short ID_JOINT_RL_HIP = 7;
    public static final short ID_JOINT_RL_KNEE = 5;
    public static final short ID_JOINT_RR_HIP = 1;
    public static final short ID_JOINT_RR_KNEE = 0;
    
    public static final String[] ANGLE_NAMES = {
       "KNEE RR",
        "HIP RR",
        "KNEE FR",
        "HIP FR",
        "HIP FL",
        "KNEE RL",
        "KNEE FL",
        "HIP RL"
    };
}
