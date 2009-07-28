/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.parser;
import java.util.regex.*;
import PET.Constants;
import PET.model.Highlight;
import PET.model.Variables;
import PET.validator.Errors;
/**
 * This class is responsible for any expression that can be
 * evaluated (simplified to a return value)
 * @author crayment
 */
public class Evaluator
{
    /** Regex used to match expressions. **/
    // [2, -4] (Integer)
    public static final String IS_VALID_NUMBER = "(-)?\\d+";
    // [true]
    public static final String IS_TRUE = "true";
    // [false]
    public static final String IS_FALSE = "false";
    // [- (expr)]
    public static final String NEGATE_EXPRESSION = "^\\-\\s+((.*))";
    // [--(expr)]
    public static final String DOUBLE_NEGATIVE_START = "^\\-\\s*\\-((.*))";
    // matches smallest, most inner set of parens possible
    // also group(2) is a check for preceding -
    public static final String HAS_PARENTHESIS = "(.*?)(-)?(\\([^()]*\\))((.*))";
    // [(expr)[-, *, /, +, ==]]
    public static final String LAST_CHAR_IS_OPERATOR = ".*([-*/+]|={2})$";
    // [(expr)-]
    public static final String LAST_CHAR_IS_DASH = ".*(-)$";
    // operators [(expr)(operator(expr)]
    public static final String ADD = "+";
    public static final String HAS_ADDITION = "(.+?)\\+((.+))";
    public static final String MULTIPLICATION = "*";
    public static final String HAS_MULTIPLICATION = "(.+)\\*((.+))";
    public static final String DIVISION = "/";
    public static final String HAS_DIV = "(.+)\\/((.+))";
    public static final String SUBTRACT = "-";
    public static final String HAS_SUB = "(.+)\\-((.+))";
    public static final String EQUAL = "==";
    public static final String HAS_EQUAL = "(.+)\\={2}((.+))";
    public static final String NOT_EQUAL = "not =";
    public static final String HAS_NOT_EQUAL = "(.+) not = ((.+))";
    public static final String AND = " and ";
    public static final String HAS_AND = "(.+) and ((.+))";
    public static final String OR = " or ";
    public static final String HAS_OR = "(.+) or ((.+))";
    public static final String GT = ">";
    public static final String HAS_GT = "(.+)\\>([^=]((.*)))";
    public static final String LT = "<";
    public static final String HAS_LT = "(.+)\\<([^=]((.*)))";
    public static final String GTE = ">=";
    public static final String HAS_GTE = "(.+)\\>\\=((.+))";
    public static final String LTE = "<=";
    public static final String HAS_LTE = "(.+)\\<\\=((.+))";
    // [!(expr)]
    public static final String NOT = "not";
    public static final String HAS_NOT = "(.*)not \\s*([^=]?((.*)))\\s*";
    //variables
    public static final String IS_VALID_VARIABLE = "([a-zA-Z_]+)";
    public static final String IS_VALID_ARRAY_ACCESS = "(([a-zA-Z_]+))\\s*\\[\\s*((.+))\\s*\\]";

    /**
     * This is the same as calling <code>eval(String expr, boolean debug)</code>
     * with <code>debug</code> false.
     * @param expr The expression to evaluate
     * @return The result of evaluating the expression
     * @throws PET.parser.ParserException if the expression can not be evaluated
     */
    public static Object eval(String expr, Highlight h) throws ParserException
    {
        return eval(expr, false, h);
    }

