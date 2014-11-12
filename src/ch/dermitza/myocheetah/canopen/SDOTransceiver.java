package ch.dermitza.myocheetah.canopen;

import ch.dermitza.jcanopen.can.CanReaderIF;
import ch.dermitza.jcanopen.can.CanWriterIF;
import ch.dermitza.jcanopen.canopen.async.AbstractSDOTransceiver;
import ch.dermitza.jcanopen.canopen.async.PDODataListener;

/**
 *
 * @author exe
 */
public class SDOTransceiver extends AbstractSDOTransceiver<PDOReceiver>{
    
    public void enableAllPDOs(boolean enable){
        receiver.enableAll(enable);
    }
    
    public void enablePDO(int id, boolean enable){
        receiver.enable(id, enable);
    }
    
    public void addDataListener(PDODataListener listener){
        receiver.addDataListener(listener);
    }
    
    public void removeDataListener(PDODataListener listener){
        receiver.removeDataListener(listener);
    }
    
    @Override
    public void setReader(CanReaderIF reader){
        this.reader = reader;
        if(receiver == null){
            receiver = new PDOReceiver(reader);
            receiverThread = new Thread(receiver);
            receiverThread.start();
        }else{
            receiver.setReader(reader);
        }
    }
    
    @Override
    public void setWriter(CanWriterIF writer){
        this.writer = writer;
        if(receiver != null){
            receiver.setWriter(writer);
        }
    }
    
}
