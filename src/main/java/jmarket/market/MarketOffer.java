/*
 * This file is part of jmarket.
 *
 * Copyright (c) ${project.inceptionYear} Oliver Stanley
 * Politics is licensed under the Affero General Public License Version 3.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jmarket.market;

import jmarket.Agent;
import jmarket.Currency;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an offer made to sell a quantity of something at a certain unit price.
 */
public final class MarketOffer implements MarketEntry, Comparable<MarketOffer> {
    private final Agent seller;
    private final Currency offerCurrency;
    private final double quantity;
    private final double unitPrice;

    private final UUID uniqueId;
    private final Date submissionTime;

    public MarketOffer(Agent seller, Currency offerCurrency, double quantity, double unitPrice) {
        this(seller, offerCurrency, quantity, unitPrice, UUID.randomUUID(), new Date());
    }

    private MarketOffer(Agent seller, Currency offerCurrency, double quantity, double unitPrice, UUID uniqueId, Date submissionTime) {
        this.seller = seller;
        this.offerCurrency = offerCurrency;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.uniqueId = uniqueId;
        this.submissionTime = submissionTime;
    }

    public Agent getSeller() {
        return seller;
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    @Override
    public double getUnitPrice() {
        return unitPrice;
    }

    @Override
    public Date getSubmissionTime() {
        return submissionTime;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public Currency getCurrency() {
        return offerCurrency;
    }

    @Override
    public double getValue() {
        return quantity * unitPrice;
    }

    /**
     * Compares the unit price (and, as a tie-breaker, submission time) of this offer to another. Note that lower unit
     * prices mean an offer is 'better' and therefore if this offer's unit price is lower then a positive value will be
     * returned.
     *
     * @param o the offer to compare this one to
     * @return positive if this offer's price is better, negative if it's worse
     */
    @Override
    public int compareTo(MarketOffer o) {
        // note: for an offer we want the lowest unit prices to produce the highest values
        if (!o.offerCurrency.equals(offerCurrency)) {
            throw new IllegalArgumentException("Cannot compare offers in different currencies.");
        }
        if (equals(o)) {
            return 0;
        }

        double significantUnitPrice = offerCurrency.getSignificantComponent(unitPrice);
        double oSignificantUnitPrice = o.offerCurrency.getSignificantComponent(unitPrice);

        int significantDecimals = offerCurrency.getSignificantDecimals();
        double unitPriceDiff = oSignificantUnitPrice - significantUnitPrice;

        // the integer to be returned (barring tie-break scenario)
        // note: this will always be a multiple of 10 as 1 is added to significantDecimals in the power
        // multiples of 10 are so tie-breaks (+/-1) can be distinguished from price being slightly better
        int comparison = (int) (unitPriceDiff * Math.pow(10, significantDecimals + 1));

        if (comparison == 0) {
            // as a tie-breaker, check which offer was submitted first
            // an offer submitted earlier is prioritised over one submitted later
            if (submissionTime.before(o.submissionTime)) {
                comparison += 1;
            } else if (submissionTime.after(o.submissionTime)) {
                comparison -= 1;
            }
        }

        return comparison;
    }

    // comparing unique ids should be enough to compare offer equality

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarketOffer)) {
            return false;
        }
        MarketOffer offer = (MarketOffer) o;
        return uniqueId.equals(offer.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}
