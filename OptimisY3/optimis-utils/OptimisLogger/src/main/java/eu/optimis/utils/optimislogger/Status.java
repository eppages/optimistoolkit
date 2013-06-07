package eu.optimis.utils.optimislogger;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Status object sent to the GUI
 * 
 * @author Daniel Henriksson (<a
 *         href="mailto:danielh@cs.umu.se">danielh@cs.umu.se</a>)
 * 
 */
@XmlRootElement
public class Status {

    private String operation;  
    private String component;
    private Double progress;
    private boolean useProgressBar = false;
    private boolean done = false;

    
    public Status() {
    }

    /**
     * Constructor for message based status reports, without progress bar
     * 
     * @param component
     *            The affected component
     * @param operation
     *            The current operation
     * @param done
     *            Indicates if the process is complete or not
     */
    public Status(String component, String operation, boolean done) {
        this.component = component;
        this.operation = operation;
        this.done = done;
    }

    /**
     * Contstructor for progressbar-based status reports
     * 
     * @param component
     *            The affected component
     * @param operation
     *            The current operation
     * @param progress
     *            Progress of the current operation
     * @param done
     *            Indicates if the process is complete or not
     */
    public Status(String component, String operation, double progress, boolean done) {
        this.component = component;
        this.useProgressBar = true;
        this.progress = progress;
        this.operation = operation;
        this.done = done;
    }

    public String getOperation() {
        return operation;
    }

    public String getComponent() {
        return component;
    }

    public Double getProgress() {
        return progress;
    }
    
    public boolean isDone() {
        return done;
    }

    public boolean isUseProgressBar() {
        return useProgressBar;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public void setUseProgressBar(boolean useProgressBar) {
        this.useProgressBar = useProgressBar;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Status [operation=" + operation + ", component=" + component + ", progress=" + progress
                + ", useProgressBar=" + useProgressBar + ", done=" + done + "]";
    }
}
