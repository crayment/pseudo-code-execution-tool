/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.validator;

import PET.Constants;
import PET.model.*;
import PET.parser.Evaluator;
import PET.parser.Parser;
import java.util.Vector;

/**
 *
 * @author crayment
 */
public final class Errors {

    private static Errors instance;



    private Errors(){}

    public static synchronized Errors getInstance(){
        if(instance == null){
            instance = new Errors();
        }
        return instance;
    }

    private static Vector errors = new Vector();

    public static void add(PETError e){
        errors.add(e);
    }
    
    public static void remove(PETError e){
        errors.remove(e);
    }

    public static int numberOfErrors()
    {
        return errors.size();
    }

    public static PETError getFirstError()
    {
        if(errors.size() == 0) return null;
        return (PETError) errors.firstElement();
    }
    
    public static void reset(){
        errors.clear();
        PossibleErrors.reset();
    }


    public static PETError createError(String msg, Highlight h, String helpFile)
    {
        String message = msg;
        if(helpFile != null) {
            message += "  Click for help.";
        }
        return new PETError(message, h, helpFile);
    }

    static PETError createUnrecognizedTokenError(String name, Highlight h) {
        String msg = "Unrecognized token '" + name.trim() + "'.";
        return createError(msg, h, UNRECOGNIZED_TOKEN_HF);
    }

    public static PETError createQuoteInStringError(Highlight h) {
        String msg = "Can not have a \" character inside of a string variable.";
        return createError(msg, h, INVALID_STRING_ASSIGNMENT_HF);
    }

    public static PETError createInvalidNumberError(Highlight h) {
        String msg = "Invalid number value.";
        return createError(msg, h, INVALID_NUMBER_HF);
    }

    public static PETError createInvalidUserInput(String input, Highlight h) {
        String msg = "Invalid user input '"+input+"'.";
        return createError(msg, h, INVALID_USER_INPUT_HF);
    }

    static PETError createInvalidArrayIndexError(Object o, Highlight h) {
        String cor = Constants.classNames(new Integer(0));
        String wrong = Constants.classNames(o);
        String msg = "Invalid array index, Expected: '"+cor+"', found: '"+wrong+"'.";
        return createError(msg, h, INVALID_ARRAY_INDEX_HF);
    }

    public static PETError createIncompatibleTypesInAssignmentError(Object var, Object val, Highlight h) {
        String varType = Constants.classNames(var);
        String valType = Constants.classNames(val);
        String msg = "Incompatible types in assignment. Expected: '"+varType+"', found: '"+valType+"'.";
        return createError(msg, h, INCOMPATIBLE_TYPES_IN_ASSIGNMENT_HF);
    }

    public static PETError createTextOnClosingBracketLine(Highlight h) {
        return createError("Unexptected text on same line as '}'.", h, TEXT_ON_CLOSING_BRACKET_LINE_HF);
    }


    public static PETError createVariableAlreadyDefinedError(String varName, Highlight h)
    {
        return createError("Variable '" + varName + "' is already defined.", h, VARIABLE_ALREADY_DEFINED_HF);
    }

    public static PETError createVariableKeywordError(String varName, Highlight h)
    {
        return createError("Can not use keyword '" + varName + "' for a variable name.", h, VARIABLE_IS_KEYWORD_HF);
    }

    public static PETError createMissingClosingBracketError(Highlight h)
    {
        return createError("Missing '}'.", h, MISSING_CLOSING_BRACKET_HF);
    }

    public static PETError createMissingMainError()
    {
        return createError("Must define main function.", null, MISSING_MAIN_HF);
    }

    public static PETError createDuplicateFunctionError(String Name, Highlight h)
    {
        return createError("Function with name '" + Name + "' already defined.", h, DUPLICATE_FUNCTION_HF);
    }

    public static PETError createInvalidPositionError(String invalidCommand, String invalidPosition, Highlight h)
    {
        String msg = "Invalid position: " + invalidCommand + " can not appear " + invalidPosition + ".";
        return createError(msg, h, INVALID_POSITION_HF);
    }

    public static PETError createInvalidIntegerDeclaration(Highlight h)
    {
        String msg = "Invalid integer declaration.";
        return createError(msg, h, INVALID_INTEGER_DECLARATION_HF);
    }

    public static PETError createInvalidBooleanDeclaration(Highlight h){
        String msg = "Invalid boolean declaration.";
        return createError(msg, h, INVALID_BOOLEAN_DECLARATION_HF);
    }

