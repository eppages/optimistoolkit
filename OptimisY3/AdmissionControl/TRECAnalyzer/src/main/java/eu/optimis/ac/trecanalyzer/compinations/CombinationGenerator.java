/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer.compinations;

import java.math.BigInteger;

class CombinationGenerator {

    private int[] a;
    private int n;
    private int r;
    private BigInteger numLeft;
    private BigInteger total;

	  //------------
	  // Constructor
	  //------------

	    protected CombinationGenerator (int n, int r) {
	    if (r > n) {
	      throw new IllegalArgumentException ();
	    }
	    if (n < 1) {
	      throw new IllegalArgumentException ();
	    }
	    this.n = n;
	    this.r = r;
	    a = new int[r];
	    BigInteger nFact = getFactorial (n);
	    BigInteger rFact = getFactorial (r);
	    BigInteger nminusrFact = getFactorial (n - r);
	    total = nFact.divide (rFact.multiply (nminusrFact));
	    reset ();
	  }

	  //------
	  // Reset
	  //------

	  protected void reset () {
	    for (int i = 0; i < a.length; i++) {
	      a[i] = i;
	    }
	    numLeft = new BigInteger (total.toString ());
	  }

	  //------------------------------------------------
	  // Return number of combinations not yet generated
	  //------------------------------------------------

	  protected BigInteger getNumLeft () {
	    return numLeft;
	  }

	  //-----------------------------
	  // Are there more combinations?
	  //-----------------------------

	  protected boolean hasMore () {
	    return numLeft.compareTo (BigInteger.ZERO) == 1;
	  }

	  //------------------------------------
	  // Return total number of combinations
	  //------------------------------------

	  protected BigInteger getTotal () {
	    return total;
	  }

	  //------------------
	  // Compute factorial
	  //------------------

	  private static BigInteger getFactorial (int n) {
	    BigInteger fact = BigInteger.ONE;
	    for (int i = n; i > 1; i--) {
	      fact = fact.multiply (new BigInteger (Integer.toString (i)));
	    }
	    return fact;
	  }

	  //--------------------------------------------------------
	  // Generate next combination (algorithm from Rosen p. 286)
	  //--------------------------------------------------------

	  protected int[] getNext () {

	    if (numLeft.equals (total)) {
	      numLeft = numLeft.subtract (BigInteger.ONE);
	      return a;
	    }

	    int i = r - 1;
	    while (a[i] == n - r + i) {
	      i--;
	    }
	    a[i] = a[i] + 1;
	    for (int j = i + 1; j < r; j++) {
	      a[j] = a[i] + j - i;
	    }

	    numLeft = numLeft.subtract (BigInteger.ONE);
	    return a;

	  }

}//class
