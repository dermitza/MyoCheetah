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
package ch.dermitza.myocheetah.ui;

import ch.dermitza.myocheetah.CheetahIF;
import ch.dermitza.myocheetah.canopen.CheetahDataListener;
import ch.dermitza.myocheetah.leg.LegIF;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class RawDataPanel implements CheetahDataListener, ActionListener {

    private JPanel panel;

    private JCheckBox rawCheckBox;

    private JCheckBox flCheckBox;
    private JCheckBox frCheckBox;
    private JCheckBox rlCheckBox;
    private JCheckBox rrCheckBox;

    private Chart2DPanel posChart;
    private Chart2DPanel speedChart;
    private Chart2DPanel ampChart;
    private Chart2DPanel ampAvgChart;
    private Chart2DPanel digitalInputChart;
    private Chart2DPanel angleChart;

    private ITrace2D[] motorPositions;
    private ITrace2D[] motorSpeeds;
    private ITrace2D[] motorCurrents;
    private ITrace2D[] motorCurrentsAvg;
    private ITrace2D[] digitalInputs;
    private ITrace2D[] jointAngles;

    private boolean[] selectedLegs;

    private int samples = 500;
    public static final int SENSORS_NUM = 8;
    public static final String[] MOT_SENSOR_NAMES = {
        "FL_FLEXOR",
        "FL_EXTENSOR",
        "FR_FLEXOR",
        "FR_EXTENSOR",
        "RL_FLEXOR",
        "RL_EXTENSOR",
        "RR_FLEXOR",
        "RR_EXTENSOR"};
    public static final String[] JOINT_SENSOR_NAMES = {
        "FL_HIP",
        "FL_KNEE",
        "FR_HIP",
        "FR_KNEE",
        "RL_HIP",
        "RL_KNEE",
        "RR_HIP",
        "RR_KNEE"};

    public static final Color[] PLOT_COLORS = {
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.ORANGE,
        Color.PINK,
        Color.GRAY,
        Color.BLACK,
        Color.DARK_GRAY
    };

    private boolean rawData = false;

    private void initComponents() {

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Leg Data Charts"));

        JPanel abovePanel = new JPanel();
        abovePanel.setLayout(new BoxLayout(abovePanel, BoxLayout.X_AXIS));

        rawCheckBox = new JCheckBox("Raw data");
        rawCheckBox.setActionCommand("rawCheckBox");
        rawCheckBox.addActionListener(this);

        JPanel legSelectionPanel = new JPanel();
        legSelectionPanel.setBorder(new TitledBorder("Leg selection"));
        legSelectionPanel.setLayout(new BoxLayout(legSelectionPanel, BoxLayout.X_AXIS));

        selectedLegs = new boolean[4];
        selectedLegs[0] = true;
        selectedLegs[1] = true;
        selectedLegs[2] = true;
        selectedLegs[3] = true;

        flCheckBox = new JCheckBox(LegIF.LEG_NAMES[LegIF.LEG_FL]);
        flCheckBox.setSelected(true);
        flCheckBox.setActionCommand("flCheckBox");
        flCheckBox.addActionListener(this);

        frCheckBox = new JCheckBox(LegIF.LEG_NAMES[LegIF.LEG_FR]);
        frCheckBox.setSelected(true);
        frCheckBox.setActionCommand("frCheckBox");
        frCheckBox.addActionListener(this);

        rlCheckBox = new JCheckBox(LegIF.LEG_NAMES[LegIF.LEG_RL]);
        rlCheckBox.setSelected(true);
        rlCheckBox.setActionCommand("rlCheckBox");
        rlCheckBox.addActionListener(this);

        rrCheckBox = new JCheckBox(LegIF.LEG_NAMES[LegIF.LEG_RR]);
        rrCheckBox.setSelected(true);
        rrCheckBox.setActionCommand("rrCheckBox");
        rrCheckBox.addActionListener(this);

        legSelectionPanel.add(flCheckBox);
        legSelectionPanel.add(frCheckBox);
        legSelectionPanel.add(rlCheckBox);
        legSelectionPanel.add(rrCheckBox);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        posChart = new Chart2DPanel(samples);
        speedChart = new Chart2DPanel(samples);
        ampChart = new Chart2DPanel(samples);
        ampAvgChart = new Chart2DPanel(samples);
        digitalInputChart = new Chart2DPanel(samples);
        angleChart = new Chart2DPanel(samples);

        // do any chartPanel initializations AFTER we get the panel,
        // such that values are initialized, otherwise we will get a
        // nullpointer.
        topPanel.add(posChart.getPanel());
        topPanel.add(speedChart.getPanel());
        middlePanel.add(ampChart.getPanel());
        middlePanel.add(angleChart.getPanel());
        bottomPanel.add(ampAvgChart.getPanel());
        bottomPanel.add(digitalInputChart.getPanel());

        posChart.setTitle("Position");
        speedChart.setTitle("Speed");
        ampChart.setTitle("Current");
        ampAvgChart.setTitle("Average Current (50Hz)");
        digitalInputChart.setTitle("Digital Inputs");
        angleChart.setTitle("Angle");

        posChart.setYAxisTitle("Degrees");
        posChart.setXAxisTitle("Time (s)");
        speedChart.setYAxisTitle("RPM");
        speedChart.setXAxisTitle("Time (s)");
        ampChart.setYAxisTitle("mA");
        ampChart.setXAxisTitle("Time (s)");
        ampAvgChart.setYAxisTitle("mA");
        ampAvgChart.setXAxisTitle("Time (s)");
        digitalInputChart.setXAxisTitle("Time (s)");
        angleChart.setYAxisTitle("Raw data (12bit)");
        angleChart.setXAxisTitle("Time (s)");

        initTraces();

        abovePanel.add(rawCheckBox);
        abovePanel.add(legSelectionPanel);

        panel.add(abovePanel);
        panel.add(topPanel);
        panel.add(middlePanel);
        panel.add(bottomPanel);
    }

    public void initTraces() {
        motorPositions = new ITrace2D[SENSORS_NUM];
        motorSpeeds = new ITrace2D[SENSORS_NUM];
        motorCurrents = new ITrace2D[SENSORS_NUM];
        motorCurrentsAvg = new ITrace2D[SENSORS_NUM];
        digitalInputs = new ITrace2D[SENSORS_NUM];
        jointAngles = new ITrace2D[SENSORS_NUM];

        for (int i = 0; i < SENSORS_NUM; i++) {
            motorPositions[i] = new Trace2DLtd(samples, MOT_SENSOR_NAMES[i]);
            motorSpeeds[i] = new Trace2DLtd(samples, MOT_SENSOR_NAMES[i]);
            motorCurrents[i] = new Trace2DLtd(samples, MOT_SENSOR_NAMES[i]);
            motorCurrentsAvg[i] = new Trace2DLtd(samples, MOT_SENSOR_NAMES[i]);
            digitalInputs[i] = new Trace2DLtd(samples, MOT_SENSOR_NAMES[i]);
            jointAngles[i] = new Trace2DLtd(samples, JOINT_SENSOR_NAMES[i]);

            motorPositions[i].setColor(PLOT_COLORS[i]);
            motorSpeeds[i].setColor(PLOT_COLORS[i]);
            motorCurrents[i].setColor(PLOT_COLORS[i]);
            motorCurrentsAvg[i].setColor(PLOT_COLORS[i]);
            digitalInputs[i].setColor(PLOT_COLORS[i]);
            jointAngles[i].setColor(PLOT_COLORS[i]);

            posChart.getChart().addTrace(motorPositions[i]);
            speedChart.getChart().addTrace(motorSpeeds[i]);
            ampChart.getChart().addTrace(motorCurrents[i]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[i]);
            digitalInputChart.getChart().addTrace(digitalInputs[i]);
            angleChart.getChart().addTrace(jointAngles[i]);
        }
    }

    private void removeAllTraces() {
        posChart.getChart().removeAllTraces();
        speedChart.getChart().removeAllTraces();
        ampChart.getChart().removeAllTraces();
        ampAvgChart.getChart().removeAllTraces();
        digitalInputChart.getChart().removeAllTraces();
        angleChart.getChart().removeAllTraces();
    }

    private void updateLegSelection() {
        clearTraces();
        removeAllTraces();

        if (selectedLegs[0]) {
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_FLEXOR_FL - 1]);
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_EXTENSOR_FL - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_FLEXOR_FL - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_EXTENSOR_FL - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_FLEXOR_FL - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_EXTENSOR_FL - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_FLEXOR_FL - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_EXTENSOR_FL - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_FLEXOR_FL - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_EXTENSOR_FL - 1]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_FL_HIP]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_FL_KNEE]);
        }

        if (selectedLegs[1]) {
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_FLEXOR_FR - 1]);
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_EXTENSOR_FR - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_FLEXOR_FR - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_EXTENSOR_FR - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_FLEXOR_FR - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_EXTENSOR_FR - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_FLEXOR_FR - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_EXTENSOR_FR - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_FLEXOR_FR - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_EXTENSOR_FR - 1]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_FR_HIP]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_FR_KNEE]);
        }

        if (selectedLegs[2]) {
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_FLEXOR_RL - 1]);
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_EXTENSOR_RL - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_FLEXOR_RL - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_EXTENSOR_RL - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_FLEXOR_RL - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_EXTENSOR_RL - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_FLEXOR_RL - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_EXTENSOR_RL - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_FLEXOR_RL - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_EXTENSOR_RL - 1]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_RL_HIP]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_RL_KNEE]);
        }

        if (selectedLegs[3]) {
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_FLEXOR_RR - 1]);
            posChart.getChart().addTrace(motorPositions[CheetahIF.ID_EXTENSOR_RR - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_FLEXOR_RR - 1]);
            speedChart.getChart().addTrace(motorSpeeds[CheetahIF.ID_EXTENSOR_RR - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_FLEXOR_RR - 1]);
            ampChart.getChart().addTrace(motorCurrents[CheetahIF.ID_EXTENSOR_RR - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_FLEXOR_RR - 1]);
            ampAvgChart.getChart().addTrace(motorCurrentsAvg[CheetahIF.ID_EXTENSOR_RR - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_FLEXOR_RR - 1]);
            digitalInputChart.getChart().addTrace(digitalInputs[CheetahIF.ID_EXTENSOR_RR - 1]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_RR_HIP]);
            angleChart.getChart().addTrace(jointAngles[CheetahIF.ID_JOINT_RR_KNEE]);
        }

    }

    public void clearTraces() {
        for (int i = 0; i < SENSORS_NUM; i++) {
            motorPositions[i].removeAllPoints();
            motorSpeeds[i].removeAllPoints();
            motorCurrents[i].removeAllPoints();
            motorCurrentsAvg[i].removeAllPoints();
            digitalInputs[i].removeAllPoints();
            jointAngles[i].removeAllPoints();
        }
    }

    public JPanel getPanel() {
        if (panel == null) {
            initComponents();
        }
        return panel;
    }

    /*
    @Override
    public void dataArrived(int dataType, int sensorNum, double timestamp, int value) {

        switch (dataType) {
            case DATA_MOT_POS:

                motorPositions[sensorNum].addPoint(timestamp, value);
                break;
            case DATA_MOT_SPD:

                motorSpeeds[sensorNum].addPoint(timestamp, value);
                break;
            case DATA_MOT_CURR:
                motorCurrents[sensorNum].addPoint(timestamp, value);
                break;
            case DATA_MOT_CURR_AVG:
                motorCurrentsAvg[sensorNum].addPoint(timestamp, value);
                break;
            case DATA_MOT_DIN:
                digitalInputs[sensorNum].addPoint(timestamp, value);
                break;
        }
    }
    */

    @Override
    public void dataArrived(int id, double time, int data) {
        // Only joint angles here
        jointAngles[id].addPoint(time, data);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if (command.equals(flCheckBox.getActionCommand())) {
            selectedLegs[0] = flCheckBox.isSelected();
            selectedLegs[1] = frCheckBox.isSelected();
            selectedLegs[2] = rlCheckBox.isSelected();
            selectedLegs[3] = rrCheckBox.isSelected();
            updateLegSelection();
        } else if (command.equals(rawCheckBox.getActionCommand())) {
            rawData = rawCheckBox.isSelected();
            if(rawData){
                posChart.setYAxisTitle("QC");
                speedChart.setYAxisTitle("QC/sec");
            } else {
                posChart.setYAxisTitle("Degrees");
                speedChart.setYAxisTitle("RPM");
            }
        }
    }

    @Override
    public void dataPDO1(int sensorNum, double timestamp, int pos, int spd) {
        posChart.setValue(timestamp);
        speedChart.setValue(timestamp);
        if(rawData){
            motorPositions[sensorNum].addPoint(timestamp, pos);
            motorSpeeds[sensorNum].addPoint(timestamp, spd);
        }else{
            motorPositions[sensorNum].addPoint(timestamp, (double) pos * LegIF.QC_DEG);
            // 60/360 = 6 to convert to RPM
            motorSpeeds[sensorNum].addPoint(timestamp, (double) spd * LegIF.QC_DEG / 6d);
        }
    }

    @Override
    public void dataPDO2(int sensorNum, double timestamp, int curr, int currAvg, int din) {
        motorCurrents[sensorNum].addPoint(timestamp, curr);
        motorCurrentsAvg[sensorNum].addPoint(timestamp, currAvg);
        digitalInputs[sensorNum].addPoint(timestamp, din);
    }

}
