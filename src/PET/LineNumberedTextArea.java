package PET;

import PET.model.LineWatches;
import PET.model.PersistentSettings;
import PET.model.Program;
import PET.model.SettingsMgr;
import java.io.IOException;
import java.util.logging.*;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;

/**
 * This control implements the text window used of editing and running code.
 * @author Abhinav
 */
public class LineNumberedTextArea extends JTextArea implements ActionListener, ItemListener, CaretListener, MouseListener
{
    int _currentStartLine = 0;
    int _fontHeight = 0;
    ArrayList<Integer> cboxList;
    static UndoManager undoManager = new UndoManager();
    public Color lineColor = new Color(232,232,246);

    LineNumberedTextArea()
    {
        this.setOpaque(false);
        this.createPopupMenu();
        this.addCaretListener(this);
        this.addMouseListener(this);
        cboxList = new ArrayList<Integer>();
        DefaultCaret caret = (DefaultCaret) this.getCaret();
        this.setCaretPosition(0);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.getDocument().addUndoableEditListener(
                new UndoableEditListener()
                {

                    public void undoableEditHappened(UndoableEditEvent e)
                    {
                        undoManager.addEdit(e.getEdit());
                    }
                });
        setCustomSettings();
    }

    @Override
    public Insets getInsets()
    {
        return getInsets(new Insets(0, 0, 0, 0));
    }

    @Override
    public Insets getInsets(Insets insets)
    {
        insets = super.getInsets(insets);
        insets.left += lineNumberWidth();
        return insets;
    }

    private int lineNumberWidth()
    {
        int lineCount = Math.max(getRows(), getLineCount() + 1);
        int result = 0;
        String baseWidth = "   ";
        if (lineCount < 10)
        {
            result = getFontMetrics(getFont()).stringWidth(10 + baseWidth);
        } else
        {
            result = getFontMetrics(getFont()).stringWidth(lineCount + baseWidth);
        }
        return result;
    }

    private int currentLine()
    {
        int caretPos = getCaretPosition();
        Element root = getDocument().getDefaultRootElement();
        int line = root.getElementIndex(caretPos) + 1;
        return line;
    }

    public void setScrollToPos(int pos) {
        Element root = getDocument().getDefaultRootElement();
        int line = root.getElementIndex(pos);
        // put  the position in view
        int spaceAbove = this.getParent().getHeight() / 5;
        int spaceBelow = spaceAbove + (this.getParent().getHeight() / 4);
        this.scrollRectToVisible(new Rectangle(0, (line * _fontHeight) - spaceAbove, this.getWidth(),spaceBelow));
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        // go to the top
        setScrollToPos(0);
        this.updateUI();
    }

