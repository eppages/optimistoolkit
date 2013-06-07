/*
 Copyright (C) 2012-2013 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.optimis.sd.util.io;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import eu.optimis.sd.util.concurrent.DaemonThreadFactory;

/**
 * File monitor to keep track of changes in a certain file. The monitoring is
 * started once the first listener is added, and goes on until all listeners are
 * removed.
 * 
 * @author Daniel Henriksson (<a
 *         href="mailto:danielh@cs.umu.se">danielh@cs.umu.se</a>)
 * 
 */
public class FileMonitor extends TimerTask {

    private static final Logger log = Logger.getLogger(FileMonitor.class.getName());

    private File monitoredFile;
    private long period;
    private Set<FileChangeListener> listeners;
    private long lastModified;
    private ScheduledExecutorService ex;

    private ScheduledFuture<?> future;

    /**
     * Initialize the Filemonitor
     * 
     * @param file
     *            The File to monitor
     * @param period
     *            period (in ms) to watch the file for changes
     */
    public FileMonitor(File file, long period) {
        this.period = period;
        this.lastModified = file.lastModified();
        this.monitoredFile = file;
        this.listeners = new HashSet<FileChangeListener>();
    }

    /**
     * Adds another FileChangeListener for updates done to this file
     * 
     * @param listener
     *            The listener to add
     * @Throws {@link IllegalArgumentException} If the listener already exists
     */
    public void addFileChangeListener(FileChangeListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("Adding listener already in set!");
        }

        listeners.add(listener);

        // Start timer on first listener
        if (listeners.size() == 1) {
            ex = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
            future = ex.scheduleAtFixedRate(this, period, period, TimeUnit.MILLISECONDS);
            log.info("File monitor started");
        }
    }

    /**
     * Removes an existing FileChangeListener for this file
     * 
     * @param listener
     *            The listener to remove
     * @Throws {@link IllegalArgumentException} If the listener does not exists
     */
    public void removeFileChangeListener(FileChangeListener listener) {
        if (!listeners.remove(listener)) {
            throw new IllegalArgumentException("Removing listener not in set!");
        }

        // Stop monitor when last listener is removed
        if (listeners.size() == 0) {
            this.cancel();
            log.info("File monitor stopped");
        }
    }

    @Override
    /**
     * Called by the background thread to look for changes in the file
     */
    public void run() {
        long lastModified = monitoredFile.lastModified();

        if (this.lastModified != lastModified) {
            this.lastModified = lastModified;

            log.info("File update detected, notifying " + listeners.size() + " listeners");

            for (FileChangeListener listener : listeners) {
                listener.fireFileChanged(this.monitoredFile);
            }
        }
    }

    /**
     * Reconfigure the update period for this file
     * 
     * @param newPeriod
     *            The new update period (in ms)
     */
    public void setPeriod(Long newPeriod) {
        this.period = newPeriod;
        future.cancel(false);
        
        // Reschedule a new future
        log.info("Rescheduling new update period for file monitor, new interval is: " + period + " ms.");
        future = ex.scheduleAtFixedRate(this, period, period, TimeUnit.MILLISECONDS);
    }
}
