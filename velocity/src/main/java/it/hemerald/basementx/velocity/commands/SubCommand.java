package it.hemerald.basementx.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.types.PermissionNode;

import java.util.UUID;

public class SubCommand implements SimpleCommand {

    private final LuckPerms luckPerms = LuckPermsProvider.get();

    @Override
    public void execute(Invocation invocation) {
        if(!(invocation.source() instanceof ConsoleCommandSource)) return;
        if(invocation.arguments().length < 2) return;
        UUID uuid = UUID.fromString(invocation.arguments()[0]);
        String permission = invocation.arguments()[1];

        luckPerms.getUserManager().modifyUser(uuid, user -> {
            user.data().add(PermissionNode.builder().permission(permission).build());
        });
    }
}
