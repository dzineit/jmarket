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

public final class WealthTransfer implements Transfer<WealthTransferResult> {
    private final WealthHolder sender;
    private final WealthHolder recipient;
    private final Currency transactionCurrency;
    private final double amount;

    private boolean executed = false;
    private WealthTransferResult executionResult = WealthTransferResult.NOT_EXECUTED;

    public WealthTransfer(WealthHolder sender, WealthHolder recipient, Currency transactionCurrency, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.transactionCurrency = transactionCurrency;
        this.amount = amount;
    }

    public WealthHolder getSender() {
        return sender;
    }

    public WealthHolder getRecipient() {
        return recipient;
    }

    public Currency getTransactionCurrency() {
        return transactionCurrency;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public synchronized WealthTransferResult execute() {
        if (executed) {
            return WealthTransferResult.ALREADY_EXECUTED;
        }
        WealthSnapshot senderWealth = sender.getCurrentWealth();
        if (senderWealth.getAmount(transactionCurrency) < amount) {
            executionResult = WealthTransferResult.SENDER_TOO_POOR;
            return WealthTransferResult.SENDER_TOO_POOR;
        }
        if (sender.take(transactionCurrency, amount)) {
            recipient.give(transactionCurrency, amount);
            executed = true;
            executionResult = WealthTransferResult.SUCCESS;
            return WealthTransferResult.SUCCESS;
        }
        executionResult = WealthTransferResult.ERROR;
        return WealthTransferResult.ERROR;
    }

    @Override
    public synchronized WealthTransferResult getPredictedResult() {
        if (executed) {
            return WealthTransferResult.ALREADY_EXECUTED;
        }
        WealthSnapshot senderWealth = sender.getCurrentWealth();
        if (senderWealth.getAmount(transactionCurrency) < amount) {
            return WealthTransferResult.SENDER_TOO_POOR;
        }
        return WealthTransferResult.SUCCESS;
    }

    @Override
    public WealthTransferResult getState() {
        return executionResult;
    }
}
