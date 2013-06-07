/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.trecdb.sp.utils;

import java.util.Date;

/**
 *
 * @author jsubirat
 */
public class EcoValue {
    
    private Date timeStamp;
    private Double ecoValue;
    
    public EcoValue(Date dat, Double eco) {
        this.timeStamp = dat;
        this.ecoValue = eco;
    }
    
    public String getTimeStampString() {
        return Long.toString(timeStamp.getTime());
    }
    
    public String getEcoValueString() {
        return ecoValue.toString();
    }
}
