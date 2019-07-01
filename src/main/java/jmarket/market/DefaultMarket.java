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
import jmarket.MarketedTransfer;
import jmarket.Transaction;
import jmarket.TransactionResult;
import jmarket.WealthTransfer;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Default implementation of a market.
 * <p>
 * Thread-safe.
 */
public class DefaultMarket<T extends Marketed<T>> implements Market<T> {
    private final T marketed;
    private final Currency transactionCurrency;
    private final Set<MarketBid> bids;
    private final Set<MarketOffer> offers;

    private ReadWriteLock bidsLock = new ReentrantReadWriteLock();
    private ReadWriteLock offersLock = new ReentrantReadWriteLock();

    public DefaultMarket(T marketed, Currency transactionCurrency) {
        this(marketed, transactionCurrency, new HashSet<>(), new HashSet<>());
    }

    public DefaultMarket(T marketed, Currency transactionCurrency, Set<MarketBid> bids, Set<MarketOffer> offers) {
        this.marketed = marketed;
        this.transactionCurrency = transactionCurrency;
        this.bids = bids;
        this.offers = offers;
    }

    @Override
    public T getMarketed() {
        return marketed;
    }

    @Override
    public Currency getTransactionCurrency() {
        return transactionCurrency;
    }

    @Override
    public Collection<MarketBid> getBids() {
        Lock readLock = bidsLock.readLock();
        readLock.lock();
        try {
            return new THashSet<>(bids);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void addBid(MarketBid bid) {
        if (!bid.getCurrency().equals(transactionCurrency)) {
            throw new IllegalArgumentException("Cannot add a bid to a market with a different transaction currency.");
        }

        Lock writeLock = bidsLock.writeLock();
        writeLock.lock();
        try {
            bids.add(bid);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Collection<MarketOffer> getOffers() {
        Lock readLock = offersLock.readLock();
        readLock.lock();
        try {
            return new THashSet<>(offers);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void addOffer(MarketOffer offer) {
        if (!offer.getCurrency().equals(transactionCurrency)) {
            throw new IllegalArgumentException("Cannot add an offer to a market with a different transaction currency.");
        }

        Lock writeLock = offersLock.writeLock();
        writeLock.lock();
        try {
            offers.add(offer);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<Transaction<T>> update() {
        Lock bWriteLock = bidsLock.writeLock();
        bWriteLock.lock();

        bids.removeIf(bid -> !bid.getBidder().has(bid.getCurrency(), bid.getValue()));

        Lock oWriteLock = offersLock.writeLock();
        oWriteLock.lock();

        Set<Transaction<T>> result = new HashSet<>();

        try {
            List<MarketBid> bidList = new ArrayList<>(bids);
            Collections.sort(bidList);
            List<MarketOffer> offerList = new ArrayList<>(offers);
            Collections.sort(offerList);

            // as the list is sorted this will now loop from highest bid to lowest
            for (int i = 0; i < bidList.size(); i++) {
                MarketBid bid = bidList.get(i);
                MarketOffer bestOffer = offerList.get(0);
                if (bid.getUnitPrice() >= bestOffer.getUnitPrice()) {
                    double transactionQuantity = Math.min(bestOffer.getQuantity(), bid.getQuantity());
                    MarketedTransfer<T> marketedTransfer = marketed.getTransfer(bestOffer.getSeller(), bid.getBidder(), transactionQuantity);
                    double unitPrice = Math.min(bestOffer.getUnitPrice(), bid.getUnitPrice());
                    WealthTransfer wealthTransfer = new WealthTransfer(bid.getBidder(), bestOffer.getSeller(), transactionCurrency, transactionQuantity * unitPrice);
                    Transaction<T> transaction = new Transaction<>(marketedTransfer, wealthTransfer);
                    TransactionResult transactionResult = transaction.execute();
                    result.add(transaction);

                    /*
                     * success -> remove both offer and bid (and replace if not exact quantity match)
                     * marketed sender not enough -> remove offer, retain bid
                     * wealth sender too poor -> remove bid, retain offer
                     * marketed recipient cannot receive -> remove bid, retain offer
                     */

                    if (transactionResult == TransactionResult.SUCCESS || transactionResult == TransactionResult.MARKETED_SENDER_NOT_ENOUGH) {
                        if (bestOffer.getQuantity() > transactionQuantity) {
                            offerList.set(0, new MarketOffer(bestOffer.getSeller(), transactionCurrency, bestOffer.getQuantity() - transactionQuantity, bestOffer.getUnitPrice()));
                        } else {
                            offerList.remove(0);
                        }
                    }

                    if (transactionResult == TransactionResult.SUCCESS || transactionResult == TransactionResult.WEALTH_SENDER_TOO_POOR
                            || transactionResult == TransactionResult.MARKETED_RECIPIENT_CANNOT_RECEIVE) {
                        if (bid.getQuantity() > transactionQuantity) {
                            bidList.set(i, new MarketBid(bid.getBidder(), transactionCurrency, bid.getQuantity() - transactionQuantity, bid.getUnitPrice()));
                        } else {
                            bidList.remove(i--);
                        }
                    }
                } else {
                    break;
                }
            }
        } finally {
            oWriteLock.unlock();
            bWriteLock.unlock();
        }

        return result;
    }
}
