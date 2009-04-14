/*
 * XMLParser.java - The XML Parser
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


package net.sourceforge.helpgui.parser;

import org.xml.sax.Attributes;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

import net.sourceforge.helpgui.gui.HelpView;
import net.sourceforge.helpgui.page.Page;
import net.sourceforge.helpgui.page.PageRoot;
import net.sourceforge.helpgui.util.Out;

/** 
  * XML Parser class
	* @author Alexandre THOMAS
   */
class XMLParser  extends DefaultHandler {
	
	MutableTreeNode parent;
	DefaultTreeModel model;
		
	public XMLParser (HelpView helpView) {
		parent = (MutableTreeNode)helpView.getJTree().getModel().getRoot();
		model = (DefaultTreeModel)(helpView.getJTree().getModel());	
	}
		
		
	/** Start to parse a tag */
   public void startElement(String namespaceURI,
                            String sName, // simple name
                            String qName, // qualified name
                            Attributes attrs) throws SAXException {
   				
		String text=null, image=null, target=null;
		boolean home=false;
			
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
         String aName = attrs.getQName(i); // Attr name
         if (qName.equals("tocitem")) {
           if (aName == "text") {
						text = attrs.getValue(i);
					} else if (aName == "image") {
						image = attrs.getValue(i);
					} else if (aName == "target") {
						target = attrs.getValue(i);
					} else if (aName == "home") {
						home = attrs.getValue(i).equals("true");
					} 
				}
			}
		}
			
		if (qName.equals("tocitem")) {
			MutableTreeNode node=null;
			if(parent instanceof Page) node = new Page(text, image, target, home, (Page)parent);
			else if(parent instanceof PageRoot) node = new Page(text, image, target, home, (PageRoot)parent); 
			
			//parent.insert ((MutableTreeNode)node, parent.getChildCount());
			model.insertNodeInto((MutableTreeNode)node, parent, parent.getChildCount());
				parent = node;
		}
	}

	/** Finish to parse a tag */
   public void endElement(String namespaceURI,
                          String sName,
                          String qName
                          ) throws SAXException {
	   	if(qName.equals("tocitem"))
				parent = (MutableTreeNode)parent.getParent();
   }

}
