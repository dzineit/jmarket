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
package jmarket;

/**
 * Represents a currency which is able to be traded in markets.
 */
public class MarketedCurrency implements Marketed<MarketedCurrency> {
    private final Currency currency;

    public MarketedCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String getName() {
        return currency.getName();
    }

    @Override
    public MarketedType getType() {
        return MarketedType.CURRENCY;
    }

    @Override
    public MarketedTransfer<MarketedCurrency> getTransfer(Agent sender, Agent recipient, double quantity) {
        return new MarketedCurrencyTransfer(sender, recipient, this, quantity);
    }
}
