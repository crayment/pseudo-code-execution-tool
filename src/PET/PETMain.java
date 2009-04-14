/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PET;

import PET.model.Highlight;
import PET.model.LineWatches;
import PET.model.PersistentSettings;
import PET.model.Program;
import PET.model.SettingsMgr;
import PET.model.Variables;
import PET.parser.ParserException;
import PET.validator.Errors;
import PET.validator.PETError;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;

/**
 *
 * @author crayment
 */
public class PETMain
{
    
    PETThread thread;
    private PETError error;

    private boolean stateBeforeRead = false;
    public boolean errorsToClear = false;

    public Color runningBGColor;
    public Color bgColor;
    public Color runningFontColor;
    public Color fontColor;

    public PETMain()
    {
        PETApp.petMain = this;



        // set up observers
        Variables.getInstance().addObserver(PETApp.mainForm.getVariableView());
        Variables.getInstance().addObserver(PETApp.mainForm.getArrayView());
        LineWatches.getInstance().addObserver(PETApp.mainForm.getLineWatchPanel());

        setCustomSettings();

        this.thread = new PETThread(this);
        this.thread.start();

    }

    public void setSpeed(int speed)
    {
        thread.setSpeed(speed);
    }

    public int getSpeed()
    {
        return thread.getSpeed();
    }

    public void stepPET()
    {
        thread.stepPET();
        updateRunButton();
    }

    public void runPET()
    {
        thread.runPET();
        updateRunButton();
    }

    private void pausePET() {
        thread.pausePET();
        Variables.updateAllVariables(); // make variables update their views
        updateHighlight();
        

        updateRunButton();
    }

    public void stopPET()
    {
        thread.stopPET();
        setGuiStoppedMode();
        updateRunButton();
        Variables.updateAllVariables(); // make variables update their views
    }

    public void updateRunButton(){
        if(thread.running) PETApp.mainForm.getButtonRun().setText("Pause");
        else PETApp.mainForm.getButtonRun().setText("Run");
    }

    Highlight lastHighlight = null;
    public void step()
    {
        if(thread.running && thread.getSpeed() == 5) {
            PETApp.updateGui = false;
        }
        else {
            PETApp.updateGui = true;
        }

        if (PETApp.Status == Constants.STOPPED)
        {
            setGuiRunningMode();


            
                PETApp.mainForm.getTerminal().getTerm().setText("");
                PrintStream out = new PrintStream(new TextAreaOutputStream(PETApp.mainForm.getTerminal().getTerm()));
                Program.load(PETApp.mainForm.getTextAreaCode().getText(), out, System.in);
                if(Errors.numberOfErrors() > 0) {
                    handleError();
                    errorsToClear = true;
                }
                else {
                    try {
                        Highlight h = Program.getHighlight();
                        lastHighlight = h;
                        highlightLine(h);
                        if(Program.CodeBreakpoints.contains(h.getLineNumber())) {
                            pausePET();
                        }
                        
                        
                    } catch (Exception e) {
                        PETApp.mainForm.setLblErrorMessage("Runtime error: " + e.toString().substring(0, Math.min(e.toString().length(), 80)));
                        this.stopPET();
                    }
                }
            
        } else {
            try {
                // step
                Program.step();
                // increment total executions count
                LineWatches.incrementLineWatch(LineWatches.ALL_LINES);
                // increment count for line if its being watched
                if(LineWatches.contains(lastHighlight.getLineNumber())) {
                    LineWatches.incrementLineWatch(lastHighlight.getLineNumber());
                }

                

                if(Errors.numberOfErrors() > 0) {
                    handleError();
                    errorsToClear = true;
                } else if(PETApp.Status == Constants.WAITING_FOR_USER_INPUT) {

                    setGetUserInput();
                    
                }else {
                    PETHighlightPainter.removeHighlights(PETApp.mainForm.getTextAreaCode());

                    Highlight h = Program.getHighlight();
                    highlightLine(h);
                    lastHighlight = h;

                    int nextLine = Program.getHighlight().getLineNumber();
                    if(Program.CodeBreakpoints.contains(nextLine)) {
                        pausePET();
                    }
                }
                
            } catch (Exception e) {
                PETApp.mainForm.setLblErrorMessage("Runtime error: " + e.toString().substring(0, Math.min(e.toString().length(), 80)));
                this.stopPET();
                
            }


            if (Program.getStatus() == Constants.STOPPED) {
                this.stopPET();
                setGuiStoppedMode();
            }

        }
    }


    private void handleError() {
        PETError e = Errors.getFirstError();
        this.error = e;
        Highlight h = e.getHighlight();
        PETApp.mainForm.setLblErrorMessage(e.toString());
        this.stopPET();
        highlightErrorLine(h);
    }


