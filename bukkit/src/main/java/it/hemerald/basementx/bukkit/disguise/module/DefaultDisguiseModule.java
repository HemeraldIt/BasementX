package it.hemerald.basementx.bukkit.disguise.module;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.disguise.adapter.DisguiseAdapter;
import it.hemerald.basementx.api.bukkit.disguise.module.DisguiseModule;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerald.basementx.api.persistence.maria.structure.column.MariaType;
import it.hemerald.basementx.api.player.PlayerManager;
import it.hemerald.basementx.bukkit.disguise.adapter.DefaultDisguiseAdapter;
import it.hemerald.basementx.bukkit.player.BukkitBasementPlayer;
import it.hemerald.basementx.bukkit.plugin.config.BasementBukkitConfig;

import org.bukkit.entity.Player;

public class DefaultDisguiseModule extends DisguiseModule {

    private final AbstractMariaDatabase database;
    private final PlayerManager<BukkitBasementPlayer> playerManager;

    public DefaultDisguiseModule(BasementBukkit basement) {
        super(basement, BasementBukkitConfig.DISGUISE);
        database = basement.getDatabase();
        database.createTable("disguise_names").ifNotExists(true)
                .addColumn("name", MariaType.VARCHAR, 32)
                .withPrimaryKeys("name").build().exec();

        this.playerManager = basement.getPlayerManager();
    }

    @Override
    protected DisguiseAdapter getDefaultAdapter() {
        return new DefaultDisguiseAdapter(this);
    }

    @Override
    public void onStart() {
        database.select().columns("*").from("disguise_names").build().execReturnAsync().thenAccept(queryData -> {
            while (queryData.next()) {
                names.add(queryData.getString(1));
            }
        });
    }

    @Override
    public void onStop() {

    }

    @Override
    public void disguise(Player player) {
        if (isDisguised(player)) return;
        String name = getRandomUsername();
        this.names.remove(name);

        player.setFakeNameAndSkin(name, org.bukkit.Skin.EMPTY);

        getBasement().getNameTagModule().update(player);

        basement.getStreamMode().sendPackets(
                player,
                playerManager.getStreamers().parallelStream().map(BukkitBasementPlayer::getPlayer).toArray(Player[]::new)
        );
    }

    @Override
    public void softHide(Player player, Player target) {
        player.softHidePlayer(target);
    }

    @Override
    public void undisguise(Player player) {
        if (!isDisguised(player)) return;
        names.add(player.getSafeFakeName());

        player.clearFakeNamesAndSkins();

        /*player.setSkin(null);
        player.setFakeName(null);*/

        getBasement().getNameTagModule().update(player);

        basement.getStreamMode().sendPackets(
                player,
                playerManager.getStreamers().parallelStream().map(BukkitBasementPlayer::getPlayer).toArray(Player[]::new)
        );
    }

    @Override
    public boolean isDisguised(Player player) {
        try { return player.getFakeName() != null; } catch (NoSuchMethodError e) { return false; }
    }

    @Override
    public String getDisguisedName(Player player) {
        return player.getSafeFakeName();
    }
}
