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

public abstract class MarketedTransfer<T extends Marketed> implements Transfer<MarketedTransferResult> {
    private final Agent sender;
    private final Agent recipient;
    private final T marketed;
    private final double quantity;

    public MarketedTransfer(Agent sender, Agent recipient, T marketed, double quantity) {
        this.sender = sender;
        this.recipient = recipient;
        this.marketed = marketed;
        this.quantity = quantity;
    }

    public Agent getSender() {
        return sender;
    }

    public Agent getRecipient() {
        return recipient;
    }

    public T getMarketed() {
        return marketed;
    }

    public double getQuantity() {
        return quantity;
    }

    @Override
    public abstract boolean isExecuted();

    @Override
    public abstract MarketedTransferResult execute();

    @Override
    public abstract MarketedTransferResult getPredictedResult();

    @Override
    public abstract MarketedTransferResult getState();
}
