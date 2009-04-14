/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.parser;

import PET.model.*;

import PET.validator.Errors;
import PET.validator.PossibleErrors;
import PET.validator.Validator;
import java.util.regex.*;
import java.util.Vector;

/**
 * Responsible for flow control, declarations, and assignments
 * @author crayment
 */
public class Parser
{

    public static Parser instance;
    public static String program;
    // The lines and there corresponding indexes in the program String
    public static Vector lines = new Vector(),  indexes = new Vector();
    public static final String VALID_NAME = "([a-zA-Z_]+)";
    public static final String LINE = "((.*)\\n)|((.*)$)"; // a line or up to the end of the string (so that it doesn't require a new line at the end)
    public static final String READ = "Read";
    public static final String WRITE = "Write";
    public static final String WRITE_LINE = "WriteLine";
    public static final String STARTS_WITH_READ = READ + "\\s*((.*))";
    public static final String STARTS_WITH_WRITE = WRITE + "(\\s+((.*)))?";
    public static final String STARTS_WITH_WRITE_LINE = WRITE_LINE + "(\\s+((.*)))?";
    public static final String INTEGER_DECLARATOR = "int";
    public static final String INTEGER_ARRAY_DECLARATOR = "array";
    public static final String BOOLEAN_DECLARATOR = "bool";
    public static final String STRING_DECLARATOR = "string";
    public static final String VARIABLE_DECLARATION_INTEGER_SINGLE = INTEGER_DECLARATOR + "\\s*([a-zA-Z_]+)\\s*(=\\s*(([^,]*)))?";
    public static final String VARIABLE_DECLARATION_BOOLEAN_SINGLE = BOOLEAN_DECLARATOR + "\\s*([a-zA-Z_]+)\\s*(=\\s*(([^,]*)))?";
    public static final String VARIABLE_DECLARATION_STRING_SINGLE = STRING_DECLARATOR + "\\s*(([a-zA-Z_]+))\\s*(=\\s*(([^,]*)))?";
    public static final String VARIABLE_DECLARATION_INTEGER_ARRAY_SINGLE = INTEGER_ARRAY_DECLARATOR + "\\s*(([a-zA-Z_]+))\\s*\\[(([^]]+))\\]";
    public static final String VARIABLE_DECLARATION_MULTIPLE_ON_ONE_LINE = "(" + INTEGER_DECLARATOR + "|" + BOOLEAN_DECLARATOR + "|" + INTEGER_ARRAY_DECLARATOR + ")" + "\\s*([a-zA-Z_]+)(\\[[^]]+\\])?\\s*(=\\s*(([^,]*)))?(\\s*,\\s*([a-zA-Z_]+)(\\[[^]]+\\])?\\s*(=\\s*(([^,]*)))?)+";
    public static final String VARIABLE_ASSIGNMENT = "([a-zA-Z_]+)\\s*=\\s*((.+))";
    public static final String ARRAY_ASSIGNMENT = "([a-zA-Z_]+)\\s*\\[([^]]+)\\]\\s*=\\s*((.+))";
    public static final String DEFINE = "define";
    public static final String FUNCTION_DECLARATION = DEFINE + "\\s*(([a-zA-Z_]+))\\s*\\{\\s*";
    public static final String CALL = "call";
    public static final String FUNCTION_CALL = CALL + "\\s*(([a-zA-Z_]+))\\s*$";
    public static final String WHILE = "while";
    public static final String WHILE_LOOP = WHILE + "\\s*\\(((.+))\\)\\s*\\{";
    public static final String IF = "if";
    public static final String IF_STATEMENT = IF + "\\s*\\(((.+))\\)\\s*\\{";
    public static final String ELSE = "else";
    public static final String ELSE_STATEMENT =  ELSE + "\\s*\\{";
    public static final String GET_NEXT_WRITE_ARG = "(\\\"([^\"]*?)\\\"((.*)))|(([a-zA-Z_]+\\s*\\[([^]]+)\\])((.*)))|((([a-zA-Z_]+))((.*)))";
    public static final String GET_NEXT_READ_ARG = "((([a-zA-Z_]+)\\s*\\[([^]]+)\\])((.*)))|((([a-zA-Z_]+))((.*)))";
    public static final String COMMENT = "#";
    public static final String FILL_ARRAY = "FillArray";
    public static final String FILL_ARRAY_STATEMENT = FILL_ARRAY + "\\s*(([a-zA-Z_]+))\\s*$";






