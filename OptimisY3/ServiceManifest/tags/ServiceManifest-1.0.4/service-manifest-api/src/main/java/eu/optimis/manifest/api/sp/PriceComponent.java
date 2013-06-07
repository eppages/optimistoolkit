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
 */

package eu.optimis.manifest.api.sp;

import eu.optimis.manifest.api.impl.PriceLevelImpl;

/**
 * @author arumpl
 *         PriceComponents are fees included in a PricePlan, which subject to conditions (expressed as
 *         PriceFences) may contribute to the total amount charged. Components within the same plan are
 *         summed together in order to get the total amount (price of the service). Common examples of
 *         PriceComponents that may coexist in the same PricePlan are: startup or membership charges (to
 *         access the service), periodic subscription fees (with a certain recurrence - e.g. monthly - as
 *         long as committed to by the contract), pay-per-unit charges (whose total will be proportional to
 *         the metered usage), options or feature dependent charges. The final value of the component will
 *         depend on the active PriceLevel (determined by the evaluation of the relative PriceFences) and
 *         the PriceAdjustments that may apply (e.g. discounts).
 */
public interface PriceComponent {

    /**
     * @return all price levels
     */
    PriceLevel[] getPriceLevels();

    /**
     * @param i
     * @return
     */
    PriceLevel getPriceLevels(int i);

    /**
     * @return
     */
    String getMultiplier();

    /**
     * @param multiplier
     */
    void setMultiplier(String multiplier);

    /**
     * @return
     */
    float getComponentCap();

    /**
     * @param componentCap
     */
    void setComponentCap(float componentCap);

    /**
     * @return
     */
    float getComponentFloor();

    /**
     * @param componentFloor
     */
    void setComponentFloor(float componentFloor);
}
