package it.mineblock.basementx.bukkit.player;

import com.viaversion.viaversion.api.Via;
import it.mineblock.basementx.api.bukkit.BasementBukkit;
import it.mineblock.basementx.api.locale.Locale;
import it.mineblock.basementx.api.locale.LocaleManager;
import it.mineblock.basementx.api.persistence.maria.queries.builders.WhereBuilder;
import it.mineblock.basementx.api.player.BasementPlayer;
import it.mineblock.basementx.api.player.version.MinecraftVersion;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BukkitBasementPlayer implements BasementPlayer {

    private final Player player;
    private final LocaleManager localeManager;
    private String language;

    public BukkitBasementPlayer(Player player, BasementBukkit basement) {
        this.player = player;
        this.localeManager = basement.getLocaleManager();

        try {
            ResultSet resultSet = basement.getDatabase().select().columns("language")
                    .from("players").where(WhereBuilder.builder().equals("uuid", player.getUniqueId().toString()).close())
                    .build().execReturn();
            if(resultSet.next()) {
                language = resultSet.getString(1);
            } else {
                language = "en-us";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            language = "en-us";
        }
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public Optional<Locale> getLocale(String context) {
        return localeManager.getLocale(context, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.byProtocolVersion(Via.getAPI().getPlayerVersion(player));
    }
}