    private static enum positions { GLOBAL, AFTER_FIRST_FUNCTION, IN_FUNCTION };

    private static positions POSITION = positions.GLOBAL;


    /**
     * @return the program
     */
    public static String getProgram() {
        return program;
    }


    /**
     * constructor does nothing but prevent multiple instances
     * required for singleton
     */
    private Parser() {
    }

    /**
     * get the instance of the singleton class
     * @return the instance
     */
    public static Parser getInstance() {
        if (instance == null)
        {
            instance = new Parser();
        }
        return instance;
    }



    /**
     * Loads the program. Must be called before calling compile.
     * The same as calling <code>load(String program, PrintStream out, InputStream in)</code>
     * with System.out and System.in for out and in respectively.
     * @param program the string containing the program.
     */
    public static void load(String program) {
        reset();

        Parser.program = program;


        // Set up the lines Vector and the coresponding indexes vector
        int runningIndex = 0;
        Matcher m = Pattern.compile(LINE).matcher(program);
        while (m.find())
        {
            if (m.group(2) != null)
            {
                lines.addElement(m.group(2));
            } else if (m.group(3) != null)
            {
                lines.addElement(m.group(3));
            } else
            {
                continue;
            }
            indexes.addElement(runningIndex);
            runningIndex += m.group().length();
        }
    }

    public static void reset() {
        lines.clear();
        indexes.clear();
        POSITION = positions.GLOBAL;
        Validator.reset();
    }

    /**
     * Compiles the program into our language.
     */
    public static void compile() {
        // the block containing everything outside of functions (global block)
        // can never be called so the highlight info is null
        Block global = new Block("global");


        for (int i = 0; i < lines.size(); i++) {
            String line = lines.elementAt(i).toString();

            // set up highlight object that will be used in most (if not all) cases.
            int startIndex = (Integer) indexes.elementAt(i);
            int length = startIndex + line.length();
            Highlight h = new Highlight(startIndex, length, i+1);

            // strip out comments
            int commentIndex = line.indexOf(COMMENT);
            if(commentIndex >= 0) {
                line = line.substring(0, commentIndex);
            }

            if (line.trim().equals("")) { //skip blank lines
                
            } else if (line.trim().matches(VARIABLE_DECLARATION_INTEGER_SINGLE)) {
                translateSingleIntegerDeclaration(line, i, h, global);

            } else if (line.trim().matches(VARIABLE_DECLARATION_BOOLEAN_SINGLE)) {
                translateSingleBooleanDeclaration(line, i, h, global);

            } else if (line.trim().matches(VARIABLE_DECLARATION_INTEGER_ARRAY_SINGLE)) {
                translateSingleArrayDeclaration(line, i, h, global);

            } else if (line.trim().matches(VARIABLE_DECLARATION_STRING_SINGLE)) {
                translateStringDeclaration(h, line, global);

            } else if (line.trim().matches(VARIABLE_DECLARATION_MULTIPLE_ON_ONE_LINE)) {
                translateMultipleVariablesDeclarationsOnOneLine(line, i, h, global);

            } else if (line.trim().matches(VARIABLE_ASSIGNMENT)) {
                translateVariableAssignment(line, i, h, global);

            } else if (line.trim().matches(ARRAY_ASSIGNMENT)) {
                translateArrayAssignment(line, i, h, global);

            } else if (line.trim().matches(FUNCTION_DECLARATION)) {
                i = translateFunctionDeclaration(line, i, global, h);
                POSITION = positions.AFTER_FIRST_FUNCTION;

            } else if (line.trim().matches(FUNCTION_CALL)) {
                Errors.add(Errors.createInvalidPositionError("function call", "outside of function", h));

            } else if (line.trim().matches(IF_STATEMENT)) {
                Errors.add(Errors.createInvalidPositionError("if statement", "outside of function", h));

            } else if (line.trim().matches(WHILE_LOOP)) {
                Errors.add(Errors.createInvalidPositionError("while loop", "outside of function", h));

            } else if (line.trim().matches(STARTS_WITH_WRITE)) {
                translateWrite(line, global, h);

            } else if (line.trim().matches(STARTS_WITH_WRITE_LINE)) {
                translateWriteLine(line, global, h);

            } else if (line.trim().matches(STARTS_WITH_READ)) {
                translateReadCommand(line, h, global);

            } else if (line.trim().matches((FILL_ARRAY_STATEMENT))) {
                translateFillArray(line, h, global);

            } else {
                Validator.addAppropriateError(line, h);

            }
        }
        global.add(Language.createMainCall());
        Program.setGlobal(global);

        PossibleErrors.removeFixedPossibleErrors();
    }

