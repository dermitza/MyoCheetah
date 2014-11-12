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

import ch.dermitza.jcanopen.can.CanReaderIF;
import ch.dermitza.jcanopen.can.CanWriterIF;
import ch.dermitza.jcanopen.canopen.async.AbstractSDOTransceiver;
import ch.dermitza.jcanopen.canopen.async.PDODataListener;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
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
