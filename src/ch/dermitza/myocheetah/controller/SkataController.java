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
 * @author exe
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
