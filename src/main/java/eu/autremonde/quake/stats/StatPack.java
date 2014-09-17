/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.stats;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class StatPack {

    private int coinCount;
    private int killCount;
    private int winCount;

    public StatPack() {
        this.coinCount = 0;
        this.killCount = 0;
        this.winCount = 0;
    }

    public StatPack(int coinCount, int killCount, int winCount) {
        this.coinCount = coinCount;
        this.killCount = killCount;
        this.winCount = winCount;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public void addCoins(int coinCount) {
        this.coinCount += coinCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public void addKill() {
        this.killCount += 1;
    }

    public int getWinCount() {
        return winCount;
    }

    public void addWin() {
        this.winCount += 1;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> output = new HashMap<String, Object>();
        output.put("coinCount", coinCount);
        output.put("killCount", killCount);
        output.put("winCount", winCount);
        return output;
    }

    public static StatPack deserialize(ConfigurationSection c) {
        return new StatPack(c.getInt("coinCount"), c.getInt("killCount"), c.getInt("winCount"));
    }
}
