/*
 * Copyright © ReasonDev 2014
 * All Rights Reserved
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package eu.autremonde.quake.match;

import eu.autremonde.quake.config.Lang;
import eu.autremonde.quake.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class MatchBoard {

    private Match match;
    private final Scoreboard matchBoard;

    private Objective mainObj;

    public MatchBoard(Match match) {
        this.match = match;
        this.matchBoard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void loadMatchBoard() {
        if(mainObj != null) mainObj.unregister();
        mainObj = matchBoard.registerNewObjective("mainObj", "dummy");
        mainObj.setDisplaySlot(DisplaySlot.SIDEBAR);
        mainObj.setDisplayName(match.getActiveArena().getDisplayName(true));
    }

    public void addPlayer(Player player) {
        mainObj.getScore(player.getName()).setScore(1);
        mainObj.getScore(player.getName()).setScore(0);
        player.setScoreboard(matchBoard);
    }

    public void removePlayer(Player player) {
        matchBoard.resetScores(player.getName());
    }

    public int addPoint(Player player) {
        Score score = mainObj.getScore(player.getName());
        score.setScore(score.getScore() + 1);
        if(Lang.KillCounts.hasMessage(score.getScore())) Messaging.broadcast(match, Lang.KillCounts.toString(player, score.getScore()));
        return score.getScore();
    }

    public String getWinner() {
        Score topScore = null;
        for(String player : matchBoard.getEntries()) {
            if (topScore == null) topScore = mainObj.getScore(player);
            else if(mainObj.getScore(player).getScore() > topScore.getScore()) topScore = mainObj.getScore(player);
        }
        return topScore == null ? "NONE" : topScore.getEntry();
    }

    public void unregister() {
        if(mainObj != null) mainObj.unregister();
    }
}
