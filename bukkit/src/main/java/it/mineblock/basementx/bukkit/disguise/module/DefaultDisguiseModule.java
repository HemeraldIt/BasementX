package it.mineblock.basementx.bukkit.disguise.module;

import it.mineblock.basementx.api.bukkit.BasementBukkit;
import it.mineblock.basementx.api.bukkit.disguise.adapter.DisguiseAdapter;
import it.mineblock.basementx.api.bukkit.disguise.module.DisguiseModule;
import it.mineblock.basementx.api.persistence.generic.connection.Connector;
import it.mineblock.basementx.api.persistence.generic.connection.TypeConnector;
import it.mineblock.basementx.api.persistence.maria.structure.AbstractMariaDatabase;
import it.mineblock.basementx.api.persistence.maria.structure.column.MariaType;
import it.mineblock.basementx.bukkit.disguise.adapter.DefaultDisguiseAdapter;
import it.mineblock.basementx.bukkit.plugin.config.BasementBukkitConfig;
import it.mineblock.cobusco.player.CobuscoSkin;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

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
        database.select().columns("*").from("disguise_names").build().execReturnAsync().thenAccept(resultSet -> {
            try {
                while (resultSet.next()) {
                    names.add(resultSet.getString(1));
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
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

        CobuscoSkin skin = CobuscoSkin.values()[ThreadLocalRandom.current().nextInt(CobuscoSkin.values().length)];
        player.setFakeNameAndSkin(name, skin.getSkin());

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
