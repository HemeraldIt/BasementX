package it.hemerald.basementx.velocity.together.commands;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.velocity.together.manager.PartyManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class CommandArgument {

    protected final FriendsManager friendService;
    private final String argument;
    private final int length;

    public abstract void execute(Player player, String[] args);

    public List<String> suggest(CommandSource source, String[] currentArgs) {
        return ImmutableList.of();
    }
}
