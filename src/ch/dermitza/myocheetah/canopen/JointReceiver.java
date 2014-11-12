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
package ch.dermitza.myocheetah.canopen;

import ch.dermitza.jcanopen.can.AbstractCanReceiver;
import ch.dermitza.jcanopen.can.CanReaderIF;
import ch.dermitza.jcanopen.canopen.async.CanDataListener;
import ch.dermitza.myocheetah.CheetahIF;
import java.util.ArrayList;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class JointReceiver extends AbstractCanReceiver{
    
    protected ArrayList<CanDataListener> listeners;
    
    public JointReceiver(CanReaderIF reader) {
        super(reader);
        listeners = new ArrayList<>();
    }

    @Override
    protected void handleCanMessage() {
        // Make sure we are indeed waiting for this particular (or any) message
        if((msg.getID() & CheetahIF.ID_JOINT_BASE) == CheetahIF.ID_JOINT_BASE){
            // Joint data has arrived
            byte[] data = msg.getData();
            
            int id = (msg.getID() - CheetahIF.ID_JOINT_BASE);
            
            int jointAngle = (data[0] & 0xFF) |
                                ((data[1] & 0xFF) << 8);
            
            // Notify any listeners
            fireListeners(id, msg.getTimestamp(), jointAngle);
            
        } else {
            // We have read a message that we shouldnt have, log it
            System.err.println("Unknown message read: " + msg);
        }
    }
    
    private void fireListeners(int id, double timestamp, int value){
        CanDataListener[] tmp = listeners.toArray(new CanDataListener[0]);
        for(int i=0; i < tmp.length; i++){
            tmp[i].dataArrived(id, timestamp, value);
        }
    }
    
    protected void removeAllListeners(){
        listeners.clear();
    }

    @Override
    public void addDataListener(CanDataListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDataListener(CanDataListener listener) {
        listeners.remove(listener);
    }
    
}
