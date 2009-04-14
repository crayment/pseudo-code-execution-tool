/*
 * MainForm.java
 *
 * Created on Jan 20, 2009, 9:12:52 PM
 */
package PET;

import PET.model.PersistentSettings;
import PET.model.RecentFiles;
import PET.model.SettingsMgr;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.Task;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentListener;
import org.jdesktop.application.Application;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.CannotRedoException;

public class MainForm extends FrameView
{

    private File file;
    private boolean modified = false;

    public JLabel getLblErrorMessage()
    {
        return lblErrorMessage;
    }

    public void setLblErrorMessage(String lblErrorMessage)
    {
        this.lblErrorMessage.setText(lblErrorMessage);
    }

    public MainForm(SingleFrameApplication app)
    {
        super(app);
    
        // generated GUI builder code
        initComponents();

        //init helpFrame
        helpFrame = new net.sourceforge.helpgui.gui.MainFrame("/PET/resources/docs/help/", "java");
        helpFrame.setSize(1000, 600);

        // Make jspinner for speed not editable... more work than it was worth.
        ((NumberEditor)this.SpinnerSpeed.getEditor()).getTextField().setEditable(false);

        InputStream stream = getClass().getResourceAsStream(Constants.DEFAULT_FILE_LOCATION);

        Task t = new LoadFileFromInputStreamTask(stream);
        t.run();

        PETApp.mainForm = this;
        PETApp.petMain = new PETMain();
        
        updateRecentFilesMenu();








        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();

        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++)
        {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connect action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {

            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName))
                {
                    if (!busyIconTimer.isRunning())
                    {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    getProgressBar().setVisible(true);
                    getProgressBar().setIndeterminate(true);
                } else if ("done".equals(propertyName))
                {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    getProgressBar().setVisible(false);
                    getProgressBar().setValue(0);
                } else if ("message".equals(propertyName))
                {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName))
                {
                    int value = (Integer) (evt.getNewValue());
                    getProgressBar().setVisible(true);
                    getProgressBar().setIndeterminate(false);
                    getProgressBar().setValue(value);
                }
            }
        });

        // if the document is ever edited, assume that it needs to be saved
        TextAreaCode.getDocument().addDocumentListener(new DocumentListener()
        {

            public void changedUpdate(DocumentEvent e)
            {
                setModified(true);
            }

            public void insertUpdate(DocumentEvent e)
            {
                setModified(true);
            }

            public void removeUpdate(DocumentEvent e)
            {
                setModified(true);
            }
        });

        // ask for confirmation on exit
        getApplication().addExitListener(new ConfirmExit());
        setLblErrorMessage("");
    }

    /**
     * The File currently being edited.  The default value of this
     * property is "untitled.txt".
     * <p>
     * This is a bound read-only property.  It is never null.
     * 
     * @return the value of the file property.
     * @see #isModified
     */
    public File getFile()
    {
        return file;
    }

    public void updateRecentFilesMenu() {
        try {
            RecentFiles rf = new RecentFiles();
            SettingsMgr sm = new SettingsMgr();
            rf = (RecentFiles) sm.readObject(rf.getClass().getName());
            if (rf != null) {
                this.RecentFileMenu.removeAll();
                for (String filename : rf.getFiles()) {

                    JMenuItem menuItem = new JMenuItem(filename);
                    menuItem.addActionListener(new RecentFilesActionListener());
                    this.RecentFileMenu.add(menuItem);
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PETMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PETMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private class RecentFilesActionListener implements ActionListener {
        

        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem)e.getSource();
            File f = new File(source.getText());

            if(!f.exists()) {
                JOptionPane.showMessageDialog(getFrame(), "This file appears to no longer exists.", "Can not open file", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int result = JOptionPane.OK_OPTION;
            if(isModified()) {
                result = JOptionPane.showConfirmDialog(getFrame(), "Opening a new file will cause you to lose unsaved changes.",
                        "Careful", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            }
            if(result == JOptionPane.OK_OPTION) {
                Task t = new LoadFileTask(f);
                t.run();
            }
        }
        
    }




    /* Set the bound file property and update the GUI.
     */
    private void setFile(File file)
    {
        File oldValue = this.file;
        this.file = file;
        String appId = getResourceMap().getString("Application.id");
        getFrame().setTitle(file.getName() + " - " + appId);
        firePropertyChange("file", oldValue, this.file);
    }

    /**
     * True if the file value has been modified but not saved.  The 
     * default value of this property is false.
     * <p>
     * This is a bound read-only property.  
     * 
     * @return the value of the modified property.
     * @see #isModified
     */
    public boolean isModified()
    {
        return modified;
    }

    /* Set the bound modified property and update the GUI.
     */
    private void setModified(boolean modified)
    {
        boolean oldValue = this.modified;
        this.modified = modified;
        firePropertyChange("modified", oldValue, this.modified);
    }

    /**
     * Prompt the user for a filename and then attempt to load the file.
     * <p>
     * The file is loaded on a worker thread because we don't want to
     * block the EDT while the file system is accessed.  To do that,
     * this Action method returns a new LoadFileTask instance, if the
     * user confirms selection of a file.  The task is executed when
     * the "open" Action's actionPerformed method runs.  The
     * LoadFileTask is responsible for updating the GUI after it has
     * successfully completed loading the file.
     * 
     * @return a new LoadFileTask or null
     */
    @Action
    public Task open()
    {

        int result = JOptionPane.OK_OPTION;
        if(isModified()) {
            result = JOptionPane.showConfirmDialog(getFrame(), "Opening a new file will cause you to lose unsaved changes.",
                    "Careful", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        }

        if(result == JOptionPane.OK_OPTION) {
            JFileChooser fc = createFileChooser("openFileChooser");

            int option = fc.showOpenDialog(getFrame());
            Task task = null;
            if (JFileChooser.APPROVE_OPTION == option)
            {
                task = new LoadFileTask(fc.getSelectedFile());
            }
            return task;

        }
        return null;
    }

    /**
     * @return the textArea
     */
    public javax.swing.JTextArea getTextAreaCode()
    {
        return TextAreaCode;
    }

    /**
     * @return the textArea1
     */
    public PET.Terminal getTerminal() {
        return Terminal;
    }

    /**
     * @return the progressBar
     */
    public javax.swing.JProgressBar getProgressBar()
    {
        return progressBar;
    }

    /**
     * @return the ArrayView
     */
    public PET.ArrayView getArrayView()
    {
        return ArrayView;
    }

    /**
     * @return the VariableView
     */
    public PET.VariableView getVariableView()
    {
        return VariableView;
    }

    /**
     * @return the ButtonRun
     */
    public javax.swing.JButton getButtonRun()
    {
        return ButtonRun;
    }

    /**
     * get line watch panel
     * @return
     */
    public PET.LineWatchView getLineWatchPanel()
    {
        return runTimeStats.getLineWatchView();
    }

    /**
     * @return the ButtonStep
     */
    public javax.swing.JButton getButtonStep() {
        return ButtonStep;
    }

    /**
     * @return the ButtonStop
     */
    public javax.swing.JButton getButtonStop() {
        return ButtonStop;
    }

    /**
     * A Task that loads the contents of a file into a String.  The
     * LoadFileTask constructor runs first, on the EDT, then the
     * #doInBackground methods runs on a background thread, and finally
     * a completion method like #succeeded or #failed runs on the EDT.
     * 
     * The resources for this class, like the message format strings are 
     * loaded from resources/LoadFileTask.properties.
     */
    private class LoadFileTask extends PETApp.LoadTextFileTask
    {

        LoadFileTask(File file)
        {
            super(MainForm.this.getApplication(), file);
        }

        /* Called on the EDT if doInBackground completes without 
         * error and this Task isn't cancelled.  We update the
         * GUI as well as the file and modified properties here.
         */
        @Override
        protected void succeeded(String fileContents)
        {
            setFile(getFile());
            getTextAreaCode().setText(fileContents);
            setModified(false);


            // save the file as a recent file
            try {
            RecentFiles rf = new RecentFiles();
            SettingsMgr sm = new SettingsMgr();
            rf = (RecentFiles) sm.readObject(rf.getClass().getName());
            if (rf == null) {
                rf = new RecentFiles();
            }
            rf.addFile(getFile().toString());
            sm.writeObject(rf);
            
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PETMain.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex);
            } catch (IOException ex) {
                Logger.getLogger(PETMain.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex);
            }
            updateRecentFilesMenu();
        }

        /* Called on the EDT if doInBackground fails because
         * an uncaught exception is thrown.  We show an error
         * dialog here.  The dialog is configured with resources
         * loaded from this Tasks's ResourceMap.
         */
        @Override
        protected void failed(Throwable e)
        {
            logger.log(Level.WARNING, "couldn't load " + getFile(), e);
            String msg = getResourceMap().getString("loadFailedMessage", getFile());
            String title = getResourceMap().getString("loadFailedTitle");
            int type = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(getFrame(), msg, title, type);
        }
    }

    private class LoadFileFromInputStreamTask extends PETApp.LoadTextFileFromInputStreamTask
    {

        LoadFileFromInputStreamTask(InputStream stream)
        {
            super(MainForm.this.getApplication(), stream);
        }

        /* Called on the EDT if doInBackground completes without
         * error and this Task isn't cancelled.  We update the
         * GUI as well as the file and modified properties here.
         */
        @Override
        protected void succeeded(String fileContents)
        {
            getTextAreaCode().setText(fileContents);
        }

        /* Called on the EDT if doInBackground fails because
         * an uncaught exception is thrown.  We show an error
         * dialog here.  The dialog is configured with resources
         * loaded from this Tasks's ResourceMap.
         */
        @Override
        protected void failed(Throwable e)
        {
            logger.log(Level.WARNING, "couldn't load " + getFile(), e);
            String msg = getResourceMap().getString("loadFailedMessage", getFile());
            String title = getResourceMap().getString("loadFailedTitle");
            int type = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(getFrame(), msg, title, type);
        }
    }

    /**
     * Save the contents of the textArea to the current {@link #getFile file}.
     * <p>
     * The text is written to the file on a worker thread because we don't want to 
     * block the EDT while the file system is accessed.  To do that, this
     * Action method returns a new SaveFileTask instance.  The task
     * is executed when the "save" Action's actionPerformed method runs.
     * The SaveFileTask is responsible for updating the GUI after it
     * has successfully completed saving the file.
     * 
     * @see #getFile
     */
    @Action(enabledProperty = "modified")
    public Task save()
    {
        return new SaveFileTask(getFile());
    }

    /**
     * Save the contents of the textArea to the current file.
     * <p>
     * This action is nearly identical to {@link #open open}.  In
     * this case, if the user chooses a file, a {@code SaveFileTask}
     * is returned.  Note that the selected file only becomes the
     * value of the {@code file} property if the file is saved
     * successfully.
     */
    @Action
    public Task saveAs()
    {
        JFileChooser fc = createSafeFileChooser("saveAsFileChooser");
        int option = fc.showSaveDialog(getFrame());
        Task task = null;
        if (JFileChooser.APPROVE_OPTION == option)
        {
            task = new SaveFileTask(fc.getSelectedFile());
        }
        return task;
    }

    /**
     * A Task that saves the contents of the textArea to the current file.
     * This class is very similar to LoadFileTask, please refer to that
     * class for more information.  
     */
    private class SaveFileTask extends PETApp.SaveTextFileTask
    {

        SaveFileTask(File file)
        {
            super(MainForm.this.getApplication(), file, getTextAreaCode().getText());
        }

        @Override
        protected void succeeded(Void ignored)
        {
            setFile(getFile());
            setModified(false);


            // save the file as a recent file
            try {
                RecentFiles rf = new RecentFiles();
                SettingsMgr sm = new SettingsMgr();
                rf = (RecentFiles) sm.readObject(rf.getClass().getName());
                if (rf == null) {
                    rf = new RecentFiles();
                }
                rf.addFile(getFile().toString());
                sm.writeObject(rf);

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PETMain.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex);
            } catch (IOException ex) {
                Logger.getLogger(PETMain.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println(ex);
            }
            updateRecentFilesMenu();
        }

        @Override
        protected void failed(Throwable e)
        {
            logger.log(Level.WARNING, "couldn't save " + getFile(), e);
            String msg = getResourceMap().getString("saveFailedMessage", getFile());
            String title = getResourceMap().getString("saveFailedTitle");
            int type = JOptionPane.ERROR_MESSAGE;
            JOptionPane.showMessageDialog(getFrame(), msg, title, type);
        }
    }

    @Action
    public void showAboutBox()
    {
        if (aboutBox == null)
        {
            JFrame mainFrame = PETApp.getApplication().getMainFrame();
            aboutBox = new AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        PETApp.getApplication().show(aboutBox);
    }

    private JFileChooser createFileChooser(String name)
    {
        JFileChooser fc = new JFileChooser(System.getProperty("user.hom"));
        fc.setFileFilter(new FileFilter()
        {

            public boolean accept(File f)
            {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            public String getDescription()
            {
                return "text files (*.txt)";
            }
        });
        fc.setAcceptAllFileFilterUsed(false);
        return fc;
    }

    private JFileChooser createSafeFileChooser(String name)
    {
        JFileChooser fc = new SafeFileChooser(System.getProperty("user.hom"));
        fc.setFileFilter(new FileFilter()
        {

            public boolean accept(File f)
            {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            public String getDescription()
            {
                return "text files (*.txt)";
            }
        });
        fc.setAcceptAllFileFilterUsed(false);
        return fc;
    }

    /** This is a substitute for FileNameExtensionFilter, which is
     * only available on Java SE 6.
     */
    private static class TextFileFilter extends FileFilter
    {

        private final String description;

        TextFileFilter(String description)
        {
            this.description = description;
        }

        @Override
        public boolean accept(File f)
        {
            if (f.isDirectory())
            {
                return true;
            }
            String fileName = f.getName();
            int i = fileName.lastIndexOf('.');
            if ((i > 0) && (i < (fileName.length() - 1)))
            {
                String fileExt = fileName.substring(i + 1);
                if ("txt".equalsIgnoreCase(fileExt))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription()
        {
            return description;
        }
    }

    private class ConfirmExit implements Application.ExitListener
    {

        public boolean canExit(EventObject e)
        {
            if (isModified())
            {
                String confirmExitText = getResourceMap().getString("confirmTextExit", getFile());
                int option = JOptionPane.showConfirmDialog(getFrame(), confirmExitText);
                return option == JOptionPane.YES_OPTION;
            // TODO: also offer saving
            } else
            {
                return true;
            }
        }

        public void willExit(EventObject e)
        {
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        lblErrorMessage = new javax.swing.JLabel();
        PanelMain = new javax.swing.JPanel();
        SplitPaneMainSplit = new javax.swing.JSplitPane();
        PanelLeft = new javax.swing.JPanel();
        ControlPanel = new javax.swing.JPanel();
        SpinnerSpeed = new javax.swing.JSpinner();
        LabelRunSpeed = new javax.swing.JLabel();
        ButtonStop = new javax.swing.JButton();
        ButtonStep = new javax.swing.JButton();
        ButtonRun = new javax.swing.JButton();
        LabelCode = new javax.swing.JLabel();
        ScrollPaneCode = new javax.swing.JScrollPane();
        TextAreaCode = new LineNumberedTextArea();
        PanelRight = new javax.swing.JPanel();
        SplitPaneTerminalVariables = new javax.swing.JSplitPane();
        PanelTerminal = new javax.swing.JPanel();
        LabelTerminal = new javax.swing.JLabel();
        Terminal = new PET.Terminal();
        SplitPaneVariables = new javax.swing.JSplitPane();
        ArrayView = new PET.ArrayView();
        VariableView = new PET.VariableView();
        runTimeStats = new PET.RunTimeStats();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem openMenuItem = new javax.swing.JMenuItem();
        RecentFileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem saveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        RunMenuItem = new javax.swing.JMenuItem();
        StepMenuItem = new javax.swing.JMenuItem();
        StopMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator fileMenuSeparator = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu editMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem cutMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem copyMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem pasteMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        userManualMenuItem = new javax.swing.JMenuItem();
        toolBar = new javax.swing.JToolBar();
        openToolBarButton = new javax.swing.JButton();
        saveToolBarButton = new javax.swing.JButton();
        cutToolBarButton = new javax.swing.JButton();
        copyToolBarButton = new javax.swing.JButton();
        pasteToolBarButton = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        mainPanel.setMaximumSize(new java.awt.Dimension(1000, 700));
        mainPanel.setMinimumSize(new java.awt.Dimension(1000, 700));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(1065, 600));

        statusPanel.setName("statusPanel"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        lblErrorMessage.setName("lblErrorMessage"); // NOI18N
        lblErrorMessage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblErrorMessageMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblErrorMessageMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblErrorMessageMouseEntered(evt);
            }
        });

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblErrorMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                .add(199, 199, 199)
                .add(statusMessageLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(statusAnimationLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusAnimationLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(statusMessageLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lblErrorMessage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(PET.PETApp.class).getContext().getResourceMap(MainForm.class);
        lblErrorMessage.getAccessibleContext().setAccessibleName(resourceMap.getString("jLabel1.AccessibleContext.accessibleName")); // NOI18N

        PanelMain.setName("PanelMain"); // NOI18N

        SplitPaneMainSplit.setResizeWeight(0.6);
        SplitPaneMainSplit.setName("SplitPaneMainSplit"); // NOI18N

        PanelLeft.setName("PanelLeft"); // NOI18N

        ControlPanel.setName("ControlPanel"); // NOI18N

        SpinnerSpeed.setName("SpinnerSpeed"); // NOI18N
        SpinnerSpeed.setModel(new SpinnerNumberModel(3, 0, 5, 1));
        SpinnerSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SpinnerSpeedStateChanged(evt);
            }
        });

        LabelRunSpeed.setName("LabelRunSpeed"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(PET.PETApp.class).getContext().getActionMap(MainForm.class, this);
        ButtonStop.setAction(actionMap.get("Stop")); // NOI18N
        ButtonStop.setFocusable(false);
        ButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ButtonStop.setName("btnStop"); // NOI18N
        ButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        ButtonStep.setAction(actionMap.get("Step")); // NOI18N
        ButtonStep.setFocusable(false);
        ButtonStep.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ButtonStep.setName("btnStep"); // NOI18N
        ButtonStep.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        ButtonRun.setAction(actionMap.get("Run")); // NOI18N
        ButtonRun.setFocusable(false);
        ButtonRun.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ButtonRun.setName("btnRun"); // NOI18N
        ButtonRun.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        org.jdesktop.layout.GroupLayout ControlPanelLayout = new org.jdesktop.layout.GroupLayout(ControlPanel);
        ControlPanel.setLayout(ControlPanelLayout);
        ControlPanelLayout.setHorizontalGroup(
            ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ControlPanelLayout.createSequentialGroup()
                .add(ButtonRun, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(ButtonStep)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(ButtonStop)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(LabelRunSpeed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(SpinnerSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(215, Short.MAX_VALUE))
        );
        ControlPanelLayout.setVerticalGroup(
            ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(ButtonRun)
                .add(ButtonStep)
                .add(ButtonStop)
                .add(LabelRunSpeed))
            .add(SpinnerSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        LabelCode.setName("LabelCode"); // NOI18N

        ScrollPaneCode.setName("ScrollPaneCode"); // NOI18N

        TextAreaCode.setColumns(20);
        TextAreaCode.setRows(5);
        TextAreaCode.setTabSize(1);
        TextAreaCode.setName("TextAreaCode"); // NOI18N
        ScrollPaneCode.setViewportView(TextAreaCode);

        org.jdesktop.layout.GroupLayout PanelLeftLayout = new org.jdesktop.layout.GroupLayout(PanelLeft);
        PanelLeft.setLayout(PanelLeftLayout);
        PanelLeftLayout.setHorizontalGroup(
            PanelLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PanelLeftLayout.createSequentialGroup()
                .add(LabelCode)
                .addContainerGap(544, Short.MAX_VALUE))
            .add(ControlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(ScrollPaneCode, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
        );
        PanelLeftLayout.setVerticalGroup(
            PanelLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PanelLeftLayout.createSequentialGroup()
                .add(LabelCode)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ScrollPaneCode, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(ControlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        SplitPaneMainSplit.setLeftComponent(PanelLeft);

        PanelRight.setMaximumSize(new java.awt.Dimension(350, 550));
        PanelRight.setMinimumSize(new java.awt.Dimension(280, 300));
        PanelRight.setName("PanelRight"); // NOI18N

        SplitPaneTerminalVariables.setBorder(null);
        SplitPaneTerminalVariables.setDividerLocation(250);
        SplitPaneTerminalVariables.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        SplitPaneTerminalVariables.setResizeWeight(0.6);
        SplitPaneTerminalVariables.setMinimumSize(new java.awt.Dimension(60, 33));
        SplitPaneTerminalVariables.setName("SplitPaneTerminalVariables"); // NOI18N

        PanelTerminal.setMaximumSize(new java.awt.Dimension(300, 300));
        PanelTerminal.setName("PanelTerminal"); // NOI18N

        LabelTerminal.setName("LabelTerminal"); // NOI18N

        Terminal.setName("Terminal"); // NOI18N

        org.jdesktop.layout.GroupLayout PanelTerminalLayout = new org.jdesktop.layout.GroupLayout(PanelTerminal);
        PanelTerminal.setLayout(PanelTerminalLayout);
        PanelTerminalLayout.setHorizontalGroup(
            PanelTerminalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PanelTerminalLayout.createSequentialGroup()
                .add(LabelTerminal)
                .add(394, 394, 394))
            .add(Terminal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );
        PanelTerminalLayout.setVerticalGroup(
            PanelTerminalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PanelTerminalLayout.createSequentialGroup()
                .add(LabelTerminal)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(Terminal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
        );

        SplitPaneTerminalVariables.setLeftComponent(PanelTerminal);

        SplitPaneVariables.setName("SplitPaneVariables"); // NOI18N

        ArrayView.setName("ArrayView"); // NOI18N
        SplitPaneVariables.setRightComponent(ArrayView);

        VariableView.setName("VariableView"); // NOI18N
        SplitPaneVariables.setLeftComponent(VariableView);

        SplitPaneTerminalVariables.setRightComponent(SplitPaneVariables);

        runTimeStats.setName("runTimeStats"); // NOI18N

        org.jdesktop.layout.GroupLayout PanelRightLayout = new org.jdesktop.layout.GroupLayout(PanelRight);
        PanelRight.setLayout(PanelRightLayout);
        PanelRightLayout.setHorizontalGroup(
            PanelRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SplitPaneTerminalVariables, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, runTimeStats, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
        );
        PanelRightLayout.setVerticalGroup(
            PanelRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, PanelRightLayout.createSequentialGroup()
                .add(SplitPaneTerminalVariables, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .add(1, 1, 1)
                .add(runTimeStats, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        SplitPaneMainSplit.setRightComponent(PanelRight);

        org.jdesktop.layout.GroupLayout PanelMainLayout = new org.jdesktop.layout.GroupLayout(PanelMain);
        PanelMain.setLayout(PanelMainLayout);
        PanelMainLayout.setHorizontalGroup(
            PanelMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SplitPaneMainSplit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        PanelMainLayout.setVerticalGroup(
            PanelMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(SplitPaneMainSplit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PanelMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(statusPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .add(PanelMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        resourceMap.injectComponents(mainPanel);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setName("fileMenu"); // NOI18N

        openMenuItem.setAction(actionMap.get("open")); // NOI18N
        openMenuItem.setName("openMenuItem"); // NOI18N
        fileMenu.add(openMenuItem);

        RecentFileMenu.setName("RecentFileMenu"); // NOI18N
        fileMenu.add(RecentFileMenu);

        saveMenuItem.setAction(actionMap.get("save")); // NOI18N
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setAction(actionMap.get("saveAs")); // NOI18N
        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.setName("saveAsMenuItem"); // NOI18N
        fileMenu.add(saveAsMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        RunMenuItem.setAction(actionMap.get("Run")); // NOI18N
        RunMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        RunMenuItem.setName("RunMenuItem"); // NOI18N
        fileMenu.add(RunMenuItem);

        StepMenuItem.setAction(actionMap.get("Step")); // NOI18N
        StepMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        StepMenuItem.setName("StepMenuItem"); // NOI18N
        fileMenu.add(StepMenuItem);

        StopMenuItem.setAction(actionMap.get("Stop")); // NOI18N
        StopMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        StopMenuItem.setName("StopMenuItem"); // NOI18N
        fileMenu.add(StopMenuItem);

        fileMenuSeparator.setName("fileMenuSeparator"); // NOI18N
        fileMenu.add(fileMenuSeparator);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setName("editMenu"); // NOI18N

        cutMenuItem.setAction(actionMap.get("cut"));
        cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        cutMenuItem.setName("cutMenuItem"); // NOI18N
        editMenu.add(cutMenuItem);

        copyMenuItem.setAction(actionMap.get("copy"));
        copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyMenuItem.setName("copyMenuItem"); // NOI18N
        editMenu.add(copyMenuItem);

        pasteMenuItem.setAction(actionMap.get("paste"));
        pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        pasteMenuItem.setName("pasteMenuItem"); // NOI18N
        editMenu.add(pasteMenuItem);

        jMenuItem1.setAction(actionMap.get("undo")); // NOI18N
        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        editMenu.add(jMenuItem1);

        jMenuItem2.setAction(actionMap.get("redo")); // NOI18N
        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        editMenu.add(jMenuItem2);

        menuBar.add(editMenu);

        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        userManualMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        userManualMenuItem.setName("userManualMenuItem"); // NOI18N
        userManualMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userManualMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(userManualMenuItem);

        menuBar.add(helpMenu);
        resourceMap.injectComponents(menuBar);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        openToolBarButton.setAction(actionMap.get("open")); // NOI18N
        openToolBarButton.setFocusable(false);
        openToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openToolBarButton.setName("openToolBarButton"); // NOI18N
        openToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(openToolBarButton);

        saveToolBarButton.setAction(actionMap.get("save")); // NOI18N
        saveToolBarButton.setFocusable(false);
        saveToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveToolBarButton.setName("saveToolBarButton"); // NOI18N
        saveToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(saveToolBarButton);

        cutToolBarButton.setAction(actionMap.get("cut"));
        cutToolBarButton.setFocusable(false);
        cutToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cutToolBarButton.setName("cutToolBarButton"); // NOI18N
        cutToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(cutToolBarButton);

        copyToolBarButton.setAction(actionMap.get("copy"));
        copyToolBarButton.setFocusable(false);
        copyToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        copyToolBarButton.setName("copyToolBarButton"); // NOI18N
        copyToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(copyToolBarButton);

        pasteToolBarButton.setAction(actionMap.get("paste"));
        pasteToolBarButton.setFocusable(false);
        pasteToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pasteToolBarButton.setName("pasteToolBarButton"); // NOI18N
        pasteToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(pasteToolBarButton);

        btnSettings.setAction(actionMap.get("settingsMgr")); // NOI18N
        btnSettings.setIcon(resourceMap.getIcon("icon")); // NOI18N
        btnSettings.setFocusable(false);
        btnSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSettings.setLabel(resourceMap.getString("label")); // NOI18N
        btnSettings.setName(""); // NOI18N
        btnSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnSettings);
        btnSettings.getAccessibleContext().setAccessibleName(resourceMap.getString("Settings.AccessibleContext.accessibleName")); // NOI18N
        resourceMap.injectComponents(toolBar);

        jButton1.setName("jButton1"); // NOI18N
        resourceMap.injectComponents(jButton1);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(toolBar);
    }// </editor-fold>//GEN-END:initComponents

    private void SpinnerSpeedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SpinnerSpeedStateChanged
        int speed = (Integer) SpinnerSpeed.getValue();
        PETApp.petMain.setSpeed(speed);
}//GEN-LAST:event_SpinnerSpeedStateChanged

    private void lblErrorMessageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblErrorMessageMouseClicked
        
        if (lblErrorMessage.getText() != null && !lblErrorMessage.getText().equals("")
                && !lblErrorMessage.getText().equals("<html><p style=\"color:blue\">Waiting for input...</p></html>")) {
            if (PETApp.petMain.getError() != null && PETApp.petMain.getError().getHelpFile() != null) {
                helpFrame.goToPage(PETApp.petMain.getError().getHelpFile());
            }
            helpFrame.setVisible(true);
        }
    }//GEN-LAST:event_lblErrorMessageMouseClicked

    private void lblErrorMessageMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblErrorMessageMouseEntered

        if (lblErrorMessage.getText() != null && !lblErrorMessage.getText().equals("")
                && !lblErrorMessage.getText().equals("<html><p style=\"color:blue\">Waiting for input...</p></html>")) {
            Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
            getFrame().setCursor(handCursor);
        }
    }//GEN-LAST:event_lblErrorMessageMouseEntered

    private void lblErrorMessageMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblErrorMessageMouseExited

        if (lblErrorMessage.getText() != null && !lblErrorMessage.getText().equals("")
                && !lblErrorMessage.getText().equals("<html><p style=\"color:blue\">Waiting for input...</p></html>")) {
            Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            getFrame().setCursor(defaultCursor);
        }
    }//GEN-LAST:event_lblErrorMessageMouseExited

    private void userManualMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userManualMenuItemActionPerformed
        helpFrame.setVisible(true);
    }//GEN-LAST:event_userManualMenuItemActionPerformed

    @Action
    public void Step()
    {
        PETApp.petMain.stepPET();
    }

    @Action
    public void Run()
    {
        PETApp.petMain.runPET();
    }

    @Action
    public void Stop()
    {

        PETApp.petMain.stopPET();
    }

    @Action
    public void undo()
    {
        try
        {
            LineNumberedTextArea.undoManager.undo();
        }
        catch (CannotRedoException cre)
        {
            cre.printStackTrace();
        }
    }

    @Action
    public void redo()
    {
        try
        {
            LineNumberedTextArea.undoManager.redo();
        }
        catch (CannotRedoException cre)
        {
            cre.printStackTrace();
        }
    }

    @Action
    public void load(File f) {

        System.out.println("f = " + f);
    }
    

    @Action
    public void settingsMgr()
    {
        if(PETApp.Status == Constants.RUNNING) {
            JOptionPane.showMessageDialog(getFrame(), "Settings can not be changed while in running mode.", "Sorry", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        Settings jpnl = new Settings();

        Font f = TextAreaCode.getFont();
        jpnl.settings_fontType.setSelectedItem(f.getName());
        jpnl.settings_cboxFontSize.setSelectedItem(((Integer)f.getSize()).toString());

        jpnl.setCodeBGColor(TextAreaCode.getBackground());
        jpnl.setCodeFontColor(TextAreaCode.getForeground());
        jpnl.setCodeLineColor(((LineNumberedTextArea)TextAreaCode).lineColor);
        jpnl.setCodeRunBGColor(PETApp.petMain.runningBGColor);
        jpnl.setCodeRunFontColor(PETApp.petMain.runningFontColor);

        jpnl.setTermBGColor(Terminal.getBackground());
        jpnl.setTermFontColor(Terminal.getForeground());

        int ret = JOptionPane.showConfirmDialog(null, jpnl, "Display Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
        if (ret == JOptionPane.OK_OPTION)
        {
            try
            {
                PersistentSettings ps = new PersistentSettings();
                SettingsMgr sm = new SettingsMgr();
               
                int fontSize = Integer.parseInt(jpnl.settings_cboxFontSize.getSelectedItem().toString());
                String fontType = jpnl.settings_fontType.getSelectedItem().toString();
               
                ps.setFontSize(fontSize);
                ps.setFontType(fontType);

                ps.setCodeBGColor(jpnl.getCodeBGColor());
                ps.setCodeTextColor(jpnl.getCodeFontColor());
                ps.setCodeLineColor(jpnl.getCodeLineColor());
                ps.setCodeRunningBGColor(jpnl.getCodeRunBGColor());
                ps.setCodeRunningFontColor(jpnl.getCodeRunFontColor());

                ps.setTerminalBGColor(jpnl.getTermBGColor());
                ps.setTerminalTextColor(jpnl.getTermFontColor());

                sm.writeObject(ps);

                
                ((LineNumberedTextArea) TextAreaCode).setCustomSettings();
                TextAreaCode.repaint();

                ((Terminal) Terminal).setCustomSettings();
                Terminal.repaint();

                PETApp.petMain.setCustomSettings();

                ArrayView.setCustomSettings();

                VariableView.setCustomSettings();

                runTimeStats.getLineWatchView().setCustomSettings();
                
            }
            catch (IOException ex)
            {

                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null,
                        ex);
            }

        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private PET.ArrayView ArrayView;
    private javax.swing.JButton ButtonRun;
    private javax.swing.JButton ButtonStep;
    private javax.swing.JButton ButtonStop;
    private javax.swing.JPanel ControlPanel;
    private javax.swing.JLabel LabelCode;
    private javax.swing.JLabel LabelRunSpeed;
    private javax.swing.JLabel LabelTerminal;
    private javax.swing.JPanel PanelLeft;
    private javax.swing.JPanel PanelMain;
    private javax.swing.JPanel PanelRight;
    private javax.swing.JPanel PanelTerminal;
    private javax.swing.JMenu RecentFileMenu;
    private javax.swing.JMenuItem RunMenuItem;
    private javax.swing.JScrollPane ScrollPaneCode;
    private javax.swing.JSpinner SpinnerSpeed;
    private javax.swing.JSplitPane SplitPaneMainSplit;
    private javax.swing.JSplitPane SplitPaneTerminalVariables;
    private javax.swing.JSplitPane SplitPaneVariables;
    private javax.swing.JMenuItem StepMenuItem;
    private javax.swing.JMenuItem StopMenuItem;
    private PET.Terminal Terminal;
    private javax.swing.JTextArea TextAreaCode;
    private PET.VariableView VariableView;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton copyToolBarButton;
    private javax.swing.JButton cutToolBarButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblErrorMessage;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton openToolBarButton;
    private javax.swing.JButton pasteToolBarButton;
    private javax.swing.JProgressBar progressBar;
    private PET.RunTimeStats runTimeStats;
    private javax.swing.JButton saveToolBarButton;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenuItem userManualMenuItem;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private static final Logger logger = Logger.getLogger(MainForm.class.getName());
    private net.sourceforge.helpgui.gui.MainFrame helpFrame = null;
}