    /**
     *
     * @param expr The expression to be evaluated
     * @param debug true if you want to print debug statements
     * @return The result of evaluating the expression
     * @throws PET.parser.ParserException if the expression can not be evaluated
     */
    public static Object eval(String expr, boolean debug,Highlight h) throws ParserException
    {
        boolean alreadyHandled = false;
        
        expr = expr.trim();
        if (debug)
        {
            System.out.println("Evaluating: " + expr);
        }

        // A matcher to use.
        Matcher m;

        // recognize true
        if (!alreadyHandled && Pattern.matches(IS_TRUE, expr))
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
        } // recognize false
        if (!alreadyHandled && Pattern.matches(IS_FALSE, expr))
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
        } // check if expr is a number

        if (!alreadyHandled && Pattern.matches(IS_VALID_NUMBER, expr))
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
                throw new ParserException("Invalid Number");
            }
        }

        // recognize variables
        if (!alreadyHandled && Pattern.matches(IS_VALID_VARIABLE, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: variable");
            }
            m = Pattern.compile(IS_VALID_VARIABLE).matcher(expr);
            m.find();
            try
            {
                Object o = Variables.get(m.group());
                if (o instanceof Boolean)
                {
                    Boolean b = Boolean.parseBoolean(o.toString());
                    if (debug)
                    {
                        System.out.println("Return Boolean: " + b.toString());
                    }
                    return b;
                }
                if (o instanceof Integer)
                {
                    Integer d = Integer.parseInt(o.toString());
                    if (debug)
                    {
                        System.out.println("Return Integer: " + d.toString());
                    }
                    return d;
                }
                if (o instanceof String)
                {
                    String s = o.toString();
                    if (debug)
                    {
                        System.out.println("Return String: " + s);
                    }
                    return s;
                } else
                {
                    throw new ParserException("Variable of type " + o.getClass().toString() + " is not supported");
                }
            }
            catch (ParserException e)
            {
                throw new ParserException("Undefined reference to " + m.group() + ".");
            }
        }

        // string literals
        if(!alreadyHandled && expr.trim().startsWith("\"") && expr.trim().endsWith("\"")) {
            // remove the quotes
            return new String(expr.trim().substring(1, expr.length()-1));
        }

        // recognize array accessors
        if (!alreadyHandled && Pattern.matches(IS_VALID_ARRAY_ACCESS, expr))
        {
            m = Pattern.compile(IS_VALID_ARRAY_ACCESS).matcher(expr);
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
                    System.out.println("Matched: array access");
                }
                try
                {
                    Object o = Variables.get(m.group(1));
                    if (o instanceof Integer[])
                    {
                        Integer[] array = (Integer[]) o;

                        o = eval(m.group(3), debug,null);
                        if (o instanceof Integer)
                        {
                            try
                            {
                                int index = (int) Integer.parseInt(o.toString());
                                if(index >= array.length) {
                                    Errors.add(Errors.createIndexOutOfBoundsException(h));
                                    return null;
                                }
                                Integer value = array[index];
                                if (debug)
                                {
                                    System.out.println("Return eval(\"" + value.toString() + "\")");
                                }
                                return eval(value.toString(), debug,null);
                            }
                            // all these exceptions should never be caught b/c of the strictness of the regex.
                            // however we may want to open up the regex to allow these to be caught to get better error messages.
                            catch (Exception e)
                            {
                                throw new ParserException(m.group(3) + " is an invalid index");
                            }
                        } else
                        {
                            throw new ParserException(m.group(3) + " is an invalid index");
                        }
                    } else
                    {
                        throw new ParserException("[] is an invalid operator on type " + Constants.classNames(o));
                    }
                }
                catch (ParserException e)
                {
                    throw new ParserException("Undefined reference to " + m.group() + ".");
                }
            }
        }
        /**
         * special case for and conditional.  inspired by the fact that if the
         * first part of the and fails, the second part need not be evaluated.
         * We need to support this for the cases where evaluating the second part
         * could cause runtime errors.  ie. (a > 0 && array[a] != 5)
         *
         * we solve this by catching and first and only actually considering it an
         * end if the part before the and is valid (has evenly matched or no parens)
         *
         * this seems to work for now, hopefully works for all cases.
         */
        if (!alreadyHandled && Pattern.matches(HAS_AND, expr))
        {
            
            m = Pattern.compile(HAS_AND).matcher(expr);
            m.find();

            String pre = m.group(1);

            // if the first expr is not clean then move on and let parens catch it
            if(isCleanExpr(pre))
            {
                alreadyHandled = true;


                if (debug)
                {
                    System.out.println("Matched: and");
                }

                Object objA = eval(m.group(1), debug,null);
                if (objA instanceof Boolean)
                {
                    Boolean a = (Boolean) objA;
                    if(!a) return Boolean.FALSE;

                    Object objB = eval(m.group(2), debug,null);

                    if (objB instanceof Boolean)
                    {
                        Boolean b = Boolean.parseBoolean(objB.toString());
                        Boolean val = a && b;
                        if (debug)
                        {
                            System.out.println("Return: eval(\"" + val + "\")");
                        }
                        return eval(val.toString(), debug,null);
                    }
                    else
                    {
                        String message = AND + "can not be evaluated for ";
                        message += Constants.classNames(objA.getClass().toString());
                        message += " and ";
                        message += Constants.classNames(objB.getClass().toString());

                        throw new ParserException(message);
                    }
                }
                else
                {
                    String message = "Invalid first argument to " + AND;
                    throw new ParserException(message);
                }
            }
        }


        if (!alreadyHandled && Pattern.matches(HAS_OR, expr))
        {

            m = Pattern.compile(HAS_OR).matcher(expr);
            m.find();

            String pre = m.group(1);

            // if the first expr is not clean then move on and let parens catch it
            if(isCleanExpr(pre))
            {
                alreadyHandled = true;


                if (debug)
                {
                    System.out.println("Matched: or");
                }

                Object objA = eval(m.group(1), debug,null);
                if (objA instanceof Boolean)
                {
                    Boolean a = (Boolean) objA;
                    if(a) return Boolean.TRUE;

                    Object objB = eval(m.group(2), debug,null);

                    if (objB instanceof Boolean)
                    {
                        Boolean b = Boolean.parseBoolean(objB.toString());
                        Boolean val = a || b;
                        if (debug)
                        {
                            System.out.println("Return: eval(\"" + val + "\")");
                        }
                        return eval(val.toString(), debug,null);
                    }
                    else
                    {
                        String message = OR + "can not be evaluated for ";
                        message += Constants.classNames(objA.getClass().toString());
                        message += " and ";
                        message += Constants.classNames(objB.getClass().toString());

                        throw new ParserException(message);
                    }
                }
                else
                {
                    String message = "Invalid first argument to " + OR;
                    throw new ParserException(message);
                }
            }
        }



        



        //handle parenthesis with the highest after special and case,
        // can not be else if becuase of that case.
        if (!alreadyHandled && Pattern.matches(HAS_PARENTHESIS, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: parens");
            }
            m = Pattern.compile(HAS_PARENTHESIS).matcher(expr);
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
                Object evaledObject = eval(innerParens, debug,null); // eval(...)
                String evaledString;

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
                    if (evaledString.startsWith("-") && m.group(2) != null && m.group(2).trim().equals("-"))
                    {
                        evaledString = evaledString.substring(1);
                        ignorePrecedingNeg = true;
                    }
                } else
                {
                    evaledString = evaledObject.toString();
                }

                if (!ignorePrecedingNeg && m.group(2) != null && m.group(2).trim().equals("-"))
                {
                    evaledString = "-" + evaledString;
                }

                //concat the pre and post () and eval it.
                String newexpr = m.group(1).toString() + evaledString + m.group(4).toString();
                if (debug)
                {
                    System.out.println("Return: eval(\"" + newexpr + "\")");
                }
                return eval(newexpr, debug,null);
            }
        }

        

        // equal
        if (!alreadyHandled && Pattern.matches(HAS_EQUAL, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: equals");
            }
            m = Pattern.compile(HAS_EQUAL).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Boolean val = a.equals(b);
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else if (objA instanceof Boolean && objB instanceof Boolean)
                {
                    Boolean a = Boolean.parseBoolean(objA.toString());
                    Boolean b = Boolean.parseBoolean(objB.toString());
                    Boolean val = a.equals(b);
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else if (objA instanceof String && objB instanceof String)
                {
                    String a = objA.toString();
                    String b = objB.toString();
                    Boolean val = a.equals(b);
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = EQUAL + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // not equal
        if (!alreadyHandled && Pattern.matches(HAS_NOT_EQUAL, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: equals");
            }
            m = Pattern.compile(HAS_NOT_EQUAL).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Boolean val = !a.equals(b);
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else if (objA instanceof Boolean && objB instanceof Boolean)
                {
                    Boolean a = Boolean.parseBoolean(objA.toString());
                    Boolean b = Boolean.parseBoolean(objB.toString());
                    Boolean val = !a.equals(b);
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else if (objA instanceof String && objB instanceof String)
                {
                    String a = objA.toString();
                    String b = objB.toString();
                    Boolean val = !a.equals(b);
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = NOT_EQUAL + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // greater than
        if (!alreadyHandled && Pattern.matches(HAS_GT, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: greater than");
            }
            m = Pattern.compile(HAS_GT).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Boolean val = a > b;
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = GT + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // less than
        if (!alreadyHandled && Pattern.matches(HAS_LT, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: less than");
            }
            m = Pattern.compile(HAS_LT).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Boolean val = a < b;
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = LT + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // less than or equal
        if (!alreadyHandled && Pattern.matches(HAS_LTE, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: less than or equal");
            }
            m = Pattern.compile(HAS_LTE).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Boolean val = a <= b;
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = LTE + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // greater than or equal
        if (!alreadyHandled && Pattern.matches(HAS_GTE, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: greater than or equal");
            }
            m = Pattern.compile(HAS_GTE).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Boolean val = a >= b;
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = GTE + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }


        // add
        if (!alreadyHandled && Pattern.matches(HAS_ADDITION, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: addition");
            }
            m = Pattern.compile(HAS_ADDITION).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Integer val = a + b;
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = ADD + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // subtract
        if (!alreadyHandled && Pattern.matches(HAS_SUB, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: subtraction");
            }
            m = Pattern.compile(HAS_SUB).matcher(expr);

            if (m.find())
            {
                /**
                 * If the (a) part of (a - b) ends with an operator then we might
                 * not be subtracting. (negative)
                 */
                if (!Pattern.matches(LAST_CHAR_IS_OPERATOR, m.group(1).trim()))
                {
                    Object objA = eval(m.group(1), debug,null);
                    Object objB = eval(m.group(2), debug,null);
                    if (objA instanceof Integer && objB instanceof Integer)
                    {
                        Integer a = Integer.parseInt(objA.toString());
                        Integer b = Integer.parseInt(objB.toString());
                        Integer val = a - b;
                        if (debug)
                        {
                            System.out.println("Return: eval(\"" + val + "\")");
                        }
                        return eval(val.toString(), debug,null);
                    } else
                    {
                        String message = SUBTRACT + " can not be evaluated for ";
                        message += Constants.classNames(objA.getClass().toString());
                        message += " and ";
                        message += Constants.classNames(objB.getClass().toString());

                        throw new ParserException(message);
                    }
                } else
                {
                    String g1 = m.group(1).trim();
                    String g2 = m.group(2).trim();
                    while (Pattern.matches(LAST_CHAR_IS_DASH, g1))
                    {
                        g1 = g1.substring(0, g1.length() - 1).trim();
                        g2 = "-" + g2;
                    }
                    if (g1.equals(""))
                    {
                        if (debug)
                        {
                            System.out.println("subtraction aborted!! damn negs");
                        }
                        if (Pattern.matches(DOUBLE_NEGATIVE_START, expr))
                        {
                            if (debug)
                            {
                                System.out.println("Matched: double negative");
                            }
                            m = Pattern.compile(DOUBLE_NEGATIVE_START).matcher(expr);
                            if (m.find())
                            {
                                if (debug)
                                {
                                    System.out.println("Return: eval(\"" + m.group(1) + "\")");
                                }
                                return eval(m.group(1), debug,null);
                            }
                        }
                    }
                    // if we are still subtracting
                    if (!Pattern.matches(LAST_CHAR_IS_OPERATOR, g1))
                    {
                        Object objA = eval(g1, debug,null);
                        Object objB = eval(g2, debug,null);
                        if (objA instanceof Integer && objB instanceof Integer)
                        {
                            Integer a = Integer.parseInt(objA.toString());
                            Integer b = Integer.parseInt(objB.toString());
                            Integer val = a - b;
                            if (debug)
                            {
                                System.out.println("Return: eval(\"" + val + "\")");
                            }
                            return eval(val.toString(), debug,null);
                        } else
                        {
                            String message = SUBTRACT + " can not be evaluated for ";
                            message += Constants.classNames(objA.getClass().toString());
                            message += " and ";
                            message += Constants.classNames(objB.getClass().toString());

                            throw new ParserException(message);
                        }
                    } else
                    {
                        if (debug)
                        {
                            System.out.println("subtraction aborted!! damn negs");
                        }
                        Object g2Evaled = eval("-" + g2, debug,null);
                        return eval(g1 + g2Evaled.toString(), debug,null);
                    }
                }
            }
        }

        // multiply
        if (!alreadyHandled && Pattern.matches(HAS_MULTIPLICATION, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: multiplication");
            }
            m = Pattern.compile(HAS_MULTIPLICATION).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Integer val = a * b;
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = MULTIPLICATION + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // divide
        if (!alreadyHandled && Pattern.matches(HAS_DIV, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: division");
            }
            m = Pattern.compile(HAS_DIV).matcher(expr);
            if (m.find())
            {
                Object objA = eval(m.group(1), debug,null);
                Object objB = eval(m.group(2), debug,null);
                if (objA instanceof Integer && objB instanceof Integer)
                {
                    Integer a = Integer.parseInt(objA.toString());
                    Integer b = Integer.parseInt(objB.toString());
                    Integer val = a / b;
                    if (debug)
                    {
                        System.out.println("Return: eval(\"" + val + "\")");
                    }
                    return eval(val.toString(), debug,null);
                } else
                {
                    String message = DIVISION + " can not be evaluated for ";
                    message += Constants.classNames(objA.getClass().toString());
                    message += " and ";
                    message += Constants.classNames(objB.getClass().toString());

                    throw new ParserException(message);
                }
            }
        }

        // not
        if (!alreadyHandled && Pattern.matches(HAS_NOT, expr))
        {
            alreadyHandled = true;


            if (debug)
            {
                System.out.println("Matched: not");
            }
            m = Pattern.compile(HAS_NOT).matcher(expr);
            if (m.find())
            {
                if(Variables.isDefined(m.group(2)) && Variables.get(m.group(2)) instanceof Boolean) {
                    return !(Boolean)(Variables.get(m.group(2)));
                }
                String[] values = negationHelper(m.group(2));
                if (debug)
                {
                    System.out.println("eval for negation: " + values[0]);
                }
                Object toNegate = eval(values[0], debug,null);
                if (!(toNegate.getClass() == Boolean.class))
                {
                    throw new ParserException(NOT + " can not be applied to: " + values[0]);
                }
                Boolean negated = !(Boolean) toNegate;
                String newexpr = m.group(1) + negated.toString() + (values[1] == null ? "" : values[1]);
                if (debug)
                {
                    System.out.println("Return: eval(\"" + newexpr + "\")");
                }
                return eval(newexpr,null);
            }
        }

        

        // Evaluates expression with leading negatives
        if (!alreadyHandled && Pattern.matches(NEGATE_EXPRESSION, expr))
        {
            alreadyHandled = true;
            if (debug)
            {
                System.out.println("Matched: negative expression");
            }
            m = Pattern.compile(NEGATE_EXPRESSION).matcher(expr);
            if (m.find())
            {
                if (debug)
                {
                    System.out.println("Return: eval(\"-\"eval(\"" + m.group(1) + "\"))");
                }
                return eval("-" + eval(m.group(1), debug,null), debug,null);
            }
        }

        throw new ParserException("Failed to evaluate: " + expr);
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



    /**
     * This method is used to help with negating expressions.  It returns String
     * array with 2 values in it.  The 0 index contains the part of the
     * expression which should be negated and the 1 index contains the rest of
     * the string.
     * @param expr a string that was following a !
     * @return <code>Array[0]</code> will be the pare of the expression
     * that should be negated, <code>Array[1]</code> will be the part of the
     * expression that follows
     * @throws PET.parser.ParserException
     */
    private static String[] negationHelper(String expr) throws ParserException
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

        throw new ParserException(NOT + " cannot be applied in: " + expr);
    }

    /**
     * Prints the values of each group in a <code>Matcher</code>
     * Thie method is only used for debugging and could be removed upon
     * deployment
     * @param m The matcher to print
     */
    private static void printMatcher(Matcher m)
    {
        for (int i = 0; i < m.groupCount(); i++)
        {
            System.out.print("g" + i + ": " + m.group(i));
        }
        System.out.println();
    }
}