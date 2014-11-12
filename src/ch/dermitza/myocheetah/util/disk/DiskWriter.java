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
package ch.dermitza.myocheetah.util.disk;

import ch.dermitza.myocheetah.util.TimeUtil;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author K. Dermitzakis
 * @email dermitza <AT> ifi.uzh.ch
 * @filename DiskWriter.java
 * @version 0.1
 *
 * Created: 02/03/2009 rev 0.1 dermitza
 * Edited :
 *
 *
 */
public class DiskWriter implements Runnable {

    public static final int WRITER_BUFFER = 6553600; // 6.5 MB write buffer
    private String prefix = null;
    private String header = null;
    private String dataDesc = null;
    private String filename = null;
    private String dirPath;
    private String timestamp;
    private String commentTimestamp;
    private boolean running = false;
    private boolean timestampInFilename = true;
    private boolean dataToWrite = false;
    private boolean useHeader = true;
    private Queue<DiskWriterIF> dataQueue;
    ///////////////////////////////////////
    private FileOutputStream fos;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private PrintWriter prw;

    public DiskWriter() {
        dataQueue = new ConcurrentLinkedQueue<>();
    }

    public void useHeader(boolean useHeader) {
        this.useHeader = useHeader;
    }

    public boolean usingHeader() {
        return this.useHeader;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public void setTimestampInFilename(boolean timestampInFileName) {
        this.timestampInFilename = timestampInFileName;
    }

    public boolean getTimestampInFilename() {
        return this.timestampInFilename;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return this.header;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setDataDescription(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getDataDescription() {
        return this.dataDesc;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    private void writeHeader() {
        if (header == null) {
            header =
                    "# Auto-generated header\n"
                    + "# Created on " + commentTimestamp + "\n\n";
        }

        prw.write(header);
        if(dataDesc != null){
            prw.write(dataDesc);
        }
        prw.write("# Created on " + commentTimestamp + "\n\n");
    }

    public void addSample(DiskWriterIF sample) {
        dataQueue.add(sample);
        dataToWrite = true;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void writeDataSample(DiskWriterIF packet) {
        prw.write(packet.getData());
    }

    private void openFile() {
        timestamp = TimeUtil.getTimeStamp("yyyy-MM-dd_HH-mm-ss");
        commentTimestamp = TimeUtil.getTimeStamp("yyyy/MM/dd HH:mm:ss");
        String s = dirPath + "/";
        filename = "";

        if (prefix != null) {
            filename = filename + prefix;
            s = s + prefix;
        }

        if (timestampInFilename) {
            filename = filename + "_" + timestamp;
            s = s + "_" + timestamp;
        }

        filename = filename + ".dat";
        s = s + ".dat";

        try {
            fos = new FileOutputStream(s);
            osw = new OutputStreamWriter(fos);
            bw = new BufferedWriter(osw, WRITER_BUFFER);
            prw = new PrintWriter(bw, false); // dont want to flush every '\n'

        } catch (IOException ioe) {
            System.err.println("Could not write on file " + s + " "
                    + ioe.getMessage());
            running = false;
        }
    }

    @Override
    public void run() {
        running = true;
        openFile();
        if (useHeader) {
            writeHeader();
        }
        while (running) {
            if (dataToWrite) {
                while (!dataQueue.isEmpty()) {
                    // write the crap
                    writeDataSample(dataQueue.poll());
                }
                if (dataQueue.isEmpty()) {
                    dataToWrite = false;
                }
            }
            trySleep(500);
        }
        System.err.println("disk writer stopping..");
        shutdown();
    }

    private void shutdown() {
        // close file and streams
        // try to write any remaining elements
        System.err.println("Diskwriter waiting for additional data..");
        trySleep(200);
        if (dataToWrite) {
            while (!dataQueue.isEmpty()) {
                writeDataSample(dataQueue.poll());
            }
            dataToWrite = false;
        }
        // remove anything left
        dataQueue.clear();
        // flush and close the stream
        prw.flush();
        prw.close();
        try {
            bw.close();
            osw.close();
            fos.close();
        } catch (IOException ioe) {
            System.err.println("IOException while closing streams");
        }
        System.err.println("Diskwriter down.");
    }

    /**
     *  Sleep the DiskWriter thread. Can be interrupted.
     * 
     * @param millis Time to sleep in milliseconds
     */
    protected void trySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }
}
