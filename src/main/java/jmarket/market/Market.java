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

import jmarket.Currency;
import jmarket.Marketed;
import jmarket.Transaction;

import java.util.Collection;
import java.util.Set;

/**
 * Represents a market for one good, service, commodity, currency or other.
 */
public interface Market<T extends Marketed> {
    T getMarketed();

    Currency getTransactionCurrency();

    Collection<MarketBid> getBids();

    void addBid(MarketBid bid);

    Collection<MarketOffer> getOffers();

    void addOffer(MarketOffer offer);

    /**
     * Attempts to match existing bids and offers, then executes matched transactions. Note that the returned
     * transactions have already been executed, although may not have succeeded.
     * <p>
     * The states of the returned transactions can be used to give feedback.
     *
     * @return a set of all transactions made in the update
     */
    Set<Transaction<T>> update();
}
