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

public class MarketedProductTransfer<T extends Product> extends MarketedTransfer<MarketedProduct<T>> {
    public MarketedProductTransfer(Agent sender, Agent recipient, MarketedProduct<T> marketed, double quantity) {
        super(sender, recipient, marketed, quantity);
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public MarketedTransferResult execute() {
        return null;
    }

    @Override
    public MarketedTransferResult getPredictedResult() {
        return null;
    }

    @Override
    public MarketedTransferResult getState() {
        return null;
    }
}
