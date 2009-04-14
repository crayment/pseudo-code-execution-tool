/*
 * MainFrame.java - HelpGui main frame
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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.helpgui.page.PageBookMarks;
import net.sourceforge.helpgui.util.Language;
import net.sourceforge.helpgui.parser.TocOpen;
import net.sourceforge.helpgui.util.Out;
import net.sourceforge.helpgui.HelpGui;

/**
  * Main frame of the help GUI
	* @author Alexandre THOMAS
	*/
public class MainFrame extends JFrame implements ActionListener{


	/** Version of HelpGUI.*/
	public String version = "1.0";

	
	/**Buttons on the toolbar*/
	protected JButton jbPrev;
	protected JButton jbNext;
	protected JButton jbHome;
	protected JButton jbPrint;
	protected JButton jbBookmarks;
	/** Menu for bookmarks */
	JMenu menuBookMarks;
	
	//The view of the data
	protected HelpView helpView;

	/** The help path where the data are */
	public static String helpPath = "/docs/help";
	
	/** The icons path where the icons are */
	public static String iconsPath = "java";
	

////////////////////////////////////////////////////////////////////

	/** Standard Constructor. */
	public MainFrame () {
		super("User Manual");
		initFrame("/docs/help", iconsPath);	
	}
	
	/** Standard Constructor. */
	public MainFrame (String helpPath) {
		super("User Manual");
		initFrame(helpPath, iconsPath);	
	}

	/** Standard Constructor. */
	public MainFrame(String helpPath, String iconsPath) {
		super("User Manual");
		initFrame(helpPath, iconsPath);
	}


    //-------BEGIN CODY------//
    public void goToPage(String pageTarget) {
        helpView.goToPagePage(pageTarget);

    }
    //-------END CODY------//


