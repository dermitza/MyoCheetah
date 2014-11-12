package ch.dermitza.myocheetah.util.geometry;

/**
 *
 * @author exe
 */
public class Point2D {
    
    private volatile double x, y;
    
    public Point2D(){
        this.x = 0;
        this.y = 0;
    }
    
    public Point2D(Point2D point){
        this.x = point.getX();
        this.y = point.getY();
    }
    
    public Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public synchronized void setX(double x){
        this.x = x;
    }
    
    public synchronized void setY(double y){
        this.y = y;
    }
    
    public synchronized double getX(){
        return this.x;
    }
    
    public synchronized double getY(){
        return this.y;
    }
    
    public synchronized Point2D getCopy(){
        return new Point2D(this.x, this.y);
    }
    
}
