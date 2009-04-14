/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.model;

import PET.Constants;
import PET.PETApp;
import PET.parser.Evaluator;
import PET.parser.Language;
import PET.parser.ParserException;
import PET.validator.Errors;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author crayment
 */
public class Block
{

    private static int count = 0;
    private int Status = Constants.STOPPED;
    private int id;
    private String name;
    private Vector commands;
    private int curIndex = 0;
    private Highlight nextHighlight;
    private Block nestedBlock = null;

    public Block()
    {
        this.id = count++;
        commands = new Vector();
        reset();
    }

    public Block(String name)
    {
        this();
        this.setName(name);
    }

    public Block(String name, Vector commands)
    {
        this(name);
        this.commands = commands;
    }


    private Block createNewInstance() {
        Block copy = new Block(String.valueOf(id), commands);
        return copy;
    }

    public void step() throws ParserException
    {
        Command c;

        if (getStatus() == Constants.STOPPED)
        {
            throw new ParserException("Block can not be stepped when it is stopped, call reset to restart the block");
        }
        if (curIndex > commands.size())
        {
            throw new ParserException("Block is finished but somehow not set to stopped, we probably arent setting it stopped where we should be.");
        }


        // If we are currently in a nested block step in it
        if (nestedBlock != null)
        {
            // step in nested block
            nestedBlock.step();
            // if the nested block is done
            if (nestedBlock.getStatus() == Constants.STOPPED)
            {
                nestedBlock = null;
                if(curIndex < commands.size())
                    nextHighlight = ((Command)commands.elementAt(curIndex)).getHighlight();
                else
                    Status = Constants.STOPPED;
            }
            // the nested block is not done
            else
            {
                nextHighlight = nestedBlock.getNextHighlight();
            }
            return;
        }
        // execute the current line in our block
        else
        {
            // get command
            c = (Command) commands.elementAt(curIndex++);
            // execute command
            exec(c);

            // update status (we are done if we are out of commands and
            // we are not in a nested block.)
            if(curIndex >= commands.size() && nestedBlock == null)
            {
                Status = Constants.STOPPED;
                return;
            }

            // if we are stepping into another block
            if (nestedBlock != null)
            {
                nextHighlight = nestedBlock.getNextHighlight();
            }

            else
            {
                nextHighlight = ((Command) commands.elementAt(curIndex)).getHighlight();
            }
        }
    }

    public void reset()
    {
        nestedBlock = null;
        curIndex = 0;
        nextHighlight = null;
        Status = Constants.RUNNING;
    }


    public Highlight getNextHighlight() throws ParserException
    {
        if(nextHighlight == null)
        {
            Command c = ((Command)commands.elementAt(curIndex));
            nextHighlight = c.getHighlight();

            // if we have no next highlight then we need to step to get one.
            // step only stops once it finds a highlight so it will be set
            // and we can just return it.
            if(nextHighlight == null)
            {
                step();
            }
        }


        return nextHighlight;
    }

    // default parameter hack (stupid java)
    public void exec(Command c) throws ParserException
    {
        exec(c, true);
    }

    /**
     * Execute a command of opcode.
     * @param c the command to execute
     * @param andFollowingNulls if true will execute all following opcodes
     * that have a null highlight field.
     * @throws PET.parser.ParserException when there is a runtime exception.
     */
    public void exec(Command c, boolean andFollowingNulls) throws ParserException
    {
        String command = c.getLine();
        //System.out.println(command);

        if (command.trim().equals(""))
        {
            return;
        } else if (command.matches(Language.INT_DECLARATION))
        {
            handleIntDeclaration(c);
        } else if (command.matches(Language.BOOL_DECLARATION))
        {
            handleBooleanDeclaration(c);
        } else if (command.matches(Language.STRING_DECLARATION))
        {
            handleStringDeclaration(c,command);
        } else if (command.matches(Language.ARRAY_DECLARATION))
        {
            handleArrayDeclaration(c);
        } else if (command.matches(Language.VARIABLE_ASSIGNMENT))
        {
            handleVariableAssignment(c);
        } else if (command.matches(Language.ARRAY_ASSIGNMENT))
        {
            handleArrayAssignment(c);
        } else if (command.matches(Language.FUNCTION_CALL))
        {
            handleFunctionCall(c);
        } else if (command.matches(Language.BLOCK_CALL))
        {
            handleBlockCall(c);
        } else if(command.matches(Language.LOOP_EXPRESSION))
        {
            andFollowingNulls = false;
            handleLoopCommand(c);
        } else if (command.matches(Language.CONDITION_EXPRESSION))
        {
            handleConditionalExpression(c);

        } else if(command.matches(Language.WRITE_EXPRESSION))
        {
            handleWriteExpression(c);
        } else if(command.matches(Language.WRITE_LITERAL))
        {
            handleWriteLiteralExpression(c);
        }else if(command.matches(Language.WRITE_LINE))
        {
            Program.getOut().println();
        } else if(command.matches(Language.READ_EXPRESSION)){
            handleReadExpression(c);
            andFollowingNulls = false;

        } else if(command.matches(Language.ASSIGN_FROM_READ_INTO_VARIABLE)){
            handleAssignFromReadIntoVariable(c);

        } else if(command.matches(Language.ASSIGN_FROM_READ_INTO_ARRAY)){
            handleAssignFromReadIntoArray(c);
            
        } else if(command.matches(Language.FILL_ARRAY_RND_EXPRESSION)) {
            handleFillArrayRandom(c);

        } else
        {
            throw new ParserException("Failed to evaluate " + command);
        }

        if(andFollowingNulls)
        {
            // execute all command with no highlight set following the first one
            int nextCommandIndex = curIndex;
            if (nextCommandIndex < commands.size())
            {
                Command nextCommand = (Command) commands.elementAt(nextCommandIndex);
                if(nextCommand.getHighlight() == null ||
                        ((c.getHighlight() != null) && (nextCommand.getHighlight() != null) && nextCommand.getHighlight().equals(c.getHighlight()))) {
                    curIndex++;
                    exec(nextCommand);
                }
            }
        }
    }



