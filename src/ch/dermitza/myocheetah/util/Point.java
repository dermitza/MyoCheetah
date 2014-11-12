package ch.dermitza.myocheetah.util;

/**
 *
 * @author exe
 */
public class Point {
    
    private volatile double xPos = 0;
    private volatile double yPos = 0;
    
    public synchronized void setXPos(double xPos){
        this.xPos = xPos;
    }
    
    public synchronized void setYPos(double yPos){
        this.yPos = yPos;
    }
    
    public synchronized double getXPos(){
        return this.xPos;
    }
    
    public synchronized double getYPos(){
        return this.yPos;
    }
    
}
