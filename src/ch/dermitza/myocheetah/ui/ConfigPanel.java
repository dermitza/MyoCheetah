package ch.dermitza.myocheetah.ui;


import ch.dermitza.myocheetah.StatusListener;
import ch.dermitza.myocheetah.UIController;
import ch.dermitza.myocheetah.controller.ControllerIF;
import ch.dermitza.myocheetah.util.UIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author exe
 */
public class ConfigPanel implements ActionListener, StatusListener, ChangeListener {

    private JFrame paramsFrame;
    private ProfilePositionSetupPanel paramsPanel;
    
    private JPanel panel;
    private JButton connectButton;
    private JButton enableButton;
    private JButton loggingButton;
    private JButton parameterButton;
    private JButton controlButton;
    private JButton stopButton;
    //--------- Controller constrains
    private JCheckBox constrainCheckBox;
    private TitledBorder constrainBorder;
    private JSlider constrainSlider;
    //--------- Control programs
    private JComboBox<String> controllerComboBox;
    private UIController controller;
    
    private JTextField dataWriterPath;
    
    private boolean operational = false;
    
    private void initParamsFrame(){
        
        paramsPanel = new ProfilePositionSetupPanel();
        paramsFrame = new JFrame("Profile Position Parameters");
        paramsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        paramsFrame.add(paramsPanel.getPanel());
        paramsFrame.pack();
        paramsFrame.setVisible(false);
        
        paramsPanel.setController(controller);
    }

