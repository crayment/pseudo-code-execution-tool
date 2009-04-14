/*
 * Page.java - List of page for the help topic
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
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

/**
  * Class where the date are showed
	* @author Alexandre THOMAS
	*/
public class Page implements MutableTreeNode{

	/** Title of the page. */
	private String text=null;
	
	/** The image icon uri. */
	private String image=null;
	
	/** Target url for the page. */
	private String target=null;
	
	/** subpages list */
	protected PageList pages;
	
	/** project this page belong to */
	protected PageRoot rootPage	= null;
	
	/** Page this page is a child of */
	protected Page parentPage	= null;
	
	/** User object */
	protected Object userObject = null;
	
	/** If this page is home */
	protected boolean home=false;
	
////////////////////////////////////////////////////////////////////	
	
	/** Constructor */
	public Page(String text, String image, String target, boolean home, Page parent) {
		this.text = text;
		this.image = image;
		this.target = target;
		this.parentPage = parent;
		pages = new PageList();
		this.home=home;
	}
	
	/** Constructor */
	public Page(String text, String image, String target, boolean home, PageRoot root) {
		this.text = text;
		this.image = image;
		this.target = target;
		this.rootPage = root;
		pages = new PageList();
		this.home=home;
	}
	
	/** Return true is the page is the same. */
	public boolean equals (Page page) {
		return page.text.equals(text) &&
		       page.image.equals(image) &&
					 page.target.equals(target);
	}
	

	/** Return the Title of the Page. */
	public String getText() {
		return text;
	}
	
	/** Return the icon name for this page. */
	public String getImage() {
		return image;
	}
	
	/** Return the target of the page (the HTML URI). */
	public String getTarget() {
		return target;
	}
	
	/** Convert the Page into String. */
	public String toString() {
		return text;
	}
	
	/** Convert the Page into String. */
	public boolean getHome() {
		return home;
	}


/// MutableTreeNode Implementation /////////////////////////////////


	/** Adds child to the receiver at index.*/
	public void 	insert(MutableTreeNode child, int index) {
		if (child.getClass() == Page.class)
			addSubPage((Page)child);
	}
  
	/** Removes the child at index from the receiver. */        
	public void 	remove(int index) {
		if ((0 < index) && (index < pages.size()))
			remove(pages.get(index));
	}
  
	/** Removes node from the receiver. */
	public void remove(MutableTreeNode node) {
		// can deal only with pages
		/*if (node.getClass() == Page.class)
		{
			Page page = (Page)node;
			// removes the page from the tree
			removeSubPage(page);
		}*/
	}
	
	/** Removes the receiver from its parent. */  
	public void removeFromParent() {
		if (null != parentPage)
			parentPage.remove(this);
		else if (null != rootPage)
			rootPage.remove(this);
	}

	/** Sets the parent of the receiver to newParent. */       
	public void setParent(MutableTreeNode newParent) {
		if (newParent.getClass() == Page.class)
		{
			// removes from old parent
			removeFromParent();
			// sets the new parent for this page
			parentPage = (Page)newParent;
			// adds this to its new parent
			((Page)newParent).addSubPage(this);
		}
		else if (newParent.getClass() == PageRoot.class)
		{
			// removes from old parent
			removeFromParent();
			// sets the new parent for this page
			rootPage = (PageRoot)newParent;
			// adds this to its new parent
			((PageRoot)newParent).add(this);
		}
	}
          
	/** Resets the user object of the receiver to object. */
	public void setUserObject(Object object) {
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
		if (null != parentPage)
			return parentPage;
		return rootPage;
	}
	
	/** Returns the index of node in the receivers children.*/
	public int getIndex(TreeNode node) {
		if (node.getClass() != Page.class)
			return -1;
		return pages.getIndex((Page)node);
	}
	
	/** Returns true if the receiver is a leaf.*/
	public boolean 	isLeaf() {
		return pages.isEmpty();
	}
	
	/** Returns true if the receiver allows children. */
	public boolean 	getAllowsChildren() {
		return true;
	}
	
	/** Returns the children of the receiver as an Enumeration.*/
	public Enumeration 	children(){
		return new PageEnumeration(pages);
	}
	
	/** Return the path of the Page on the Tree */
	public TreePath getPath(){
		MutableTreeNode node = (MutableTreeNode)getParent();
		ArrayList pathList = new ArrayList();
		pathList.add(this);
		
		while(node!=null) {
			pathList.add(node);
			node = (MutableTreeNode) node.getParent();
		}
		
		Object [] path = new Object[pathList.size()];
		
		int i = pathList.size()-1;
		for(Iterator it = pathList.iterator(); it.hasNext();--i)
			path[i]=it.next();
		
		pathList=null;
		return new TreePath(path);
	}
	
	
	
	/**Adds a sub page to this page.
	 * This function is for internal use only.
	 * A subpage "A" is added automatically to a page "B" when
	 * "A"'s constructor is called given "B" as parameter.*/
	protected void addSubPage(Page page) {
		// check if not already in the list
		if (pages.indexOf(page) < 0) {
			pages.add(page);
		}
	}
	
	
}

