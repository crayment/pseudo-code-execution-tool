package net.sourceforge.helpgui.gui;

/**
 * This class is from jedit.org
  * RolloverButton.java - Class for buttons that implement rollovers
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2002 Kris Kopicki
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
  * Special button for tests on TaskPropertiesBeans
  */
public class TestRolloverButton
    extends JButton {
  /**
   * Setup the border (invisible initially)
   */
  public TestRolloverButton() {
    setBorder(new EtchedBorder());
    setBorderPainted(false);
    setMargin(new Insets(0, 0, 0, 0));

    setRequestFocusEnabled(false);

    addMouseListener(new MouseOverHandler());
  }

  /**
   * Setup the border (invisible initially)
   */
  public TestRolloverButton(Icon icon) {
    this();

    setIcon(icon);
  }

  public boolean isOpaque() {
    return false;
  }

  public void setEnabled(boolean b) {
    super.setEnabled(b);
    setBorderPainted(false);
    repaint();
  }

  public void paint(Graphics g) {
    if (isEnabled()) {
      super.paint(g);
    }
    else {
      Graphics2D g2 = (Graphics2D) g;
      g2.setComposite(c);
      super.paint(g2);
    }
  }

  private static AlphaComposite c = AlphaComposite.getInstance(
      AlphaComposite.SRC_OVER, 0.5f);

  /**
   * Make the border visible/invisible on rollovers
   */
  class MouseOverHandler
      extends MouseAdapter {
    public void mouseEntered(MouseEvent e) {
      if (isEnabled()) {
        setBorderPainted(true);
      }
    }

    public void mouseExited(MouseEvent e) {
      setBorderPainted(false);
    }
  }
}