    public void add(Command c)
    {
        commands.add(c);
    }

    public Command elementAt(int index)
    {
        return (Command) commands.elementAt(index);
    }

    public int size()
    {
        return commands.size();
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\tBlock: id=" + this.getId() + "  name= " + this.name + "\n");
        for (int i = 0; i < this.size(); i++)
        {
            sb.append("\t\t" + this.elementAt(i) + "\n");
        }
        return sb.toString();
    }


    /**
     * @return the Status
     */
    public int getStatus()
    {
        return Status;
    }


    private void handleArrayAssignment(Command c) throws ParserException
    {
        String command = c.getLine();
        Matcher m = Pattern.compile(Language.ARRAY_ASSIGNMENT).matcher(command);
        m.find();
        String varName = m.group(1).trim();
        String indexStr = m.group(3).trim();
        Integer index = (Integer) Evaluator.eval(indexStr,c.getHighlight());
        if(index == null)
            return;
        String valueStr = m.group(5).trim();

        Integer value = (Integer) Evaluator.eval(valueStr,c.getHighlight());
        if(value == null)
            return;
        Integer[] array = (Integer[]) Variables.get(varName);
        try {
            array[index] = value;
        } catch (Exception e) {
            Errors.add(Errors.createIndexOutOfBoundsException(c.getHighlight()));
        }
        Variables.set(varName, array);
    }

    private void handleBlockCall(Command c) throws NumberFormatException
    {
        String command = c.getLine();

        Matcher m = Pattern.compile(Language.BLOCK_CALL).matcher(command);
        m.find();
        String idStr = m.group(1);
        Integer blockID = Integer.parseInt(idStr);
        Block b = Program.getBlock(blockID).createNewInstance();
        b.reset();
        this.nestedBlock = b;
    }

    private void handleConditionalExpression(Command c) throws ParserException {
        String command = c.getLine();

        Matcher m = Pattern.compile(Language.CONDITION_EXPRESSION).matcher(command);
        m.find();
        String[] condCommandPairs = m.group(1).split(Language.OUTER_SEPERATOR);
        // go through all the conditional command pairs and execute the first one that returns true.
        for (String pair : condCommandPairs) {
            String[] exprAndCommand = pair.split(Language.INNER_SEPERATOR);
            String expr = exprAndCommand[0].trim(); // |expr|
            expr = expr.substring(1, expr.length() - 1); // trim off ||
            String com = exprAndCommand[1].trim();
            Boolean value = (Boolean) Evaluator.eval(expr,c.getHighlight());
            if(value == null)
                return;
            if (value) {
                exec(new Command(com), false);
                break;
            }
        }
    }

    private void handleFillArrayRandom(Command c) throws ParserException{
        String command = c.getLine();

        Matcher m = Pattern.compile(Language.FILL_ARRAY_RND_EXPRESSION).matcher(command);
        m.find();

        String arrayName = m.group(1);

        if(!Variables.isDefined(arrayName)) {
            // create error
            System.err.println("Variable not defined");
            return;
        }
        
        Object o = Variables.get(arrayName);

        if(!(o instanceof Integer[])) {
            //create error
            System.err.println("Variable not an array");
            return;
        }


        Integer[] array = (Integer[]) o;
        int maxRandomValue = array.length * 10;

        for (int i = 0; i < array.length; i++) {
            array[i] = (int)(Math.random() * maxRandomValue) + 1;
        }
        
        Variables.set(arrayName, array);
    }

    private void handleFunctionCall(Command c)
    {
        String command = c.getLine();

        Matcher m = Pattern.compile(Language.FUNCTION_CALL).matcher(command);
        m.find();
        String funcName = m.group(1);
        Block funcBlock = Program.getFunctionBlock(funcName).createNewInstance();
        funcBlock.reset();
        this.nestedBlock = funcBlock;
    }

