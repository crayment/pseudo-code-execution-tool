/*
 * HelpView.java - The View of the help (tree on left, text on right)
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.print.PrinterJob;

import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JScrollPane;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.*;

import net.sourceforge.helpgui.page.LinkedPage;
import net.sourceforge.helpgui.page.PageRoot;
import net.sourceforge.helpgui.page.Page;
import net.sourceforge.helpgui.page.PageEnumeration;
import net.sourceforge.helpgui.util.Out;
import net.sourceforge.helpgui.util.BrowserControl;




/**
  * View for the project, contain the tree on the left 
	* and the text on the right
	* @author Alexandre THOMAS
	*/
public class HelpView extends JPanel implements  MouseListener, HyperlinkListener {

	/** Tree on the left. */
  private JTree tree;
	/** Panel on the right. */
	private TextArea textarea;
	/** Model of the tree. */
	private DefaultTreeModel model;
	/** Root of the Tree */
	private PageRoot pageRoot;
	/** The linked page to store the list of the viewed pages */
	LinkedPage linkedPage = new  LinkedPage();
	
	/** Constructor. */
	public HelpView() {
		super();
		
		//Construct the objects
		pageRoot = new PageRoot();
		model    = new DefaultTreeModel(pageRoot);
		tree     = new JTree(model);
		textarea = new TextArea();
		textarea.addHyperlinkListener (this);	
		
		//Tree parameters
		tree.setShowsRootHandles(false);
    tree.setRowHeight(20);
    tree.setRootVisible(false);
		tree.setCellRenderer(new TreeRenderer());	
		
		//A split pane to put the 
		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(180, 550));
		JScrollPane textAreaScrollPane = new JScrollPane(textarea);
		textAreaScrollPane.setPreferredSize(new Dimension(470, 550));		
		JSplitPane splitPane = new JSplitPane (JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, textAreaScrollPane);
		//splitPane.setDividerSize(4);
		splitPane.setOneTouchExpandable(true);
		
		GridBagLayout gbPanel = new GridBagLayout();
		GridBagConstraints gbcPanel = new GridBagConstraints();
		setLayout( gbPanel );
		
		//Put the split pane on the window
		gbcPanel.gridx = 0;
		gbcPanel.gridy = 0;
		gbcPanel.gridwidth = 1;
		gbcPanel.gridheight = 1;
		gbcPanel.fill = GridBagConstraints.BOTH;
		gbcPanel.weightx = 1;
		gbcPanel.weighty = 1;
		gbcPanel.anchor = GridBagConstraints.CENTER;
		gbPanel.setConstraints( splitPane, gbcPanel );
		add( splitPane );		
		
		//Conect the listeners
		//model.addTreeModelListener(textarea);
		tree.addMouseListener(this);		
	}

	/** Get the JTree of the panel */
	public JTree getJTree () {
		return tree;
	}

	/** Get the TextArea of the panel */
	public TextArea getTextArea () {
		return textarea;
	}
	
	/** Invoked when the mouse button has been clicked (pressed and released) on a component. */
	public void 	mouseClicked(MouseEvent e) {}
	/** Invoked when the mouse enters a component. */
	public void 	mouseEntered(MouseEvent e) {}
	/** Invoked when the mouse exits a component. */
	public void 	mouseExited(MouseEvent e) {}
	
	/** Invoked when a mouse button has been pressed on a component. */	
	public void 	mousePressed(MouseEvent e) { 
		//updateTreeSelection(e); 
	}
	
	/** Invoked when a mouse button has been released on a component. */
	public void 	mouseReleased(MouseEvent e) {
		updateTreeSelection(e);
	}

	/** Updates tree selection on a mouse event. */
	public void updateTreeSelection(MouseEvent e) {

		int selectedRow = tree.getRowForLocation(e.getX(), e.getY());
		
		if (selectedRow != -1) {
			tree.setSelectionRow(selectedRow);
			TreePath path = tree.getSelectionPath();
			updatePage((Page)path.getLastPathComponent(), true);
		}
		else	
			tree.clearSelection();		
	}
	
	/** Go to the home page */
	public void goHome () {
		if(!goHomePage (pageRoot.children())) {
			Out.msg("Be carreful you've any Home page defined");
		}
	}

	
	/** Recursive function to parse all page to get the home page */
	private boolean goHomePage (Enumeration e) {
		while(e.hasMoreElements()) {
			Page page = (Page)e.nextElement();
			if(page.getHome()) {
				updatePage(page, true);
				return true;
			}				
			if(goHomePage(page.children())) return true;
		}
		return false;
	}
	
	/** Expand the first node of the Tree */
	public void firstNodeExpand () {
		Page firstChild = (Page) pageRoot.getFirstChild();
		if(firstChild!=null) {
			Enumeration e = firstChild.children();
			while(e.hasMoreElements()) {
				Page child = (Page)e.nextElement();
				Object path[]={pageRoot,firstChild, child};
				tree.scrollPathToVisible(new TreePath(path));
			}
		}
	}
	
	/** Set the previous page */
	public void previousPage() {
		updatePage(linkedPage.getPreviousPage(), false);
	}
	
	/** Set the next page */
	public void nextPage () {
		updatePage(linkedPage.getNextPage(), false);
	}
	
	/** Return the current page */
	public Page getCurrentPage() {
		return linkedPage.getCurrentPage();
	}
	
	/** Action when a link is pressed (for HTML listener)*/
	public void hyperlinkUpdate(HyperlinkEvent e) {		
		if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			//get the URL
			String url = e.getURL().toString();
				
			//Run the browser for http viewer
			if(url.startsWith("http://") || url.startsWith("mailto:"))
				BrowserControl.displayURL(url);
			
			else //It's perhaps a page on the help toppic
			{
				
				//Serach the page from	
				int ind = url.lastIndexOf('!');
				url = url.substring(ind+1,url.length());
				url = url.replaceFirst(MainFrame.helpPath+"/","");
				updatePage(getLinkedPage(pageRoot.children(), url), true);
				
									
			}
			//Else I don't know what it is	
		}
	}
	
	/** Return the page with the specify url */
	private Page getLinkedPage (Enumeration e, String url) {
		while(e.hasMoreElements()) {
			Page page = (Page)e.nextElement();
			if(page.getTarget()!=null && page.getTarget().equals(url)) {
				return page;
			}				
			Page p = getLinkedPage(page.children(), url);
			if(p!=null) return p;
		}
		return null;
	}
	
	/** Set a new page on the browser and and it on the list of the last page view */
	public void updatePage (Page page, boolean insert) {
		if(page==null) return;

		linkedPage.addPage(page, insert);
		textarea.update(page);
		tree.setSelectionPath(page.getPath());
		
		//Out.msg (linkedPage.toString());
	}


    //------BEGIN CODY-------//
    void goToPagePage(String pageTarget) {
        Page p = getLinkedPage(pageRoot.children(), pageTarget);
        updatePage(p, false);
    }
    //------END CODY---------//


	
	/** Print the page */
	public void print() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(textarea);
		if (printJob.printDialog()) {
      try {
				printJob.print();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
