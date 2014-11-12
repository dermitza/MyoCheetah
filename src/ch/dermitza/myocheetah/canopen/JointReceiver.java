package ch.dermitza.myocheetah.canopen;

import ch.dermitza.jcanopen.can.AbstractCanReceiver;
import ch.dermitza.jcanopen.can.CanReaderIF;
import ch.dermitza.jcanopen.canopen.async.CanDataListener;
import ch.dermitza.myocheetah.CheetahIF;
import java.util.ArrayList;

/**
 *
 * @author exe
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