    private boolean handleLoopCommand(Command c) throws ParserException {

        String command = c.getLine();


        Matcher m = Pattern.compile(Language.LOOP_EXPRESSION).matcher(command);
        m.find();
        String expr = m.group(1);
        boolean value = (Boolean) Evaluator.eval(expr,c.getHighlight());
        if (value) {
            // execute the command
            exec(new Command(m.group(3)));
            // set the current index back to the loop
            curIndex--;
            return true;
        }
        return false;
    }

    private void handleStringDeclaration(Command c, String command) throws ParserException {
        String com = c.getLine();
        Matcher m = Pattern.compile(Language.STRING_DECLARATION).matcher(command);
        m.find();
        String varName = m.group(1);
        Variables.defineString(varName);
    }

    private void handleVariableAssignment(Command c) throws ParserException
    {

        String command = c.getLine();


        Matcher m = Pattern.compile(Language.VARIABLE_ASSIGNMENT).matcher(command);
        m.find();
        String varName = m.group(1).trim();
        String valueStr = m.group(3).trim();
        Object valueObj = Evaluator.eval(valueStr,c.getHighlight());
        if(valueObj == null) {
            return;
        }
        Variables.set(varName, valueObj);
    }

    private void handleArrayDeclaration(Command c) throws ParserException
    {
        String command = c.getLine();

        Matcher m = Pattern.compile(Language.ARRAY_DECLARATION).matcher(command);
        m.find();
        String varName = m.group(1);
        String sizeStr = m.group(3);
        Integer size = (Integer) Evaluator.eval(sizeStr,c.getHighlight());
        if(size==null)
            return;
        Variables.defineArray(varName, size);
    }

    private void handleBooleanDeclaration(Command c) throws ParserException
    {
        String command = c.getLine();


        Matcher m = Pattern.compile(Language.BOOL_DECLARATION).matcher(command);
        m.find();
        String varName = m.group(1);
        Variables.defineCond(varName);
    }

    private void handleIntDeclaration(Command c) throws ParserException
    {

        String command = c.getLine();


        Matcher m = Pattern.compile(Language.INT_DECLARATION).matcher(command);
        m.find();
        String varName = m.group(1);
        Variables.defineNumber(varName);
    }

    private void handleReadExpression(Command c) {
        PETApp.Status = Constants.WAITING_FOR_USER_INPUT;
    }

    private void handleAssignFromReadIntoArray(Command c) throws ParserException{
        String command = c.getLine();
        Matcher m = Pattern.compile(Language.ASSIGN_FROM_READ_INTO_ARRAY).matcher(command);
        m.find();
        String varName = m.group(1).trim();

        String indexStr = m.group(3).trim();
        Integer index = (Integer) Evaluator.eval(indexStr,null);
        if(index == null)
            return;

        String valueStr = PETApp.userInput;

        Integer[] array = (Integer[]) Variables.get(varName);
        try {
            array[index] = Integer.parseInt(valueStr);
        } catch (NumberFormatException ne) {
            Errors.add(Errors.createInvalidUserInput(valueStr, c.getHighlight()));
            return;

        } catch (Exception e) {
            Errors.add(Errors.createIndexOutOfBoundsException(c.getHighlight()));
            return;
        }
        Variables.set(varName, array);
    }
    private void handleAssignFromReadIntoVariable(Command c) throws ParserException{

        String command = c.getLine();

        Matcher m = Pattern.compile(Language.ASSIGN_FROM_READ_INTO_VARIABLE).matcher(command);
        m.find();
        String varName = m.group(1).trim();
        String valueStr = PETApp.userInput;
        Object o = Variables.get(varName);
        if(o instanceof Integer) {
            Integer i;
            try { i = Integer.parseInt(valueStr);
            } catch (NumberFormatException e) {
                Errors.add(Errors.createInvalidUserInput(valueStr, c.getHighlight()));
                return;
            }
            Variables.set(varName, i);
        }
        else if(o instanceof Boolean) {
            Boolean b;
            if(valueStr.equals(Boolean.TRUE.toString())) {
                Variables.set(varName, Boolean.TRUE);
            } else if(valueStr.equals(Boolean.FALSE.toString())) {
                Variables.set(varName, Boolean.FALSE);
            } else {
                Errors.add(Errors.createInvalidUserInput(valueStr, c.getHighlight()));
                return;
            }
        }
        else {
            if(valueStr.contains("\"")) {
                Errors.add(Errors.createInvalidUserInput(valueStr, c.getHighlight()));
                return;
            }
            Variables.set(varName, valueStr);
        }
    }


    private void handleWriteExpression(Command c) throws ParserException {
        String command = c.getLine();
        Matcher m = Pattern.compile(Language.WRITE_EXPRESSION).matcher(command);
        m.find();
        String expr = m.group(1);
        Object o = Evaluator.eval(expr, c.getHighlight());
        if(o == null) return;
        expr = o.toString();
        expr = " " + expr;
        Program.getOut().print(expr);
    }

    private void handleWriteLiteralExpression(Command c) {
        String command = c.getLine();

        Matcher m = Pattern.compile(Language.WRITE_LITERAL).matcher(command);
        m.find();
        String expr = m.group(1);
        Program.getOut().print(expr);
    }
}