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
package eu.optimis.ecoefficiencytool.core.tools;

import java.util.LinkedList;
import java.util.List;

/**
 * Estimates the future value of a variable using multiple estimators.
 *
 * @author jsubirat
 */
public class VariableEstimator {

    //Real Values List
    private LinkedList<double[]> realValues;
    private int maxRealValues;
    //Moving Average Parameters
    private int mMovAvg;
    //Exponential Smoothing Parameters
    private double alphaExpSmooth;
    private double prevBExpSmooth;
    //Linear Regression Parameters
    private int samplesLinReg;
    //Double Exponential Smoothing Parameter
    private double alphaDoubleExpSmooth;
    private double betaDoubleExpSmooth;
    private double prevADoubleExpSmooth;
    private double prevBDoubleExpSmooth;
    //
    private int lastNSamplesError;
    private String lastSelectedEstimator;
    private LinkedList<Double> absErrorMovAvg;
    private LinkedList<Double> absErrorExpSmooth;
    private LinkedList<Double> absErrorLinReg;
    private LinkedList<Double> absErrorDoubleExpSmooth;

    public VariableEstimator() {
        realValues = new LinkedList<double[]>();
        maxRealValues = 100;

        mMovAvg = 2;
        //mMovAvg = 3;

        alphaExpSmooth = 0.7;
        //alphaExpSmooth = 0.2;
        prevBExpSmooth = 0.0;

        samplesLinReg = 11;
        //samplesLinReg = 10;

        alphaDoubleExpSmooth = 0.36;
        betaDoubleExpSmooth = 0.1111;
        prevADoubleExpSmooth = 0.0;
        prevBDoubleExpSmooth = 0.0;

        lastNSamplesError = 8;
        lastSelectedEstimator = "Moving Average";
        absErrorMovAvg = new LinkedList<Double>();
        absErrorExpSmooth = new LinkedList<Double>();
        absErrorLinReg = new LinkedList<Double>();
        absErrorDoubleExpSmooth = new LinkedList<Double>();
    }
    
    public VariableEstimator(int mMovAvg, double alphaExpSmooth, int samplesLinReg, double alphaDoubleExpSmooth, double betaDoubleExpSmooth, int lastNSamplesError) {
        this();
        
        this.mMovAvg = mMovAvg;
        this.alphaExpSmooth = alphaExpSmooth;
        this.samplesLinReg = samplesLinReg;
        this.alphaDoubleExpSmooth = alphaDoubleExpSmooth;
        this.betaDoubleExpSmooth = betaDoubleExpSmooth;
        this.lastNSamplesError = lastNSamplesError;
    }

    public synchronized void addValue(long timestamp, double value) {
        double newValue[] = new double[2];
        newValue[0] = Double.parseDouble(Long.toString(timestamp));
        newValue[1] = value;

        if(checkIfExistant(newValue[0]) == false) {
            //Calculate forecasts based on new timestamp and calculate error metric of performed forecasts with the real value.
            updateErrorMetric(timestamp, value);

            //Update estimator's fields
            updateEstimatorFields();

            //Add new value
            if (realValues.size() == maxRealValues) {
                realValues.remove(0);
                realValues.addLast(newValue);
            } else {
                realValues.addLast(newValue);
            }
        }
    }
    
    private synchronized boolean checkIfExistant(double timestamp) {
        for(double value[] : realValues) {
            if(value[0] == timestamp) {
                return true;
            }
        }
        return false;
    }