    /**
     * Translates a block into our language, storing it in a block and adding
     * it to the program.
     * @param name The name of the block, a function name or simply while, if, ...
     * @param blockStartIndex The start index of the block in the lines Vector.
     * @param blockEndIndex The ending index of the block in the lines Vector.
     * @param createStartHighlight true if you want a the first line to be an
     * empty command with a highlight for the start of the block
     */
    private static Block createBlock(String name, int blockStartIndex, int blockEndIndex, boolean createStartHighlight)
    {
        //create block, get id
        Block block = new Block(name);
        int blockID = block.getId();

        if(createStartHighlight){
            // from the first line of the block we create an empty command that does nothing
            // but contains the highlight for the start of the block
            String firstLine = lines.elementAt(blockStartIndex).toString();
            int si = (Integer) indexes.elementAt(blockStartIndex);
            int ei = si + firstLine.length();
            Highlight firstH = new Highlight(si, ei, blockStartIndex+1);
            // create empty command and add it to the block
            Command c = Language.createEmptyHighlightCommand(firstH);
            block.add(c);
        }
        


        for (int i = blockStartIndex+1; i < blockEndIndex; i++)
        {
            String line = lines.elementAt(i).toString();

            // set up highlight object that will be used in most (if not all) cases.
            Highlight h = createHighlightForLine(i);
            
            // strip out comments
            int commentIndex = line.indexOf(COMMENT);
            if(commentIndex >= 0) {
                line = line.substring(0, commentIndex);
            }

            if (line.trim().equals("")) //skip blank lines
            {
            } else if (line.trim().matches(VARIABLE_ASSIGNMENT))
            {
                translateVariableAssignment(line, i, h, block);
            } else if (line.trim().matches(ARRAY_ASSIGNMENT))
            {
                translateArrayAssignment(line, i, h, block);
            } else if (line.trim().matches(FUNCTION_CALL))
            {
                translateFunctionCall(line, i, h, block);
            } else if (line.trim().matches(IF_STATEMENT))
            {
                i = translateIfElse(line, i, h, block);
            } else if (line.trim().matches(WHILE_LOOP))
            {
                i = translateWhile(line, i, h, block);
            } else if (line.trim().matches(STARTS_WITH_WRITE))
            {
                translateWrite(line, block, h);
            } else if (line.trim().matches(STARTS_WITH_WRITE_LINE))
            {
                translateWriteLine(line, block, h);
            } else if (line.trim().matches(STARTS_WITH_READ))
            {
                translateReadCommand(line, h, block);
            } else if (line.trim().matches(VARIABLE_DECLARATION_INTEGER_SINGLE))
            {
                Errors.add(Errors.createInvalidPositionError("variable declaration", "inside of function", h));
            } else if (line.trim().matches(VARIABLE_DECLARATION_BOOLEAN_SINGLE))
            {
                Errors.add(Errors.createInvalidPositionError("variable declaration", "inside of function", h));
            } else if (line.trim().matches(VARIABLE_DECLARATION_STRING_SINGLE)) {

                Errors.add(Errors.createInvalidPositionError("variable declaration", "inside of function", h));
            } else if (line.trim().matches(VARIABLE_DECLARATION_INTEGER_ARRAY_SINGLE))
            {
                Errors.add(Errors.createInvalidPositionError("variable declaration", "inside of function", h));
            } else if (line.trim().matches(VARIABLE_DECLARATION_MULTIPLE_ON_ONE_LINE))
            {
                Errors.add(Errors.createInvalidPositionError("variable declaration", "inside of function", h));
            } else if (line.trim().matches(FUNCTION_DECLARATION))
            {
                Errors.add(Errors.createInvalidPositionError("function declaration", "inside of function", h));
            } else if (line.trim().matches((FILL_ARRAY_STATEMENT))) {
                translateFillArray(line, h, block);

            } else
            {
                Validator.addAppropriateError(line, h);
            }
        }

        String lastLine = lines.elementAt(blockEndIndex).toString();
        int si = (Integer) indexes.elementAt(blockEndIndex);
        int ei = si + lastLine.length();
        Highlight lastH = new Highlight(si, ei, blockEndIndex+1);
        // create empty command and add it to the block
        Command c = Language.createEmptyHighlightCommand(lastH);
        block.add(c);

        return block;
    }


