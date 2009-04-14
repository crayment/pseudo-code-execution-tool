/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET;

/**
 *
 * @author crayment
 */
public class PETThread extends Thread {

    PETMain PETMain;

    boolean running;

    long nextUpdate;

    long delay;

    public PETThread(PETMain PETMain)
    {
        this.PETMain = PETMain;
        this.running = false;
        this.delay = 100;
    }

    public void setSpeed(int speed)
    {
        this.delay = (5-speed)*100;
    }

    public int getSpeed()
    {
        return (int)(this.delay/100) + 5;
    }

    public void stopPET()
    {
        this.running = false;
    }

    public void stepPET()
    {
        this.running = false;
        PETMain.step();
    }

    public void runPET()
    {
        this.running = !this.running;
        this.nextUpdate = System.currentTimeMillis() + this.delay;
    }

    void pausePET() {
        this.running = false;
    }

    public boolean isRunning()
    {
        return this.running;
    }

    public void setDelay(int delay)
    {
        this.delay = delay;
        this.nextUpdate += delay;
    }

    @Override
    public void run()
    {
        long t;

        while(true)
        {
            if(running)
            {
                t = System.currentTimeMillis();
                if(t >= this.nextUpdate)
                {
                   this.nextUpdate = t + this.delay;
                   PETMain.step();
                }
            }
            try
            {
                if(this.delay != 0)
                    Thread.sleep(10);
            }
            catch(Exception e)
            {
                System.err.println(e);
            }
        }
    }

}
