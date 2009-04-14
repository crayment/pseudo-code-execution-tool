/*
 * PageEnumeration.java - Enumeration of the pages
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
import java.util.Iterator;

/**
  * Enumeration of the pages
	* @author Alexandre THOMAS
	*/
public class PageEnumeration implements Enumeration{
	
	/** An iterator on the list */
	protected Iterator iterator;

	/** Standard constructor.*/
	public PageEnumeration(PageList pages){
		iterator = pages.iterator();
	}
	
	/** Tests if this enumeration contains more elements.*/
	public boolean hasMoreElements(){
		return iterator.hasNext();
	}
	 
	 /** Returns the next element of this enumeration if this enumeration
	   * object has at least one more element to provide. */
	public Object nextElement(){
		return iterator.next();
	}
	
}