    private static Highlight createHighlightForLine(int index)
    {
        int startIndex = (Integer) indexes.elementAt(index);
        int endIndex = startIndex + lines.elementAt(index).toString().length();
        Highlight h = new Highlight(startIndex, endIndex, index+1);
        return h;
    }

    private static void createWriteCommand(Block b, String toWrite, Highlight h, boolean writeln)
    {
        toWrite = toWrite.trim();

        if(toWrite == null || toWrite.equals(""))
        {
            if(writeln)
            {
                Command c = new Command(Language.createWriteLine(), h);
                b.add(c);
            }
            else
            {
                Command c = new Command("", h);
                b.add(c);
            }
            return;
        }

        if(toWrite.matches(GET_NEXT_WRITE_ARG))
        {

            Matcher m = Pattern.compile(GET_NEXT_WRITE_ARG).matcher(toWrite);
            m.find();
            if(m.group(2) != null)
            {
                String literal = m.group(2);
                Command c = new Command(Language.createWriteLiteral(literal), h);
                b.add(c);

                String rest = m.group(3);
                createWriteCommand(b, rest, null, writeln);
            }
            else if(m.group(6) != null)
            {
                String arrayAccess = m.group(6);  Validator.validate(arrayAccess, h);

                Command c = new Command(Language.createWriteExpr(arrayAccess), h);
                b.add(c);

                String rest = m.group(8);
                createWriteCommand(b, rest, null, writeln);
            }
            else if(m.group(11) != null)
            {
                String var = m.group(11); Validator.validate(var, h);

                Command c = new Command(Language.createWriteExpr(var), h);
                b.add(c);

                String rest = m.group(13);
                createWriteCommand(b, rest, null, writeln);

            }
            else
            {
                Errors.add(Errors.createInvalidWriteLine(h));
            }
        }
        else
        {
            Errors.add(Errors.createInvalidWriteLine(h));
        }

    }

    private static void createReadCommand(Block b, String toRead, Highlight h) {

        toRead = toRead.trim();
        if(toRead.matches(GET_NEXT_READ_ARG)) {

            Matcher m = Pattern.compile(GET_NEXT_READ_ARG).matcher(toRead);
            m.find();

            if(m.group(2) != null) {
                String arrayAccess = m.group(2);  Validator.validate(arrayAccess, h);
                String arrayname = m.group(3);
                String arrayIndex = m.group(4);

                Command c = new Command(Language.createReadExpr(), h);
                b.add(c);

                Highlight h2 = new Highlight(h.getStartIndex(), h.getEndIndex(), h.getLineNumber());
                c = new Command(Language.createAssignFromReadIntoArray(arrayname, arrayIndex), h2);
                b.add(c);

                String rest = m.group(5);
                if(rest != null && !rest.equals("")) {
                    Errors.add(Errors.createInvalidReadLine(h));
                }
            }
            else if(m.group(8) != null) {
                String var = m.group(8); Validator.validate(var, h);

                Command c = new Command(Language.createReadExpr(), h);
                b.add(c);

                Highlight h2 = new Highlight(h.getStartIndex(), h.getEndIndex(), h.getLineNumber());
               c = new Command(Language.createAssignFromReadIntoVariable(var), h2);
                b.add(c);

                String rest = m.group(10);
                if(rest != null && !rest.equals("")) {
                    Errors.add(Errors.createInvalidReadLine(h));
                }

            }
            else {
                Errors.add(Errors.createInvalidReadLine(h));
                return;
            }
        }
        else {
            Errors.add(Errors.createInvalidReadLine(h));
            return;
        }
    }


