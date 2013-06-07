/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.manifest.api.sp;

/**
 * @author arumpl
 * 
 *         PriceFence represents a conditional expression evaluated to determine if a price element (i.e.
 *         PricePlan, PriceComponent or PriceLevel) applies. Within a PriceFence a certain business entity
 *         (represented by the businessTerm) is compared to a certain value (or set of values - the literals
 *         available to account for the different dimensions of the service provision process).
 */
public interface PriceFence
{
    /**
     * Retrieve businessTerm
     * 
     * @return businessTerm
     */
    String getBusinessTerm();

    /**
     * Set businessTerm
     * 
     * @param a
     *            businessTerm
     */
    void setBusinessTerm( String businessTerm );

    /**
     * Retrieve businessTermExpression
     * 
     * @return a businessTermExpression
     */
    String getBusinessTermExpression();

    /**
     * Set businessTermExpression
     * 
     * @param a
     *            businessTermExpression
     */
    void setBusinessTermExpression( String businessTermExpression );

    /**
     * Retrieve quantityLiterals array
     * 
     * @return quantityLiterals
     * @see Quantity
     */
    Quantity[] getQuantityLiterals();

    /**
     * Retrieve certain quantityLiteral
     * 
     * @param index
     * @return quantityLiteral
     * @see Quantity
     */
    Quantity getQuantityLiterals( int index );
}