    private synchronized void updateErrorMetric(long timestamp, double value) {

        double absErrorMovAvgCurrent = Math.abs(value - forecastValueMovingAverage());
        if (absErrorMovAvg.size() == maxRealValues) {
            absErrorMovAvg.remove(0);
            absErrorMovAvg.addLast(absErrorMovAvgCurrent);
        } else {
            absErrorMovAvg.addLast(absErrorMovAvgCurrent);
        }

        double absErrorExpSmoothCurrent = Math.abs(value - forecastValueExponentialSmoothing());
        if (absErrorExpSmooth.size() == maxRealValues) {
            absErrorExpSmooth.remove(0);
            absErrorExpSmooth.addLast(absErrorExpSmoothCurrent);
        } else {
            absErrorExpSmooth.addLast(absErrorExpSmoothCurrent);
        }

        double absErrorLinRegCurrent = Math.abs(value - forecastValueLinearRegression(timestamp));
        if (absErrorLinReg.size() == maxRealValues) {
            absErrorLinReg.remove(0);
            absErrorLinReg.addLast(absErrorLinRegCurrent);
        } else {
            absErrorLinReg.addLast(absErrorLinRegCurrent);
        }

        double absErrorDoubleExpSmoothCurrent = Math.abs(value - forecastValueDoubleExponentialSmoothing(timestamp));
        if (absErrorDoubleExpSmooth.size() == maxRealValues) {
            absErrorDoubleExpSmooth.remove(0);
            absErrorDoubleExpSmooth.addLast(absErrorDoubleExpSmoothCurrent);
        } else {
            absErrorDoubleExpSmooth.addLast(absErrorDoubleExpSmoothCurrent);
        }
        
        /*DecimalFormat formatter = new DecimalFormat("#0.00");
        System.out.println(formatter.format(absErrorMovAvgCurrent) + " " 
                        + formatter.format(absErrorExpSmoothCurrent) + " " 
                        + formatter.format(absErrorLinRegCurrent) + " " 
                        + formatter.format(absErrorDoubleExpSmoothCurrent));*/
    }

    private synchronized void updateEstimatorFields() {

        if(realValues.size()>0) {
            prevBExpSmooth = prevBExpSmooth + alphaExpSmooth * (realValues.getLast()[1] - prevBExpSmooth);
        
            double aT = alphaDoubleExpSmooth * realValues.getLast()[1] + (1 - alphaDoubleExpSmooth) * (prevADoubleExpSmooth + prevBDoubleExpSmooth);
            double bT = betaDoubleExpSmooth * (aT - prevADoubleExpSmooth) + (1 - betaDoubleExpSmooth) * prevBDoubleExpSmooth;
            prevADoubleExpSmooth = aT;
            prevBDoubleExpSmooth = bT;
        }
    }

    public synchronized double obtainBestForecast(long timeStamp) {
        
        double ret = Double.NaN;
        
        //Calculate mean error metric of last X forecasts
        String bestEstimator = findBestEstimator();

        //Return the one with smaller mean MSD for the requested timestamp.
        if(bestEstimator.equals("MovingAverage")) {
            ret = forecastValueMovingAverage();
        } else if(bestEstimator.equals("ExponentialSmoothing")) {
            ret = forecastValueExponentialSmoothing();
        } else if(bestEstimator.equals("LinearRegression")) {
            ret = forecastValueLinearRegression(timeStamp);
        } else if(bestEstimator.equals("DoubleExponentialSmoothing")) {
            ret = forecastValueDoubleExponentialSmoothing(timeStamp);            
        }
        lastSelectedEstimator = bestEstimator;
        
        return ret;
    }

    public synchronized String findBestEstimator() {

        String ret = "LinearRegression";

        double meanErrorMetrics[] = new double[4];
        meanErrorMetrics[0] = calculateLastMeanErrorMetric(absErrorMovAvg, lastNSamplesError);
        meanErrorMetrics[1] = calculateLastMeanErrorMetric(absErrorExpSmooth, lastNSamplesError);
        meanErrorMetrics[2] = calculateLastMeanErrorMetric(absErrorLinReg, lastNSamplesError);
        meanErrorMetrics[3] = calculateLastMeanErrorMetric(absErrorDoubleExpSmooth, lastNSamplesError);

        double min = Double.POSITIVE_INFINITY;
        int index = 2;
        for (int i = 0; i < 4; i++) {
            if (meanErrorMetrics[i] < min) {
                min = meanErrorMetrics[i];
                index = i;
            }
        }

        switch (index) {
            case 0:
                ret = "MovingAverage";
                break;
            case 1:
                ret = "ExponentialSmoothing";
                break;
            case 2:
                ret = "LinearRegression";
                break;
            case 3:
                ret = "DoubleExponentialSmoothing";
                break;
        }

        return ret;
    }

