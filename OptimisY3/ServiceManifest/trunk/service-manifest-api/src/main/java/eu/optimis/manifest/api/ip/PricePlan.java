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
package eu.optimis.manifest.api.ip;

/**
 * @author arumpl
 */
public interface PricePlan
{

    /**
     * @return the planCap, as a float num.
     */
    float getPlanCap();

    /**
     * providing this maximum PricePlan value prevents from charging the user a higher total price, regardless
     * of the cumulative total price the components and adjustments within this PricePlan may eventually
     * amount to. Example: A cap may be used to set an upper limit in a strictly usage-based plan.
     * 
     * @param planCap
     *            the planCap, as a float num.
     */
    void setPlanCap( float planCap );

    /**
     * @return planFloor, as a float num.,
     */
    float getPlanFloor();

    /**
     * providing this minimum PricePlan value prevents from charging the user a lower total price, regardless
     * of the cumulative total price the components and adjustments within this PricePlan may eventually
     * amount to. Example: A floor may be used to set a lower limit to discounts that may result in an
     * excessively low price.
     * 
     * @param planFloor
     *            the planFloor as a float num.,
     */
    void setPlanFloor( float planFloor );

    /**
     * retrieves the currency for all price amounts within this PricePlan, e.g. , EUR.
     * 
     * @return the currency as a name string
     */
    String getCurrency();

    /**
     * sets the currency for all price amounts within this PricePlan, e.g. , EUR.
     * 
     * @param currency
     *            the currency as a name string
     */
    void setCurrency( String currency );

    /**
     * @return price components
     */
    PriceComponent[] getPriceComponentArray();

    /**
     * @param i
     * @return price component at ith position
     */
    PriceComponent getPriceComponentArray( int i );

    /**
     * Adds a new price component.
     * 
     * @param name
     *            the name of the price component
     * @return the newly created price component
     */
    PriceComponent addNewPriceComponent( String name );

    /**
     * @see eu.optimis.manifest.api.sp.PricePlan#removePriceComponent(String)
     * @param name
     */
    void removePriceComponent( String name );

    /**
     * retrieves the price component for the provided name
     * 
     * @param name
     * @return
     */
    PriceComponent getPriceComponent( String name );
}
