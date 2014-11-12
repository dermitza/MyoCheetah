package ch.dermitza.myocheetah;

import ch.dermitza.jcanopen.can.CanDeviceIF;
import ch.dermitza.myocheetah.canopen.JointReceiver;
import ch.dermitza.myocheetah.canopen.SDOTransceiver;
import ch.dermitza.myocheetah.controller.AbstractController;
import ch.dermitza.myocheetah.controller.ControllerIF;
import ch.dermitza.myocheetah.data.DataManager;
import ch.dermitza.myocheetah.leg.RawLeg;
import ch.dermitza.myocheetah.ui.DataPanel;
import ch.dermitza.myocheetah.ui.EndpointPanel;
import ch.dermitza.myocheetah.ui.RawDataPanel;
import java.awt.EventQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author exe
 */
public class UIController implements Runnable {

    private boolean running = false;
    private final CanDeviceIF canDevice;

    private Thread jointThread;
    private JointReceiver jointReader;
    private final SDOTransceiver trans;

    private AbstractController controller;

    private final ArrayList<StatusListener> listeners;
    private final DataManager dataManager;
    private RawDataPanel rawDataPanel;
    private DataPanel dataPanel;
    private EndpointPanel endpointPanel;

    private final Cheetah cheetah;

    private boolean connected = false;
    private boolean enabled = false;
    private boolean logging = false;
    private boolean controlling = false;
    private final Object threadLock;
    private int requestStatus;

    private String writerPath;
    private int controllerIdx = 0;

    // CONTROLLER FREQUENCY CONSTRAIN PARAMS
    private boolean constrain = true;
    private long constrainPeriod = 0;

    // PROFILE POSITION PARAMS
    private short eposIdx = 0;
    private int profileVelocity = 0;
    private int profileAcceleration = 0;
    private int profileDeceleration = 0;
    private short motionProfileType = 0;

    public void addPanels(EndpointPanel endpointPanel, DataPanel dataPanel,
            RawDataPanel rawDataPanel) {
        this.endpointPanel = endpointPanel;
        this.dataPanel = dataPanel;
        this.rawDataPanel = rawDataPanel;
    }
    public void setWriterPath(String path) {
        this.writerPath = path;
    }

    public void setProfilePositionParams(short motionProfileType, int profileVelocity, int profileAcceleration, int profileDeceleration) {
        this.motionProfileType = motionProfileType;
        this.profileVelocity = profileVelocity;
        this.profileAcceleration = profileAcceleration;
        this.profileDeceleration = profileDeceleration;
    }

    public void setConstrainParams(boolean constrain, long constrainPeriod) {
        this.constrain = constrain;
        this.constrainPeriod = constrainPeriod;
        if (controller != null) {
            if (controller.isRunning()) {
                controller.setControlPeriod(constrainPeriod);
                controller.fixedLoopPeriod(constrain);
            }
        }
    }

    public void setEposIdx(short eposIdx) {
        this.eposIdx = eposIdx;
    }

    public void setControllerIdx(int controllerIdx) {
        this.controllerIdx = controllerIdx;
    }

    public UIController(CanDeviceIF canDevice, DataManager manager) {
        listeners = new ArrayList<>();
        this.canDevice = canDevice;
        this.dataManager = manager;

        trans = new SDOTransceiver();

        requestStatus = StatusListener.REQUEST_EMPTY;
        threadLock = new Object();

        cheetah = new Cheetah(dataManager);
    }

    public void addStatusListener(StatusListener listener) {
        listeners.add(listener);
    }

    public void removeStatusListener(StatusListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    private void fireStatusChange(int status) {
        StatusListener[] temp = listeners.toArray(new StatusListener[0]);
        for (StatusListener l : temp) {
            l.statusChanged(status);
        }
    }

    public void requestOperation(int request) {
        synchronized (threadLock) {
            requestStatus = request;
            switch (requestStatus) {
                case StatusListener.REQUEST_CONNECT:
                    fireStatusChange(StatusListener.STATUS_CONNECTING);
                    break;
                case StatusListener.REQUEST_DISCONNECT:
                    fireStatusChange(StatusListener.STATUS_DISCONNECTING);
                    break;
                case StatusListener.REQUEST_ENABLE:
                    fireStatusChange(StatusListener.STATUS_ENABLING);
                    break;
                case StatusListener.REQUEST_DISABLE:
                    fireStatusChange(StatusListener.STATUS_DISABLING);
                    break;
                case StatusListener.REQUEST_START_LOGGING:
                    fireStatusChange(StatusListener.STATUS_LOGGING_ENABLING);
                    break;
                case StatusListener.REQUEST_STOP_LOGGING:
                    fireStatusChange(StatusListener.STATUS_LOGGING_DISABLING);
                    break;
                case StatusListener.REQUEST_START_CONTROLLING:
                    fireStatusChange(StatusListener.STATUS_CONTROLLING_ENABLING);
                    break;
                case StatusListener.REQUEST_STOP_CONTROLLING:
                    fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLING);
                    break;
            }
            threadLock.notify();
        }
    }

