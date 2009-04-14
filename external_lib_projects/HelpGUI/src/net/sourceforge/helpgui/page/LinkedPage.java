/*
 * LinkedPage.java - List of page viewed by the user
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

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;

import net.sourceforge.helpgui.util.Out;

/**
  *List of page viewed by the user
	* @author Alexandre THOMAS
	*/
public class LinkedPage {
	
	/** List of viewing page */
	LinkedList pagesList = new LinkedList();
	
	/** Index of the current page */
	int index = 0;

	/** Get the previous page */
	public Page getPreviousPage () {
		if(index<pagesList.size()-1)
			index++;
		return getCurrentPage();
	}

	/** Get the next page */
	public Page getNextPage () {
		if(index>0)
			index--;
		return getCurrentPage();
	}
	
	/** Return the current page */
	public Page getCurrentPage () {
		try {
			return (Page)pagesList.get(index);
		} catch (Exception e){}
		return null;
	}
	
	/** Insert a new page on the list */
	public void addPage (Page page, boolean insert) {
		if(!page.isLeaf())return;
		
		if(index!=0 && insert) {
			for (;index>0;index--)
				try{pagesList.removeFirst();}catch(Exception e) {}
			index=0;
		}
		
		Page p=null;
		try {
			p = getCurrentPage();
		}catch (Exception e) {}
		
		if(p!=page)pagesList.addFirst(page);
		if(pagesList.size()>10)
			pagesList.removeLast();			
	}
	
	/** Return the string correcponding to the LinkedPage class */
	public String toString() {
		return ""+index+" --"+pagesList.toString();
	}
	
}

