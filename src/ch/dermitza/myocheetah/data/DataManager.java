package ch.dermitza.myocheetah.data;

import ch.dermitza.myocheetah.CheetahIF;
import ch.dermitza.myocheetah.StatusListener;
import ch.dermitza.myocheetah.canopen.CheetahDataListener;
import static ch.dermitza.myocheetah.canopen.CheetahDataListener.DATA_JOINT_ANGLE;
import static ch.dermitza.myocheetah.canopen.CheetahDataListener.DATA_MOT_CURR;
import static ch.dermitza.myocheetah.canopen.CheetahDataListener.DATA_MOT_CURR_AVG;
import static ch.dermitza.myocheetah.canopen.CheetahDataListener.DATA_MOT_DIN;
import static ch.dermitza.myocheetah.canopen.CheetahDataListener.DATA_MOT_POS;
import static ch.dermitza.myocheetah.canopen.CheetahDataListener.DATA_MOT_SPD;
import ch.dermitza.myocheetah.error.ErrorListener;
import ch.dermitza.myocheetah.geometry.kinematics.FKine;
import ch.dermitza.myocheetah.util.disk.DiskWriter;
import java.util.ArrayList;

/**
 *
 * @author exe
 */
public class DataManager implements CheetahDataListener, StatusListener {
    
    public static final int[] AMPS_MAX = {0,0,0,0,0,0,0,0};

    private final int[] rawPos;
    private final int[] rawSpd;
    private final int[] rawAmps;
    private final int[] rawAmpsAvg;
    private final int[] digitalInputs;
    private final int[] rawAngles;
    
    private final double[] angles;
    private final double[] amps;
    private final double[] ampsAvg;
    private final double[] speed;
    private final double[] pos;
    
    private final double[] timestamps;
    private DiskWriter writer;
    private Thread writerThread;
    
    private final ArrayList<ErrorListener> listeners;

    public DataManager() {
        
        listeners = new ArrayList<>();

        rawPos = new int[CheetahIF.SENSORS_NUM];
        rawSpd = new int[CheetahIF.SENSORS_NUM];
        rawAmps = new int[CheetahIF.SENSORS_NUM];
        rawAngles = new int[CheetahIF.SENSORS_NUM];
        rawAmpsAvg = new int[CheetahIF.SENSORS_NUM];
        digitalInputs = new int[CheetahIF.SENSORS_NUM];
        timestamps = new double[CheetahIF.SENSORS_NUM];
        
        pos = new double[CheetahIF.SENSORS_NUM];
        speed = new double[CheetahIF.SENSORS_NUM];
        amps = new double[CheetahIF.SENSORS_NUM];
        ampsAvg = new double[CheetahIF.SENSORS_NUM];
        angles = new double[CheetahIF.SENSORS_NUM];
 
        resetData();
    }
    
    public void addErrorListener(ErrorListener listener) {
        listeners.add(listener);
    }

    public void removeErrorListener(ErrorListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }


    public void resetData() {
        for (int i = 0; i < CheetahIF.SENSORS_NUM; i++) {
            rawPos[i] = 0;
            rawSpd[i] = 0;
            rawAmps[i] = 0;
            rawAngles[i] = 0;
            rawAmpsAvg[i] = 0;
            digitalInputs[i] = 0;
            timestamps[i] = 0d;
            pos[i] = 0d;
            speed[i] = 0d;
            amps[i] = 0d;
            ampsAvg[i] = 0d;
            angles[i] = 0d;
        }
    }

    public void startWriter(String controllerName, String path) {
        // Start the disk writer
        writer = new DiskWriter();
        writer.setHeader("# Controller: " + controllerName + "\n"
        + "# Data format: Timestamp:type:id:datatype:data\n"
                + "# Data description: seconds:0=EPOS,1=JOINT:SENSORID:CheetahDataListener:data\n");
        writer.setTimestampInFilename(true);
        writer.setDirPath(path);
        writerThread = new Thread(writer);
        writerThread.start();
    }

    public void stopWriter() {
        writer.setRunning(false);
    }
    
    private synchronized void addPosPoint(int sensorNum, int value){
        rawPos[sensorNum] = value;
        // conversion happens here!
    }
    
    private synchronized void addSpdPoint(int sensorNum, int value){
        rawSpd[sensorNum] = value;
        // conversion happens here!
    }
    
    private synchronized void addCurrPoint(int sensorNum, int value){
        rawAmps[sensorNum] = value;
        // conversion happens here!
    }
    
    private synchronized void addCurrAvgPoint(int sensorNum, int value){
        if(value > AMPS_MAX[sensorNum]){
            // EMERGENCY STOP, too much current!
            fireErrorEvent(sensorNum, ErrorListener.ERROR_MAX_CURRENT, "Raw value: " + value);
        }
        // conversion happens here!
        rawAmpsAvg[sensorNum] = value;
    }
    
    private synchronized void addDINPoint(int sensorNum, int value){
        digitalInputs[sensorNum] = value;
    }
    
