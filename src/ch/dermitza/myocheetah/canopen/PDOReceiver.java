package ch.dermitza.myocheetah.canopen;

import ch.dermitza.jcanopen.can.CanMessage;
import ch.dermitza.jcanopen.can.CanReaderIF;
import ch.dermitza.jcanopen.can.CanWriterIF;
import ch.dermitza.jcanopen.canopen.io.AbstractPDOReceiver;

/**
 *
 * @author exe
 */
public class PDOReceiver extends AbstractPDOReceiver {

    private CanWriterIF writer;

    public PDOReceiver(CanReaderIF reader) {
        super(reader);
    }

    public void setWriter(CanWriterIF writer) {
        this.writer = writer;
    }
    private void firePDO1(int sensorNum, double timestamp, int pos, int spd){
        CheetahDataListener[] tmp = listeners.toArray(new CheetahDataListener[0]);
        for (CheetahDataListener l : tmp) {
            l.dataPDO1(sensorNum, timestamp, pos, spd);
        }
    }
    
    private void firePDO2(int sensorNum, double timestamp, int curr, int currAvg, int din){
        CheetahDataListener[] tmp = listeners.toArray(new CheetahDataListener[0]);
        for (CheetahDataListener l : tmp) {
            l.dataPDO2(sensorNum, timestamp, curr, currAvg, din);
        }
    }

    /**
     * At this point we have received a message. This message should NOT be an
     * SDO response as the SDOTransceiver has the lock on the reader until a
     * response is read. This message however can be an error frame.
     */
    @Override
    protected void handleCanMessage() {
        //System.out.println("PDO msg");
        // Make sure we are indeed waiting for this particular (or any) message
        if ((msg.getID() & PDOIF.COB_PDO1) == PDOIF.COB_PDO1) {
            // PDO1 arrived, this contains position and speed
            byte[] data = msg.getData();

            int id = (msg.getID() - PDOIF.COB_PDO1);
            int motorPos = (data[0] & 0xFF)
                    | ((data[1] & 0xFF) << 8)
                    | ((data[2] & 0xFF) << 16)
                    | ((data[3] & 0xFF) << 24);
            int motorSpd = (data[4] & 0xFF)
                    | ((data[5] & 0xFF) << 8)
                    | ((data[6] & 0xFF) << 16)
                    | ((data[7] & 0xFF) << 24);

            if (id > 0 && id < 9) {
                // Notify any listeners
                firePDO1(id-1, msg.getTimestamp(), motorPos, motorSpd);
            } else {
                System.err.println("INVALID EPOS2 ID: " + id);
            }

        } else if ((msg.getID() & PDOIF.COB_PDO2) == PDOIF.COB_PDO2) {
            // PDO2 arrived, this contains current and (TODO) foot switch data
            byte[] data = msg.getData();

            int id = (msg.getID() - PDOIF.COB_PDO2);
            short current = (short) ((data[0] & 0xFF) | ((data[1] & 0xFF) << 8));
            short currentAvg = (short) ((data[2] & 0xFF) | ((data[3] & 0xFF) << 8));
            short digitalIn = (short) ((data[4] & 0xFF) | ((data[5] & 0xFF) << 8));

            //System.out.println(id + ":" + current);
            if (id > 0 && id < 9) {
                // Notify any listeners
                firePDO2(id-1, msg.getTimestamp(), current, currentAvg, digitalIn);
            } else {
                System.err.println("INVALID EPOS2 ID: " + id);
            }

        } else {
            // We have read a message that we shouldnt have, log it
            System.err.println("Unknown message read: " + msg);
        }
    }

    @Override
    protected void shutdown() {
        // Remove all listeners
        removeAllListeners();
        super.shutdown();
    }

    @Override
    public void enableAll(boolean enable) {
        boolean write;
        byte[] data = new byte[2];
        if (enable) {
            data[0] = (byte) 0x01;
            data[1] = (byte) 0x00;
        } else {
            data[0] = (byte) 0x80;
            data[1] = (byte) 0x00;
        }
        CanMessage message = new CanMessage();
        message.setID(0);
        message.setData(data);
        message.setTimestamp(0);

        synchronized (writer) {
            write = writer.writeMessageImmediate(message);
        }

        if (!write) {
            System.err.println("PDO: Could not write to the CAN-bus");
        }
    }

    @Override
    public void enable(int id, boolean enable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
