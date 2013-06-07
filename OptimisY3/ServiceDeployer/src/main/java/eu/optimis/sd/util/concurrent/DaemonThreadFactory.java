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
package eu.optimis.sd.util.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

/**
 * A ThreadFactory for Daemon Threads. Encapsulates the {@link Executors.defaultThreadFactory} and only modifies the daemon parameter.
 * @author Daniel Henriksson (<a href="mailto:danielh@cs.umu.se">danielh@cs.umu.se</a>)
 *
 */
public class DaemonThreadFactory implements ThreadFactory {

    private static final Logger log = Logger.getLogger(DaemonThreadFactory.class.getName());
    
    private ThreadFactory threadFactory;

    /**
     * Creates a new DaemonThreadFactory
     */
    public DaemonThreadFactory() {
        this.threadFactory = Executors.defaultThreadFactory();
        log.debug("Daemon thread factory initialized");
    }
    
    @Override
    public Thread newThread(Runnable r) {
        Thread t = threadFactory.newThread(r);
        t.setDaemon(true);
        return t;
    }

}
