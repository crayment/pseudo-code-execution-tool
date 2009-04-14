/*
 * TreeRenderer.java - The Tree Rendering
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

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ImageIcon;

import net.sourceforge.helpgui.page.Page;
import net.sourceforge.helpgui.gui.MainFrame;

/**
  * Tree rendering 
	* @author Alexandre THOMAS
	*/
public class TreeRenderer extends DefaultTreeCellRenderer {
	
	public Component getTreeCellRendererComponent(
					JTree tree,
					Object value,
					boolean sel,
					boolean expanded,
					boolean leaf,
					int row,
					boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			Page page;
			try{
				page = (Page)value;
			}catch(Exception e) {return this;}
			
			String image = page.getImage();
			if(image==null) {
				if(leaf) image = "/net/sourceforge/helpgui/icons/"+MainFrame.iconsPath+"/sheet.gif";
				else if(expanded) image = "/net/sourceforge/helpgui/icons/"+MainFrame.iconsPath+"/contents.gif";
				else image = "/net/sourceforge/helpgui/icons/"+MainFrame.iconsPath+"/contents2.gif";
			}
			else 
				image = MainFrame.helpPath+"/images/"+image+".gif";
			try {
				setIcon(new ImageIcon(getClass().getResource(image)));			
			} catch(Exception e) {}
			
			return this;
		}
	

}
