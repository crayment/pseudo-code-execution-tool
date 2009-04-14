/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.parser;
import PET.model.Command;
import PET.model.Highlight;
import PET.model.Program;
import PET.validator.Errors;

/**
 * A place to have our opcode language specification and helper creation methods
 * @author crayment
 */
public class Language
{
    public static final String SP = " ";
    public static final String MAIN = "main";
    public static final String CALL = "call";
    public static final String CALL_MAIN = CALL + SP + MAIN;
    public static final String EMPTY = "";
    public static final String INNER_SEPERATOR = ":";
    public static final String OUTER_SEPERATOR = ",";
    public static final String ANYTHING = "((.*))";
    public static final String DECLARE = "declare";
    public static final String ARRAY = "array";
    public static final String ASSIGN = "assign";
    public static final String INT = "int";
    public static final String BOOL = "bool";
    public static final String STRING = "string";
    public static final String FUNCTION = "function";
    public static final String COND = "cond";
    public static final String LOOP = "loop";
    public static final String LITERAL = "literal";
    public static final String EXPR_SYMB = "|";
    public static final String VALID_EXPR = "\\|(([^|]+))\\|";
    public static final String VALID_NAME = "(([a-zA-Z_]+))";
    public static final String VALID_NUMBER = "((-)?\\d+)";
    public static final String WRITE = "write";
    public static final String WRITELN = "writeln";
    public static final String READ = "read";
    public static final String ASSIGN_FROM_READ = "assign_from_read";
    public static final String FILL_ARRAY_RANDOM = "fill_array_random";

    
    /* Declarations */
    public static final String INT_DECLARATION = DECLARE + SP + INT + SP + VALID_NAME;
    // Group 1 - name
    public static final String BOOL_DECLARATION = DECLARE + SP + BOOL + SP + VALID_NAME;
    // Group 1 - name
    public static final String STRING_DECLARATION = DECLARE + SP + STRING + SP + VALID_NAME;

    public static final String ARRAY_DECLARATION = DECLARE + SP + ARRAY + SP + VALID_NAME + SP + VALID_EXPR;
    // Group 1 - name, Group 3 - size expr

    /* Assignments */
    public static final String VARIABLE_ASSIGNMENT = ASSIGN + SP + VALID_NAME + SP + VALID_EXPR;
    //Group 1 - name, Group 3 - value expr
    public static final String ARRAY_ASSIGNMENT = ASSIGN + SP + VALID_NAME + SP + VALID_EXPR + SP + VALID_EXPR;
    //Group 1 - name, Group 3 - index expr, Group 5 - value expr
    /* Calls */
    public static final String FUNCTION_CALL = CALL + SP + VALID_NAME;
    // Group 1 - name
    public static final String BLOCK_CALL = CALL + SP + VALID_NUMBER;
    // Group 1 - number to call

    /* Conditional */
    public static final String CONDITION_EXPRESSION = COND + ANYTHING; // could be made more strict but since we are creating the strings coming in and its the only command starting with cond...
    // Group 1 - conditional command pairs seperated by OUTER_SEPERATOR
    public static final String LOOP_EXPRESSION = LOOP + SP + VALID_EXPR + INNER_SEPERATOR + ANYTHING;
    // Group 1 - expr

    /* Writes */
    public static final String WRITE_EXPRESSION = WRITE + SP + VALID_EXPR;
    // Group 1 - expr
    public static final String WRITE_LITERAL = WRITE + SP + LITERAL + SP + ANYTHING;
    // Group 1 - anything
    public static final String WRITE_LINE = WRITELN;
    // no groups needed

    /* Reads */
    public static final String READ_EXPRESSION = READ;
    public static final String ASSIGN_FROM_READ_INTO_ARRAY = ASSIGN_FROM_READ + SP + VALID_NAME + SP + VALID_EXPR;
    public static final String ASSIGN_FROM_READ_INTO_VARIABLE = ASSIGN_FROM_READ + SP + VALID_NAME;

    /* fill array */
    public static final String FILL_ARRAY_RND_EXPRESSION = FILL_ARRAY_RANDOM + SP + VALID_NAME;
    


    public static String createReadExpr()
    {
        return new String(READ);
    }


    public static String createWriteLine()
    {
        return new String(WRITE_LINE);
    }

    public static String createWriteExpr(String expr)
    {
        return new String(WRITE + SP + EXPR_SYMB + expr + EXPR_SYMB);
    }

    public static String createWriteLiteral(String literal)
    {
        return new String(WRITE + SP + LITERAL + SP + literal);
    }


    // declare int name
    public static String createIntDeclaration(String name)
    {
        return new String(DECLARE + SP + INT + SP + name);
    }
    // declare bool name

    public static String createBoolDeclaration(String name)
    {
        return new String(DECLARE + SP + BOOL + SP + name);
    }

    public static String createStringDeclaration(String name)
    {
        return new String(DECLARE + SP + STRING + SP + name);
    }

    // declare array name |size|
    public static String createArrayDeclaration(String name, String size)
    {
        return new String(DECLARE + SP + ARRAY + SP + name + SP + EXPR_SYMB + size + EXPR_SYMB);
    }

    // assign name |value|
    public static String createVariableAssignment(String name, String value)
    {
        return new String(ASSIGN + SP + name + SP + EXPR_SYMB + value + EXPR_SYMB);
    }

    // assign array name |index|:|value|
    public static String createArrayAssignment(String name, String index, String value)
    {
        return new String(ASSIGN + SP + name + SP + EXPR_SYMB + index + EXPR_SYMB + SP + EXPR_SYMB + value + EXPR_SYMB);
    }

    static String createAssignFromReadIntoVariable(String var) {
        return new String(ASSIGN_FROM_READ + SP + var);
    }

    static String createAssignFromReadIntoArray(String name, String index) {
        return new String(ASSIGN_FROM_READ + SP + name + SP + EXPR_SYMB + index + EXPR_SYMB);
    }

    // call number
    static String createBlockCall(int block)
    {
        return new String(CALL + SP + block);
    }

    static String createCond(String expr, String command)
    {
        return new String(COND + SP + EXPR_SYMB + expr + EXPR_SYMB + INNER_SEPERATOR + command);
    }

    static String appendToCond(String cond, String expr, String command)
    {
        return new String(cond + OUTER_SEPERATOR + EXPR_SYMB + expr + EXPR_SYMB + INNER_SEPERATOR + command);
    }

    static String createLoop(String expr, String command)
    {
        return new String(LOOP + SP + EXPR_SYMB + expr + EXPR_SYMB + INNER_SEPERATOR + command);
    }

    static Command createEmptyHighlightCommand(Highlight highlight) {
        return new Command(EMPTY, highlight);
    }

    // call function name
    static String createFunctionCall(String name)
    {
        return new String(CALL + SP + name);
    }

    static String createFillArrayRandom(String name) {
        return new String(FILL_ARRAY_RANDOM + SP + name);
    }

    static Command createMainCall()
    {
        if (!Program.isFunctionDefined(MAIN))
        {
            Errors.add(Errors.createMissingMainError());
        }

        Command c = new Command(CALL_MAIN);
        return c;
    }

}
