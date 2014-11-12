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

import ch.dermitza.myocheetah.Cheetah;
import ch.dermitza.myocheetah.error.ErrorListener;
import ch.dermitza.myocheetah.ui.DataPanel;
import ch.dermitza.myocheetah.ui.EndpointPanel;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
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
