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
package ch.dermitza.myocheetah.programs;

import ch.dermitza.jcanopen.can.CanDeviceIF;
import ch.dermitza.jcanopen.can.devices.ixxat.Ixxat;
import ch.dermitza.myocheetah.UIController;
import ch.dermitza.myocheetah.data.DataManager;
import ch.dermitza.myocheetah.ui.MainUI;
import java.awt.EventQueue;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
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
