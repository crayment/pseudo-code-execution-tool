/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package PET.validator;

import PET.model.Program;
import java.util.Vector;

/**
 *
 * @author crayment
 */
public class PossibleErrors {

    private static Vector neededFunctions = new Vector();

    public static void reset() {
        PossibleErrors.neededFunctions.clear();
    }

    public static void addNeededFunction(PETError undefinedFunctionError) {
        neededFunctions.add(undefinedFunctionError);
    }


    public static void removeFixedPossibleErrors() {
        for(Object error : neededFunctions) {
            PETError e = (PETError) error;
            //remove the error then add it back if it still applies
            // this order so that the error goes to the end of the stack
            Errors.remove(e);
            if(!Program.isFunctionDefined(e.funcitonName)) {
                Errors.add(e);
            }
        }
    }


}
