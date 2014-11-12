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
package ch.dermitza.myocheetah.util.geometry;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
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
