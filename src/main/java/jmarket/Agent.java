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
 * A person participating in a market. Always represents a player in-game, but this player may be offline.
 */
public final class Agent implements WealthHolder {
    private final UUID playerId;
    private final Map<Currency, Double> wealth;

    private String name;

    public Agent(UUID playerId, String name) {
        this(playerId, name, new HashMap<>());
    }

    public Agent(UUID playerId, String name, Map<Currency, Double> wealth) {
        if (playerId == null) {
            throw new IllegalArgumentException("Agent must have a player ID.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Agent must have a name.");
        }
        if (wealth == null) {
            wealth = new HashMap<>();
        }

        this.playerId = playerId;
        this.name = name;
        this.wealth = new THashMap<>(wealth);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot set Agent name to null.");
        }

        this.name = name;
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
        return playerId.equals(that.playerId) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, name);
    }
}
