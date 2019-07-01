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

public enum TransactionResult {
    WEALTH_SENDER_TOO_POOR,
    MARKETED_SENDER_NOT_ENOUGH,
    MARKETED_RECIPIENT_CANNOT_RECEIVE,
    WEALTH_ERROR,
    MARKETED_ERROR,
    SUCCESS,
    ALREADY_EXECUTED,
    WEALTH_ALREADY_EXECUTED,
    MARKETED_ALREADY_EXECUTED,
    NOT_EXECUTED
}
