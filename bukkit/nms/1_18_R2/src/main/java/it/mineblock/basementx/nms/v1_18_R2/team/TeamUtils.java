package it.mineblock.basementx.nms.v1_18_R2.team;

import it.mineblock.basementx.api.bukkit.nametag.module.NameTagModule;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class TeamUtils implements NameTagModule.TeamUtils {

    @Override
    public void setColor(Team team, ChatColor color) {
        team.setColor(color);
    }
}
