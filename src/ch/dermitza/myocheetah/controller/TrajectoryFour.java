package ch.dermitza.myocheetah.controller;

import ch.dermitza.myocheetah.Cheetah;
import ch.dermitza.myocheetah.CheetahIF;
import ch.dermitza.myocheetah.geometry.TrajectoryGenerator;
import static ch.dermitza.myocheetah.geometry.TrajectoryGenerator.genEllipse;
import ch.dermitza.myocheetah.geometry.kinematics.MotorValues;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_FL;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_FR;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_RL;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_RR;
import ch.dermitza.myocheetah.ui.DataPanel;
import ch.dermitza.myocheetah.ui.EndpointPanel;
import ch.dermitza.myocheetah.util.geometry.Point2D;

/**
 *
 * @author exe
 */
public class TrajectoryFour extends AbstractController{
    
    //private static Point2D[] trajectory = TrajectoryGenerator.genEllipse(new Point2D(0, 550), 70, 30, 50);
    //private static Point2D[] trajectory = genEllipse(new Point2D(0, 600), 70, 30, 12);
    //private static Point2D[] trajectory = genEllipse(new Point2D(0, 600), 0, 40, 12); // too much inside
    private static Point2D[] trajectory = genEllipse(new Point2D(200, 550), 0, 40, 12);
    private static Point2D[] trajectory2 = genEllipse(new Point2D(50, 600), 0, 40, 12);
    private static MotorValues[][] vals =
                        {TrajectoryGenerator.motorValuesFromTrajectory(LEG_FL, 
                            trajectory2, false),
                        TrajectoryGenerator.motorValuesFromTrajectory(LEG_FR, 
                            trajectory2, false),
                        TrajectoryGenerator.motorValuesFromTrajectory(LEG_RL, 
                            trajectory, false),
                        TrajectoryGenerator.motorValuesFromTrajectory(LEG_RR, 
                            trajectory,false ),
                        };
    
    private Point2D[] initialEndpoints;
    private int pointCounter;
    //private int pointCounter2;
    
    public TrajectoryFour(EndpointPanel endpointPanel, DataPanel dataPanel,
            Cheetah cheetah, boolean constrain, long constrainPeriod) {
        super(endpointPanel, dataPanel, cheetah, constrain, constrainPeriod);
    }

    @Override
    protected void initController() {
        initialEndpoints = new Point2D[CheetahIF.LEGS_NUM];
        
        for(int i=0; i < CheetahIF.LEGS_NUM; i++){
            initialEndpoints[i] = cheetah.legs[i].getEndpoint();
        }
        //pointCounter = closestTrajPointIDX(LEG_RR);
        pointCounter = 0;
        //pointCounter2 = 5;
        //frontPointCounter = 0;
        endpointPanel.addGeneratedEndpointData(LEG_FL, trajectory);
        endpointPanel.addGeneratedEndpointData(LEG_FR, trajectory);
        endpointPanel.addGeneratedEndpointData(LEG_RL, trajectory);
        endpointPanel.addGeneratedEndpointData(LEG_RR, trajectory);
    }

    @Override
    protected void shutdown() {
        endpointPanel.clearTraces();
    }

    @Override
    protected void controlLoop() {
        if(pointCounter >= vals[0].length){
            pointCounter = 0;
        }
        //if(pointCounter2 >= vals[0].length){
        //    pointCounter2 = 0;
        //}
        
        //System.out.println("Front: " + trajectory[frontPointCounter].getX() + ":" + trajectory[frontPointCounter].getY());
        //System.out.println("Rear: " + trajectory[rearPointCounter].getX() + ":" + trajectory[rearPointCounter].getY());
        //System.out.println();
        
        endpointPanel.addMeasuredEndpointData(LEG_FL, cheetah.legs[LEG_FL].getEndpoint());
        endpointPanel.addMeasuredEndpointData(LEG_FR, cheetah.legs[LEG_FR].getEndpoint());
        endpointPanel.addMeasuredEndpointData(LEG_RL, cheetah.legs[LEG_RL].getEndpoint());
        endpointPanel.addMeasuredEndpointData(LEG_RR, cheetah.legs[LEG_RR].getEndpoint());
        
        if(vals[0][pointCounter].isValid() && vals[1][pointCounter].isValid()
                && vals[2][pointCounter].isValid() && vals[3][pointCounter].isValid()){
            cheetah.legs[LEG_FL].setPositions(vals[0][pointCounter].getFlexorAngle(), vals[0][pointCounter].getExtensorAngle());
            cheetah.legs[LEG_FR].setPositions(vals[1][pointCounter].getFlexorAngle(), vals[1][pointCounter].getExtensorAngle());
            cheetah.legs[LEG_RL].setPositions(vals[2][pointCounter].getFlexorAngle(), vals[2][pointCounter].getExtensorAngle());
            cheetah.legs[LEG_RR].setPositions(vals[3][pointCounter].getFlexorAngle(), vals[3][pointCounter].getExtensorAngle());
        }
        pointCounter++;
        //pointCounter2++;
        //frontPointCounter--;
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
