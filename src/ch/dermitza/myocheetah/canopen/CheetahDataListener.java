package ch.dermitza.myocheetah.canopen;

import ch.dermitza.jcanopen.canopen.async.PDODataListener;

/**
 *
 * @author exe
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