    private synchronized void addAnglePoint(int sensorNum, int value){
        rawAngles[sensorNum] = value;
        
        double angle = FKine.getAngleDegrees(sensorNum, value);
        if(angle == Double.NaN){
            // ERROR, POTENTIALLY EMERGENCY STOP
            fireErrorEvent(sensorNum, ErrorListener.ERROR_INVALID_ANGLE, "Raw value: "+ value);
            return;
        }
        angles[sensorNum] = angle;
    }
    
    private void fireErrorEvent(int id, int type, String data){
        ErrorListener[] temp = listeners.toArray(new ErrorListener[0]);
        for (ErrorListener l : temp) {
            //l.errorReceived(STATE_IDLE, STATE_IDLE, null);
            l.errorReceived(id, type, data);
        }
    }

    @Override
    public void dataArrived(int id, double time, int data) {
        // Only joint angles here
        addAnglePoint(id, data);
        //synchronized (rawAngles) {
        //    rawAngles[id] = data;
        //}
        
        if(writer != null && writer.isRunning()){
            writer.addSample(new CheetahDataPoint(time, 1, id, DATA_JOINT_ANGLE, data));
            
        }
    }

    // As EPOS IDs start at 1, subtract 1 to align with
    // array indexing
    public int getRawPos(int id) {
        synchronized (rawPos) {
            return rawPos[id - 1];
        }
    }

    // As EPOS IDs start at 1, subtract 1 to align with
    // array indexing
    public int getRawSpeed(int id) {
        synchronized (rawSpd) {
            return rawSpd[id - 1];
        }
    }

    // As EPOS IDs start at 1, subtract 1 to align with
    // array indexing
    public int getRawAmps(int id) {
        synchronized (rawAmps) {
            return rawAmps[id - 1];
        }
    }

    // As EPOS IDs start at 1, subtract 1 to align with
    // array indexing
    public int getRawAmpsAvg(int id) {
        synchronized (rawAmpsAvg) {
            return rawAmps[id - 1];
        }
    }

    // As EPOS IDs start at 1, subtract 1 to align with
    // array indexing
    public int getDigitalInputs(int id) {
        synchronized (digitalInputs) {
            return digitalInputs[id - 1];
        }
    }

    public boolean getDigitalInput(int id, int inputIdx) {
        int inputs;
        synchronized (digitalInputs) {
            inputs = digitalInputs[id];
        }
        return ((inputs & (1 << inputIdx)) == 1);
    }

    public double getTimestamp(int id) {
        synchronized (timestamps) {
            return timestamps[id];
        }
    }

    public double getLatestTimestamp() {
        double timestamp = 0;
        synchronized (timestamps) {
            for (double d : timestamps) {
                if (Math.abs(d) > timestamp) {
                    timestamp = d;
                }
            }
            return timestamp;
        }
    }

    // Angle ids do not suffer from alignment issues
    public int getRawAngle(int id) {
        synchronized (rawAngles) {
            return rawAngles[id];
        }
    }
    
    public double getAngle(int id){
        synchronized(angles){
            return angles[id];
        }
    }

    @Override
    public void statusChanged(int status) {
        System.out.println("STATUS: " + StatusListener.STATUS_NAMES[status]);
    }

    @Override
    public void dataPDO1(int sensorNum, double timestamp, int pos, int spd) {
        timestamps[sensorNum] = timestamp;
        addPosPoint(sensorNum, pos);
        addSpdPoint(sensorNum, spd);
        //synchronized (rawPos) {
        //    rawPos[sensorNum] = pos;
        //}

        //synchronized (rawSpd) {
        //    rawSpd[sensorNum] = spd;
        //}
        
        if(writer != null && writer.isRunning()){
            writer.addSample(new CheetahDataPoint(timestamp, 0, sensorNum, DATA_MOT_POS, pos));
            writer.addSample(new CheetahDataPoint(timestamp, 0, sensorNum, DATA_MOT_SPD, spd));
        }
    }

    @Override
    public void dataPDO2(int sensorNum, double timestamp, int curr, int currAvg, int din) {
        timestamps[sensorNum] = timestamp;
        
        addCurrPoint(sensorNum, curr);
        addCurrAvgPoint(sensorNum, currAvg);
        addDINPoint(sensorNum, din);
        //synchronized (rawAmps) {
        //    rawAmps[sensorNum] = curr;
        //}
        //synchronized (rawAmpsAvg) {
        //    rawAmpsAvg[sensorNum] = currAvg;
        //}
        //synchronized (digitalInputs) {
        //    digitalInputs[sensorNum] = din;
        //}
        
        if(writer != null && writer.isRunning()){
            writer.addSample(new CheetahDataPoint(timestamp, 0, sensorNum, DATA_MOT_CURR, curr));
            writer.addSample(new CheetahDataPoint(timestamp, 0, sensorNum, DATA_MOT_CURR_AVG, currAvg));
            writer.addSample(new CheetahDataPoint(timestamp, 0, sensorNum, DATA_MOT_DIN, din));
        }
    }

}
