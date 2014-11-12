package ch.dermitza.myocheetah.controller;

import ch.dermitza.myocheetah.Cheetah;
import ch.dermitza.myocheetah.error.ErrorListener;
import ch.dermitza.myocheetah.ui.DataPanel;
import ch.dermitza.myocheetah.ui.EndpointPanel;

/**
 *
 * @author exe
 */
public abstract class AbstractController implements Runnable, ErrorListener{
    
    public static final long DEFAULT_PERIOD = 2; // ms
    
    private boolean fixedPeriod = true;
    private Thread controllerThread;
    private boolean running = false;
    
    private long controlPeriod = 0;
    private long prevMs = 0;
    private long currMs = 0;
    private double startTime;
    protected final Cheetah cheetah;
    protected final DataPanel dataPanel;
    protected final EndpointPanel endpointPanel;
    
    private double pcTimestamp;
    
    public AbstractController(EndpointPanel endpointPanel, DataPanel dataPanel,
            Cheetah cheetah, boolean constrain, long constrainPeriod){
        this.endpointPanel = endpointPanel;
        this.dataPanel = dataPanel;
        this.cheetah = cheetah;
        this.fixedPeriod = constrain;
        this.controlPeriod = constrainPeriod;
    }
    
    public void setControlPeriod(long periodMs){
        this.controlPeriod = periodMs;
    }
    
    public void fixedLoopPeriod(boolean fixedPeriod){
        this.fixedPeriod = fixedPeriod;
        if(!fixedPeriod){
            prevMs = 0;
            currMs = 0;
        }
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public void start(){
        running = true;
        controllerThread = new Thread(this);
        controllerThread.start();
    }
    
    public void stop(){
        running = false;
    }
    
    protected double getPCTimestamp(){
        return this.currMs/1000000000.;
    }
    protected double startTimer(){
        return this.startTime;
    }
    
    protected double updatePCTimestamp(){
        pcTimestamp = System.nanoTime()/1000000;
        return pcTimestamp;
    }
    
    protected abstract void initController();
    protected abstract void shutdown();
    protected abstract void controlLoop();

    @Override
    public void run() {
        
        initController();
        startTime = System.nanoTime()/1000000000.;
        while(running){
            updatePCTimestamp();
            if(fixedPeriod){
                currMs = System.nanoTime();
                if(currMs-prevMs >= controlPeriod*1000){
                     
                     prevMs = currMs;
                     // Control
                     controlLoop();
                }
                if(controlPeriod > 10){
                    trySleep(controlPeriod);
                }
            }else{
                controlLoop();
            }
            
        }
        shutdown();
    }
    
    private void trySleep(long ms){
        try{
            Thread.sleep(ms);
        }catch(InterruptedException ie){
            
        }
    }
    
}
