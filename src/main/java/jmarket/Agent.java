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

import gnu.trove.map.hash.THashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A person participating in a market.
 */
public class Agent implements WealthHolder {
    private final UUID uniqueId;
    private final Map<Currency, Double> wealth;

    public Agent(UUID uniqueId, String name) {
        this(uniqueId, name, new HashMap<>());
    }

    public Agent(UUID uniqueId, String name, Map<Currency, Double> wealth) {
        if (uniqueId == null) {
            throw new IllegalArgumentException("Agent must have a player ID.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Agent must have a name.");
        }

        this.uniqueId = uniqueId;
        this.wealth = wealth == null ? new HashMap<>() : new THashMap<>(wealth);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public WealthSnapshot getCurrentWealth() {
        synchronized (wealth) {
            return new WealthSnapshot(wealth);
        }
    }

    @Override
    public boolean give(Currency currency, double amount) {
        if (amount < 0) {
            return take(currency, -amount);
        }

        synchronized (wealth) {
            Double current = wealth.get(currency);
            if (current == null) {
                current = 0.0;
            }
            wealth.put(currency, current + amount);
            return true;
        }
    }

    @Override
    public boolean take(Currency currency, double amount) {
        if (amount < 0) {
            return give(currency, -amount);
        }

        synchronized (wealth) {
            Double current = wealth.get(currency);
            if (current != null && current >= amount) {
                wealth.put(currency, current - amount);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean has(Currency currency, double amount) {
        synchronized (wealth) {
            Double current = wealth.get(currency);
            return current != null && current >= amount;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }
        Agent that = (Agent) o;
        return uniqueId.equals(that.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}
