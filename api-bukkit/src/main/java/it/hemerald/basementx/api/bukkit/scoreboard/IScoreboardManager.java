package it.hemerald.basementx.api.bukkit.scoreboard;

import it.hemerald.basementx.api.bukkit.scoreboard.board.Scoreboard;

import java.util.Map;
import java.util.UUID;

public interface IScoreboardManager {

    void start();

    void stop();

    Map<UUID, Scoreboard> getScoreboars();

    void forceUpdate();
}
