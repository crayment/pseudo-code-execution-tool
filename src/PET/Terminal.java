/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Terminal.java
 *
 * Created on Mar 24, 2009, 3:56:10 PM
 */
package PET;

import PET.model.PersistentSettings;
import PET.model.SettingsMgr;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author ianphillipchuk
 */
public class Terminal extends javax.swing.JPanel
{

    public static boolean inputSet = false;
    public static boolean wanted = false;
    public static boolean resetTriggered = false;
    public static String input;

    /** Creates new form Terminal */
    public Terminal()
    {
        initComponents();
        this.InputTextField.setCaret(new DefaultCaret());
        this.InputTextField.setCaretColor(getForeground());

        setCustomSettings();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField2 = new javax.swing.JTextField();
        ScrollPaneTerminal = new javax.swing.JScrollPane();
        TextAreaTerminal = new javax.swing.JTextArea();
        InputPanel = new javax.swing.JPanel();
        InputTextField = new InputTerminalTextField();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(PET.PETApp.class).getContext().getResourceMap(Terminal.class);
        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N

        setName("Form"); // NOI18N

        ScrollPaneTerminal.setName("ScrollPaneTerminal"); // NOI18N

        TextAreaTerminal.setBackground(resourceMap.getColor("TextAreaTerminal.background")); // NOI18N
        TextAreaTerminal.setColumns(20);
        TextAreaTerminal.setEditable(false);
        TextAreaTerminal.setForeground(resourceMap.getColor("TextAreaTerminal.foreground")); // NOI18N
        TextAreaTerminal.setRows(5);
        TextAreaTerminal.setMaximumSize(new java.awt.Dimension(512, 500));
        TextAreaTerminal.setName("TextAreaTerminal"); // NOI18N
        ScrollPaneTerminal.setViewportView(TextAreaTerminal);

        InputPanel.setBackground(resourceMap.getColor("InputPanel.background")); // NOI18N
        InputPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(resourceMap.getColor("InputPanel.border.highlightColor"), resourceMap.getColor("InputPanel.border.shadowColor"))); // NOI18N
        InputPanel.setName("InputPanel"); // NOI18N

        InputTextField.setBackground(resourceMap.getColor("InputTextField.background")); // NOI18N
        InputTextField.setEditable(false);
        InputTextField.setForeground(resourceMap.getColor("InputTextField.foreground")); // NOI18N
        InputTextField.setBorder(null);
        InputTextField.setName("InputTextField"); // NOI18N
        InputTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InputTextFieldActionPerformed(evt);
            }
        });
        InputTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                InputTextFieldKeyTyped(evt);
            }
        });
        InputTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                InputTextFieldMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                InputTextFieldMouseEntered(evt);
            }
        });

        org.jdesktop.layout.GroupLayout InputPanelLayout = new org.jdesktop.layout.GroupLayout(InputPanel);
        InputPanel.setLayout(InputPanelLayout);
        InputPanelLayout.setHorizontalGroup(
            InputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, InputTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
        );
        InputPanelLayout.setVerticalGroup(
            InputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(InputTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ScrollPaneTerminal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
            .add(InputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(ScrollPaneTerminal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .add(1, 1, 1)
                .add(InputPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void InputTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InputTextFieldKeyTyped

        if(PETApp.Status == Constants.WAITING_FOR_USER_INPUT) {
            if(evt.getKeyChar() == '\n') {
                PETApp.userInput = this.getInputTextField().getText();
                PETApp.petMain.setRecievedUserInput();
            }
        }
        
}//GEN-LAST:event_InputTextFieldKeyTyped

    private void InputTextFieldMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InputTextFieldMouseEntered

            Cursor textCursor = new Cursor(Cursor.TEXT_CURSOR);
            PETApp.mainForm.getFrame().setCursor(textCursor);

}//GEN-LAST:event_InputTextFieldMouseEntered

    private void InputTextFieldMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InputTextFieldMouseExited

        Cursor defCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        PETApp.mainForm.getFrame().setCursor(defCursor);
}//GEN-LAST:event_InputTextFieldMouseExited

    private void InputTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InputTextFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_InputTextFieldActionPerformed
         

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
            TextAreaTerminal.setFont(font);

            setBackground(ps.getTerminalBGColor());
            setForeground(ps.getTerminalTextColor());

            TextAreaTerminal.repaint();
            InputTextField.repaint();
        }

        catch (IOException ex)
        {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        }        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(LineNumberedTextArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel InputPanel;
    private javax.swing.JTextField InputTextField;
    private javax.swing.JScrollPane ScrollPaneTerminal;
    private javax.swing.JTextArea TextAreaTerminal;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

   @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if(TextAreaTerminal != null)
            this.TextAreaTerminal.setBackground(bg);
        if(InputTextField != null)
            this.InputTextField.setBackground(bg);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if(TextAreaTerminal != null)
            this.TextAreaTerminal.setForeground(fg);
        if(InputTextField != null)
            this.InputTextField.setForeground(fg);
    }

    @Override
    public Color getBackground() {
        if(this.TextAreaTerminal == null)
            return super.getBackground();
        return this.TextAreaTerminal.getBackground();
    }

    @Override
    public Color getForeground() {
        if(this.TextAreaTerminal == null)
            return super.getForeground();
        return this.TextAreaTerminal.getForeground();
    }






    public javax.swing.JTextArea getTerm()
    {

        return this.TextAreaTerminal;

    }
    /**
     * @return the jTextField1
     */
    public javax.swing.JTextField getInputTextField() {
        return InputTextField;
    }

}
