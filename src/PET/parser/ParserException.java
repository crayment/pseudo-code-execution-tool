/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET.parser;

/**
 *
 * @author crayment
 */
public class ParserException extends Exception
{

    //constructor without parameters
    public ParserException()
    {
    }

    //constructor for exception description
    public ParserException(String description)
    {
        super(description);
    }
}