    public static PETError createInvalidStringDeclaration(Highlight h){
        String msg = "Invalid string declaration.";
        return createError(msg, h, INVALID_STRING_DECLARATION_HF);
    }

    public static PETError createInvalidStringAssignment(Highlight h){
        String msg = "Invalid string assignment.";
        return createError(msg, h, INVALID_STRING_ASSIGNMENT_HF);
    }

    public static PETError createInvalidArrayDeclaration(Highlight h){
        String msg = "Invalid array declaration.";
        return createError(msg, h, INVALID_ARRAY_DECLARATION_HF);
    }

    public static PETError createInvalidFunctionDeclaration(Highlight h) {
        String msg = "Invalid function declaration.";
        return createError(msg, h, INVALID_FUNCTION_DECLARATION_HF);
    }

    public static PETError createInvalidReadLine(Highlight h) {
        String msg = "Invalid read command.";
        return createError(msg, h, INVALID_READ_LINE_HF);
    }

    public static PETError createInvalidWriteLine(Highlight h) {
        String msg = "Invalid write command.";
        return createError(msg, h, INVALID_WRITE_LINE_HF);
    }

    public static PETError createInvalidIfLine(Highlight h) {
        String msg = "Invalid if statement.";
        return createError(msg, h, INVALID_IF_LINE_HF);
    }

    public static PETError createInvalidElseLine(Highlight h) {
        String msg = "Invalid else statement.";
        return createError(msg, h, INVALID_ELSE_LINE_HF);
    }

    public static PETError createInvalidCallLine(Highlight h) {
        String msg = "Invalid function call line.";
        return createError(msg, h, INVALID_CALL_LINE_HF);
    }

    public static PETError createInvalidNotLine(String expr, Highlight h) {
        String msg = Evaluator.NOT + " can not be applied to : " + expr + ".";
        return createError(msg, h, INVALID_NOT_LINE_HF);
    }

    public static PETError createInvalidNotLine(Highlight h) {
        String msg = Evaluator.NOT + " can not be applied.";
        return createError(msg, h, INVALID_NOT_LINE_HF);
    }

    public static PETError createInvalidWhileLine(Highlight h) {
        String msg = "Invalid while statement.";
        return createError(msg, h, INVALID_WHILE_LINE_HF);
    }

    public static PETError createUnclosedQuotationInWrite(Highlight h) {
        String msg = "Unclosed quotation.";
        return createError(msg, h, UNCLOSED_QUOTATION_IN_WRITE_HF);
    }

    static PETError createInvalidFillArrayLine(Highlight h) {
        String msg = "Invalid "+Parser.FILL_ARRAY+" line.";
        return createError(msg, h, INVALID_FILL_ARRAY_HF);
    }

    static PETError createBracketAccessOnInvalidTypeError(Object var, Highlight h) {
        String msg = "Can not use [] accessor on type '" + Constants.classNames(var) + "'.";
        return createError(msg, h, INVALID_BRACKET_ACCESS_HF);
    }

    static PETError createInvalidTypesForBinaryOperatorError(String operator, Object objA, Object objB, Highlight h) {
        String classA = Constants.classNames(objA);
        String classB = Constants.classNames(objB);

        String msg = "Binary operator '" + operator + "' can not be applied to types '" +classA+"' and '"+classB+"'.";
        return createError(msg, h, INVALID_TYPE_BINARY_OP_HF);
    }

    static PETError createInvalidTypeForUnaryOperator(String operator, Object objA, Highlight h) {
        String classA = Constants.classNames(objA);

        String msg = "Unary operator '" + operator + "' can not be applied to type '" +classA+"'.";
        return createError(msg, h, INVALID_TYPE_UNARY_OP_HF);
    }

    public static PETError createFunctionNameKeywordError(String functionName, Highlight h) {
        String msg = "Can not use keyword '"+functionName+"' as function name.";
        return createError(msg, h, INVALID_USE_OF_KEYWORD_HF);
    }


    static PETError createFunctionNotDefinedError(String funcName, Highlight h) {
        String msg = "Function with name '"+funcName+"' not defined.";
        return createError(msg, h, FUNCTION_NOT_DEFINED_HF);
    }

    static PETError createInvalidIfExpressionType(Object o, Highlight h) {
        String msg = "Invalid expression inside if statement, expected: "
                +Constants.classNames(new Boolean(true)) + ", found: "
                +Constants.classNames(o)+".";
        return createError(msg, h, INVALID_IF_TYPE_HF);
    }

