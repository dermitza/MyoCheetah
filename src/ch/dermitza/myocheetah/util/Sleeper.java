package ch.dermitza.myocheetah.util;

/**
 *
 * @author exe
 */
public class Sleeper implements Runnable{

    @Override
    public void run() {
        try{
            Thread.sleep(Integer.MAX_VALUE);
        } catch(InterruptedException ie){
            // Shutdown
        }
    }
    
}
