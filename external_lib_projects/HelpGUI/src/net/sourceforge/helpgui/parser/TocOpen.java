/*
 * TocOpen.java - List of page for the help topic
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import net.sourceforge.helpgui.gui.HelpView;
import net.sourceforge.helpgui.gui.MainFrame;


/**
  * Aible to to load a toc and construct the tree
	* @author Alexandre THOMAS
	*/
public class TocOpen {

	//Temporary
	StringBuffer XMLFile = new StringBuffer();
	
	/** View of data */
	HelpView helpView;
	
	
////////////////////////////////////////////////////////////////////	
	
	/** Constructor */
	public TocOpen(HelpView helpView) {
		this.helpView = helpView;
	}


	/** Load the toc.xml file and create the tree */
	public boolean load() throws IOException {
		
		
		// Use an instance of ourselves as the SAX event handler
    DefaultHandler handler = new XMLParser(helpView);

    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
      	// Parse the input
      	SAXParser saxParser;
				saxParser = factory.newSAXParser();
				saxParser.parse(TocOpen.class.getResourceAsStream(MainFrame.helpPath+"/toc.xml"), handler);
                System.out.println(MainFrame.helpPath);
		} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return false;
		} catch (SAXException e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
		}
		
		 return true;
	}


	

}