    private static int getStartIndexOfElse(int endOfBlockIndex) {

        String line = lines.elementAt(endOfBlockIndex).toString();
        Matcher m = Pattern.compile(ELSE_STATEMENT).matcher(line);
        if(m.find()) // the else was on the same line as the }
        {
            return endOfBlockIndex;
        }
        else {
            if(!lines.elementAt(endOfBlockIndex).toString().trim().equals("}")){
                Errors.add(Errors.createTextOnClosingBracketLine(createHighlightForLine(endOfBlockIndex)));
            }
        }

        do{
            endOfBlockIndex++;
        }
        while(endOfBlockIndex < lines.size() && lines.elementAt(endOfBlockIndex).toString().trim().equals(""));

        if(endOfBlockIndex < lines.size())
        {
            line = lines.elementAt(endOfBlockIndex).toString();
            if(line.trim().matches(ELSE_STATEMENT))
            {
                return endOfBlockIndex;
            }
            return lines.size();
        }
        else
            return endOfBlockIndex;
    }



    private static void translateFillArray(String line, Highlight h, Block block) {
        Matcher m = Pattern.compile(FILL_ARRAY_STATEMENT).matcher(line);
        m.find();

        String name = m.group(1);

        Validator.validateVariableType(name, new Integer[0], h);

        Command c = new Command(Language.createFillArrayRandom(name), h);

        block.add(c);
    }


    private static void translateStringDeclaration(Highlight h, String line, Block global) {
        if (POSITION == positions.AFTER_FIRST_FUNCTION) {
            Errors.add(Errors.createInvalidPositionError("string declaration", "after first function", h));
            return;
        }
        Matcher m;
        m = Pattern.compile(VARIABLE_DECLARATION_STRING_SINGLE).matcher(line);
        m.find();
        String name = m.group(1).trim();
        Validator.validateNewVariableName(name, new String(), h);
        String command = Language.createStringDeclaration(name);
        Command c = new Command(command, h);
        global.add(c);
        if (m.group(4) != null) {
            String value = m.group(4).trim();
            Validator.validateVariableAssignment(name, value, h);
            command = Language.createVariableAssignment(name, value);
            c = new Command(command);
            global.add(c);
        }
    }


    private static void translateSingleArrayDeclaration(String line, int i, Highlight h, Block b)
    {
        if(POSITION == positions.AFTER_FIRST_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("array declaration", "after first function", h));
        }
        
        Matcher m;
        String command;
        m = Pattern.compile(VARIABLE_DECLARATION_INTEGER_ARRAY_SINGLE).matcher(line);
        m.find();
        String arrayName = m.group(1).trim();
        String sizeStr = m.group(3).trim();

        Validator.validateNewVariableName(arrayName, new Integer[] {}, h);

        Validator.validateArrayDeclaration(arrayName, sizeStr, h);

