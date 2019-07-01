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
 * Represents a bid made to buy a quantity of something at a certain unit price.
 */
public final class MarketBid implements MarketEntry, Comparable<MarketBid> {
    private final Agent bidder;
    private final Currency bidCurrency;
    private final double quantity;
    private final double unitPrice;

    private final UUID uniqueId;
    private final Date submissionTime;

    public MarketBid(Agent bidder, Currency bidCurrency, double quantity, double unitPrice) {
        this(bidder, bidCurrency, quantity, unitPrice, UUID.randomUUID(), new Date());
    }

    private MarketBid(Agent bidder, Currency bidCurrency, double quantity, double unitPrice, UUID uniqueId, Date submissionTime) {
        this.bidder = bidder;
        this.bidCurrency = bidCurrency;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.uniqueId = uniqueId;
        this.submissionTime = submissionTime;
    }

    public Agent getBidder() {
        return bidder;
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
        return bidCurrency;
    }

    @Override
    public double getValue() {
        return quantity * unitPrice;
    }

    /**
     * Compares the unit price (and, as a tie-breaker, submission time) of this bid to another. Note that higher unit
     * prices mean a bid is 'better' and therefore if this bid's unit price is higher then a positive value will be
     * returned.
     *
     * @param o the bid to compare this one to
     * @return positive if this bid's price is better, negative if it's worse
     */
    @Override
    public int compareTo(MarketBid o) {
        // note: for a bid we want the highest unit prices to produce the highest values
        if (!o.bidCurrency.equals(bidCurrency)) {
            throw new IllegalArgumentException("Cannot compare bids in different currencies.");
        }
        if (equals(o)) {
            return 0;
        }

        double significantUnitPrice = bidCurrency.getSignificantComponent(unitPrice);
        double oSignificantUnitPrice = o.bidCurrency.getSignificantComponent(unitPrice);

        int significantDecimals = bidCurrency.getSignificantDecimals();
        double unitPriceDiff = significantUnitPrice - oSignificantUnitPrice;

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

    // comparing unique ids should be enough to compare bid equality

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarketBid)) {
            return false;
        }
        MarketBid bid = (MarketBid) o;
        return uniqueId.equals(bid.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}