	/** Standard Constructor. */
	public void initFrame(String helpPath, String iconsPath) {
				
		//Remove the last "/" character
		if(helpPath.endsWith("/")) helpPath = helpPath.substring(0,helpPath.length()-1);
		
		this.helpPath = helpPath;
		this.iconsPath = iconsPath;
		
		
		
		
		//Default action on close
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				setVisible(false);
			}
		});
		
		
		//Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu(Language.getInstance().getText("file"));
		JMenu menuAction = new JMenu(Language.getInstance().getText("action"));
		menuBookMarks = new JMenu(Language.getInstance().getText("bookmarks"));
		
		menuBar.add(menuFile);
    menuBar.add(menuAction);
    menuBar.add(menuBookMarks);
		
	
		menuFile.add(Language.getInstance().getText("print")).addActionListener(this);
		menuFile.add(Language.getInstance().getText("quit")).addActionListener(this);
		menuAction.add(Language.getInstance().getText("previous")).addActionListener(this);
		menuAction.add(Language.getInstance().getText("next")).addActionListener(this);
		menuAction.add(Language.getInstance().getText("home")).addActionListener(this);
		menuBookMarks.add(Language.getInstance().getText("addBookmarks")).addActionListener(this);		
		menuBookMarks.addSeparator();
		
		
		//Construct the buttons
		jbPrev  = new TestRolloverButton(new ImageIcon(getClass().getResource("/net/sourceforge/helpgui/icons/"+iconsPath+"/previous.gif")));
		jbNext  = new TestRolloverButton(new ImageIcon(getClass().getResource("/net/sourceforge/helpgui/icons/"+iconsPath+"/next.gif")));
		jbHome  = new TestRolloverButton(new ImageIcon(getClass().getResource("/net/sourceforge/helpgui/icons/"+iconsPath+"/home.gif")));
		jbPrint = new TestRolloverButton(new ImageIcon(getClass().getResource("/net/sourceforge/helpgui/icons/"+iconsPath+"/print.gif")));
		jbBookmarks = new TestRolloverButton(new ImageIcon(getClass().getResource("/net/sourceforge/helpgui/icons/"+iconsPath+"/addbookmarks.gif")));
		
		
		jbPrev.addActionListener(this);
		jbNext.addActionListener(this);
		jbHome.addActionListener(this);
		jbPrint.addActionListener(this);
		jbBookmarks.addActionListener(this);
		
		
		//Construct a toolbar
		JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);
		toolBar.setFloatable(false);
		toolBar.setBorderPainted(true);
    
		//Add buttons to toolbar
		toolBar.add(jbPrev);
		toolBar.add(jbNext);
		toolBar.add(jbHome);
		toolBar.add(jbPrint);
		toolBar.add(jbBookmarks);

		//Set ToolTipsText to the button	
		jbPrev.setToolTipText(Language.getInstance().getText("previous"));
		jbNext.setToolTipText(Language.getInstance().getText("next"));
		jbHome.setToolTipText(Language.getInstance().getText("home"));
		jbPrint.setToolTipText(Language.getInstance().getText("print"));
		jbBookmarks.setToolTipText(Language.getInstance().getText("addBookmarks"));
	
		//View of Data
		helpView = new HelpView();
		
		//Construct gui parameters
		GridBagLayout gbPanel = new GridBagLayout();
		GridBagConstraints gbcPanel = new GridBagConstraints();
		getContentPane().setLayout( gbPanel );
		
		//Add the main tool bar
		gbcPanel.gridx = 0;
		gbcPanel.gridy = 0;
		gbcPanel.gridwidth = 1;
		gbcPanel.gridheight = 1;
		gbcPanel.fill = GridBagConstraints.VERTICAL;
		gbcPanel.weightx = 1;
		gbcPanel.weighty = 0;
		gbcPanel.anchor = GridBagConstraints.WEST;
		gbPanel.setConstraints( toolBar, gbcPanel );
		getContentPane().add( toolBar );
		
		//Add the panel with data to the mainframe
		gbcPanel.gridx = 0;
		gbcPanel.gridy = 1;
		gbcPanel.gridwidth = 1;
		gbcPanel.gridheight = 1;
		gbcPanel.fill = GridBagConstraints.BOTH;
		gbcPanel.weightx = 1;
		gbcPanel.weighty = 1;
		gbcPanel.anchor = GridBagConstraints.CENTER;
		gbPanel.setConstraints( helpView, gbcPanel );
		getContentPane().add( helpView );
		
		//Pack the window
		pack();
		
		setLocation(100,100);
		//Set a message
		Out.msg("Construction of the GUI", Out.OK);
		
		//Load the TOC
		try {
			TocOpen opener = new TocOpen (helpView);
			opener.load();			
			Out.msg("Loading the Table of Content", Out.OK);
		} catch (Exception e) {
			Out.msg("Table of Content XML parsing", Out.FAILED);
			System.out.println(e);
		}
				
		//Go to the home page
		helpView.goHome();
		//helpView.firstNodeExpand();
	}
	
	/** Handles buttons events */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			if(e.getSource().equals(jbPrev)) 
				helpView.previousPage();
			else if(e.getSource().equals(jbNext)) 
				helpView.nextPage();
			else if(e.getSource().equals(jbHome))
				helpView.goHome();
			else if(e.getSource().equals(jbPrint)) 
				helpView.print();
			else if(e.getSource().equals(jbBookmarks)) 
				addBookMarks();
		} else if (e.getSource() instanceof JMenuItem) {
				String arg = e.getActionCommand();
				if (arg.equals(Language.getInstance().getText("previous"))) 
					helpView.previousPage();
				else if (arg.equals(Language.getInstance().getText("next"))) 
					helpView.nextPage();
				else if (arg.equals(Language.getInstance().getText("home"))) 
					helpView.goHome();
				else if (arg.equals(Language.getInstance().getText("print"))) 
					helpView.print();
				else if (arg.equals(Language.getInstance().getText("quit"))) 
					quit();
				else if (arg.equals(Language.getInstance().getText("addBookmarks"))) 
					addBookMarks();
				else helpView.updatePage (PageBookMarks.getInstance().getBookMark((JMenuItem)e.getSource()), true);
		}
	}
	
	/** Close the Frame */
	public void quit() {
		if(HelpGui.debug) System.exit(0);
		else setVisible(false);
	}
		
	/** Set a bookmark to the current page */
	public void addBookMarks() {
		//System.out.println("Add bookmark to "+helpView.getCurrentPage());
		
		JMenuItem menuItem = new JMenuItem(helpView.getCurrentPage().toString());
		menuItem.addActionListener(this);
		menuBookMarks.add(menuItem);
		PageBookMarks.getInstance().addBookMark(menuItem, helpView.getCurrentPage());
	}
	
}

