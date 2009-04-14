/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.model;

import PET.LineWatchView;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;

/**
 *
 * @author crayment
 */
public final class LineWatches extends Observable
{

    private static Hashtable watches = new Hashtable();
    public static final int ALL_LINES = -1;
    /**
     * The instance of the singleton
     */
    private static LineWatches instance;

    /**
     * private constructor to defeat instatiation
     */
    private LineWatches()
    {
    }

    /**
     * Get the static instance of the singleton
     * @return the static instance
     */
    public synchronized static LineWatches getInstance()
    {
        if (LineWatches.instance == null)
        {
            LineWatches.instance = new LineWatches();
        }
        return LineWatches.instance;
    }

    public static int get(Object line)
    {
        int result = 0;
        if (LineWatches.watches != null)
        {
            result = (Integer) LineWatches.watches.get(line);
        }
        return result;
    }

    public static boolean contains(int line)
    {
        return LineWatches.watches.containsKey(new Integer(line));
    }

    public static void addWatchToLine(int lineNum)
    {
        if (!watches.containsKey(new Integer(lineNum)))
        {
            watches.put(lineNum, 0);
        }

        LineWatches.getInstance().setChanged();
        LineWatches.getInstance().notifyObservers(lineNum);
    }

    public static void removeLineToWatch(int lineNum)
    {
        if (watches.containsKey(new Integer(lineNum)))
        {
            watches.remove(lineNum);
        }
        if (watches.size() >= 0)
        {
            LineWatches.getInstance().setChanged();
            LineWatches.getInstance().notifyObservers(lineNum);
        }
    }

    public static void incrementLineWatch(int line)
    {

        if (line == ALL_LINES)
        {
            if (!watches.containsKey(LineWatchView.ALL))
            {
                watches.put(LineWatchView.ALL, 1);
            } else
            {
                int cur = (Integer) watches.get(LineWatchView.ALL);
                watches.put(LineWatchView.ALL, cur + 1);
            }

            LineWatches.getInstance().setChanged();
            LineWatches.getInstance().notifyObservers(LineWatchView.ALL);

        } else
        {

            int cur = (Integer) watches.get(line);
            watches.put(line, cur + 1);


            LineWatches.getInstance().setChanged();
            LineWatches.getInstance().notifyObservers(line);
        }
    }

    public static void reset()
    {
        for (Enumeration e = LineWatches.watches.keys(); e.hasMoreElements();)
        {
            Object o = e.nextElement();
            watches.put(o, 0);

            LineWatches.getInstance().setChanged();
            LineWatches.getInstance().notifyObservers(o);

        }
    }
}
