/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.validator;

import PET.model.Highlight;
import PET.model.Program;
import PET.parser.Evaluator;
import PET.parser.Parser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.nio.cs.ext.GB18030;


/**
 *
 * @author crayment
 */
public class Validator {

    public static void reset()
    {
        Variables.clearAllVariables();
    }

    public static void validateArrayAssignment(String varName, String indexStr, String newValueStr, Highlight h) {
        if(isKeyword(varName)) {
            addAppropriateError(varName, h);
        }

        validateVariableExists(varName, h);

        Object var = Variables.get(varName);
        Object index = Validator.validate(indexStr, h);
        Object value = Validator.validate(newValueStr, h);

        if(index != null && value != null) {
            if(!(index instanceof Integer)) {
                Errors.add(Errors.createInvalidArrayIndexError(index, h));
            }
            if(!(value instanceof Integer)) {
                Errors.add(Errors.createIncompatibleTypesInAssignmentError(new Integer(0), value, h));
            }
        }
    }

    public static void validateArrayDeclaration(String varName, String sizeStr, Highlight h) {
        if(isKeyword(varName)) {
            addAppropriateError(varName, h);
        }

        Object var = Variables.get(varName);
        Object size = Validator.validate(sizeStr, h);

        if(size != null) {
            if(!(size instanceof Integer)) {
                Errors.add(Errors.createInvalidArrayIndexError(size, h));
            }
        }
    }

    

    public static void validateFunctionCall(String funcName, Highlight h) {
        if(!Program.getFunctions().containsKey(funcName)) {
            PETError error = Errors.createFunctionNotDefinedError(funcName, h);
            error.funcitonName = funcName;
            Errors.add(error);
            PossibleErrors.addNeededFunction(error);
        }
    }

    public static void validateFunctionName(String functionName, Highlight h) {
        if(isKeyword(functionName)) {
            Errors.add(Errors.createFunctionNameKeywordError(functionName, h));
        }
    }

    public static void validateIfExpression(String expr, Highlight h) {
        Object o = Validator.validate(expr, h);
        if(o != null && !(o instanceof Boolean)) {
            Errors.add(Errors.createInvalidIfExpressionType(o, h));
        }
    }


    public static void validateWhileExpression(String expr, Highlight h) {
        Object o = Validator.validate(expr, h);
        if(o != null && !(o instanceof Boolean)) {
            Errors.add(Errors.createInvalidWhileExpressionType(o, h));
        }
    }

    public static void validateNewVariableName(String name, Object o, Highlight h) {
        if(isKeyword(name)) {
            Errors.add(Errors.createVariableKeywordError(name, h));
        }
        if(Variables.isDefined(name)) {
            Errors.add(Errors.createVariableAlreadyDefinedError(name, h));
        } else {
            Variables.define(name, o);
        }
    }

    public static boolean validateVariableExists(String name, Highlight h) {
        if(!Variables.isDefined(name)) {
            Errors.add(Errors.createUnrecognizedTokenError(name, h));
            return false;
        }
        return true;
    }

    public static void validateVariableAssignment(String name, String value, Highlight h) {
        if(isKeyword(name)){
            addAppropriateError(name, h);
        }

        boolean error = !validateVariableExists(name, h);
        
        Object var = Variables.get(name);
        Object val = Validator.validate(value, h);


        if(val != null && !error) {
            if(var.getClass() != val.getClass()) {
                Errors.add(Errors.createIncompatibleTypesInAssignmentError(var, val, h));
            }
        }

        if(!error && var instanceof String && val instanceof String) {

            if(value.trim().startsWith("\"")) {

                if(value.trim().length() < 2) {
                    Errors.add(Errors.createInvalidStringAssignment(h));
                }
                else
                    validateQuotesForString(value, h);
            }
        }

    }



    public static void validateVariableType(String name, Object[] typeObj, Highlight h) {
        if(isKeyword(name)) {
            addAppropriateError(name, h);
        }

        boolean error = !validateVariableExists(name, h);

        if(!error) {
            Object var = Variables.get(name);
            if(var.getClass() != typeObj.getClass()) {
                Errors.add(Errors.createIncompatibleTypesInAssignmentError(typeObj, var, h));
            }
        }
    }
    
