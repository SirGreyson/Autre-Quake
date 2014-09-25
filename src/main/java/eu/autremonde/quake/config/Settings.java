package eu.autremonde.quake.config;/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

import org.bukkit.configuration.file.YamlConfiguration;

public enum Settings {

    SPAWN_WORLD,
    POINTS_TO_WIN,
    WINNER_COINS,

    MIN_PLAYER_COUNTDOWN,
    MAX_PLAYER_COUNTDOWN,
    KILL_CHECK_COUNTDOWN,
    END_GAME_COUNTDOWN,
    FORCE_RESPAWN_DELAY,

    DEFAULT_MIN_PLAYERS,
    DEFAULT_MAX_PLAYERS;

    private static YamlConfiguration c = Configuration.getConfig("config");

    public String asString() {
        return c.getString(this.name());
    }

    public int asInt() {
        return c.getInt(this.name());
    }

}
