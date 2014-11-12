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

import ch.dermitza.myocheetah.util.UIUtils;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.util.Range;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * eligible for generalization
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class Chart2DPanel implements ActionListener {

    private static DecimalFormat twoDForm = new DecimalFormat("#.##");
    private Chart2D chart;
    private JPanel panel;
    private JCheckBox xGridBox;
    private JCheckBox yGridBox;
    private JCheckBox showBox;
    private JLabel valueLabel;
    private JLabel freqLabel;
    private TitledBorder titledBorder;
    private RangePolicyFixedViewport xRange;
    private RangePolicyFixedViewport yRange;
    private String valueText = null;
    private long startTime = 0;
    private int sampleCnt = 0;
    private static final int SAMPLES = 100;
    private int samples;
    private double frequency = 1500;

    public Chart2DPanel(){
        this(SAMPLES);
    }

    public Chart2DPanel(int samples){
        this.samples = samples;
        xRange = new RangePolicyFixedViewport(new Range(0, 5*samples));
        yRange = new RangePolicyFixedViewport(new Range(0, 300));
    }

    private void initComponents(){
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        titledBorder = new TitledBorder("DEFAULT CHART");
        panel.setBorder(titledBorder);
        
        chart = new Chart2D();
        chart.setVisible(false);
        UIUtils.setAllSizes(chart, new Dimension(600, 240));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        JPanel showPanel = new JPanel();
        showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.X_AXIS));

        showBox = new JCheckBox("Show");
        showBox.setActionCommand("showBox");
        showBox.addActionListener(this);

        showPanel.add(showBox);
        
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.X_AXIS));
        gridPanel.setBorder(new TitledBorder("Paint Grids"));

        xGridBox = new JCheckBox("X Grid");
        xGridBox.setActionCommand("xGridBox");
        xGridBox.addActionListener(this);

        yGridBox = new JCheckBox("Y Grid");
        yGridBox.setActionCommand("yGridBox");
        yGridBox.addActionListener(this);

        gridPanel.add(xGridBox);
        gridPanel.add(yGridBox);

        valueLabel = new JLabel("    ");
        valueLabel.setBorder(new TitledBorder("Value(s)"));
        UIUtils.setAllSizes(valueLabel, new Dimension(200, 40));

        freqLabel = new JLabel("    ");
        freqLabel.setBorder(new TitledBorder("Frequency (Hz)"));
        UIUtils.setAllSizes(freqLabel, new Dimension(120, 40));


        bottomPanel.add(showPanel);
        bottomPanel.add(gridPanel);
        bottomPanel.add(valueLabel);
        bottomPanel.add(freqLabel);
        bottomPanel.add(Box.createHorizontalGlue());

        panel.add(chart);
        panel.add(bottomPanel);
    }

    /**
     * Set the title of this chartpanel
     * @param title
     */
    public void setTitle(String title){
        titledBorder.setTitle(title);
        panel.revalidate();
    }

    /**
     * Set the value to be displayed in this chartpanel. use this function for
     * single values.
     * @param value
     */
    public void setValue(double value){
        valueText = "" + twoDForm.format(value);
        valueLabel.setText(valueText);
        checkFreq();
    }

    /**
     * Set the value to be displayed in this chartpanel. use this function for
     * multiple values
     * @param values
     */
    public void setValues(double[] values){
        valueText = "";
        for(int i=0; i < values.length; i++){
            valueText += twoDForm.format(values[i]) + "  ";
        }
        valueLabel.setText(valueText);
        checkFreq();
    }
    
    public void setIntValues(int[] values){
        valueText = "";
        for(int i=0; i < values.length; i++){
            valueText += twoDForm.format(values[i]) + "  ";
        }
        valueLabel.setText(valueText);
        checkFreq();
    }

    private void checkFreq(){
        // one sample already added
        sampleCnt++;
        if(sampleCnt >= samples){
            long elapsed = (System.nanoTime()-startTime)/1000000; // in msec
            //System.out.println(elapsed);
            if(elapsed > 0){ // cannot divide by zero
                frequency = (double)samples*1000/elapsed;
            }
            freqLabel.setText("" + twoDForm.format(frequency));
            sampleCnt = 0;
            startTime = System.nanoTime();
        }
    }

    /**
     * Set the title of the x axis
     * @param xTitle
     */
    public void setXAxisTitle(String xTitle){
        chart.getAxisX().setAxisTitle(new AxisTitle(xTitle));
    }

    /**
     * Set the title of the Y axis
     * @param yTitle
     */
    public void setYAxisTitle(String yTitle){
        chart.getAxisY().setAxisTitle(new AxisTitle(yTitle));
    }

    /**
     * Set the range of x values
     * @param max
     */
    public void setXRange(double max){
        xRange = new RangePolicyFixedViewport(new Range(0, max));
        chart.getAxisX().setRangePolicy(xRange);
    }

    /**
     * Set the range of Y values
     * @param min
     * @param max
     */
    public void setYRange(double min, double max){
        yRange = new RangePolicyFixedViewport(new Range(min, max));
        chart.getAxisY().setRangePolicy(yRange);
    }

    public void destroyChart(){
        chart.destroy();
    }

    public JPanel getPanel(){
        if(panel == null){
            initComponents();
        }
        return panel;
    }

    public Chart2D getChart(){
        return this.chart;
    }

    public double getFrequency(){
        return this.frequency;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String com = e.getActionCommand();

        if(com.equals(xGridBox.getActionCommand())){
            chart.getAxisX().setPaintGrid(xGridBox.isSelected());
        } else if(com.equals(yGridBox.getActionCommand())){
            chart.getAxisY().setPaintGrid(yGridBox.isSelected());
        } else if(com.equals(showBox.getActionCommand())){
            chart.setVisible(showBox.isSelected());
            panel.validate();
            JFrame f = UIUtils.getTopFrame(panel);
            if(f != null){
                f.pack();
            }
        }
    }

}