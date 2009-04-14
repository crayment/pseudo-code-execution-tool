/*
 * PageList.java - List of page for the help topic
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

import java.util.ArrayList;
import java.util.Iterator;


/**
  * List of page for the help topic
	* @author Alexandre THOMAS
	*/
public class PageList {

	ArrayList pages = null;
////////////////////////////////////////////////////////////////////


	/** Constructor. */
	public PageList() {
		pages = new ArrayList();
	}

	/** Add a new page on the list. */
	public void add(Page page) {
		pages.add(page);
	}
	
	/** Return a page by its index. */
	public Page get(int pageIndex) throws IndexOutOfBoundsException {
		if ((pageIndex < 0) || (pages.size() <= pageIndex))
			return null;
		return (Page)pages.get(pageIndex);
	}
	
	/**Returns the index of the specified task. */
	public int getIndex(Page page){
		return pages.indexOf(page);
	}
	
	/** Returns a list iterator on this list.*/
	public Iterator iterator() {
		return pages.listIterator();
	}
	
	/** Returns the index of the specified page. */
	public int indexOf(Page page) {
		return pages.indexOf(page);
	}
	
	/** Tells if the list is empty or not.*/
	public boolean isEmpty() {
		return pages.isEmpty();
	}
	
	/** Removes the specified page form the list.*/
	public void remove(int pageIndex) {
		pages.remove(pageIndex);
	}
	
	/** Removes the specified page form the list. */
	public void remove(Page page) {
		pages.remove(page);
	}
	
	/** Returns the number of pages in this list. */
	public int size() {
		return pages.size();
	}
	
	/** Removes all the pages from this list. */
	public void clear() {
		pages.clear();
	}
	
	/** Tells if a page is or not in this list. */
	public boolean isInList(Page page) {
		if (pages.indexOf(page) >= 0)
			return true;
		return false;
	}

	/** Returns a string that describes current instance content. */
	public String toString(){
		StringBuffer strRet = new StringBuffer("[");
		for(Iterator iterator = pages.iterator(); iterator.hasNext(); ) {
			Page page = (Page)iterator.next();
			strRet.append("("+page.getText()+" "+page.getTarget()+"), ");
		}
		strRet.append("]");
		return strRet.toString();
	}
	
	
}