    public static void validateQuotesForString(String value, Highlight h) {
        if (value.trim().substring(1, value.length() - 1).contains("\"")) {
            Errors.add(Errors.createQuoteInStringError(h));
            return;
        }
    }

    public static void addAppropriateError(String line, Highlight h) {

        String[] keys = line.split(" ");

        if(keys.length < 1){
            Errors.add(Errors.createError("Unknown error on line "+h.getLineNumber() , h, null));

        } else if(keys[0].trim().startsWith(Parser.INTEGER_DECLARATOR)){
            Errors.add(Errors.createInvalidIntegerDeclaration(h));

        } else if(keys[0].trim().startsWith(Parser.BOOLEAN_DECLARATOR)){
            Errors.add(Errors.createInvalidBooleanDeclaration(h));

        } else if(keys[0].trim().startsWith(Parser.STRING_DECLARATOR)){
            Errors.add(Errors.createInvalidStringDeclaration(h));

        } else if(keys[0].trim().startsWith(Parser.INTEGER_ARRAY_DECLARATOR)){
            Errors.add(Errors.createInvalidArrayDeclaration(h));

        } else if(keys[0].trim().startsWith(Parser.FUNCTION_DECLARATION)){
            Errors.add(Errors.createInvalidFunctionDeclaration(h));

        } else if(keys[0].trim().startsWith(Parser.READ)){
            Errors.add(Errors.createInvalidReadLine(h));

        } else if(keys[0].trim().startsWith(Parser.WRITE)){
            Errors.add(Errors.createInvalidWriteLine(h));

        } else if(keys[0].trim().startsWith(Parser.WRITE_LINE)){
            Errors.add(Errors.createInvalidWriteLine(h));

        } else if(keys[0].trim().startsWith(Parser.IF)){
            Errors.add(Errors.createInvalidIfLine(h));

        } else if(keys[0].trim().startsWith(Parser.ELSE)){
            Errors.add(Errors.createInvalidElseLine(h));

        } else if(keys[0].trim().startsWith(Parser.CALL)){
            Errors.add(Errors.createInvalidCallLine(h));

        } else if(keys[0].trim().startsWith(Parser.DEFINE)) {
            Errors.add(Errors.createInvalidFunctionDeclaration(h));

        } else if(keys[0].trim().startsWith(Parser.WHILE)) {
            Errors.add(Errors.createInvalidWhileLine(h));

        } else if(keys[0].trim().startsWith(Evaluator.NOT)) {
            Errors.add(Errors.createInvalidNotLine(h));

        } else if(keys[0].trim().startsWith(Parser.FILL_ARRAY)) {
            Errors.add(Errors.createInvalidFillArrayLine(h));
        }
        else{
            int i = 0;
            String token = keys[i];
            while(token.trim().equals("") && i < keys.length) {
                i++;
                token = keys[i];
            }
            Errors.add(Errors.createUnrecognizedTokenError(token, h));
        }
    }




    public static boolean isKeyword(String word){
        if(word.trim().equals(Parser.INTEGER_DECLARATOR)) return true;
        if(word.trim().equals(Parser.BOOLEAN_DECLARATOR)) return true;
        if(word.trim().equals(Parser.STRING_DECLARATOR)) return true;
        if(word.trim().equals(Parser.FUNCTION_DECLARATION)) return true;
        if(word.trim().equals(Parser.INTEGER_ARRAY_DECLARATOR)) return true;
        if(word.trim().equals(Parser.READ)) return true;
        if(word.trim().equals(Parser.WRITE)) return true;
        if(word.trim().equals(Parser.WRITE_LINE)) return true;
        if(word.trim().equals(Parser.IF)) return true;
        if(word.trim().equals(Parser.ELSE)) return true;
        if(word.trim().equals(Parser.CALL)) return true;
        if(word.trim().equals(Parser.WHILE)) return true;
        if(word.trim().equals(Parser.DEFINE)) return true;
        if(word.trim().equals(Parser.FILL_ARRAY)) return true;
        if(word.trim().equals("true")) return true;
        if(word.trim().equals("false")) return true;

        return false;
    }












    
    /**
     * Evaluates strictly based on types, returns an object of the same type
     * that the expression would return from Evaluator.eval except does use actual
     * values, just types.  Creates apporpriate errors and returns null if an
     * error is found
     * @param expr The exprssion to evaluate
     * @return an object with appropriate class or null if ther was an error
     */
    public static Object validate(String expr, Highlight h)
    {
        return validate(expr, h, false);
    }