    public void setCustomSettings()
    {
        try
        {
            PersistentSettings ps = new PersistentSettings();
            SettingsMgr sm = new SettingsMgr();
            ps = (PersistentSettings) sm.readObject(ps.getClass().getName());
            if (ps == null) //Write the default object
            {
                ps = new PersistentSettings();
                sm.writeObject(ps);
            }

            Font font = new Font(ps.getFontType(), Font.PLAIN, ps.getFontSize());
            this.setFont(font);
            _fontHeight = ps.getFontSize();

            this.setBackground(ps.getCodeBGColor());
            this.setForeground(ps.getCodeTextColor());
            this.lineColor = (ps.getCodeLineColor());

            this.repaint();
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(LineNumberedTextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(LineNumberedTextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Insets insets = getInsets();
        Rectangle clip = g.getClipBounds();
        g.setColor(getBackground());
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        int startY = 0;
        int endY = 0;
        int startLine = 0;

        if (clip.x < insets.left)
        {
            FontMetrics fm = g.getFontMetrics();
            _fontHeight = fm.getHeight();
            int y = fm.getAscent() + insets.top;
            int startingLineNumber = ((clip.y + insets.top) / _fontHeight) + 1;

            if (startingLineNumber != 1)
            {
                if (y < clip.y)
                {
                    y = startingLineNumber * _fontHeight - (_fontHeight - fm.getAscent());
                }
            }

            int yend = y + clip.height + _fontHeight;
            int lnxstart = insets.left;

            lnxstart -= lineNumberWidth();
            g.setColor(getForeground());

            startY = y;
            endY = yend;
            startLine = startingLineNumber;

            while (y < yend)
            {
                String brpt = "";
                String frmtString = "  %02d";
                Color tmp = g.getColor();

                g.setColor(Color.DARK_GRAY);
                g.fill3DRect(0, y - _fontHeight + 4, lineNumberWidth(), _fontHeight, true);

                brpt = String.format(frmtString, startingLineNumber);


                
                if (Program.CodeBreakpoints.contains(startingLineNumber)) {
                    g.setColor(new Color(255,50,50));
                    g.fillRect(0, y - _fontHeight + 4, lineNumberWidth()/2, _fontHeight);

                }
                if (LineWatches.contains(startingLineNumber)) {
                    g.setColor(new Color(10,200,51));
                    g.fillRect(lineNumberWidth()/2, y - _fontHeight + 4, lineNumberWidth()/2, _fontHeight);

                }

                if (startingLineNumber == currentLine())
                {
                    g.setColor(this.lineColor);
                    if(PETApp.Status == Constants.STOPPED) {
                        g.fillRect(lineNumberWidth(), y - _fontHeight + 4, this.getWidth(), _fontHeight);
                    }
                }

                g.setColor(new Color(255,255,255));
                g.drawString(brpt + "  ", lnxstart, y);
                
                g.setColor(tmp);

                y += _fontHeight;
                startingLineNumber++;
            }
        }
        super.paintComponent(g);

    }

    public void createPopupMenu()
    {
        JMenuItem menuItem = null;
        JPopupMenu popup = null;
        menuItem = null;
        popup = new JPopupMenu();
        for (MenuAction ma : MenuAction.values())
        {
            menuItem = new JMenuItem(ma.toString());
            menuItem.setName(ma.toString());
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }
        //Add listener to the text area so the popup menu can come up.
        MouseListener popupListener = new PopupListener(popup);
        super.addMouseListener(popupListener);
    }

    public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem) (e.getSource());
        JPopupMenu c = (JPopupMenu) source.getParent();
        LineNumberedTextArea temp = (LineNumberedTextArea) c.getInvoker();

        int caretPos = temp.getCaretPosition();
        Element root = temp.getDocument().getDefaultRootElement();
        int line = root.getElementIndex(caretPos) + 1;

        if (e.getActionCommand().equals(MenuAction.BreakPoint.toString()))
        {
            toggleBreakPoint(line, temp);
        }
        if (e.getActionCommand().equals(MenuAction.StatPoint.toString()))
        {
            if (!LineWatches.contains(line))
            {
                LineWatches.addWatchToLine(line);
                temp.repaint();
            } else
            {
                LineWatches.removeLineToWatch(line);
                temp.repaint();
            }
        }
    }

    public void itemStateChanged(ItemEvent e)
    {
        Object source = e.getItemSelectable();
        if (source.getClass().getName().contains("javax.swing.JCheckBox"))
        {
            JCheckBox src = (JCheckBox) source;
            int line = Integer.parseInt(src.getName());
            LineNumberedTextArea temp = (LineNumberedTextArea) src.getParent().getParent();
            if (src.getActionCommand().contains("code"))
            {
                if (src.isSelected())
                {
                    addCodeBreakpoint(line, temp);
                } else
                {
                    removeCodeBreakpoint(line, temp);
                }
            }
            if (src.getActionCommand().contains("stat"))
            {
                if (src.isSelected())
                {
                    if (!LineWatches.contains(line))
                    {
                        LineWatches.addWatchToLine(line);
                        temp.repaint();
                    }
                } else
                {
                    if (LineWatches.contains(line))
                    {
                        LineWatches.removeLineToWatch(line);
                        temp.repaint();
                    }
                }
            }
        }
        this.repaint();
    }

    public void caretUpdate(CaretEvent arg0)
    {
        //removeCheckboxes();
        this.repaint();
    }

    public void mouseClicked(MouseEvent arg0)
    {
        //Required
    }

    public void mousePressed(MouseEvent arg0)
    {
        if(PETApp.Status == Constants.STOPPED && PETApp.petMain.errorsToClear) {
            PETApp.petMain.clearErrorHighlights();
            PETApp.petMain.errorsToClear = false;
        }
        if (arg0.getButton() == MouseEvent.BUTTON3) {
            updateCurrentLine(arg0);
        }
    }

    public void mouseReleased(MouseEvent arg0)
    {
        //Required
    }

    public void mouseEntered(MouseEvent arg0)
    {
        //Required
    }

    public void mouseExited(MouseEvent arg0)
    {
        //Required
    }

    public void mouseWheelMoved(MouseWheelEvent arg0)
    {
        try
        {
            int notches = arg0.getWheelRotation();
            LineNumberedTextArea area = (LineNumberedTextArea) arg0.getSource();
            int val = notches * 50;
            int docnum = area.getCaretPosition() + val;
            area.setCaretPosition(docnum);
        }
        catch (Exception ex)
        {
            Logger.getLogger(LineNumberedTextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateCurrentLine(MouseEvent arg0)
    {
        try
        {
            int approxLine = getApproxLine(arg0.getY());
            LineNumberedTextArea area = (LineNumberedTextArea) arg0.getSource();
            int docnum = area.getLineStartOffset(approxLine);
            area.setCaretPosition(docnum);
        }
        catch (BadLocationException ex)
        {
            Logger.getLogger(LineNumberedTextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getApproxLine(int coordinateY)
    {
        int line = 0;
        if (coordinateY > 0 && _fontHeight > 0)
        {
            line = coordinateY / _fontHeight;
            if (line < 0)
            {
                line = 0;
            }
        }
        return line;
    }

    class PopupListener extends MouseAdapter
    {

        JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu)
        {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {   
            
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e)
        {
            if (e.isPopupTrigger())
            {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public enum MenuAction
    {

        BreakPoint
        {

            @Override
            public String toString()
            {
                return "Toggle Breakpoint";
            }
        },
        StatPoint
        {

            @Override
            public String toString()
            {
                return "Toggle Statistics Line";

            }
        }
    }

    void addCodeBreakpoint(int line, LineNumberedTextArea area)
    {
        if (!Program.CodeBreakpoints.contains(line))
        {
            Program.CodeBreakpoints.add(line);
            area.repaint();
        }

    }

    void removeCodeBreakpoint(int line, LineNumberedTextArea area)
    {
        if (Program.CodeBreakpoints.contains(line))
        {
            Program.CodeBreakpoints.remove(Program.CodeBreakpoints.indexOf(line));
            area.repaint();
        }
    }

    void toggleBreakPoint(int line, LineNumberedTextArea area)
    {
        if (Program.CodeBreakpoints.contains(line))
        {
            removeCodeBreakpoint(line, area);
        } else
        {
            addCodeBreakpoint(line, area);
        }
    }

    private void addBreakPointButtons(int sy, int ey, int startLine, Graphics g)
    {
        //Rectangle clip = this.getBounds();
        int starty = sy;
        int endy = ey;

        int checkBoxWidth = 10;
        int panelWidth = 26;
        int panelHeight = 10;
        int line = startLine;
        while (starty < endy)
        {
            String panelname = "PNL_" + line;
            if (!cboxList.contains(line))
            {
                Border border = BorderFactory.createEmptyBorder(0, 0, 0, 0);
                JPanel breakpt = new JPanel(new GridBagLayout()); //For setting breakpoints
                breakpt.setName(panelname);
                breakpt.setOpaque(false);
                breakpt.setBounds(0, starty - _fontHeight + checkBoxWidth, panelWidth, panelHeight);
                breakpt.setSize(panelWidth, panelHeight);
                breakpt.setBorder(border);
                JCheckBox code = new JCheckBox();
                code.setActionCommand("code");
                code.setName(String.format("%d", line));
                code.setBorder(border);
                code.setPreferredSize(new Dimension(10, 10));
                code.setOpaque(false);
                code.setCursor(new Cursor(Cursor.HAND_CURSOR));
                code.addItemListener(this);
                breakpt.add(code);

                JLabel spacer = new JLabel(" ");
                breakpt.add(spacer);

                JCheckBox statbtn = new JCheckBox();
                statbtn.setActionCommand("stat");
                statbtn.setName(String.format("%d", line));
                statbtn.setBorder(border);
                statbtn.setPreferredSize(new Dimension(10, 10));
                statbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                statbtn.addItemListener(this);
                breakpt.add(statbtn);

                add(breakpt);
                cboxList.add(line);
            }
            starty += _fontHeight;
            line++;
        }
    }
} 