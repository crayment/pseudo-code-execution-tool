/*
 * TextArea.java - The Area where date are written
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


package net.sourceforge.helpgui.gui;

import java.awt.print.Printable;
import java.awt.Graphics;
import java.awt.print.PageFormat;

import javax.swing.JTextPane;
import javax.swing.event.*;
import javax.swing.tree.TreePath;



import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

import net.sourceforge.helpgui.page.Page;
import net.sourceforge.helpgui.util.Out;

/**
  * Class where the date are showed
	* @author Alexandre THOMAS
	*/
public class TextArea extends JTextPane implements Printable{

		/** Constructor */
		public TextArea () {
			super();
			setEditable(false);	
		}
	
		/** Fonction call when a new page has to be show */
		public void update (Page page) {
			if(!page.isLeaf()) return;
			try {
			
				/** URL of the web page */
				URL url = new URL(TextArea.class.getResource(MainFrame.helpPath+"/"+page.getTarget()).toString());

				/** Set the URL page to the current TextPane */
		    setPage(url);
				
			} catch (IOException e) {
		    Out.msg("Unable to show the page",Out.FAILED);
				setText("");
			}  catch (Exception e) {
				Out.msg("Error in showing the page",Out.FAILED);
				setText("Error in showing the page");
			}
		}
		
		/** Print the current page */
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
			if (pageIndex >= 1) {
      	return Printable.NO_SUCH_PAGE;
			}
			graphics.translate((int) pageFormat.getImageableX(),(int) pageFormat.getImageableY());
			paint(graphics);
	    return Printable.PAGE_EXISTS;
    }
	
}

