package it.hemerald.basementx.api.bukkit.scoreboard.board.task.lines;

import it.hemerald.basementx.api.bukkit.scoreboard.board.line.ScoreboardLine;

public interface ScoreboardLineTask extends Runnable {

    ScoreboardLine getLine();

    void setLine(ScoreboardLine line);

    ScoreboardLineTrigger getTrigger();

    void start();

    void cancel();

    boolean isRunning();
}
