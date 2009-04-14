/*
 * 
 * Out.java - Show a message on the output
 * Copyright (C) 2003 Alexandre THOMAS
 * alexthomas@free.fr
 * http://helpgui.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.helpgui.util;


import java.lang.StringBuffer;
import net.sourceforge.helpgui.HelpGui;

/**
  * Class aible to write a simple message on the console
	* @author Alexandre THOMAS
	*/
public class Out {

	public static int OK = 0;
	public static int FAILED = 1;
	private static String [] _state = { "[  OK  ]", "[FAILED]"};
	public static int length = 80;

	/** Add '.' at the end of the message.  */
	private static String addPoints (String msg) {
		StringBuffer result = new StringBuffer(msg);
		for(int i=msg.length(); i<length; ++i)
			result.append(".");
		return result.toString();
	}
	
	/** Write a message on the standar output and finish it by [ OK ]. */
	public static void msg(String msg) {
		if(HelpGui.debug)
			System.out.println(addPoints(msg)+_state[0]);
	}

	/** Write a message on the standar output and finish it by the specify state (OK, or FAILED). */
	public static void msg(String msg, int state) {
		if(HelpGui.debug)
			System.out.println(addPoints(msg)+_state[state]);
	}
	
}

