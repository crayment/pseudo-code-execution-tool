/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.model;
import PET.Constants;
import PET.parser.Parser;
import PET.parser.ParserException;
import PET.validator.Errors;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author crayment
 */
public class Program {

    public static ArrayList<Integer> CodeBreakpoints = new ArrayList();

    private static Program instance;
    /**
     * hash to store blocks mapped from there id's
     */
    private static Hashtable blocks = new Hashtable(); // maps ids to blocks
    private static Hashtable functions = new Hashtable(); // maps names to functions (which are blocks)
    private static Block global;


    private static PrintStream out;
    private static InputStream in;
    public static ObjectInputStream userInput;
    public static PipedInputStream pin;
    
    private static int Status = Constants.STOPPED;

    /**
     * @return the Status
     */
    public static int getStatus() {
        return Status;
    }

    /**
     * @return the highlight
     */
    public static Highlight getHighlight() throws ParserException{
        return global.getNextHighlight();
    }

    /**
     * @return the functions
     */
    public static Hashtable getFunctions() {
        return functions;
    }

    public Program(){
        pin = new PipedInputStream();
    }

    public synchronized static Program getInstance()
    {
        if(Program.instance == null)
            Program.instance = new Program();
        return Program.instance;
    }

    public static void load(String program) throws ParserException
    {
        Program.load(program, System.out, System.in);
    }

    public static void load(String program, PrintStream out, InputStream in)
    {
        reset();

        Program.in = in;
        Program.out = out;

        Parser.load(program);
        Status = Constants.RUNNING;

        Parser.compile();

        //System.out.println(Program.getInstance());
    }


    public static void reset()
    {
        LineWatches.reset();
        PET.validator.Errors.reset();
        Variables.clearAllVariables();
        Program.blocks.clear();
        Program.getFunctions().clear();
        Program.setGlobal(null);
        Status = Constants.RUNNING;
        
        Parser.reset();
    }



    public static void step() throws ParserException
    {
        if(getStatus() == Constants.STOPPED)
            throw new ParserException("Can not step until load is called."); 

        
        else {
            if(global.getStatus() == Constants.STOPPED) {
                Status = Constants.STOPPED;
            } else {
             global.step();
             if(global.getStatus() == Constants.STOPPED) {
                Status = Constants.STOPPED;
             }
            }
        }
    }


    
    public static void run()
    {
        throw new NotImplementedException();
    }











    




    public static void addBlock(Block b)
    {
        if(b == null) throw new NullPointerException();
        if(blocks.containsKey(b.getId())) System.err.println("Block with id " + b.getId() + " already exists.");
        else
            blocks.put(b.getId(), b);
    }

    public static Block getBlock(Integer id)
    {
        return (Block)blocks.get(id);
    }

    public static boolean isBlockDefined(int id)
    {
        return blocks.containsKey(id);
    }
    public static void addFunctionBlock(Block b, Highlight h)
    {
        if(b == null) throw new NullPointerException();
        if(getFunctions().containsKey(b.getName())) Errors.add(Errors.createDuplicateFunctionError(b.getName(), h));

        getFunctions().put(b.getName(), b);
    }

    public static Block getFunctionBlock(String name)
    {
        return (Block)getFunctions().get(name);
    }

    public static boolean isFunctionDefined(String name)
    {
        return getFunctions().containsKey(name);
    }



    /**
     * @return the global
     */
    public static Block getGlobal() {
        return global;
    }

    /**
     * @param aGlobal the global to set
     */
    public static void setGlobal(Block aGlobal) {
        global = aGlobal;
    }
    
    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("----PROGRAM----\n");
       sb.append(global.toString());
       sb.append("FUNCTIONS\n");
       for(Enumeration e = Program.getFunctions().elements(); e.hasMoreElements();)
       {
           Block block = (Block)e.nextElement();
           sb.append(block.toString());
       }
       sb.append("BLOCKS\n");
       for(Enumeration e = Program.blocks.elements(); e.hasMoreElements();)
       {
           Block block = (Block)e.nextElement();
           sb.append(block.toString());
       }
       return sb.toString();
    }

      /**
     * @return the out
     */
    public static PrintStream getOut() {
        return out;
    }

    /**
     * @return the in
     */
    public static InputStream getIn() {
        return in;
    }
}