    private void initComponents() {
        initParamsFrame();

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new TitledBorder("Control Commands"));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder("Controller parameters"));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        
        constrainCheckBox = new JCheckBox("Constrain", true);
        constrainCheckBox.setActionCommand("constrainCheckBox");
        constrainCheckBox.addActionListener(this);
        constrainBorder = new TitledBorder("500Hz");
        constrainSlider = new JSlider(1, 1000, 2);
        constrainSlider.setBorder(constrainBorder);
        constrainSlider.addChangeListener(this);
        
        controllerComboBox = new JComboBox<>(ControllerIF.CONTROLLER_NAMES);
        controllerComboBox.setActionCommand("controllerComboBox");
        controllerComboBox.addActionListener(this);
         
        controlPanel.add(constrainCheckBox);
        controlPanel.add(constrainSlider);
        controlPanel.add(controllerComboBox);

        SpringLayout l = new SpringLayout();
        JPanel middlePanel = new JPanel();
        //middlePanel.setBorder(new TitledBorder("Control Commands"));
        middlePanel.setLayout(l);

        connectButton = new JButton("Connect");
        connectButton.setActionCommand("connButton");
        connectButton.setToolTipText("Connect to the robot");
        connectButton.addActionListener(this);

        enableButton = new JButton("Enable");
        enableButton.setActionCommand("enableButton");
        enableButton.setToolTipText("Enable the actuators");
        enableButton.addActionListener(this);
        enableButton.setEnabled(false);
        
        loggingButton = new JButton("Start Logging");
        loggingButton.setActionCommand("loggingButton");
        loggingButton.setToolTipText("Initialize logging of data");
        loggingButton.addActionListener(this);
        loggingButton.setEnabled(false);
        
        parameterButton = new JButton("Set parameters");
        parameterButton.setActionCommand("parameterButton");
        parameterButton.setToolTipText("Set profile position parameters");
        parameterButton.addActionListener(this);
        parameterButton.setEnabled(false);
        
        controlButton = new JButton("Start Controller");
        controlButton.setActionCommand("controlButton");
        controlButton.setToolTipText("Initialize the control loop");
        controlButton.addActionListener(this);
        controlButton.setEnabled(false);
        
        stopButton = new JButton("<html><center>EMERGENCY<br>STOP</center></html>");
        stopButton.setActionCommand("stopButton");
        stopButton.addActionListener(this);
        stopButton.setBackground(Color.RED);
        stopButton.setForeground(Color.WHITE);
        UIUtils.setAllSizes(stopButton, new Dimension(100, 100));
        stopButton.setToolTipText("Emergency stop");
        stopButton.setEnabled(false);

        middlePanel.add(connectButton);
        middlePanel.add(enableButton);
        middlePanel.add(loggingButton);
        middlePanel.add(controlButton);
        middlePanel.add(parameterButton);
        middlePanel.add(Box.createGlue());
        
        SpringUtilities.makeCompactGrid(middlePanel, 3, 2, 4, 4, 4, 4);
        
        buttonPanel.add(middlePanel);
        buttonPanel.add(stopButton);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        
        JLabel logoLabel = new JLabel(new ImageIcon("birlab.gif"));
        
        bottomPanel.add(logoLabel);

        panel.add(controlPanel);
        panel.add(buttonPanel);
        panel.add(bottomPanel);
    }

    public void setController(UIController controller) {
        this.controller = controller;
        controller.addStatusListener(this);
    }

    public JPanel getPanel() {
        if (panel == null) {
            initComponents();
        }
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        if(command.equals(stopButton.getActionCommand())){
            if(controller.isConnected()){
                controller.emergencyStop();
            }
        }else if (command.equals(connectButton.getActionCommand())) {
            if (controller.isConnected()) {
                controller.requestOperation(StatusListener.REQUEST_DISCONNECT);
            } else {
                controller.requestOperation(StatusListener.REQUEST_CONNECT);
            }
        } else if (command.equals(enableButton.getActionCommand())) {
            if (controller.isConnected()) {
                if (controller.isEnabled()) {
                    controller.requestOperation(StatusListener.REQUEST_DISABLE);
                } else {
                    System.out.println("Requesting enable");
                    controller.requestOperation(StatusListener.REQUEST_ENABLE);
                }
            } else {
            }
 
        } else if(command.equals(loggingButton.getActionCommand())){
            if(controller.isConnected()){
                if(controller.isLogging()){
                    controller.requestOperation(StatusListener.REQUEST_STOP_LOGGING);
                }else{
                    controller.requestOperation(StatusListener.REQUEST_START_LOGGING);
                }
            }
        } else if(command.equals(controlButton.getActionCommand())){
            
            if(controller.isConnected()){
                if(controller.isControlling()){
                    controller.requestOperation(StatusListener.REQUEST_STOP_CONTROLLING);
                }else{
                    controller.setWriterPath(".");
                    controller.requestOperation(StatusListener.REQUEST_START_CONTROLLING);
                }
            }
        } else if(command.equals(parameterButton.getActionCommand())){
            
            if(controller.isConnected()){
                paramsFrame.setVisible(true);
            }
        }else if(command.equals(constrainCheckBox.getActionCommand())){
            if(constrainCheckBox.isSelected()){
                constrainSlider.setEnabled(true);
            }else{
                constrainSlider.setEnabled(false);
            }
            controller.setConstrainParams(constrainCheckBox.isSelected(), constrainSlider.getValue());
        }else if(command.equals(controllerComboBox.getActionCommand())){
            controller.setControllerIdx(controllerComboBox.getSelectedIndex());
        }
    }

    @Override
    public void statusChanged(int status) {
        switch (status) {
            case StatusListener.STATUS_DISCONNECTED:
                connectButton.setText("Connect");
                connectButton.setEnabled(true);
                enableButton.setText("Enable");
                enableButton.setEnabled(false);
                loggingButton.setText("Start Logging");
                loggingButton.setEnabled(false);
                controlButton.setText("Start Controller");
                controlButton.setEnabled(false);
                parameterButton.setEnabled(false);
                stopButton.setEnabled(false);
                paramsPanel.enableControls(false);
                break;
            case StatusListener.STATUS_CONNECTING:
                connectButton.setText("Connecting..");
                connectButton.setEnabled(false);
                break;
            case StatusListener.STATUS_DISCONNECTING:
                connectButton.setText("Disconnecting..");
                connectButton.setEnabled(false);
                break;
            case StatusListener.STATUS_CONNECTED:
                connectButton.setText("Disconnect");
                connectButton.setEnabled(true);
                enableButton.setEnabled(true);
                loggingButton.setEnabled(true);
                controlButton.setEnabled(true);
                parameterButton.setEnabled(true);
                stopButton.setEnabled(true);
                paramsPanel.enableControls(true);
                break;
            case StatusListener.STATUS_DISABLED:
                enableButton.setText("Enable");
                if(controller.isConnected()){
                    enableButton.setEnabled(true);
                }else{
                    enableButton.setEnabled(false);
                }
                break;
            case StatusListener.STATUS_ENABLING:
                enableButton.setText("Enabling..");
                enableButton.setEnabled(false);
                break;
            case StatusListener.STATUS_DISABLING:
                enableButton.setText("Disabling..");
                enableButton.setEnabled(false);
                break;
            case StatusListener.STATUS_ENABLED:
                enableButton.setText("Disable");
                if(controller.isConnected()){
                    enableButton.setEnabled(true);
                }else{
                    enableButton.setEnabled(false);
                }
                break;
            case StatusListener.STATUS_LOGGING_ENABLED:
                loggingButton.setText("Stop Logging");
                if(controller.isConnected()){
                    loggingButton.setEnabled(true);
                }else{
                    loggingButton.setEnabled(false);
                }
                break;
            case StatusListener.STATUS_LOGGING_ENABLING:
                loggingButton.setText("Starting Logging..");
                loggingButton.setEnabled(false);
                break;
            case StatusListener.STATUS_LOGGING_DISABLING:
                loggingButton.setText("Stopping Logging..");
                loggingButton.setEnabled(false);
                break;
            case StatusListener.STATUS_LOGGING_DISABLED:
                loggingButton.setText("Start Logging");
                if(controller.isConnected()){
                    loggingButton.setEnabled(true);
                }else{
                    loggingButton.setEnabled(false);
                }
                break;
            case StatusListener.STATUS_CONTROLLING_ENABLED:
                controlButton.setText("Stop Controller");
                if(controller.isConnected()){
                    controlButton.setEnabled(true);
                }else{
                    controlButton.setEnabled(false);
                }
                break;
            case StatusListener.STATUS_CONTROLLING_ENABLING:
                controlButton.setText("Starting Controller..");
                controlButton.setEnabled(false);
                break;
            case StatusListener.STATUS_CONTROLLING_DISABLING:
                controlButton.setText("Stopping Controller..");
                controlButton.setEnabled(false);
                break;
            case StatusListener.STATUS_CONTROLLING_DISABLED:
                controlButton.setText("Start Controller");
                if(controller.isConnected()){
                    controlButton.setEnabled(true);
                }else{
                    controlButton.setEnabled(false);
                }
                break;
        }
        JFrame f = UIUtils.getTopFrame(panel);
        if (f != null) {
            f.invalidate();
            f.pack();
        }
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        if(!constrainSlider.getValueIsAdjusting()){
            controller.setConstrainParams(constrainCheckBox.isSelected(), constrainSlider.getValue());
            double freq = 1000d/(double)constrainSlider.getValue();
            constrainBorder.setTitle(freq + "Hz");
        }
    }
}
