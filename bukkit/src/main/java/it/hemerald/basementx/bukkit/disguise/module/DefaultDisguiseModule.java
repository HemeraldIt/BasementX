package it.hemerald.basementx.bukkit.disguise.module;

import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.bukkit.disguise.adapter.DisguiseAdapter;
import it.hemerald.basementx.api.bukkit.disguise.module.DisguiseModule;
import it.hemerald.basementx.api.persistence.generic.connection.Connector;
import it.hemerald.basementx.api.persistence.generic.connection.TypeConnector;
import it.hemerald.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.hemerald.basementx.api.persistence.maria.structure.column.MariaType;
import it.hemerald.basementx.bukkit.disguise.adapter.DefaultDisguiseAdapter;
import it.hemerald.basementx.bukkit.plugin.config.BasementBukkitConfig;
import it.mineblock.cobusco.player.CobuscoSkin;
import org.bukkit.Skin;
import org.bukkit.entity.Player;

public class DefaultDisguiseModule extends DisguiseModule {

    private final AbstractMariaDatabase database;

    public DefaultDisguiseModule(BasementBukkit basement) {
        super(basement, BasementBukkitConfig.DISGUISE);
        Connector connector = basement.getConnector(TypeConnector.MARIADB);
        connector.connect(basement.getSettingsManager().getProperty(BasementBukkitConfig.MARIA_HOST),
                basement.getSettingsManager().getProperty(BasementBukkitConfig.MARIA_USERNAME),
                basement.getSettingsManager().getProperty(BasementBukkitConfig.MARIA_PASSWORD));

        database = basement.getDatabase();
        database.createTable("disguise_names").ifNotExists(true)
                .addColumn("name", MariaType.VARCHAR, 32)
                .withPrimaryKeys("name").build().exec();
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

        player.setFakeNameAndSkin(name, Skin.EMPTY);

        getBasement().getNameTagModule().update(player);
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
    }

    @Override
    public boolean isDisguised(Player player) {
        return player.getFakeName() != null;
    }

    @Override
    public String getDisguisedName(Player player) {
        return player.getSafeFakeName();
    }
}