    /* RUN BY CONTROLLER THREAD */
    private void processRequest() {
        switch (requestStatus) {
            case StatusListener.REQUEST_CONNECT:
                connect();
                break;
            case StatusListener.REQUEST_DISCONNECT:
                disconnect();
                break;
            case StatusListener.REQUEST_ENABLE:
                enable();
                break;
            case StatusListener.REQUEST_DISABLE:
                disable();
                break;
            case StatusListener.REQUEST_START_LOGGING:
                startLogging();
                break;
            case StatusListener.REQUEST_STOP_LOGGING:
                stopLogging();
                break;
            case StatusListener.REQUEST_START_CONTROLLING:
                startController();
                break;
            case StatusListener.REQUEST_STOP_CONTROLLING:
                stopController();
                break;
            case StatusListener.REQUEST_START_PDO:
                trans.enableAllPDOs(true);
                break;
            case StatusListener.REQUEST_STOP_PDO:
                trans.enableAllPDOs(false);
                break;
            case StatusListener.REQUEST_SET_PROFILE_POSITION_PARAMS:
                setProfilePositionParams();
                break;
            case StatusListener.REQUEST_READ_PROFILE_POSITION_PARAMS:
                readProfilePositionParams();
        }
        requestStatus = StatusListener.REQUEST_EMPTY;
    }
    
    private void readProfilePositionParams(){
        for (RawLeg leg : legs) {
            if ((leg.getFlexorID() == eposIdx) || (leg.getExtensorID() == eposIdx)) {
                motionProfileType = (short)leg.getMotionProfileType(eposIdx);
                profileVelocity = leg.getProfileVelocity(eposIdx);
                profileAcceleration = leg.getProfileAcceleration(eposIdx);
                profileDeceleration = leg.getProfileDeceleration(eposIdx);
                fireStatusChange(StatusListener.STATUS_PROFILE_POSITION_PARAMS_READY);
                break;
            }
        }
    }

    private void setProfilePositionParams() {
        int ret = 0;
        for (RawLeg leg : legs) {
            if ((leg.getFlexorID() == eposIdx) || (leg.getExtensorID() == eposIdx)) {
                ret += leg.setMotionProfileType(eposIdx, motionProfileType);
                System.out.println(ret);
                ret += leg.setProfileVelocity(eposIdx, profileVelocity);
                System.out.println(ret);
                ret += leg.setProfileAcceleration(eposIdx, profileAcceleration);
                System.out.println(ret);
                ret += leg.setProfileDeceleration(eposIdx, profileDeceleration);
                System.out.println(ret);
                if(ret == 0){
                    fireStatusChange(StatusListener.STATUS_PROFILE_POSITION_PARAMS_SET_OK);
                }else{
                    fireStatusChange(StatusListener.STATUS_PROFILE_POSITION_PARAMS_SET_ERR);
                }
                break;
            }
        }
    }
    
    public short getEposIdx(){
        return this.eposIdx;
    }
    
    public short getMotionProfileType(){
        return this.motionProfileType;
    }
    
    public int getProfileVelocity(){
        return this.profileVelocity;
    }
    
    public int getProfileAcceleration(){
        return this.profileAcceleration;
    }
    
    public int getProfileDeceleration(){
        return this.profileDeceleration;
    }

    private void connect() {
        if (!isConnected()) {
            /* INIT CAN DEVICE */
            canDevice.connect();

            /* INIT EPOS BUS LINE */
            canDevice.initCan(0);
            trans.setReader(canDevice.getReader(0));
            trans.setWriter(canDevice.getWriter(0));
            trans.addDataListener(dataManager);
            if (rawDataPanel != null) {
                trans.addDataListener(rawDataPanel);
            }

            /* INIT JOINT SENSOR BUS LINE */
            canDevice.initCan(1);

            jointReader = new JointReceiver(canDevice.getReader(1));
            jointReader.addDataListener(dataManager);
            if (rawDataPanel != null) {
                jointReader.addDataListener(rawDataPanel);
            }

            jointThread = new Thread(jointReader);
            jointThread.start();

            cheetah.setTransceiver(trans);

            fireStatusChange(StatusListener.STATUS_CONNECTED);
            this.connected = true;
        }
    }