        command = Language.createArrayDeclaration(arrayName, sizeStr);
        Command c = new Command(command, h);
        b.add(c);
    }

    private static void translateSingleBooleanDeclaration(String line, int i, Highlight h, Block b)
    {
        if(POSITION == positions.AFTER_FIRST_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("boolean declaration", "after first function", h));
        }

        Matcher m;
        m = Pattern.compile(VARIABLE_DECLARATION_BOOLEAN_SINGLE).matcher(line);
        m.find();
        String name = m.group(1).trim();

        Validator.validateNewVariableName(name, new Boolean(true), h);

        String command = Language.createBoolDeclaration(name);
        Command c = new Command(command, h);
        b.add(c);

        if (m.group(3) != null)
        {
            String value = m.group(3).trim();

            Validator.validateVariableAssignment(name, value, h);
            
            command = Language.createVariableAssignment(name, value);
            c = new Command(command);
            b.add(c);
        }
    }

    private static void translateSingleIntegerDeclaration(String line, int i, Highlight h, Block b)
    {
        if(POSITION == positions.AFTER_FIRST_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("integer declaration", "after first function", h));
        }

        Matcher m;
        m = Pattern.compile(VARIABLE_DECLARATION_INTEGER_SINGLE).matcher(line);
        m.find();
        String name = m.group(1).trim();
        
        Validator.validateNewVariableName(name, new Integer(0), h);

        String command = Language.createIntDeclaration(name);
        Command c = new Command(command, h);
        b.add(c);

        if (m.group(3) != null)
        {
            String value = m.group(3).trim();

            Validator.validateVariableAssignment(name, value, h);
            
            command = Language.createVariableAssignment(name, value);
            c = new Command(command);
            b.add(c);
        }
    }

    private static void translateMultipleVariablesDeclarationsOnOneLine(String line, int i, Highlight h, Block b)
    {
        Matcher m;
        m = Pattern.compile(VARIABLE_DECLARATION_MULTIPLE_ON_ONE_LINE).matcher(line);
        m.find();
        String[] singleDeclarations = m.group().split(",");
        if (m.group(1).trim().equals(INTEGER_DECLARATOR))
        {
            String l = singleDeclarations[0];
            translateSingleIntegerDeclaration(l, i, h, b);
            for (int j = 1; j < singleDeclarations.length; j++)
            {
                translateSingleIntegerDeclaration(INTEGER_DECLARATOR + singleDeclarations[j], i, h, b);
            }
        } else if (m.group(1).trim().equals(BOOLEAN_DECLARATOR))
        {
            String l = singleDeclarations[0];
            translateSingleBooleanDeclaration(l, i, h, b);
            for (int j = 1; j < singleDeclarations.length; j++)
            {
                translateSingleBooleanDeclaration(BOOLEAN_DECLARATOR + singleDeclarations[j], i, h, b);
            }
        } else if (m.group(1).trim().equals(INTEGER_ARRAY_DECLARATOR))
        {
            String l = singleDeclarations[0];
            translateSingleArrayDeclaration(l, i, h, b);
            for (int j = 1; j < singleDeclarations.length; j++)
            {
                translateSingleArrayDeclaration(INTEGER_ARRAY_DECLARATOR + singleDeclarations[j], i, h, b);
            }
        }
    }

    private static void translateArrayAssignment(String line, int i, Highlight h, Block b)
    {

        if(POSITION == positions.AFTER_FIRST_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("array assignment", "after first function", h));
        }
        
        Matcher m;
        m = Pattern.compile(ARRAY_ASSIGNMENT).matcher(line);
        m.find();
        String varName = m.group(1);
        String indexStr = m.group(2);
        String newValueStr = m.group(3);

        Validator.validateArrayAssignment(varName, indexStr, newValueStr, h);

        
        String command = Language.createArrayAssignment(varName, indexStr, newValueStr);
        Command c = new Command(command, h);
        b.add(c);
    }

    private static void translateVariableAssignment(String line, int i, Highlight h, Block b)
    {
        if(POSITION == positions.AFTER_FIRST_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("variable assignment", "after first function", h));
        }

        Matcher m;
        String command;
        m = Pattern.compile(VARIABLE_ASSIGNMENT).matcher(line);
        m.find();
        String varName = m.group(1);
        String value = m.group(2);

        Validator.validateVariableAssignment(varName, value, h);


        command = Language.createVariableAssignment(varName, value);
        Command c = new Command(command, h);
        b.add(c);
    }

    private static int translateFunctionDeclaration(String line, int i, Block b, Highlight h)
    {
        if(POSITION == positions.IN_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("function declaration", "in function", h));
        }
        POSITION = positions.IN_FUNCTION;
        
        Matcher m = Pattern.compile(FUNCTION_DECLARATION).matcher(line);
        m.find();
        String functionName = m.group(1); Validator.validateFunctionName(functionName, h);
        int endOfBlockIndex = lines.size();
        int spread = 1;
        for (int j = i+1; j < lines.size(); j++)
        {
            if (((String) lines.elementAt(j)).contains("}"))
            {
                spread--;
            }
            if (spread == 0)
            {
                endOfBlockIndex = j;
                break;
            }
            if (((String) lines.elementAt(j)).contains("{"))
            {
                spread++;
            }
        }
        if (endOfBlockIndex < lines.size())
        {
            if(!lines.elementAt(endOfBlockIndex).toString().trim().equals("}")){
                Errors.add(Errors.createTextOnClosingBracketLine(createHighlightForLine(endOfBlockIndex)));
            }

            Block functionBlock = createBlock(functionName, i, endOfBlockIndex, true);
            Program.addFunctionBlock(functionBlock, h);
            i = endOfBlockIndex;
        } else
        {
            Errors.add(Errors.createMissingClosingBracketError(h));
        }
        POSITION = positions.GLOBAL;
        return i;
    }

    private static void translateFunctionCall(String line, int i, Highlight h, Block block)
    {
        if(POSITION != positions.IN_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("function Call", "outside of a function", h));
        }

        Matcher m = Pattern.compile(FUNCTION_CALL).matcher(line);
        m.find();
        String funcName = m.group(1);
        Validator.validateFunctionCall(funcName, h);
        String command = Language.createFunctionCall(funcName);
        Command c = new Command(command, h);
        block.add(c);
    }

    private static int translateIfElse(String line, int i, Highlight h, Block block) {
        if(POSITION != positions.IN_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("if/else", "outside of a function", h));
        }

        Matcher m = Pattern.compile(IF_STATEMENT).matcher(line);
        m.find();
        String expr = m.group(1); Validator.validateIfExpression(expr, h);
        // find the end index of the if
        int endOfIfBlockIndex = lines.size();
        int spread = 1;
        for (int j = i + 1; j < lines.size(); j++) {
            if (((String) lines.elementAt(j)).contains("}")) {
                spread--;
            }
            if (spread == 0) {
                endOfIfBlockIndex = j;
                break;
            }
            if (((String) lines.elementAt(j)).contains("{")) {
                spread++;
            }
        }
        // if it was found
        if (endOfIfBlockIndex < lines.size()) {
            
            int possibleElseBlockIndex = getStartIndexOfElse(endOfIfBlockIndex);
            boolean isElseBlock = ((possibleElseBlockIndex > 0) && (possibleElseBlockIndex < lines.size())) ? true : false;

            if (!isElseBlock) {
                if(!lines.elementAt(endOfIfBlockIndex).toString().trim().equals("}")){
                    Errors.add(Errors.createTextOnClosingBracketLine(createHighlightForLine(endOfIfBlockIndex)));
                }

                // create if block
                Block ifBlock = createBlock("if", i, endOfIfBlockIndex, false);
                Program.addBlock(ifBlock);
                String blockCall = Language.createBlockCall(ifBlock.getId());
                Command condCommand = new Command(Language.createCond(expr, blockCall), h);
                block.add(condCommand);
                i = endOfIfBlockIndex; // skip the rest of the block
            } else {
                // find the end index of the else
                int endOfElseBlockIndex = lines.size();
                spread = 1;
                for (int j = possibleElseBlockIndex + 1; j < lines.size(); j++) {
                    if (((String) lines.elementAt(j)).contains("}")) {
                        spread--;
                    }
                    if (spread == 0) {
                        endOfElseBlockIndex = j;
                        break;
                    }
                    if (((String) lines.elementAt(j)).contains("{")) {
                        spread++;
                    }
                }
                // if it was found
                if (endOfElseBlockIndex < lines.size()) {
                    if(!lines.elementAt(endOfElseBlockIndex).toString().trim().equals("}")){
                        Errors.add(Errors.createTextOnClosingBracketLine(createHighlightForLine(endOfElseBlockIndex)));
                    }

                    // create if block
                    Block ifBlock = createBlock("if", i, endOfIfBlockIndex, false);
                    Program.addBlock(ifBlock);
                    // create else block
                    Block elseBlock = createBlock("else", possibleElseBlockIndex, endOfElseBlockIndex, true);
                    Program.addBlock(elseBlock);
                    String ifBlockCall = Language.createBlockCall(ifBlock.getId());
                    String elseBlockCall = Language.createBlockCall(elseBlock.getId());
                    String cond = Language.createCond(expr, ifBlockCall);
                    cond = Language.appendToCond(cond, Boolean.TRUE.toString(), elseBlockCall);
                    Command condCommand = new Command(cond, h);
                    block.add(condCommand);
                    i = endOfElseBlockIndex; // skip the rest of the block
                } else {
                    Errors.add(Errors.createMissingClosingBracketError(h));
                }
            }
        } else {
            Errors.add(Errors.createMissingClosingBracketError(h));
        }
        return i;
    }

    private static int translateWhile(String line, int i, Highlight h, Block block){
        if(POSITION != positions.IN_FUNCTION){
            Errors.add(Errors.createInvalidPositionError("while", "outside of a function", h));
        }

        Matcher m = Pattern.compile(WHILE_LOOP).matcher(line);
        m.find();
        String expr = m.group(1);
        Validator.validateWhileExpression(expr, h);
        // find the end index of the loop
        int endOfBlockIndex = lines.size();
        int spread = 1;
        for (int j = i + 1; j < lines.size(); j++) {
            if (((String) lines.elementAt(j)).contains("}")) {
                spread--;
            }
            if (spread == 0) {
                endOfBlockIndex = j;
                break;
            }
            if (((String) lines.elementAt(j)).contains("{")) {
                spread++;
            }
        }
        // if it is found
        if (endOfBlockIndex < lines.size()) {
            if(!lines.elementAt(endOfBlockIndex).toString().trim().equals("}")){
                Errors.add(Errors.createTextOnClosingBracketLine(createHighlightForLine(endOfBlockIndex)));
            }

            // create while block
            Block whileBlock = createBlock("while", i, endOfBlockIndex, false);
            Program.addBlock(whileBlock);
            //cond expr \n call while
            String blockCall = Language.createBlockCall(whileBlock.getId());
            Command loopCommand = new Command(Language.createLoop(expr, blockCall), h);
            block.add(loopCommand);
            i = endOfBlockIndex;
        } else {
            Errors.add(Errors.createMissingClosingBracketError(h));
        }
        return i;
    }

    private static void translateReadCommand(String line, Highlight h, Block block){
        
        Matcher m = Pattern.compile(STARTS_WITH_READ).matcher(line);
        m.find();
        String targets = m.group(1) == null ? "" : m.group(1);
        
        createReadCommand(block, targets, h);
            
    }

    private static void translateWrite(String line, Block block, Highlight h) {
        Matcher m = Pattern.compile(STARTS_WITH_WRITE).matcher(line);
        m.find();
        String toWrite = m.group(2) == null ? "" : m.group(2);
        // hack to get the number of " symbols in string, take out everything that is not a " and then get the length
        int numberOfQuotes = toWrite.replaceAll("[^\"]", "").length();
        if(numberOfQuotes % 2 != 0)
            Errors.add(Errors.createUnclosedQuotationInWrite(h));
        else
            createWriteCommand(block, toWrite, h, false);
    }

    private static void translateWriteLine(String line, Block block, Highlight h) {
        Matcher m = Pattern.compile(STARTS_WITH_WRITE_LINE).matcher(line);
        m.find();
        String toWrite = m.group(2) == null ? "" : m.group(2);
        // hack to get the number of " symbols in string, take out everything that is not a " and then get the length
        int numberOfQuotes = toWrite.replaceAll("[^\"]", "").length();
        if(numberOfQuotes % 2 != 0)
            Errors.add(Errors.createUnclosedQuotationInWrite(h));
        else
            createWriteCommand(block, toWrite, h, true);
    }

    
}
