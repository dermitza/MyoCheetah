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
package ch.dermitza.myocheetah.data;

import ch.dermitza.myocheetah.util.disk.DiskWriterIF;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
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
