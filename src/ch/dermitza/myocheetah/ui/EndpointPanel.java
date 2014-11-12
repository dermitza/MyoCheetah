package ch.dermitza.myocheetah.ui;

import ch.dermitza.myocheetah.leg.LegIF;
import ch.dermitza.myocheetah.util.geometry.Point2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author exe
 */
public class EndpointPanel{
    
    private JPanel panel;
    // Graphs for each leg
    private Chart2DPanel flChart;
    private Chart2DPanel frChart;
    private Chart2DPanel rlChart;
    private Chart2DPanel rrChart;
    
    private ITrace2D[] realEndpoints;
    private ITrace2D[] generatedEndpoints;
    
    private int samples = 500;
    public static final int SENSORS_NUM = 4;
    
    private void initComponents(){
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Leg Endpoint Data"));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        
        flChart = new Chart2DPanel(samples);
        frChart = new Chart2DPanel(samples);
        rlChart = new Chart2DPanel(samples);
        rrChart = new Chart2DPanel(samples);
        
        // do any chartPanel initializations AFTER we get the panel,
        // such that values are initialized, otherwise we will get a
        // nullpointer.
        
        topPanel.add(flChart.getPanel());
        topPanel.add(frChart.getPanel());
        bottomPanel.add(rlChart.getPanel());
        bottomPanel.add(rrChart.getPanel());
        
        flChart.setTitle(LegIF.LEG_NAMES[LegIF.LEG_FL]);
        frChart.setTitle(LegIF.LEG_NAMES[LegIF.LEG_FR]);
        rlChart.setTitle(LegIF.LEG_NAMES[LegIF.LEG_RL]);
        rrChart.setTitle(LegIF.LEG_NAMES[LegIF.LEG_RR]);
        
        flChart.setYAxisTitle("Y Position (mm)");
        frChart.setYAxisTitle("Y Position (mm)");
        rlChart.setYAxisTitle("Y Position (mm)");
        rrChart.setYAxisTitle("Y Position (mm)");
        
        flChart.setXAxisTitle("X Position (mm)");
        frChart.setXAxisTitle("X Position (mm)");
        rlChart.setXAxisTitle("X Position (mm)");
        rrChart.setXAxisTitle("X Position (mm)");
        
        initTraces();
        
        panel.add(topPanel);
        panel.add(bottomPanel);
    }
    
    public void initTraces(){
        generatedEndpoints = new ITrace2D[SENSORS_NUM];
        realEndpoints = new ITrace2D[SENSORS_NUM];
        
        for(int i=0; i < SENSORS_NUM; i++){
            generatedEndpoints[i] = new Trace2DLtd(samples, LegIF.LEG_NAMES[i]);
            realEndpoints[i] = new Trace2DLtd(samples, LegIF.LEG_NAMES[i]);
        }
        
        flChart.getChart().addTrace(generatedEndpoints[0]);
        frChart.getChart().addTrace(generatedEndpoints[1]);
        rlChart.getChart().addTrace(generatedEndpoints[2]);
        rrChart.getChart().addTrace(generatedEndpoints[3]);
        
        flChart.getChart().addTrace(realEndpoints[0]);
        frChart.getChart().addTrace(realEndpoints[1]);
        rlChart.getChart().addTrace(realEndpoints[2]);
        rrChart.getChart().addTrace(realEndpoints[3]);
        
    }
    
    public void clearTraces(){
        for(int i=0; i < SENSORS_NUM; i++){
            realEndpoints[i].removeAllPoints();
            generatedEndpoints[i].removeAllPoints();
        }
    }
    
    public JPanel getPanel() {
        if (panel == null) {
            initComponents();
        }
        return panel;
    }
    
    public void addGeneratedEndpointData(int legID, Point2D[] trajectory){
        generatedEndpoints[legID].removeAllPoints();
        switch(legID){
            case LegIF.LEG_FL:
                flChart.getChart().removeTrace(generatedEndpoints[legID]);
                generatedEndpoints[legID] = new Trace2DLtd(trajectory.length, LegIF.LEG_NAMES[LegIF.LEG_FL]);
                flChart.getChart().addTrace(generatedEndpoints[legID]);
                break;
            case LegIF.LEG_FR:
                frChart.getChart().removeTrace(generatedEndpoints[legID]);
                generatedEndpoints[legID] = new Trace2DLtd(trajectory.length, LegIF.LEG_NAMES[LegIF.LEG_FR]);
                frChart.getChart().addTrace(generatedEndpoints[legID]);
                break;
            case LegIF.LEG_RL:
                rlChart.getChart().removeTrace(generatedEndpoints[legID]);
                generatedEndpoints[legID] = new Trace2DLtd(trajectory.length, LegIF.LEG_NAMES[LegIF.LEG_RL]);
                rlChart.getChart().addTrace(generatedEndpoints[legID]);
                break;
            case LegIF.LEG_RR:
                rrChart.getChart().removeTrace(generatedEndpoints[legID]);
                generatedEndpoints[legID] = new Trace2DLtd(trajectory.length, LegIF.LEG_NAMES[LegIF.LEG_RR]);
                rrChart.getChart().addTrace(generatedEndpoints[legID]);
                break;
        }
        for(int i=0; i < trajectory.length; i++){
            generatedEndpoints[legID].addPoint(trajectory[i].getX(), trajectory[i].getY());
        }
    }
    
    public void addMeasuredEndpointData(int legID, Point2D endpoint){
        realEndpoints[legID].addPoint(endpoint.getX(), endpoint.getY());
        switch(legID){
            case LegIF.LEG_FL:
                flChart.setValues(new double[]{endpoint.getX(), endpoint.getY()});
                //flChart.setTitle("Front Left Leg");
                break;
            case LegIF.LEG_FR:
                frChart.setValues(new double[]{endpoint.getX(), endpoint.getY()});
                //frChart.setTitle("Front Right Leg");

                break;
            case LegIF.LEG_RL:
                rlChart.setValues(new double[]{endpoint.getX(), endpoint.getY()});
                //rlChart.setTitle("Rear Left Leg");

                break;
            case LegIF.LEG_RR:
                rrChart.setValues(new double[]{endpoint.getX(), endpoint.getY()});
                //rrChart.setTitle("Rear Right Leg");

                break;
        }
    }
}
