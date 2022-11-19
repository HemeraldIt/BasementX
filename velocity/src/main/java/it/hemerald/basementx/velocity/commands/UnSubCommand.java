package it.hemerald.basementx.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;

import java.util.UUID;

public class UnSubCommand implements SimpleCommand  {

    private final LuckPerms luckPerms = LuckPermsProvider.get();

    @Override
    public void execute(Invocation invocation) {
        if(!(invocation.source() instanceof ConsoleCommandSource)) return;
        if(invocation.arguments().length < 2) return;
        UUID uuid = UUID.fromString(invocation.arguments()[0]);
        String permission = invocation.arguments()[1];

        User user = luckPerms.getUserManager().getUser(uuid);
        if(user != null) {
            checkAndApply(user, permission);
            return;
        }

        luckPerms.getUserManager().loadUser(uuid).whenComplete((loadedUser, throwable) -> {
            checkAndApply(loadedUser, permission);
        });
    }

    private void checkAndApply(User user, String permission) {
        if(user.getCachedData().getPermissionData().checkPermission(permission) == Tristate.FALSE) {
            user.data().remove(PermissionNode.builder().permission(permission).build());
        }
    }
}
