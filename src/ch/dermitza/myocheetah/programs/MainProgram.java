package ch.dermitza.myocheetah.programs;

import ch.dermitza.jcanopen.can.CanDeviceIF;
import ch.dermitza.jcanopen.can.devices.ixxat.Ixxat;
import ch.dermitza.myocheetah.UIController;
import ch.dermitza.myocheetah.data.DataManager;
import ch.dermitza.myocheetah.ui.MainUI;
import java.awt.EventQueue;

/**
 *
 * @author exe
 */
public class MainProgram {
    
    private MainUI ui;
    private final CanDeviceIF ixxatDevice;
    private final UIController controller;
    private final DataManager dataManager;
    private final Thread controllerThread;
    
    public MainProgram(){
        ixxatDevice = new Ixxat();
        dataManager = new DataManager();
        
        controller = new UIController(ixxatDevice, dataManager);
        controllerThread = new Thread(controller);
        controllerThread.start();
        
        controller.addStatusListener(dataManager);
    }
    
    public void loadUI() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ui = new MainUI(controller);
                ui.showFrames();
                controller.addPanels(ui.getEndpointPanel(), ui.getDataPanel(), ui.getRawDataPanel());
            }
        };
        EventQueue.invokeLater(r);
    }
    
    
    public static void main(String[] args){
        MainProgram m = new MainProgram();
        m.loadUI();
    }
    
}
