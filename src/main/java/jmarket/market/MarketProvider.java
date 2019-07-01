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

import jmarket.access.Accessible;
import jmarket.Agent;
import jmarket.Currency;
import jmarket.Marketed;
import jmarket.MarketedType;
import jmarket.Transaction;

import java.util.Map;
import java.util.Set;

/**
 * Represents an institution providing access to markets.
 */
public interface MarketProvider extends Accessible<Agent> {
    Currency getTransactionCurrency();

    <T extends Marketed> Market<T> getMarket(T marketed);

    boolean marketExists(Marketed marketed);

    Map<Market, Set<Transaction>> updateMarkets();

    boolean canProvide(MarketedType marketedType);

    @Override
    boolean canAccess(Agent participant);
}
