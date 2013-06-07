/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package eu.optimis.cloudoptimizer.blo;

import eu.optimis.cloudoptimizer.blo.BLOUtils.BLOErrorHandler;
import org.xml.sax.SAXParseException;

/**
 *
 * @author mmacias
 */
public class BLOException extends Exception {
    private BLOUtils.BLOErrorHandler errorHandler;

    public BLOException(BLOErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public BLOException(BLOErrorHandler errorHandler, String message) {
        super(message);
        this.errorHandler = errorHandler;
    }

    public BLOException(BLOErrorHandler errorHandler, String message, Throwable cause) {
        super(message, cause);
        this.errorHandler = errorHandler;
    }

    public BLOException(BLOErrorHandler errorHandler, Throwable cause) {
        super(cause);
        this.errorHandler = errorHandler;
    }

    @Override
    public String getMessage() {
        if(errorHandler == null) {
            return super.getMessage();
        } else {
            StringBuilder sb = new StringBuilder("Error while parsing XML");
            if(super.getMessage() != null) {
                sb.append(super.getMessage()).append("\n");
            }
            sb.append("Here is a list of errors:");
            if(!errorHandler.warnings.isEmpty()) {
                sb.append("\n- WARNINGS -");
                for(SAXParseException ex : errorHandler.warnings) {
                    sb.append("\n").append(ex.getMessage());
                }
            }
            if(!errorHandler.errors.isEmpty()) {
                sb.append("\n- ERRORS -");
                for(SAXParseException ex : errorHandler.errors) {
                    sb.append("\n").append(ex.getMessage());
                }
            }
            if(!errorHandler.fatalErrors.isEmpty()) {
                sb.append("\n- FATAL ERRORS -");
                for(SAXParseException ex : errorHandler.fatalErrors) {
                    sb.append("\n").append(ex.getMessage());
                }
            }

            return sb.toString();
        }
    }
    
    
    
    
}
