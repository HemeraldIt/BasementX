package it.mineblock.basementx.velocity.together.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.mineblock.basementx.velocity.together.Together;
import it.mineblock.basementx.velocity.together.commands.arguments.*;
import it.mineblock.basementx.velocity.together.manager.PartyManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class PartyCommand implements SimpleCommand {

    private final Together together;
    private final PartyManager partyService;
    private final CommandArgument unknownArgument;
    private final Map<String, CommandArgument> arguments = new HashMap<>();

    public PartyCommand(PartyManager partyService) {
        this.partyService = partyService;
        this.together = partyService.getTogether();
        unknownArgument = new UnknownArgument(partyService);
    }

    public void registerArguments() {
        registerArgument(new InviteArgument(partyService));
        registerArgument(new LeaderArgument(partyService));
        registerArgument(new JoinArgument(partyService));
        registerArgument(new InfoArgument(partyService));
        registerArgument(new ChatArgument(partyService));
        registerArgument(new LeaveArgument(partyService));
        registerArgument(new KickArgument(partyService));
        registerArgument(new OpenArgument(partyService));
        registerArgument(new WarpArgument(partyService));
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

        if(argument == null) {
            argument = arguments.get("invite");
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
            if (invocation.arguments().length == 0) return new ArrayList<>();
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
