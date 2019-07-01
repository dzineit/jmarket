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

public class MarketedCurrencyTransfer extends MarketedTransfer<MarketedCurrency> {
    private final WealthTransfer delegate;

    private boolean executed = false;
    private MarketedTransferResult executionResult = MarketedTransferResult.NOT_EXECUTED;

    public MarketedCurrencyTransfer(Agent sender, Agent recipient, MarketedCurrency marketed, double quantity) {
        super(sender, recipient, marketed, quantity);
        this.delegate = new WealthTransfer(sender, recipient, marketed.getCurrency(), quantity);
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public synchronized MarketedTransferResult execute() {
        if (executed) {
            return MarketedTransferResult.ALREADY_EXECUTED;
        }

        switch (delegate.execute()) {
            case SUCCESS:
                executed = true;
                return MarketedTransferResult.SUCCESS;
            case SENDER_TOO_POOR:
                return MarketedTransferResult.SENDER_NOT_ENOUGH;
            default:
                return MarketedTransferResult.ERROR;
        }
    }

    @Override
    public synchronized MarketedTransferResult getPredictedResult() {
        if (executed) {
            return MarketedTransferResult.ALREADY_EXECUTED;
        }

        switch (delegate.getPredictedResult()) {
            case SUCCESS:
                return MarketedTransferResult.SUCCESS;
            case SENDER_TOO_POOR:
                return MarketedTransferResult.SENDER_NOT_ENOUGH;
            default:
                return MarketedTransferResult.ERROR;
        }
    }

    @Override
    public MarketedTransferResult getState() {
        return executionResult;
    }
}
