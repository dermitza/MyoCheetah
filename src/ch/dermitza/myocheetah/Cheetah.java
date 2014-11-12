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
package ch.dermitza.myocheetah;

import ch.dermitza.myocheetah.canopen.SDOTransceiver;
import ch.dermitza.myocheetah.data.DataManager;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_FL;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_FR;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_RL;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_RR;
import ch.dermitza.myocheetah.leg.SimpleLeg;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class Cheetah implements CheetahIF{
    
    public final SimpleLeg[] legs;
    private boolean calibrated = false;
    
    public Cheetah(DataManager dataManager){
        legs = new SimpleLeg[LEGS_NUM];
        legs[LEG_FL] = new SimpleLeg(dataManager, LEG_FL);
        legs[LEG_FR] = new SimpleLeg(dataManager, LEG_FR);
        legs[LEG_RL] = new SimpleLeg(dataManager, LEG_RL);
        legs[LEG_RR] = new SimpleLeg(dataManager, LEG_RR);
    }
    
    public boolean isCalibrated(){
        if(calibrated){
            return calibrated;
        }else{
            if(legs[LEG_FL].isCalibrated()
                    && legs[LEG_FR].isCalibrated()
                    && legs[LEG_RL].isCalibrated()
                    && legs[LEG_RR].isCalibrated()){
                this.calibrated = true;
            }
            return calibrated;
        }
    }
    

    public void setTransceiver(SDOTransceiver transceiver){
        legs[LEG_FL].setTransceiver(transceiver);
        legs[LEG_FR].setTransceiver(transceiver);
        legs[LEG_RL].setTransceiver(transceiver);
        legs[LEG_RR].setTransceiver(transceiver);
    }
    
    public int enableAll(){
        int ret = -1;
        ret += legs[LEG_FL].enable();
        ret += legs[LEG_FR].enable();
        ret += legs[LEG_RL].enable();
        ret += legs[LEG_RR].enable();
        
        return ret;
    }
    
    public void disableAll(){
        legs[LEG_FL].disable();
        legs[LEG_FR].disable();
        legs[LEG_RL].disable();
        legs[LEG_RR].disable();
    }
}