    private void updateHighlight() {
        PETHighlightPainter.removeHighlights(PETApp.mainForm.getTextAreaCode());
        
        try {
            Highlight h = Program.getHighlight();
            highlightLine(h);

        } catch (ParserException e) {
            PETApp.mainForm.setLblErrorMessage("Runtime error: " + e.toString().substring(0, Math.min(e.toString().length(), 80)));
            this.stopPET();
        }

    }

    private void highlightLine(Highlight h) {
        if(thread.running && !PETApp.updateGui) {
            return;
        }
        if(h != null) {
            Highlighter hilite = PETApp.mainForm.getTextAreaCode().getHighlighter();
            try {
                hilite.addHighlight(h.getStartIndex(), h.getEndIndex(), PETHighlightPainter.getInstance());
            } catch (Exception ex) {
                System.err.println(ex);
            }


            LineNumberedTextArea code = (LineNumberedTextArea)(PETApp.mainForm.getTextAreaCode());
            code.setScrollToPos(h.getStartIndex());
        }
    }

    private void highlightErrorLine(Highlight h) {
        if(h != null) {
            Highlighter hilite = PETApp.mainForm.getTextAreaCode().getHighlighter();
            try {
                hilite.addHighlight(h.getStartIndex(), h.getEndIndex(), PETErrorHighlightPainter.getInstance());
            } catch (Exception ex) {
                System.err.println(ex);
            }

            LineNumberedTextArea code = (LineNumberedTextArea)(PETApp.mainForm.getTextAreaCode());
            code.setScrollToPos(h.getStartIndex());
        }
    }


    private void setGuiRunningMode() {
        PETApp.Status = Constants.RUNNING;

        PETApp.mainForm.setLblErrorMessage("");

        PETApp.mainForm.getTextAreaCode().setEditable(false);
        this.bgColor = PETApp.mainForm.getTextAreaCode().getBackground();
        PETApp.mainForm.getTextAreaCode().setBackground(this.runningBGColor);
        this.fontColor = PETApp.mainForm.getTextAreaCode().getForeground();
        PETApp.mainForm.getTextAreaCode().setForeground(this.runningFontColor);

        PETApp.mainForm.getProgressBar().setVisible(true);
        PETApp.mainForm.getProgressBar().setIndeterminate(true);

        PETHighlightPainter.removeHighlights(PETApp.mainForm.getTextAreaCode());
        PETErrorHighlightPainter.removeHighlights(PETApp.mainForm.getTextAreaCode());

    }

    private void setGetUserInput() {
        PETApp.mainForm.getButtonRun().setEnabled(false);
        PETApp.mainForm.getButtonStep().setEnabled(false);

        PETApp.mainForm.getTerminal().getInputTextField().setEditable(true);
        PETApp.mainForm.getTerminal().getInputTextField().requestFocus();
        
        

        PETApp.mainForm.getLblErrorMessage().setText("<html><p style=\"color:blue\">Waiting for input...</p></html>");

        stateBeforeRead = thread.running;
        this.pausePET();
    }

    public void setRecievedUserInput() {
        PETApp.Status = Constants.RUNNING;

        PETApp.mainForm.getButtonRun().setEnabled(true);
        PETApp.mainForm.getButtonStep().setEnabled(true);

        PETApp.mainForm.getTerminal().getInputTextField().setEditable(false);
        PETApp.mainForm.getTerminal().getInputTextField().setText("");

        PETApp.mainForm.getLblErrorMessage().setText("");

        if(stateBeforeRead) {
            this.runPET();
        } else {
            this.stepPET();
        }
    }

    public void clearErrorHighlights() {
        PETErrorHighlightPainter.removeHighlights(PETApp.mainForm.getTextAreaCode());
    }

    private void setGuiStoppedMode() {
        PETApp.Status = Constants.STOPPED;

        PETApp.mainForm.getTextAreaCode().setEditable(true);
        ((LineNumberedTextArea)PETApp.mainForm.getTextAreaCode()).setCustomSettings();

        PETApp.mainForm.getProgressBar().setVisible(false);
        PETApp.mainForm.getProgressBar().setValue(0);
        
        PETHighlightPainter.removeHighlights(PETApp.mainForm.getTextAreaCode());
        PETErrorHighlightPainter.removeHighlights(PETApp.mainForm.getTextAreaCode());

        
        // because the caret seems to disappear sometimes... mayne will help
        PETApp.mainForm.getTextAreaCode().setCaret(new DefaultCaret());

        PETApp.mainForm.getButtonRun().setEnabled(true);
        PETApp.mainForm.getButtonStep().setEnabled(true);

        if(PETApp.mainForm.getLblErrorMessage().getText().equals("<html><p style=\"color:blue\">Waiting for input...</p></html>")) {
            PETApp.mainForm.getLblErrorMessage().setText("");
        }
    }


    public void setCustomSettings() {

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

            this.runningBGColor = ps.getCodeRunningBGColor();
            this.runningFontColor = ps.getCodeRunningFontColor();

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

    
    /**
     * @return the error
     */
    public PETError getError() {
        return error;
    }

}
