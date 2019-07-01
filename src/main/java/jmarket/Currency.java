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

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Represents a currency which can be used to purchase things in a market.
 */
public final class Currency {
    private final String name;
    private final String plural;
    /**
     * The number of decimals which are considered significant. For instance, if the current was pounds or dollars, this
     * would be two as calculations do not go beyond pennies or cents.
     */
    private final int significantDecimals;
    private final DecimalFormat significanceFormat;
    private final String format;

    public Currency(String name, String plural, int significantDecimals, String format) {
        this.name = name;
        this.plural = plural;
        this.significantDecimals = significantDecimals;
        this.format = format;

        StringBuilder decimalFormatBuilder = new StringBuilder("#.");
        for (int i = 0; i < significantDecimals; i++) {
            decimalFormatBuilder.append("#");
        }
        significanceFormat = new DecimalFormat(decimalFormatBuilder.toString());
        significanceFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    public String getName() {
        return name;
    }

    public String getPlural() {
        return plural;
    }

    public int getSignificantDecimals() {
        return significantDecimals;
    }

    public double getSignificantComponent(double initial) {
        return Double.parseDouble(significanceFormat.format(initial));
    }

    public String format(double amount) {
        return this.format.replace("%d", significanceFormat.format(amount));
    }
}
