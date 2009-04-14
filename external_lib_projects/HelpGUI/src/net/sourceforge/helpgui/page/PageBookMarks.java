/*
 * PageBookMarks.java - BookMarks Manager for HelpGUI
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


package net.sourceforge.helpgui.page;


import java.util.Hashtable;
import javax.swing.JMenuItem;



/**
  * BookMarks Manager
	*/ 
public class PageBookMarks {
	
	/** Instance of the class */
	private static PageBookMarks bookmarks = null;
	
	/** HashTable for bookmaks manager */
	private Hashtable table = null;
	
	public static PageBookMarks getInstance() {
		if (bookmarks == null) {
			bookmarks = new PageBookMarks();
		}
		return bookmarks;
	}
	
	/** Constructor */
	public PageBookMarks () {
		table = new Hashtable();
	}
	
	/** Insert a new bookmark */
	public void addBookMark (JMenuItem menuItem, Page page) {
		table.put(menuItem, page);
	}
	
	/** Get a bookmarks */
	public Page getBookMark (JMenuItem menuItem) {
		return (Page)table.get(menuItem);
	}
	

}





