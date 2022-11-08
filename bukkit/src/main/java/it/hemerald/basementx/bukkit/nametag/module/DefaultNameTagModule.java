package it.hemerald.basementx.bukkit.nametag.module;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.nametag.adapter.NameTagAdapter;
import it.hemerald.basementx.api.bukkit.nametag.filters.NameTagFilter;
import it.hemerald.basementx.api.bukkit.nametag.filters.StreamFilter;
import it.hemerald.basementx.api.bukkit.nametag.filters.SubFilter;
import it.hemerald.basementx.api.bukkit.nametag.module.NameTagModule;
import it.hemerald.basementx.bukkit.nametag.adapter.DefaultNameTagAdapter;
import it.hemerald.basementx.bukkit.nametag.tags.TagGUI;
import it.hemerald.basementx.bukkit.plugin.config.BasementBukkitConfig;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.redisson.api.RMapCache;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultNameTagModule extends NameTagModule implements Listener {

    private RMapCache<String, String> rTagMap;

    private static final String UUID = generateUUID();
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<Player, String> tags = new ConcurrentHashMap<>();

    private final TeamUtils teamUtils;

    private final List<NameTagFilter> filters = new ArrayList<>();

    public DefaultNameTagModule(BasementBukkit basement) {
        super(basement, BasementBukkitConfig.NAME_TAG, BasementBukkitConfig.TAGS);

        String version = basement.getPlugin().getServer().getClass().getPackage().getName().split("\\.")[3];

        switch (version) {
            case "v1_8_R3" -> {
                teamUtils = new it.hemerald.basementx.common.nms.v1_8_R3.team.TeamUtils();
                filters.add(new it.hemerald.basementx.common.nms.v1_8_R3.nametag.filters.NMSSubFilter(basement));
            }
            case "v1_19_R1" -> {
                teamUtils = new it.hemerald.basementx.common.nms.v1_19_R1.team.TeamUtils();
                filters.add(new it.hemerald.basementx.common.nms.v1_19_R1.nametag.filters.NMSSubFilter(basement));
            }
            default -> throw new IllegalStateException("Unsupported version: " + version);
        }

        filters.add(new StreamFilter(basement));

        basement.getLuckPerms().getEventBus().subscribe(basement.getPlugin(), NodeRemoveEvent.class, (event) -> {
            if(!(event.getNode() instanceof PermissionNode node) || !event.isUser()) return;
            Player player = Bukkit.getPlayer(((User)event.getTarget()).getUniqueId());
            if(player == null) return;
            for (NameTagFilter filter : filters) {
                if(!(filter instanceof SubFilter subFilter)) continue;
                if(subFilter.test(node.getPermission())) {
                    subFilter.clear(player, node.getPermission());
                }
            }
        });
    }

    @Override
    public void onStart() {
        basement.getPlugin().getServer().getPluginManager().registerEvents(this, basement.getPlugin());
        rTagMap = basement.getRedisManager().getRedissonClient().getMapCache("tag");
    }

    @Override
    public void onStop() {
        HandlerList.unregisterAll(this);
        rTagMap = null;
    }

    @Override
    protected NameTagAdapter getDefaultAdapter() {
        return new DefaultNameTagAdapter(this);
    }

    private static String generateUUID() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            builder.append((char) (65 + Math.random()*26));
        }
        return builder.toString();
    }

    @Override
    public void openInventory(Player player) {
        TagGUI.getInventory(basement).open(player);
    }

    @Override
    public void setTag(Player player, String tag) {
        rTagMap.put(player.getName(), tag);
        if(player.isOnline()) tags.put(player, tag);
    }

    @Override
    public CompletableFuture<String> getTag(Player player) {
        if (!tagsEnabled()) return CompletableFuture.completedFuture("");

        String tag = tags.get(player);
        if (tag != null) return CompletableFuture.completedFuture(tag);

        return loadTag(player);
    }

    @Override
    public CompletableFuture<String> loadTag(Player player) {
        if (!tagsEnabled()) return CompletableFuture.completedFuture("");

        return CompletableFuture.supplyAsync(() -> {
            String tag = rTagMap.get(player.getName());

            if(player.isOnline()) {
                tags.put(player, tag == null ? "" : tag);
            }

            return tag == null ? "" : tag;
        });
    }

    @Override
    public Scoreboard getScoreboard(Player player) {
        if(player.getScoreboard() == null) player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        return player.getScoreboard();
    }

    private void createTeam(Player player, String name) {
        removePlayer(player);

        Scoreboard scoreboard = getScoreboard(player);
        Team team = scoreboard.getTeam(name);

        if(team == null) {
            team = scoreboard.registerNewTeam(name);
            team.setAllowFriendlyFire(true);
            adapter.onCreateTeam(player, team);
        }

        updateTeam(player, team);
        team.addEntry(player.getSafeFakeName());
        teams.put(player.getName(), team);
    }

    private void updateTeam(Player player, Team team) {
        adapter.getPrefix(player).thenAccept(prefix -> executeSync(() -> team.setPrefix(resize(prefix))));
        adapter.getSuffix(player).thenAccept(suffix -> executeSync(() -> team.setSuffix(resize(suffix))));
        adapter.getPrefixUncolored(player).thenAccept(prefix -> executeSync(() -> {
            ChatColor color = chatColor(prefix);
            if(color != null) teamUtils.setColor(team, color);
        }));

        adapter.onUpdateTeam(player, team);
    }

    private ChatColor chatColor(String string) {
        string = string.trim();
        return ChatColor.getByChar(string.charAt(string.length()-1));
    }

    @Override
    public void removePlayer(Player player) {
        Team team = teams.get(player.getName());
        if(team != null) {
            team.removeEntry(player.getName());
            teams.remove(player.getName());
            if(team.getEntries().isEmpty()) team.unregister();
        }
    }

    @Override
    public void update(Player player) {
        Team team = teams.get(player.getName());
        String newName = adapter.getTeamName(player);

        if(team == null) {
            createTeam(player, newName);
        } else {
            if (team.getName().equals(newName)) {
                updateTeam(player, team);
            } else {
                createTeam(player, newName);
            }
        }

        updateDisplayName(player);
        updateTab(player);
    }

    @Override
    public void updateDisplayName(Player player) {
        adapter.getDisplayName(player).thenAccept(player::setDisplayName);
    }

    @Override
    public void updateTab(Player player) {
        adapter.getTab(player).thenAccept(player::setPlayerListName);

        applyFilters(player);
    }

    private void applyFilters(Player player) {
        filters.parallelStream().forEach(filter -> filter.testThenApply(player));
    }

    @Override
    public String getTeamName(Player player) {
        int priority = adapter.getPriority(player);
        String priorityPrefix;

        if (priority < 0) {
            priorityPrefix = "Z";
        } else {
            char letter = (char) ((priority / 5) + 65);
            int repeat = priority % 5 + 1;
            priorityPrefix = String.valueOf(letter).repeat(repeat);
        }

        return resize(UUID + priorityPrefix + player.getSafeFakeName());
    }

    @Override
    public Collection<Team> getTeams() {
        return Collections.unmodifiableCollection(teams.values());
    }

    @Override
    public Team getTeam(Player player) {
        return teams.get(player.getName());
    }

    @Override
    public String resize(String string) {
        return string.length() > 16 ? string.substring(0, 16) : string;
    }

    private void executeSync(Runnable runnable) {
        if(Bukkit.isPrimaryThread()) runnable.run();
        else basement.getPlugin().getServer().getScheduler().runTask(basement.getPlugin(), runnable);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        adapter.onPreJoin(event);
        update(event.getPlayer());
        adapter.onPostJoin(event);
        basement.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(basement.getPlugin(), () -> applyFilters(event.getPlayer()), 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
        tags.remove(event.getPlayer());
    }

    @Override
    public void updateHealth(Player player) {
        updateHealth(player, player.getHealth(), true);
    }

    @Override
    public void updateHealth(Player player, boolean tab) {
        updateHealth(player, player.getHealth(), tab);
    }

    @Override
    public void updateHealth(Player player, double health) {
        updateHealth(player, health, true);
    }

    @Override
    public void updateHealth(Player player, double health, boolean tab) {
        Objective healthBar = getScoreboard(player).getObjective("health");
        if (healthBar == null) {
            healthBar = getScoreboard(player).registerNewObjective("health", "dummy");
            healthBar.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        healthBar.setDisplayName(ChatColor.RED + "\u2764");
        healthBar.getScore(player.getSafeFakeName()).setScore(Math.max(0, (int) Math.round(health)));

        if(tab) updateHealthTab(player, health);
    }

    @Override
    public void updateHealthTab(Player player, double health) {
        Objective healthBar = getScoreboard(player).getObjective("healthtab");
        if (healthBar == null) {
            healthBar = getScoreboard(player).registerNewObjective("healthtab", "dummy");
            healthBar.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        healthBar.setDisplayName(ChatColor.RED + "\u2764");
        healthBar.getScore(player.getSafeFakeName()).setScore(Math.max(0, (int) Math.round(health)));
    }

    @Override
    public void removeHealth(Player player) {
        removeHealth(player, true);
    }

    @Override
    public void removeHealth(Player player, boolean tab) {
        Objective healthBar = getScoreboard(player).getObjective("health");
        if(healthBar != null) {
            healthBar.unregister();
        }
        if(tab) removeHealthTab(player);
    }

    @Override
    public void removeHealthTab(Player player) {
        Objective healthBar = getScoreboard(player).getObjective("healthtab");
        if(healthBar != null) {
            healthBar.unregister();
        }
    }
}
