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
package eu.optimis.ecoefficiencytool.core;

/**
 *
 * @author jsubirat
 */
public class Constants {

    //Default timeSpan for predictions, in MILLISECONDS.
    public static final long DEFAULT_TIMESPAN = 150000;
    //Maximum previous assessments to be stored (to be used to make predictions).
    public static final int MAX_PREVIOUS_ASSESSMENTS = 3;
    public static final int MAX_PREVIOUS_SERVICE_ASSESSMENTS = 100;
    //LEEDCertification=NotRequired/Certified/Silver/Gold/Platinum
    public static final int LEED_CERT_NOTREQUIRED = 0;
    public static final int LEED_CERT_CERTIFIED = 1;
    public static final int LEED_CERT_SILVER = 2;
    public static final int LEED_CERT_GOLD = 3;
    public static final int LEED_CERT_PLATINUM = 4;
    //BREEAMCertification=NotRequired/Pass/Good/VeryGood/Excellent/Outstanding
    public static final int BREEAM_CERT_NOTREQUIRED = 0;
    public static final int BREEAM_CERT_PASS = 1;
    public static final int BREEAM_CERT_GOOD = 2;
    public static final int BREEAM_CERT_VERYGOOD = 3;
    public static final int BREEAM_CERT_EXCELLENT = 4;
    public static final int BREEAM_CERT_OUTSTANDING = 5;
    //CASBEE = No / C, B-, B+, A, 
    public static final int CASBEE_No = 0;
    public static final int CASBEE_C = 1;
    public static final int CASBEE_Bminus = 2;
    public static final int CASBEE_Bplus = 3;
    public static final int CASBEE_A = 4;
    public static final int CASBEE_S = 5;
    
    //Displaying constant
    public static final int MAX_DEPLOYMENT_MESSAGES = 20;
}
