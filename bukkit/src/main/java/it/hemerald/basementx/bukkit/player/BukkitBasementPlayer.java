package it.hemerald.basementx.bukkit.player;

import com.viaversion.viaversion.api.Via;
import it.hemerald.basementx.api.bukkit.BasementBukkit;
import it.hemerald.basementx.api.locale.Locale;
import it.hemerald.basementx.api.locale.LocaleManager;
import it.hemerald.basementx.api.persistence.maria.queries.builders.WhereBuilder;
import it.hemerald.basementx.api.persistence.maria.structure.data.QueryData;
import it.hemerald.basementx.api.player.BasementPlayer;
import it.hemerald.basementx.api.player.version.MinecraftVersion;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BukkitBasementPlayer implements BasementPlayer {

    private final Player player;
    private final LocaleManager localeManager;
    private String language;

    public BukkitBasementPlayer(Player player, BasementBukkit basement) {
        this.player = player;
        this.localeManager = basement.getLocaleManager();

        QueryData queryData = basement.getDatabase().select().columns("language")
                .from("players").where(WhereBuilder.builder().equals("uuid", player.getUniqueId().toString()).close())
                .build().execReturn();
        if(queryData.next()) {
            language = queryData.getString(1);
        } else {
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
