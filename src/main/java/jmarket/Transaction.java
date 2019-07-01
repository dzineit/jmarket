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

public final class Transaction<T extends Marketed> implements Transfer<TransactionResult>, Valuable {
    private final MarketedTransfer<T> marketedComponent;
    private final WealthTransfer wealthComponent;

    private boolean executed = false;
    private TransactionResult executionResult = TransactionResult.NOT_EXECUTED;

    public Transaction(MarketedTransfer<T> marketedComponent, WealthTransfer wealthComponent) {
        this.marketedComponent = marketedComponent;
        this.wealthComponent = wealthComponent;
    }

    public MarketedTransfer<T> getMarketedComponent() {
        return marketedComponent;
    }

    public double getQuantity() {
        return marketedComponent.getQuantity();
    }

    public WealthTransfer getWealthComponent() {
        return wealthComponent;
    }

    @Override
    public Currency getCurrency() {
        return wealthComponent.getTransactionCurrency();
    }

    @Override
    public double getValue() {
        return wealthComponent.getAmount();
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public synchronized TransactionResult execute() {
        TransactionResult expected = getPredictedResult();
        if (expected != TransactionResult.SUCCESS) {
            executionResult = expected;
            return executionResult;
        }
        MarketedTransferResult marketedResult = marketedComponent.execute();
        if (marketedResult == MarketedTransferResult.ERROR) {
            executionResult = TransactionResult.MARKETED_ERROR;
            return executionResult;
        }
        WealthTransferResult wealthResult = wealthComponent.execute();
        if (wealthResult == WealthTransferResult.ERROR) {
            executionResult = TransactionResult.WEALTH_ERROR;
            return executionResult;
        }

        executed = true;
        executionResult = TransactionResult.SUCCESS;
        return executionResult;
    }

    @Override
    public synchronized TransactionResult getPredictedResult() {
        if (executed) {
            return TransactionResult.ALREADY_EXECUTED;
        }
        WealthTransferResult expWealthResult = wealthComponent.getPredictedResult();
        if (expWealthResult == WealthTransferResult.SENDER_TOO_POOR) {
            return TransactionResult.WEALTH_SENDER_TOO_POOR;
        }
        if (expWealthResult == WealthTransferResult.ALREADY_EXECUTED) {
            return TransactionResult.WEALTH_ALREADY_EXECUTED;
        }
        MarketedTransferResult expMarketedResult = marketedComponent.getPredictedResult();
        if (expMarketedResult == MarketedTransferResult.SENDER_NOT_ENOUGH) {
            return TransactionResult.MARKETED_SENDER_NOT_ENOUGH;
        }
        if (expMarketedResult == MarketedTransferResult.RECIPIENT_CANNOT_RECEIVE) {
            return TransactionResult.MARKETED_RECIPIENT_CANNOT_RECEIVE;
        }
        if (expMarketedResult == MarketedTransferResult.ALREADY_EXECUTED) {
            return TransactionResult.MARKETED_ALREADY_EXECUTED;
        }

        return TransactionResult.SUCCESS;
    }

    @Override
    public TransactionResult getState() {
        return executionResult;
    }
}
