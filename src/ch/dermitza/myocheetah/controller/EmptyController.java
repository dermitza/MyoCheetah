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
import ch.dermitza.myocheetah.geometry.kinematics.IKine;
import ch.dermitza.myocheetah.geometry.kinematics.KineData;
import ch.dermitza.myocheetah.geometry.kinematics.MotorValues;
import ch.dermitza.myocheetah.leg.LegIF;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_IDS;
import ch.dermitza.myocheetah.ui.DataPanel;
import ch.dermitza.myocheetah.ui.EndpointPanel;
import ch.dermitza.myocheetah.util.geometry.Circle;
import ch.dermitza.myocheetah.util.geometry.Point2D;
import ch.dermitza.myocheetah.util.geometry.Vector2;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class EmptyController extends AbstractController {
    
    private static final double ANGLE_THRESH_EXT = 10d;
    private static final double ANGLE_THRESH_FLEX = 10d;
    private int[] tmpCnt;
    private double extensorRotations[];
    private double flexorRotations[];
    private double extensorStep = -0.8;
    private double flexorStep = -0.8;
    private boolean extensorCalibrated = false;
    private boolean flexorCalibrated = false;
    private boolean calibrated = false;
    Circle circ2;
    Vector2 endPt;
    boolean crap = false;
    volatile MotorValues testValues;
    
    private double[] initialAngles;
        Point2D crapPoint = new Point2D();
    double hipA;
    double kneeA;

    public EmptyController(EndpointPanel endpointPanel, DataPanel dataPanel,
            Cheetah cheetah, boolean constrain, long constrainPeriod) {
        super(endpointPanel, dataPanel, cheetah, constrain, constrainPeriod);
    }

    @Override
    protected void initController() {
        tmpCnt = new int[4];
        initialAngles = new double[8];
        flexorRotations = new double[4];
        extensorRotations = new double[4];
        for (int i = 0; i < 4; i++) {
            flexorRotations[i] = 0;
            extensorRotations[i] = 0;
            initialAngles[LegIF.HIP_IDS[i]] = cheetah.legs[i].getHipAngle();
            initialAngles[LegIF.KNEE_IDS[i]] = cheetah.legs[i].getKneeAngle();
        }

        calibrate();
    }

    private void calibrate() {
        while (!calibrated) {
            if (!extensorCalibrated) {
                for (int i = 0; i < 4; i++) {
                    if (cheetah.legs[i].getHipAngle() >= initialAngles[LegIF.HIP_IDS[i]] + ANGLE_THRESH_EXT
                            || cheetah.legs[i].getHipAngle() <= initialAngles[LegIF.HIP_IDS[i]] - ANGLE_THRESH_EXT) {
                        extensorRotations[i] = cheetah.legs[i].getExtensorRotation();
                        tmpCnt[i]++;

                    } else {
                        tmpCnt[i] = 0;
                        extensorRotations[i] += extensorStep;
                        cheetah.legs[i].setPositionsCalibration(0, extensorRotations[i]);
                    }
                }
                System.out.println("EXT: " + tmpCnt[0] + ":" + tmpCnt[1] + ":" + tmpCnt[2] + ":" + tmpCnt[3]);
                if (tmpCnt[0] > 2000 && tmpCnt[1] > 2000 && tmpCnt[2] > 2000 && tmpCnt[3] > 2000) {
                    extensorCalibrated = true;
                    System.out.println("EXTENSOR CALIBRATED");
                }
            }

            if (extensorCalibrated && !flexorCalibrated) {
                for (int i = 0; i < 4; i++) {
                    if (cheetah.legs[i].getKneeAngle() >= initialAngles[LegIF.KNEE_IDS[i]] + ANGLE_THRESH_FLEX
                            || cheetah.legs[i].getKneeAngle() <= initialAngles[LegIF.KNEE_IDS[i]] - ANGLE_THRESH_FLEX) {
                        tmpCnt[i]++;
                    } else {
                        tmpCnt[i] = 0;
                        flexorRotations[i] += flexorStep;
                        cheetah.legs[i].setPositionsCalibration(flexorRotations[i], extensorRotations[i]);
                    }
                }
                System.out.println("FLEX: " + tmpCnt[0] + ":" + tmpCnt[1] + ":" + tmpCnt[2] + ":" + tmpCnt[3]);
                if (tmpCnt[0] > 2000 && tmpCnt[1] > 2000 && tmpCnt[2] > 2000 && tmpCnt[3] > 2000) {
                    flexorCalibrated = true;
                    calibrated = true;
                    System.out.println("FLEXOR CALIBRATED");
                }
            }
        }

        KineData d0 = IKine.calculateKneeLoc(LEG_IDS[0], cheetah.legs[0].getEndpoint());
        KineData d1 = IKine.calculateKneeLoc(LEG_IDS[1], cheetah.legs[1].getEndpoint());
        KineData d2 = IKine.calculateKneeLoc(LEG_IDS[2], cheetah.legs[2].getEndpoint());
        KineData d3 = IKine.calculateKneeLoc(LEG_IDS[3], cheetah.legs[3].getEndpoint());

        double offsetE, offsetF, targetE, targetF;

        offsetE = (d1.extensorAngle + cheetah.legs[1].getExtensorRotation());
        offsetF = (d1.flexorAngle + cheetah.legs[1].getFlexorRotation());

        System.out.println("Motor angle(kine): " + d1.extensorAngle + ":" + d1.flexorAngle);
        System.out.println("Motor angle(epos): " + cheetah.legs[1].getExtensorRotation() + ":" + cheetah.legs[1].getFlexorRotation());
        System.out.println("Offset: " + offsetE + ":" + offsetF);

        //d1 = IKine.calculateKneeLoc(legs[1].getLegID(), legs[1].getEndpointX()+20, legs[1].getEndpointY());

        targetE = (d1.extensorAngle - offsetE);
        targetF = (d1.flexorAngle - offsetF);
        if (targetE > 0) {
            targetE = -targetE;
        }
        if (targetF > 0) {
            targetF = -targetF;
        }

        System.out.println("Target angle(kine): " + d1.extensorAngle + ":" + d1.flexorAngle);
        System.out.println("Target angle(epos): " + targetE + ":" + targetF);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
        }


        /* NOT ALWAYS POSITIVE */
        cheetah.legs[0].setCalibrationOffsets(d0.flexorAngle + cheetah.legs[0].getFlexorRotation(), d0.extensorAngle + cheetah.legs[0].getExtensorRotation());
        cheetah.legs[1].setCalibrationOffsets(d1.flexorAngle + cheetah.legs[1].getFlexorRotation(), d1.extensorAngle + cheetah.legs[1].getExtensorRotation());
        cheetah.legs[2].setCalibrationOffsets(d2.flexorAngle + cheetah.legs[2].getFlexorRotation(), d2.extensorAngle + cheetah.legs[2].getExtensorRotation());
        cheetah.legs[3].setCalibrationOffsets(d3.flexorAngle + cheetah.legs[3].getFlexorRotation(), d3.extensorAngle + cheetah.legs[3].getExtensorRotation());


        /*
         int stepNo = 500;
        
         double maxKneeAngle = 0;
         double maxHipAngle = 0;
         double stepSize = 0;
         double rearStepSize = 0;
        
         for(int i=0; i < 4; i++){
         if(legs[i].getKneeAngle() > maxKneeAngle){
         maxKneeAngle = legs[i].getKneeAngle();
         }
         if(legs[i].getHipAngle() > maxHipAngle){
         maxHipAngle = legs[i].getHipAngle();
         }
         }
        
         stepSize = (maxKneeAngle -40)/stepNo;
         rearStepSize = stepSize*2;
        
         MotorValues v0 = IKine.motorsFromAngles(legs[0].getLegID(), legs[0].getHipID(), legs[0].getKneeID(), maxHipAngle, maxKneeAngle);
         MotorValues v1 = IKine.motorsFromAngles(legs[1].getLegID(), legs[1].getHipID(), legs[1].getKneeID(), maxHipAngle, maxKneeAngle);
         MotorValues v2 = IKine.motorsFromAngles(legs[2].getLegID(), legs[2].getHipID(), legs[2].getKneeID(), maxHipAngle, maxKneeAngle);
         MotorValues v3 = IKine.motorsFromAngles(legs[3].getLegID(), legs[3].getHipID(), legs[3].getKneeID(), maxHipAngle, maxKneeAngle);
        
        
         if(v0.isValid() && v1.isValid() && v3.isValid() && v2.isValid()){
         legs[0].setPositions(v0.getFlexorAngle(), v0.getExtensorAngle());
         legs[1].setPositions(v1.getFlexorAngle(), v1.getExtensorAngle());
         legs[2].setPositions(v2.getFlexorAngle(), v2.getExtensorAngle());
         legs[3].setPositions(v3.getFlexorAngle(), v3.getExtensorAngle());
         }
        
         for(int i=0; i < stepNo; i++){
         System.out.println("Knee angle: " + (maxKneeAngle - (stepSize*i)));
         v0 = IKine.motorsFromAngles(legs[0].getLegID(), legs[0].getHipID(), legs[0].getKneeID(), maxHipAngle, (maxKneeAngle - (stepSize*i)));
         v1 = IKine.motorsFromAngles(legs[1].getLegID(), legs[1].getHipID(), legs[1].getKneeID(), maxHipAngle, (maxKneeAngle - (stepSize*i)));
         if(v0.isValid() && v1.isValid()){
         legs[0].setPositions(v0.getFlexorAngle(), v0.getExtensorAngle());
         legs[1].setPositions(v1.getFlexorAngle(), v1.getExtensorAngle());
         }
            
         if(i > stepNo/2){
         v2 = IKine.motorsFromAngles(legs[2].getLegID(), legs[2].getHipID(), legs[2].getKneeID(), maxHipAngle, (maxKneeAngle - (rearStepSize*(i-(stepNo/2)))));
         v3 = IKine.motorsFromAngles(legs[3].getLegID(), legs[3].getHipID(), legs[3].getKneeID(), maxHipAngle, (maxKneeAngle - (rearStepSize*(i-(stepNo/2)))));
         if(v2.isValid() && v3.isValid()){
         legs[2].setPositions(v2.getFlexorAngle(), v2.getExtensorAngle());
         legs[3].setPositions(v3.getFlexorAngle(), v3.getExtensorAngle());
         }
         }
            
         }
        
         */


        MotorValues v0 = IKine.motorsFromAngles(LEG_IDS[0], 120, 45);
        MotorValues v1 = IKine.motorsFromAngles(LEG_IDS[1], 120, 45);
        MotorValues v2 = IKine.motorsFromAngles(LEG_IDS[2], 120, 45);
        MotorValues v3 = IKine.motorsFromAngles(LEG_IDS[3], 120, 45);


        if (v0.isValid() && v1.isValid() && v3.isValid() && v2.isValid()) {
            cheetah.legs[0].setPositions(v0.getFlexorAngle(), v0.getExtensorAngle());
            cheetah.legs[1].setPositions(v1.getFlexorAngle(), v1.getExtensorAngle());
            cheetah.legs[2].setPositions(v2.getFlexorAngle(), v2.getExtensorAngle());
            cheetah.legs[3].setPositions(v3.getFlexorAngle(), v3.getExtensorAngle());
        }


        //try{
        //    Thread.sleep(20000);
        //} catch(InterruptedException ie){}

        /*
        
         int stepNo = 500;
        
         double stepSize = (60 - 30)/stepNo;
         double rearStepSize = stepSize*2;
        
         for(int i=0; i < stepNo; i++){
         v0 = IKine.motorsFromAngles(legs[0].getLegID(), legs[0].getHipID(), legs[0].getKneeID(), 90, (60 - (stepSize*i)));
         v1 = IKine.motorsFromAngles(legs[1].getLegID(), legs[1].getHipID(), legs[1].getKneeID(), 90, (60 - (stepSize*i)));
         if(v0.isValid() && v1.isValid()){
         legs[0].setPositions(v0.getFlexorAngle(), v0.getExtensorAngle());
         legs[1].setPositions(v1.getFlexorAngle(), v1.getExtensorAngle());
         }
            
         if(i > stepNo/2){
         v2 = IKine.motorsFromAngles(legs[2].getLegID(), legs[2].getHipID(), legs[2].getKneeID(), 90, (60 - (rearStepSize*(i-(stepNo/2)))));
         v3 = IKine.motorsFromAngles(legs[3].getLegID(), legs[3].getHipID(), legs[3].getKneeID(), 90, (60 - (rearStepSize*(i-(stepNo/2)))));
         if(v2.isValid() && v3.isValid()){
         legs[2].setPositions(v2.getFlexorAngle(), v2.getExtensorAngle());
         legs[3].setPositions(v3.getFlexorAngle(), v3.getExtensorAngle());
         }
         }
            
         }
        
         */

        System.out.println("DONE");
        

        crapPoint = new Point2D(cheetah.legs[1].getEndpoint());
        hipA = cheetah.legs[1].getHipAngle();
        kneeA = cheetah.legs[1].getKneeAngle();
    }

    @Override
    protected void shutdown() {
    }
    
    boolean leg2values = true;
    int leg2max = 120;
    int leg2min = 20;
    int leg2counter = leg2min;

    @Override
    protected void controlLoop() {
        /*
        
         endPt = new Vector2(legs[1].getEndpointX(), legs[1].getEndpointY());
         circ2 = new Circle(endPt, SimpleLeg.L2[1]);
        
         CircleCircleIntersection i = new CircleCircleIntersection(home, circ2);
        
         // Inverse hip angle
         // Problem when knee is fully extended, NPE (?!)
         double angle = 180-Math.toDegrees(Math.acos(-i.intersectionPoint1.x/SimpleLeg.L1[1]));
        
         // Knee joint point
         //System.out.println(i.intersectionPoint1.x + ":" + i.intersectionPoint1.y);
        
         // Triangle distances to calculate inverse knee angle
         double x2 = endPt.x - i.intersectionPoint1.x;
         double y2 = endPt.y - i.intersectionPoint1.y;
        
         // inverse knee angle
         double angle2 = -Math.toDegrees(Math.asin(y2/SimpleLeg.L2[1]))+angle;
         //System.out.println(legs[1].getKneeAngle() + ":" + angle2);
         //System.out.println(x2 + ":" + y2);
                
         calcArc(i.intersectionPoint1.x, i.intersectionPoint1.y, angle2);
        
         */
        //KineData d0 = IKine.calculateKneeLoc(legs[0].getLegID(), legs[0].getEndpointX(), legs[0].getEndpointY());
        //KineData d1 = IKine.calculateKneeLoc(legs[1].getLegID(), legs[1].getEndpointX(), legs[1].getEndpointY());
        //KineData d2 = IKine.calculateKneeLoc(legs[2].getLegID(), legs[2].getEndpointX(), legs[2].getEndpointY());
        //KineData d3 = IKine.calculateKneeLoc(legs[3].getLegID(), legs[3].getEndpointX(), legs[3].getEndpointY());
        //System.out.println(String.format("%.2f",legs[0].getEndpointX()) + ":" + String.format("%.2f",legs[0].getEndpointY()));

        
        
         oneIF:
         if(leg2values){
         if(leg2counter >= leg2max){
         leg2values = false;
         break oneIF;
         }
         System.out.println(cheetah.legs[3].getRawHipAngle() + " " + cheetah.legs[3].getHipAngle() + " " + leg2counter);
         MotorValues v3 = IKine.motorsFromAngles(LEG_IDS[3], leg2counter, 45);
         if(v3.isValid()){
         cheetah.legs[3].setPositions(v3.getFlexorAngle(), v3.getExtensorAngle());
         leg2counter++;
         }
         //try{
         //    Thread.sleep(1500);
         //}catch(InterruptedException ie){}
         }
        
         twoIF:
         if(!leg2values){
         if(leg2counter <= leg2min){
         leg2values = true;
         break twoIF;
         }
         System.out.println(cheetah.legs[3].getRawHipAngle() + " " + cheetah.legs[3].getHipAngle() + " " + leg2counter);
         MotorValues v3 = IKine.motorsFromAngles(LEG_IDS[3], leg2counter, 45);
         if(v3.isValid()){
         cheetah.legs[3].setPositions(v3.getFlexorAngle(), v3.getExtensorAngle());
         leg2counter--;
         }
         //try{
         //    Thread.sleep(1500);
         //}catch(InterruptedException ie){}
         }
        /*
        
         KineData d0, d1, d2, d3;
        
         d1 = IKine.calculateKneeLoc(legs[1].getLegID(), legs[1].getEndpointX(), legs[1].getEndpointY());
        
        
         double flex, extend;
         if(crap){
         testValues = IKine.motorsFromAngles(legs[1].getLegID(), legs[1].getHipID(), legs[1].getKneeID(), 70, kneeA);
         //d1 = IKine.calculateKneeLoc(legs[1].getLegID(), crapPoint.getX()+15, crapPoint.getY());
         crap = false;
         }else{
         //d1 = IKine.calculateKneeLoc(legs[1].getLegID(), crapPoint.getX()-15, crapPoint.getY());
         //testValues = IKine.motorsFromAngles(legs[1].getLegID(), legs[1].getHipID(), legs[1].getKneeID(), hipA, kneeA);
         testValues = IKine.motorsFromAngles(legs[1].getLegID(), legs[1].getHipID(), legs[1].getKneeID(), hipA, kneeA);
         crap = true;
         }
        
         System.out.println("Current angle(kine calculated): " + d1.extensorAngle + ":" + d1.flexorAngle);
         System.out.println("Current angle(epos calculated): " + (d1.extensorAngle-angleOffset[legs[1].getExtensorID()-1]) + ":" + (d1.flexorAngle-angleOffset[legs[1].getFlexorID()-1]));
         System.out.println("Current angle(epos real-value): " + legs[1].getExtensorRotation() + ":" + legs[1].getFlexorRotation());
         if(testValues.isValid()){
         legs[1].setPositions(testValues.getFlexorAngle(), testValues.getExtensorAngle());
         }
         //System.out.println("Target angle (kine calculated): " + testValues.getExtensorAngle()+ ":" + testValues.getFlexorAngle());
         //System.out.println("Target angle (epos calculated): " + (angleOffset[legs[1].getExtensorID()-1]+testValues.getExtensorAngle()) + ":" + (angleOffset[legs[1].getFlexorID()-1]+testValues.getFlexorAngle()));
         System.out.println();
        
         */
        //System.out.println("Target angle before correction (kine): " + testValues.getExtensorAngle() + ":" + testValues.getFlexorAngle());
        //flex = testValues.getFlexorAngle()-angleOffset[legs[1].getFlexorID()-1];
        //extend = testValues.getExtensorAngle()-angleOffset[legs[1].getExtensorID()-1];
        //System.out.println("Target angle before correction (epos): " + extend + ":" + flex);
        //flex = d1.flexorAngle-angleOffset[legs[1].getFlexorID()-1];
        //extend = d1.extensorAngle-angleOffset[legs[1].getExtensorID()-1];
        //System.out.println("Current angle: " + legs[1].getExtensorRotation() + ":" + legs[1].getFlexorRotation());
        //System.out.println("Target angle: "  + extend + ":" + flex);
        //System.out.println();
    }

    @Override
    public void errorReceived(int id, int type, String message) {
    }
}