    static PETError createInvalidWhileExpressionType(Object o, Highlight h) {
        String msg = "Invalid expression inside while statement, expected: "
                +Constants.classNames(new Boolean(true)) + ", found: "
                +Constants.classNames(o)+".";
        return createError(msg, h, INVALID_WHILE_TYPE_HF);
    }






    public static PETError createIndexOutOfBoundsException(Highlight h) {
        String msg = "Array index out of bounds.";
        return createError(msg, h, INDEX_OUT_OF_BOUNDS_HF);
    }





    private static final String ROOT                                = "user_manual/";
    private static final String INDEX_OUT_OF_BOUNDS_HF              = ROOT + "array_index_out_of_bounds.html";
    private static final String INVALID_WHILE_TYPE_HF               = ROOT + "invalid_while_type.html";
    private static final String INVALID_IF_TYPE_HF                  = ROOT + "invalid_if_type.html";
    private static final String FUNCTION_NOT_DEFINED_HF             = ROOT + "function_not_defined.html";
    private static final String INVALID_USE_OF_KEYWORD_HF           = ROOT + "invalid_keyword_use.html";
    private static final String INVALID_TYPE_UNARY_OP_HF            = ROOT + "invalid_type_unary.html";
    private static final String INVALID_TYPE_BINARY_OP_HF           = ROOT + "invalid_type_binary.html";
    private static final String INVALID_BRACKET_ACCESS_HF           = ROOT + "invalid_bracket_access.html";
    private static final String UNCLOSED_QUOTATION_IN_WRITE_HF      = ROOT + "unclosed_quote_write.html";
    private static final String INVALID_WHILE_LINE_HF               = ROOT + "invalid_while_line.html";
    private static final String INVALID_CALL_LINE_HF                = ROOT + "invalid_call_line.html";
    private static final String INVALID_ELSE_LINE_HF                = ROOT + "invalid_else_line.html";
    private static final String INVALID_IF_LINE_HF                  = ROOT + "invalid_if_line.html";
    private static final String INVALID_WRITE_LINE_HF               = ROOT + "invalid_write_line.html";
    private static final String INVALID_READ_LINE_HF                = ROOT + "invalid_read_line.html";
    private static final String INVALID_FUNCTION_DECLARATION_HF     = ROOT + "invalid_function_declaration.html";
    private static final String INVALID_ARRAY_DECLARATION_HF        = ROOT + "invalid_array_declaration.html";
    private static final String INVALID_BOOLEAN_DECLARATION_HF      = ROOT + "invalid_boolean_declaration.html";
    private static final String INVALID_STRING_DECLARATION_HF       = ROOT + "invalid_string_declaration.html";
    private static final String INVALID_STRING_ASSIGNMENT_HF        = ROOT + "invalid_string_assignment.html";
    private static final String INVALID_INTEGER_DECLARATION_HF      = ROOT + "invalid_integer_declaration.html";
    private static final String INVALID_POSITION_HF                 = ROOT + "invalid_position.html";
    private static final String DUPLICATE_FUNCTION_HF               = ROOT + "duplicate_function.html";
    private static final String MISSING_MAIN_HF                     = ROOT + "missing_main.html";
    private static final String MISSING_CLOSING_BRACKET_HF          = ROOT + "missing_closing_bracket.html";
    private static final String VARIABLE_IS_KEYWORD_HF              = ROOT + "variable_is_keyword.html";
    private static final String VARIABLE_ALREADY_DEFINED_HF         = ROOT + "variable_already_defined.html";
    private static final String TEXT_ON_CLOSING_BRACKET_LINE_HF     = ROOT + "text_on_closing_bracket_line.html";
    private static final String INCOMPATIBLE_TYPES_IN_ASSIGNMENT_HF = ROOT + "incompatible_types_in_assignment.html";
    private static final String INVALID_ARRAY_INDEX_HF              = ROOT + "invalid_array_index.html";
    private static final String INVALID_NUMBER_HF                   = ROOT + "invalid_number.html";
    private static final String UNRECOGNIZED_TOKEN_HF               = ROOT + "unrecognized_token.html";
    private static final String INVALID_NOT_LINE_HF                 = ROOT + "invalid_not_expression.html";
    private static final String INVALID_USER_INPUT_HF               = ROOT + "invalid_user_input.html";
    private static final String INVALID_FILL_ARRAY_HF               = ROOT + "invalid_fill_array.html";

}
