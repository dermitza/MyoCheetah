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
import ch.dermitza.myocheetah.CheetahIF;
import ch.dermitza.myocheetah.geometry.TrajectoryGenerator;
import ch.dermitza.myocheetah.geometry.kinematics.MotorValues;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_RR;
import ch.dermitza.myocheetah.ui.DataPanel;
import ch.dermitza.myocheetah.ui.EndpointPanel;
import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class TrajectoryOne extends AbstractController{
    
    private static Point2D[] trajectory = TrajectoryGenerator.genEllipse(new Point2D(0, 550), 70, 30, 50);
    private static MotorValues[] vals =
            TrajectoryGenerator.motorValuesFromTrajectory(LEG_RR, 
                    trajectory);
    
    private Point2D[] initialEndpoints;
    private int pointCounter;
    
    public TrajectoryOne(EndpointPanel endpointPanel, DataPanel dataPanel,
            Cheetah cheetah, boolean constrain, long constrainPeriod) {
        super(endpointPanel, dataPanel, cheetah, constrain, constrainPeriod);
    }

    @Override
    protected void initController() {
        initialEndpoints = new Point2D[CheetahIF.LEGS_NUM];
        
        for(int i=0; i < CheetahIF.LEGS_NUM; i++){
            initialEndpoints[i] = cheetah.legs[i].getEndpoint();
        }
        pointCounter = closestTrajPointIDX(LEG_RR);
        endpointPanel.addGeneratedEndpointData(LEG_RR, trajectory);
    }

    @Override
    protected void shutdown() {
        endpointPanel.clearTraces();
    }

    @Override
    protected void controlLoop() {
        if(pointCounter >= vals.length){
            pointCounter = 0;
        }
        
        endpointPanel.addMeasuredEndpointData(LEG_RR, cheetah.legs[LEG_RR].getEndpoint());
        
        if(vals[pointCounter].isValid()){
            cheetah.legs[LEG_RR].setPositions(vals[pointCounter].getFlexorAngle(), vals[pointCounter].getExtensorAngle());
        }
        
        pointCounter++;
    }
    
    /* Find the point in the trajectory that is the closest
        to the initially recorded endpoint of the leg(s)
    */
    private int closestTrajPointIDX(int legID){
        int idx = 0;
        double dist = Double.MAX_VALUE;
        for(int i=0; i < vals.length; i++){
            double d = Math.sqrt(Math.pow(trajectory[i].getX()-initialEndpoints[legID].getX(), 2) + Math.pow(trajectory[i].getY()-initialEndpoints[legID].getY(), 2));
            if(d < dist){
                dist = d;
                idx = i;
            }
        }
        System.out.println("Minimum distance: " + dist + "mm at index " + idx);
        
        return idx;
    }

    @Override
    public void errorReceived(int id, int type, String message) {
    }
    
}