    private synchronized double calculateLastMeanErrorMetric(List<Double> errorMetricVector, int lastNSamples) {

        int initialIndex = errorMetricVector.size() - lastNSamples;
        if (initialIndex < 0) {
            initialIndex = 0;
        }

        double sum = 0.0;
        for (int i = initialIndex; i < errorMetricVector.size(); i++) {
            sum += errorMetricVector.get(i).doubleValue();
        }

        return sum / ((double) (errorMetricVector.size() - initialIndex));
    }

    public synchronized double forecastValueMovingAverage() {

        if (realValues.size() < mMovAvg) {
            return Double.NaN;
        }

        int T = realValues.size() - 1;
        int beginning = T - mMovAvg + 1;

        double sum = 0.0;
        for (int i = beginning; i <= T; i++) {
            sum += realValues.get(i)[1];
        }

        return sum / mMovAvg;
    }

    public synchronized double forecastValueExponentialSmoothing() {

        if (realValues.size() < 1) {
            return Double.NaN;
        }

        return prevBExpSmooth + alphaExpSmooth * (realValues.getLast()[1] - prevBExpSmooth);
    }

    public synchronized double forecastValueLinearRegression(long timeStamp) {

        double x[] = new double[samplesLinReg];
        double y[] = new double[samplesLinReg];

        int initialIndex = realValues.size() - samplesLinReg;
        if (initialIndex < 0) {
            initialIndex = 0;
        }
        for (int i = initialIndex, j = 0; i < realValues.size() && j < samplesLinReg; i++, j++) {
            x[j] = realValues.get(i)[0];
            y[j] = realValues.get(i)[1];
        }

        LinearRegression lreg = new LinearRegression(x, y);

        return lreg.calculateY(Double.parseDouble(Long.toString(timeStamp)));
    }

    public synchronized double forecastValueDoubleExponentialSmoothing(long timeStamp) {

        if (realValues.size() < 1) {
            return Double.NaN;
        }
        double aT = alphaDoubleExpSmooth * realValues.getLast()[1] + (1 - alphaDoubleExpSmooth) * (prevADoubleExpSmooth + prevBDoubleExpSmooth);
        double bT = betaDoubleExpSmooth * (aT - prevADoubleExpSmooth) + (1 - betaDoubleExpSmooth) * prevBDoubleExpSmooth;

        return aT + bT * getNumberOfPeriods(Double.parseDouble(Long.toString(timeStamp)) - realValues.getLast()[0]);
    }
    
    private double getNumberOfPeriods(double timeStampDifference) {
        if(realValues.size() < 2) {
            return 1;
        }
        
        double meanTimeSeparation=0.0;
        for(int i=1; i < realValues.size(); i++) {
            meanTimeSeparation += realValues.get(i)[0] - realValues.get(i-1)[0];
        }
        meanTimeSeparation /= (double)(realValues.size()-1);
        
        return timeStampDifference/meanTimeSeparation;
        
//        double tMinus1 = realValues.get(realValues.size()-2)[0];
//        double t = realValues.get(realValues.size()-1)[0];
//        
//        return timeStampDifference/(t-tMinus1);
    }

    /**
     * *******DEBUGGING******
     */
    public void setPrevious(double value) {
        prevBExpSmooth = value;
    }

    public void setPreviousAandB(double a, double b) {
        prevADoubleExpSmooth = a;
        prevBDoubleExpSmooth = b;
    }
    
    public int getMMovAvg() {
        return mMovAvg;
    }
    
    public double getAlphaExpSmooth() {
        return alphaExpSmooth;
    }
    
    public int getSamplesLinReg() {
        return samplesLinReg;
    }
    
    public double getAlphaDoubleExpSmooth() {
        return alphaDoubleExpSmooth;
    }
    
    public double getBetaDoubleExpSmooth() {
        return betaDoubleExpSmooth;
    }
    
    public int getLastNSamplesError() {
        return lastNSamplesError;
    }
}
