package it.hemerald.basementx.velocity.together;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import it.hemerald.basementx.velocity.together.commands.PartyCommand;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import it.hemerlad.basementx.api.Basement;
import it.hemerald.basementx.velocity.BasementVelocity;
import it.hemerald.basementx.velocity.together.invitation.InvitationService;
import it.hemerald.basementx.velocity.together.listeners.PlayerListener;
import lombok.Getter;

@Getter
public class Together {

    private static final String REDIS = "redis.";
    private final BasementVelocity plugin;
    private final ProxyServer server;
    private final Basement basement;
    private PartyManager partyManager;
    private InvitationService invitationService;

    @Inject
    public Together(BasementVelocity plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.basement = plugin.getBasement();
    }

    public void enable() {
        invitationService = new InvitationService(this);
        partyManager = new PartyManager(this);

        PartyCommand partyCommand = new PartyCommand(partyManager);
        partyCommand.registerArguments();

        server.getEventManager().register(plugin, new PlayerListener(this));
        server.getCommandManager().register(server.getCommandManager().metaBuilder("party").aliases("p").build(), partyCommand);
    }

    public void disable() {
        partyManager.disable();
    }
}
