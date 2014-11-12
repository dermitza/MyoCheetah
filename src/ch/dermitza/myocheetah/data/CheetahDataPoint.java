package ch.dermitza.myocheetah.data;

import ch.dermitza.myocheetah.util.disk.DiskWriterIF;

/**
 *
 * @author hung
 */
public class CheetahDataPoint implements DiskWriterIF{
    
    private final double timestamp;
    private final int type;
    private final int id;
    private final int datatype;
    private final int data;
    
    public CheetahDataPoint(double timestamp, int type, int id, int datatype, int data){
        this.timestamp = timestamp;
        this.type = type;
        this.id = id;
        this.datatype = datatype;
        this.data = data;
    }

    @Override
    public String getDescription() {
        return "Format:/n Timestamp:type:id:datatype:data";
    }

    @Override
    public String getPrefix() {
        return "cheetahData";
    }

    @Override
    public String getData() {
        return timestamp + " " + type + " " + id + " " + datatype + " " + data + "\n";
    }
    
}
