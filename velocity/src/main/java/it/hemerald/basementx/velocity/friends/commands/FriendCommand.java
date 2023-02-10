package it.hemerald.basementx.velocity.friends.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.hemerald.basementx.velocity.friends.manager.FriendsManager;
import it.hemerald.basementx.velocity.together.Together;
import it.hemerald.basementx.velocity.together.commands.CommandArgument;
import it.hemerald.basementx.velocity.together.commands.arguments.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class FriendCommand implements SimpleCommand {

    private final Together together;
    private final FriendsManager friendsService;
    private final CommandArgument unknownArgument;
    private final Map<String, CommandArgument> arguments = new HashMap<>();

    public FriendCommand(FriendsManager friendsService) {
        this.friendsService = friendsService;
        this.together = friendsService.getTogether();
        unknownArgument = new HelpArgument(friendsService);
    }

    public void registerArguments() {
        registerArgument(new RemoveArgument(friendService));
        registerArgument(new AddArgument(friendService));
        registerArgument(new AcceptArgument(friendService));
        registerArgument(new AcceptArgument(friendService));
        registerArgument(new ListArgument(friendService));
    }

    public void registerArgument(CommandArgument argument) {
        arguments.put(argument.getArgument(), argument);
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) return;

        if (invocation.arguments().length == 0) {
            unknownArgument.execute(player, invocation.arguments());
            return;
        }

        CommandArgument argument = arguments.get(invocation.arguments()[0]);

        if (argument == null) {
            arguments.get("add").execute(player, new String[] {"", invocation.arguments()[0]});
            return;
        }

        if(argument.getLength() > invocation.arguments().length) {
            unknownArgument.execute(player, invocation.arguments());
        } else {
            argument.execute(player, invocation.arguments());
        }
    }

    public CommandArgument getArgument(String invocation) {
        CommandArgument argument = arguments.get(invocation);
        if(argument == null) return unknownArgument;
        return argument;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        Executor executor = (runnable) -> together.getServer().getScheduler().buildTask(together.getPlugin(), runnable).schedule();
        return CompletableFuture.supplyAsync(() -> {
            if (invocation.arguments().length == 0) {
                return new ArrayList<>(arguments.keySet());
            }
            if (invocation.arguments().length > 1) {
                return getArgument(invocation.arguments()[0])
                        .suggest(invocation.source(), Arrays.copyOfRange(invocation.arguments(), 1, invocation.arguments().length));
            }

            return arguments
                    .keySet()
                    .stream()
                    .filter(name -> name.regionMatches(true, 0, invocation.arguments()[0], 0, invocation.arguments()[0].length()))
                    .collect(Collectors.toList());

        }, executor);
    }
}