    /**
     * Evaluates strictly based on types, returns an object of the same type
     * that the expression would return from Evaluator.eval except does use actual
     * values, just types.  Creates apporpriate errors and returns null if an
     * error is found
     * @param expr The exprssion to evaluate
     * @param debug If true it will print debug statements
     * @return an object with appropriate class or null if ther was an error
     */
    public static Object validate(String expr, Highlight h, boolean debug)
    {
        boolean alreadyHandled = false;
        
        expr = expr.trim();
        if (debug)
        {
            System.out.println("Validating: " + expr);
        }

        // A matcher to use.
        Matcher m;

        // recognize true
        if (!alreadyHandled && Pattern.matches(Evaluator.IS_TRUE, expr))
        {
            alreadyHandled = true;

            
            if (debug)
            {
                System.out.println("Matched: true");
            }
            if (debug)
            {
                System.out.println("Return Boolean: " + Boolean.TRUE);
            }
            return Boolean.TRUE;
        }

        // recognize false
        if (!alreadyHandled && Pattern.matches(Evaluator.IS_FALSE, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: false");
            }
            if (debug)
            {
                System.out.println("Return Boolean: " + Boolean.FALSE);
            }
            return Boolean.FALSE;
        }

        // check if expr is a number
        if (!alreadyHandled && Pattern.matches(Evaluator.IS_VALID_NUMBER, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: number");
            }
            try
            {
                if (debug)
                {
                    System.out.println("Return Integer: " + Integer.parseInt(expr));
                }
                return Integer.parseInt(expr);
            }
            catch (NumberFormatException e)
            {
                // If this ever happens our Regex has problems.
                Errors.add(Errors.createInvalidNumberError(h));
                return null;
            }
        }

        // recognize variables
        if (!alreadyHandled && Pattern.matches(Evaluator.IS_VALID_VARIABLE, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: variable");
            }
            m = Pattern.compile(Evaluator.IS_VALID_VARIABLE).matcher(expr);
            m.find();

            String varName = m.group();

            boolean exists = Validator.validateVariableExists(varName, h);

            if(exists) {
                return Variables.get(varName);
            } else {
                return null;
            }

            
        }


        // string literals
        if(!alreadyHandled && expr.trim().startsWith("\"") && expr.trim().endsWith("\"")) {
            // remove the quotes
            return new String();
        }

        
        // recognize array accessors
        if (!alreadyHandled && Pattern.matches(Evaluator.IS_VALID_ARRAY_ACCESS, expr))
        {
            m = Pattern.compile(Evaluator.IS_VALID_ARRAY_ACCESS).matcher(expr);
            m.find();

            String indexStr = m.group(3);
            boolean pass = false;
            int count = 1;
            for(int i=0; i<indexStr.length(); i++) {
                if(indexStr.charAt(i) == ']') count --;
                else if(indexStr.charAt(i) == '[') count ++;
                if(count == 0) {
                    pass = true;
                    break;
                }
            }
            if(!pass) {

                alreadyHandled = true;
                if (debug) {
                    System.out.println("Matched: variable");
                }

                String name = m.group(1);
                String index = m.group(3);

                boolean exists = validateVariableExists(name, h);
                if(exists) {
                    Object var = Variables.get(name);
                    Object ind = Validator.validate(index, h);

                    if(! (ind instanceof Integer)) {
                        Errors.add(Errors.createInvalidArrayIndexError(ind, h));
                        return null;
                    }

                    if(! (var instanceof Integer[])) {
                        Errors.add(Errors.createBracketAccessOnInvalidTypeError(var, h));
                        return null;
                    }

                    return new Integer(0);

                } else {
                    return null;
                }
            }
            
        }

        
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_AND, expr)) {

            m = Pattern.compile(Evaluator.HAS_AND).matcher(expr);
            m.find();

            String pre = m.group(1);

            // if the first expr is not clean then move on and let parens catch it
            if(isCleanExpr(pre))
            {
                alreadyHandled = true;

                if (debug) {
                    System.out.println("Matched: and");
                }

                String a = m.group(1);
                String b = m.group(2);

                Object objA = Validator.validate(a, h);
                Object objB = Validator.validate(b, h);
                if(objA == null || objB == null)
                    return null;

                if(!((objA instanceof Boolean) && (objB instanceof Boolean))) {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.AND, objA, objB, h));
                    return null;
                }

                return new Boolean(true);
            }
        } 


        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_OR, expr)) {

            m = Pattern.compile(Evaluator.HAS_OR).matcher(expr);
            m.find();

            String pre = m.group(1);

            // if the first expr is not clean then move on and let parens catch it
            if(isCleanExpr(pre))
            {
                alreadyHandled = true;
                
                if (debug) {
                    System.out.println("Matched: or");
                }

                String a = m.group(1);
                String b = m.group(2);

                Object objA = Validator.validate(a, h);
                Object objB = Validator.validate(b, h);
                if(objA == null || objB == null)
                    return null;

                if(!((objA instanceof Boolean) && (objB instanceof Boolean))) {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.OR, objA, objB, h));
                    return null;
                }

                return new Boolean(true);
            }
        }


        //handle parenthesis with the highest priority after special and/or cases,
        // can not be else if becuase of those cases.
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_PARENTHESIS, expr)) {

            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: parens");
            }
            
            m = Pattern.compile(Evaluator.HAS_PARENTHESIS).matcher(expr);
            if (m.find())
            {
                /**
                 * The match is designed to match the smallest most inner pair
                 * of () that it can, evalutate them and put the result in its
                 * place. Then we can evaluate the resulting string.
                 * Eventually all () should be evaluated and removed.
                 *
                 * The match also checks for a - directly before the parens
                 */
                String parems = m.group(3); // ( ... )
                String innerParens = parems.trim().substring(1, parems.length() - 1); // ...
                Object evaledObject = validate(innerParens, h, debug); // eval(...)

                if(evaledObject == null)
                    return  null;

                
                String evaledString;

                String pre = m.group(1);
                String post = m.group(4);

                String possibleNegSign = m.group(2);

                /**
                 * this is a little complicated.  If the inside of the parens
                 * evaluates to a negative Integer, and there was a - sign before the
                 * parens then we consider it a Integer negative and get rid
                 * of both of them. otherwise we append everything back together
                 * normally.
                 */
                boolean ignorePrecedingNeg = false;

                if (evaledObject instanceof Integer)
                {
                    evaledString = evaledObject.toString();
                    if (evaledString.startsWith("-") && possibleNegSign != null && possibleNegSign.trim().equals("-"))
                    {
                        evaledString = evaledString.substring(1);
                        ignorePrecedingNeg = true;
                    }
                } else
                {
                    evaledString = evaledObject.toString();
                }



                if (!ignorePrecedingNeg && possibleNegSign != null && possibleNegSign.trim().equals("-"))
                {
                    evaledString = "-" + evaledString;
                }

                //concat the pre and post () and eval it.
                String newexpr = pre + evaledString + post;
                if (debug) {
                    System.out.println("Return: validate(\"" + newexpr + "\")");
                }
                return validate(newexpr, h, debug);
            }
        }

        

        // equal
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_EQUAL, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: equals");
            }

            m = Pattern.compile(Evaluator.HAS_EQUAL).matcher(expr);
            if (m.find()) {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null)
                    return null;


                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Boolean(true);

                } else if (objA instanceof Boolean && objB instanceof Boolean) {
                   return new Boolean(true);

                } else if (objA instanceof String && objB instanceof String) {

                    return new Boolean(true);
                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.EQUAL, objA, objB, h));
                    return null;
                }
            }
        }

        // not equal
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_NOT_EQUAL, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: not equals");
            }

            m = Pattern.compile(Evaluator.HAS_NOT_EQUAL).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null)
                    return null;


                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Boolean(true);

                } else if (objA instanceof Boolean && objB instanceof Boolean) {
                   return new Boolean(true);

                } else if (objA instanceof String && objB instanceof String) {

                    return new Boolean(true);
                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.NOT_EQUAL, objA, objB, h));
                    return null;
                }
            }
        }


        // greater than
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_GT, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: greater than");
            }
            m = Pattern.compile(Evaluator.HAS_GT).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null)
                    return null;

                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Boolean(true);

                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.GT, objA, objB, h));
                    return null;
                }
            }
        }


        // less than
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_LT, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: less than");
            }
            m = Pattern.compile(Evaluator.HAS_LT).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null)
                    return null;

                
                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Boolean(true);

                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.LT, objA, objB, h));
                    return null;
                }
            }
        }


        // less than or equal
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_LTE, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: less than or equal");
            }
            m = Pattern.compile(Evaluator.HAS_LTE).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null)
                    return null;


                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Boolean(true);

                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.LTE, objA, objB, h));
                    return null;
                }
            }
        }


        // greater than or equal
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_GTE, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: greater than or equal");
            }
            m = Pattern.compile(Evaluator.HAS_GTE).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null)
                    return null;


                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Boolean(true);

                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.GTE, objA, objB, h));
                    return null;
                }
            }
        }

        // add
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_ADDITION, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: addition");
            }
            m = Pattern.compile(Evaluator.HAS_ADDITION).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null)
                    return null;


                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Integer(0);

                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.ADD, objA, objB, h));
                    return null;
                }
            }
        }


        // subtract
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_SUB, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: subtraction");
            }
            m = Pattern.compile(Evaluator.HAS_SUB).matcher(expr);

            if (m.find())
            {
                /**
                 * If the (a) part of (a - b) ends with an operator then we might
                 * not be subtracting. (negative)
                 */
                if (!Pattern.matches(Evaluator.LAST_CHAR_IS_OPERATOR, m.group(1).trim()))
                {
                    Object objA = validate(m.group(1), h, debug);
                    Object objB = validate(m.group(2), h, debug);
                    if(objA == null || objB == null) {
                        return null;
                    }

                    
                    if (objA instanceof Integer && objB instanceof Integer)
                    {
                        return new Integer(0);
                    } else
                    {
                        Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.SUBTRACT, objA, objB, h));
                        return null;
                    }
                } else
                {
                    String g1 = m.group(1).trim();
                    String g2 = m.group(2).trim();
                    while (Pattern.matches(Evaluator.LAST_CHAR_IS_DASH, g1))
                    {
                        g1 = g1.substring(0, g1.length() - 1).trim();
                        g2 = "-" + g2;
                    }
                    if (g1.equals(""))
                    {
                        if (debug) {
                            System.out.println("subtraction aborted!! damn negs");
                        }

                        if (Pattern.matches(Evaluator.DOUBLE_NEGATIVE_START, expr)) {
                            if (debug) {
                                System.out.println("Matched: double negative");
                            }

                            m = Pattern.compile(Evaluator.DOUBLE_NEGATIVE_START).matcher(expr);

                            if (m.find()) {
                                if (debug) {
                                    System.out.println("Return: eval(\"" + m.group(1) + "\")");

                                }
                                return validate(m.group(1), h, debug);
                            }
                        }
                    }
                    // if we are still subtracting
                    if (!Pattern.matches(Evaluator.LAST_CHAR_IS_OPERATOR, g1))
                    {
                        Object objA = validate(g1, h, debug);
                        Object objB = validate(g2, h, debug);
                        if(objA == null || objB == null) {
                            return null;
                        }
                        
                        if (objA instanceof Integer && objB instanceof Integer) {
                           return new Integer(0);

                        } else {
                            Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.SUBTRACT, objA, objB, h));

                        }
                    } else
                    {
                        if (debug) {
                            System.out.println("subtraction aborted!! damn negs");
                        }
                        Object g2Evaled = validate("-" + g2, h, debug);
                        if(g2Evaled == null)
                            return null;
                        
                        return validate(g1 + g2Evaled.toString(), h, debug);
                    }
                }
            }
        }


        // multiply
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_MULTIPLICATION, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: multiplication");
            }

            m = Pattern.compile(Evaluator.HAS_MULTIPLICATION).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null) {
                    return null;
                }
                
                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Integer(0);

                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.MULTIPLICATION, objA, objB, h));
                    return null;
                }
            }
        }


        // divide
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_DIV, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: division");
            }

            m = Pattern.compile(Evaluator.HAS_DIV).matcher(expr);
            if (m.find())
            {
                Object objA = validate(m.group(1), h, debug);
                Object objB = validate(m.group(2), h, debug);
                if(objA == null || objB == null) {
                    return null;
                }

                if (objA instanceof Integer && objB instanceof Integer) {
                    return new Integer(0);

                } else {
                    Errors.add(Errors.createInvalidTypesForBinaryOperatorError(Evaluator.DIVISION, objA, objB, h));
                    return null;
                }
            }
        }

        // not
        if (!alreadyHandled && Pattern.matches(Evaluator.HAS_NOT, expr)) {

            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: not");
            }
            m = Pattern.compile(Evaluator.HAS_NOT).matcher(expr);
            m.find();
            String[] values = negationHelper(m.group(2), h);
            if(values == null)
                return null;

            if (debug) {
                    System.out.println("eval for negation: " + values[0]);
            }

            // TODO
            if(values[0] == null) {
                Errors.add(Errors.createError("Not must be applied to something", h, null));
                return null;
            }

            Object toNegate = validate(values[0], h, debug);
            if(toNegate == null) {
                Errors.add(Errors.createError("Not must be applied to something", h, null));
                return null;
            }


            if (!(toNegate.getClass() == Boolean.class)) {
                Errors.add(Errors.createInvalidTypeForUnaryOperator(Evaluator.NOT, toNegate, h));
                return null;
            }

            String newexpr = m.group(1) + Boolean.TRUE + (values[1] == null ? "" : values[1]);
            if (debug) {
                System.out.println("Return: validate(\"" + newexpr + "\")");
            }

            return validate(newexpr, h, debug);

        }


        // Evaluates expression with leading negatives
        if (!alreadyHandled && Pattern.matches(Evaluator.NEGATE_EXPRESSION, expr))
        {
            alreadyHandled = true;


            if (debug) {
                System.out.println("Matched: negative expression");
            }

            m = Pattern.compile(Evaluator.NEGATE_EXPRESSION).matcher(expr);
            if (m.find()) {
                if (debug) {
                    System.out.println("Return: eval(\"-\"eval(\"" + m.group(1) + "\"))");

                }
                return validate("-" + validate(m.group(1), h, debug), h, debug);
            }
        }


        Errors.add(Errors.createUnrecognizedTokenError(expr, h));
        return null;
    }



    private static boolean isCleanExpr(String expr)
    {
        int spread = 0;
        for(int i=0;i<expr.length(); i++)
        {
            if(expr.charAt(i) == '(')
                spread++;
            if(expr.charAt(i) == ')')
                spread --;
        }
        if(spread == 0)
            return true;

        return false;
    }




    private static String[] negationHelper(String expr, Highlight h)
    {
        expr = expr.trim();
        if (expr.length() == 0)
        {
            return new String[]
                    {
                    };
        }
        String tOrF = "^((true)|(false))((.*))";

        Matcher m;

        if (Pattern.matches(tOrF, expr))
        {
            m = Pattern.compile(tOrF).matcher(expr);
            if (m.find())
            {
                return new String[]
                        {
                            m.group(1), m.group(4)
                        };
            }
        }

        Errors.add(Errors.createInvalidNotLine(expr, h));
        return null;
    }

}
