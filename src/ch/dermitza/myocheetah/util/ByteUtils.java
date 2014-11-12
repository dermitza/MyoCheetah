package ch.dermitza.myocheetah.util;

/**
 *
 * @author exe
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
