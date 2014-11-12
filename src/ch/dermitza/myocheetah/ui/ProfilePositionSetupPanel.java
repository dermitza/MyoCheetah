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

import ch.dermitza.epos2.EPOS2;
import ch.dermitza.myocheetah.CheetahIF;
import ch.dermitza.myocheetah.StatusListener;
import ch.dermitza.myocheetah.UIController;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class ProfilePositionSetupPanel implements ActionListener, StatusListener {

    private JPanel panel;
    
    private JLabel eposLabel;
    private JComboBox<String> eposComboBox;
    private JLabel profileTypeLabel;
    private JComboBox<String> profileTypeComboBox;
    private JLabel velocityLabel;
    private JTextField velocityField;
    private JLabel accelerationLabel;
    private JTextField accelerationField;
    private JLabel decelerationLabel;
    private JTextField decelerationField;
    private JButton setButton;
    private JButton readButton;
    private JPanel bottomPanel;
    private JLabel statusLabel;
    private UIController controller;

    private void initComponents() {

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("EPOS2 Profile Position Parameters"));

        eposLabel = new JLabel("Controller");
        eposComboBox = new JComboBox<>(CheetahIF.CONTROLLER_NAMES);
        eposComboBox.setSelectedIndex(CheetahIF.CONTROLLER_INDICES[CheetahIF.ID_FLEXOR_FL]);
        eposComboBox.setActionCommand("controllerComboBox");
        eposComboBox.addActionListener(this);

        JPanel parameterPanel = new JPanel();
        SpringLayout l = new SpringLayout();
        parameterPanel.setLayout(l);
        
        profileTypeLabel = new JLabel("Profile type");
        profileTypeComboBox = new JComboBox<>(EPOS2.MOTION_PROFILES);

        velocityLabel = new JLabel("Profile velocity (rpm)");
        velocityField = new JTextField("0", 8);
        velocityField.setEditable(false);

        accelerationLabel = new JLabel("Profile acceleration (rpm/s)");
        accelerationField = new JTextField("0", 8);
        accelerationField.setEditable(false);

        decelerationLabel = new JLabel("Profile deceleration (rpm/s)");
        decelerationField = new JTextField("0", 8);
        decelerationField.setEditable(false);

        parameterPanel.add(eposLabel);
        parameterPanel.add(eposComboBox);
        parameterPanel.add(profileTypeLabel);
        parameterPanel.add(profileTypeComboBox);
        parameterPanel.add(velocityLabel);
        parameterPanel.add(velocityField);
        parameterPanel.add(accelerationLabel);
        parameterPanel.add(accelerationField);
        parameterPanel.add(decelerationLabel);
        parameterPanel.add(decelerationField);

        SpringUtilities.makeCompactGrid(parameterPanel, 5, 2, 4, 4, 4, 4);

        JPanel buttonPanel = new JPanel();
        SpringLayout l2 = new SpringLayout();
        buttonPanel.setLayout(l2);

        setButton = new JButton("Set");
        setButton.setActionCommand("setButton");
        setButton.setToolTipText("Set EPOS2 profile position parameters");
        setButton.addActionListener(this);
        setButton.setEnabled(false);

        readButton = new JButton("Read");
        readButton.setActionCommand("readButton");
        readButton.setToolTipText("Read EPOS2 profile position parameters");
        readButton.addActionListener(this);
        readButton.setEnabled(false);

        buttonPanel.add(readButton);
        buttonPanel.add(setButton);

        SpringUtilities.makeCompactGrid(buttonPanel, 1, 2, 4, 4, 4, 4);
        
        bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        
        statusLabel = new JLabel("Status: ");
        statusLabel.setForeground(Color.WHITE);
        
        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createHorizontalGlue());

        panel.add(parameterPanel);
        panel.add(buttonPanel);
        panel.add(bottomPanel);
    }
    
    public void setController(UIController controller){
        this.controller = controller;
        controller.addStatusListener(this);
    }

    public void enableControls(boolean enable) {
        velocityField.setEditable(enable);
        accelerationField.setEditable(enable);
        decelerationField.setEditable(enable);
        readButton.setEnabled(enable);
        setButton.setEnabled(enable);
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
        if (command.equals(readButton.getActionCommand())) {
            // read the crap
            if(eposComboBox.getSelectedIndex() == CheetahIF.CONTROLLER_INDICES[CheetahIF.ID_ALL]){
                // Do nothing
            }else{
                controller.setEposIdx(CheetahIF.CONTROLLER_INDICES[eposComboBox.getSelectedIndex()]);
                controller.requestOperation(StatusListener.REQUEST_READ_PROFILE_POSITION_PARAMS);
            }
        } else if (command.equals(setButton.getActionCommand())) {
            // set the crap
            if(eposComboBox.getSelectedIndex() == CheetahIF.CONTROLLER_INDICES[CheetahIF.ID_ALL]){
                // set all, TODO
            }else{
                // set selected
                if(controller.isConnected()){
                    //controller.setEposIdx((short)eposComboBox.getSelectedIndex());
                    controller.setProfilePositionParams((short)profileTypeComboBox.getSelectedIndex(),
                            Integer.parseInt(velocityField.getText()),
                            Integer.parseInt(accelerationField.getText()),
                            Integer.parseInt(decelerationField.getText()));
                    controller.requestOperation(StatusListener.REQUEST_SET_PROFILE_POSITION_PARAMS);
                }
            }
        } else if(command.equals(eposComboBox.getActionCommand())){
            if(eposComboBox.getSelectedIndex() == CheetahIF.CONTROLLER_INDICES[CheetahIF.ID_ALL]){
                readButton.setEnabled(false);
            }else{
                readButton.setEnabled(true);
            }
            velocityField.setText("0");
            accelerationField.setText("0");
            decelerationField.setText("0");
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        ProfilePositionSetupPanel p = new ProfilePositionSetupPanel();
        
        f.add(p.getPanel());
        f.pack();
        f.setVisible(true);
        
        //p.enableControls(true);
    }

    @Override
    public void statusChanged(int status) {
        if(status == StatusListener.STATUS_PROFILE_POSITION_PARAMS_READY){
            // read them and set them in the UI
            //eposComboBox.setSelectedIndex(controller.getEposIdx());
            profileTypeComboBox.setSelectedIndex(controller.getMotionProfileType());
            velocityField.setText(Integer.toString(controller.getProfileVelocity()));
            accelerationField.setText(Integer.toString(controller.getProfileAcceleration()));
            decelerationField.setText(Integer.toString(controller.getProfileDeceleration()));
        } else if(status == StatusListener.STATUS_PROFILE_POSITION_PARAMS_SET_OK){
            bottomPanel.setBackground(Color.GREEN);
            statusLabel.setText("Status: Set OK");
        }else if(status == StatusListener.STATUS_PROFILE_POSITION_PARAMS_SET_ERR){
            bottomPanel.setBackground(Color.RED);
            statusLabel.setText("Status: Set Error");
        }
    }
}
