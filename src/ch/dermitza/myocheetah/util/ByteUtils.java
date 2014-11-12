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
package ch.dermitza.myocheetah.util;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.11
 * @since   0.01
 */
public class ByteUtils {
    
    public static String toBits(int data){
        String bits = String.format("%16s", Integer.toBinaryString(data)).replace(' ', '0');
        // http://stackoverflow.com/a/2297450
        String[] arr = bits.split("(?<=\\G....)");
        String out = arr[0] + " " + arr[1] + " " + arr[2] + " " + arr[3];
        return out;
    }
    
    public static String toHex(int data){
        byte[] d = new byte[4];
        d[0] = (byte)(data & 0xFF);
        d[1] = (byte)((data >> 8) & 0xFF);
        d[2] = (byte)((data >> 16) & 0xFF);
        d[3] = (byte)((data >> 24) & 0xFF);
        
        return String.format("0x%02X 0x%02X 0x%02X 0x%02X", d[0], d[1], d[2], d[3]);
    }
    
    public static void main(String[] args){
        int num = 1315;
        System.out.println(toBits(num) + " " + toHex(num));
    }
    
}
