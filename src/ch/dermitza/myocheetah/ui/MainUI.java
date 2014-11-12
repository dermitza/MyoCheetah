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

import ch.dermitza.myocheetah.StatusListener;
import ch.dermitza.myocheetah.UIController;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class MainUI implements WindowListener{
    
    private JFrame mainFrame;
    //private JFrame singleRawDataFrame;
    private JFrame rawDataFrame;
    private JFrame dataFrame;
    private JFrame endpointFrame;
    private RawDataPanel rawDataPanel;
    private DataPanel dataPanel;
    private EndpointPanel endpointPanel;
    private ConfigPanel configPanel;

    private JPanel panel;
    private final UIController controller;
    
    public MainUI(UIController controller){
        this.controller = controller;
    }
    
    //public SingleRawDataPanel getSingleRawDataPanel(){
    //    return this.singleRawDataPanel;
    //}
    
    public RawDataPanel getRawDataPanel(){
        return this.rawDataPanel;
    }
    
    public DataPanel getDataPanel(){
        return this.dataPanel;
    }
    
    public EndpointPanel getEndpointPanel(){
        return this.endpointPanel;
    }
    
    private void initMainFrame() {
        
        mainFrame = new JFrame("MyoCheetah Control Interface v0.05");
        mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(this);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));    
        
        configPanel = new ConfigPanel();
        configPanel.setController(controller);

        panel.add(topPanel);
        panel.add(configPanel.getPanel());
        
        mainFrame.add(panel);
        mainFrame.pack();
    }
    
    private void initRawDataFrame() {
        rawDataFrame = new JFrame("MyoCheetah Raw Data Viewer");
        rawDataFrame.setLayout(new BoxLayout(rawDataFrame.getContentPane(), BoxLayout.Y_AXIS));
        rawDataFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        rawDataFrame.addWindowListener(this);
        
        rawDataPanel = new RawDataPanel();
        
        rawDataFrame.add(rawDataPanel.getPanel());

        rawDataFrame.pack();
    }
    
    /*
    private void initSingleRawDataFrame() {
        singleRawDataFrame = new JFrame("MyoCheetah Single Raw Data Viewer");
        singleRawDataFrame.setLayout(new BoxLayout(singleRawDataFrame.getContentPane(), BoxLayout.Y_AXIS));
        singleRawDataFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        singleRawDataFrame.addWindowListener(this);
        
        singleRawDataPanel = new SingleRawDataPanel();
        singleRawDataFrame.add(singleRawDataPanel.getPanel());

        singleRawDataFrame.pack();
    }
    */
    
    
    private void initDataFrame() {
        dataFrame = new JFrame("MyoCheetah Data Viewer");
        dataFrame.setLayout(new BoxLayout(dataFrame.getContentPane(), BoxLayout.Y_AXIS));
        dataFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dataFrame.addWindowListener(this);
        
        dataPanel = new DataPanel();
        
        dataFrame.add(dataPanel.getPanel());

        dataFrame.pack();
    }
    
    private void initEndpointFrame() {
        endpointFrame = new JFrame("MyoCheetah Endpoint Viewer");
        endpointFrame.setLayout(new BoxLayout(endpointFrame.getContentPane(), BoxLayout.Y_AXIS));
        endpointFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        endpointFrame.addWindowListener(this);
        
        endpointPanel = new EndpointPanel();
        
        endpointFrame.add(endpointPanel.getPanel());

        endpointFrame.pack();
    }
    
    public void showFrames(){
        if(mainFrame == null){
            initMainFrame();
        }
        
        //if(singleRawDataFrame == null){
        //    initSingleRawDataFrame();
        //}
        
        if(rawDataFrame == null){
            initRawDataFrame();
        }
        
        if(dataFrame == null){
            initDataFrame();
        }
        
        if(endpointFrame == null){
            initEndpointFrame();
        }
        
        mainFrame.setVisible(true);
        //singleRawDataFrame.setVisible(true);
        rawDataFrame.setVisible(true);
        dataFrame.setVisible(true);
        endpointFrame.setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosing(WindowEvent we) {
        if(controller.isConnected()){
            controller.requestOperation(StatusListener.REQUEST_DISCONNECT);
            while(controller.isConnected()){
                // busy wait until the controller has cleanly shut down
                try{
                    Thread.sleep(10);
                } catch(InterruptedException ie){
                    
                }
            }
            controller.removeAllListeners();
        }
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }

}
