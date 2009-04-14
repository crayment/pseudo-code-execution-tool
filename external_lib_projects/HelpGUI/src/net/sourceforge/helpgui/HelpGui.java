/*
 * 
 * HelpGui.java - Launch HelpGui API Demo
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


package net.sourceforge.helpgui;

import net.sourceforge.helpgui.gui.MainFrame;


/**
  * A class for Lauch the Help Viewer
	* @author Alexandre THOMAS
	*/
public class HelpGui {

	/** For debug test */
	public static boolean debug = false;

	public static void main (String [] args) {
		
		debug=true;
		MainFrame mainFrame = new MainFrame("/docs/help/","plastic");
		mainFrame.setVisible(true);
		
		
		//Action on close the window
		mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				System.exit(0);
			}
		});	
	}
}


