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
import static ch.dermitza.myocheetah.leg.LegIF.LEG_FL;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_FR;
import static ch.dermitza.myocheetah.leg.LegIF.LEG_RL;
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
public class SkataController extends AbstractController{
    
    public SkataController(EndpointPanel endpointPanel, DataPanel dataPanel,
            Cheetah cheetah, boolean constrain, long constrainPeriod) {
        super(endpointPanel, dataPanel, cheetah, constrain, constrainPeriod);
    }

    @Override
    protected void initController() {
    }

    @Override
    protected void shutdown() {
        endpointPanel.clearTraces();
    }

    @Override
    protected void controlLoop() {
        
        endpointPanel.addMeasuredEndpointData(LEG_FL, new Point2D(0, 0));
        endpointPanel.addMeasuredEndpointData(LEG_FR, new Point2D(0, 0));
        endpointPanel.addMeasuredEndpointData(LEG_RL, new Point2D(0, 0));
        endpointPanel.addMeasuredEndpointData(LEG_RR, new Point2D(0, 0));
        
    }

    @Override
    public void errorReceived(int id, int type, String message) {
    }
    
}