    private void disconnect() {
        if (isConnected()) {
            if (isControlling()) {
                fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLING);
                stopController();
            }
            if (isLogging()) {
                fireStatusChange(StatusListener.STATUS_LOGGING_DISABLING);
                stopLogging();
            }
            if (isEnabled()) {
                fireStatusChange(StatusListener.STATUS_DISABLING);
                disable();
            }

            trans.removeDataListener(dataManager);
            trans.shutdown();
            canDevice.disconnect();
            fireStatusChange(StatusListener.STATUS_DISCONNECTED);
            this.connected = false;
        }
    }

    private void enable() {
        if (!isEnabled()) {
            int ret = cheetah.enableAll();
            if(ret == 7){
                fireStatusChange(StatusListener.STATUS_ENABLED);
                this.enabled = true;
            } else{
                System.out.println("Enabling failed");
            }
        }
    }

    private void disable() {
        if (isEnabled()) {
            cheetah.disableAll();
            fireStatusChange(StatusListener.STATUS_DISABLED);
            this.enabled = false;
        }
    }

    private void startLogging() {
        if (isEnabled()) {
            logging = true;
            trans.enableAllPDOs(true);
            fireStatusChange(StatusListener.STATUS_LOGGING_ENABLED);
        } else {
            // Enable first
            enable();
            startLogging(); // RECURSION FOR THE WIN
        }
    }

    public void emergencyStop() {
        if (isConnected()) {
            if (isControlling()) {
                stopController();
            }

            if (isLogging()) {
                stopLogging();
            }

            if (isEnabled()) {
                disable();
            }
        }
        Random rnd = new Random();
        if (rnd.nextBoolean()) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "PLIZ NO BREAKE! PLIZ.", "OMG!!!!!!", JOptionPane.WARNING_MESSAGE);
                }
            };
            EventQueue.invokeLater(r);
        }
    }

    private void stopLogging() {
        if (isEnabled()) {
            logging = false;
            trans.enableAllPDOs(false);
            fireStatusChange(StatusListener.STATUS_LOGGING_DISABLED);
        }
    }

    private void startController() {
        if (!isEnabled()) {
            // Enable first
            enable();
        }
        if (isEnabled()) {
            dataManager.startWriter(ControllerIF.CONTROLLER_NAMES[controllerIdx], writerPath);

            controlling = true;
            trans.enableAllPDOs(true);
            fireStatusChange(StatusListener.STATUS_LOGGING_ENABLED);

            String className = "cheetah.controller." + ControllerIF.CONTROLLER_NAMES[controllerIdx];
            try {
                Class<?> clazz = getClass().getClassLoader().loadClass(className);
                Constructor<?> ctor = clazz.getDeclaredConstructor(EndpointPanel.class, DataPanel.class, Cheetah.class, boolean.class, long.class);
                controller = (AbstractController) ctor.newInstance(endpointPanel, dataPanel, cheetah, constrain, constrainPeriod);

                rawDataPanel.clearTraces();
                controller.start();
                fireStatusChange(StatusListener.STATUS_CONTROLLING_ENABLED);
            } catch(ExceptionInInitializerError eiie) {
            eiie.printStackTrace();
        }catch (ClassNotFoundException cnfe) {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Controller not found. "
                                + "Make sure you have correctly declared it in "
                                + "ControllerIF.java", "Controller not found",
                                JOptionPane.ERROR_MESSAGE);
                    }
                };
                EventQueue.invokeLater(r);
                cnfe.printStackTrace();
                controlling = false;
                fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLED);
            } catch (InstantiationException ie) {
                ie.printStackTrace();
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Could not instantiate controller. "
                                + "Make sure your controller extends AbstractController", "Controller not found",
                                JOptionPane.ERROR_MESSAGE);
                    }
                };
                EventQueue.invokeLater(r);
                ie.printStackTrace();
                controlling = false;
                fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLED);
            } catch (IllegalAccessException iae) {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Could not instantiate controller. "
                                + "Call me.", "IllegalAccessError",
                                JOptionPane.ERROR_MESSAGE);
                    }
                };
                EventQueue.invokeLater(r);
                iae.printStackTrace();
                controlling = false;
                fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLED);
            } catch (NoSuchMethodException nsme) {
                nsme.printStackTrace();
                controlling = false;
                fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLED);
            } catch (InvocationTargetException ite) {
                ite.printStackTrace();
                controlling = false;
                fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLED);
            }

        }
    }

    private void stopController() {
        if (isConnected()) {
            dataManager.stopWriter();
            controlling = false;
            controller.stop();
            fireStatusChange(StatusListener.STATUS_CONTROLLING_DISABLED);
            //if(isEnabled()){
            //    trans.enableAllPDOs(false);
            //    fireStatusChange(StatusListener.STATUS_LOGGING_DISABLED);
            //    disable();
            //}
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isLogging() {
        return this.logging;
    }

    public boolean isControlling() {
        return this.controlling;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {

        if (canDevice != null) {
            running = true;
        } else {
            System.err.println("ERROR: No CAN device set in main controller");
        }

        while (running) {
            synchronized (threadLock) {
                try {
                    threadLock.wait();
                } catch (InterruptedException ie) {
                }
                // Status change was requested
                processRequest();
            }
        }
        shutdown();
    }

    protected void shutdown() {
        if (canDevice != null) {
            // Disconnect the device here
            if (isConnected()) {
                fireStatusChange(StatusListener.STATUS_DISCONNECTING);
                disconnect();
            }
        }
    }

}
