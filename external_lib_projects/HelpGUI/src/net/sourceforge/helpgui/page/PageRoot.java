/*
 * PageRoot.java - Root node of the tree
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

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

/**
  * Root node for the tree
	* @author Alexandre THOMAS
	*/
public class PageRoot implements MutableTreeNode{


	//List of pages
	protected PageList pages;
	
	// parent
	protected MutableTreeNode parentNode;
	
	// user object
	protected Object userObject;
	
////////////////////////////////////////////////////////////////////

	/** Constructor. */
	public PageRoot () {
		pages = new PageList();
	}


	/** Adds a page to this project (in the root of the project).*/
	public void add(Page page)
	{
		if ((null != page) && (pages.indexOf(page) < 0))
			pages.add(page);
	}
	
	/** Convert the Page into String. */
	public String toString() {
		return "Help GUI";
	}

	
/// MutableTreeNode Implementation /////////////////////////////////	
	
	/** Adds child to the receiver at index.*/
	public void 	insert(MutableTreeNode child, int index) {
		// can deal only with pages doesn't need to support index
		if (child.getClass() == Page.class)
			pages.add((Page)child);		
	}
  
	/** Removes the child at index from the receiver. */        
	public void 	remove(int index) {
		pages.remove(index);
	}
  
	/** Removes node from the receiver. */
	public void 	remove(MutableTreeNode node) {
		// can deal only with pages
		if (node.getClass() == Page.class)
			pages.remove((Page)node);
	}
	
	/** Removes the receiver from its parent. */  
	public void 	removeFromParent() {
		if (null != parentNode)
			parentNode.remove(this);
	}

	/** Sets the parent of the receiver to newParent. */       
	public void 	setParent(MutableTreeNode newParent) {
		parentNode = newParent;
	}
          
	/** Resets the user object of the receiver to object. */
	public void 	setUserObject(Object object) {
		userObject = object;
	}
	
	/** Returns the child TreeNode at index childIndex. */
	public TreeNode getChildAt(int iChildIndex) {
		return pages.get(iChildIndex);
	}
	
	/** Returns the number of children TreeNodes the receiver contains.*/
	public int getChildCount() {
		return pages.size();
	}
	
	/** Returns the parent TreeNode of the receiver. */
	public TreeNode getParent() {
		return null;
	}
	
	/** Returns the index of node in the receivers children.*/
	public int getIndex(TreeNode node) {
		// check if it's a page
		if (node.getClass() != Page.class)
			return -1;

		// otherwise, checks if in the list
		return pages.getIndex((Page)node);
	}
		
	/** Returns true if the receiver is a leaf.*/
	public boolean 	isLeaf() {
		return false;
	} 
	
	/** Returns true if the receiver allows children. */
	public boolean 	getAllowsChildren() {
		return true;
	}
	
	/** Returns the children of the receiver as an Enumeration.*/
	public Enumeration 	children(){
		return new PageEnumeration(pages);
	}
	
	public Page getFirstChild () {
		Enumeration e = children();
		if(e.hasMoreElements())
			return (Page)e.nextElement();
		return null;
	}

}
