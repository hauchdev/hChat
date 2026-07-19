package dev.hauch.hchat.registry;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

// class CommandRegistry
public final class CommandRegistry extends Registry<CommandHolder> {

    // make CommandRegistry
    public CommandRegistry() {
        super("Command");
    }

    // register data
    public void register(String label,
                         CommandExecutor executor,
                         @Nullable TabCompleter completer) {
        register(label, new CommandHolder(label, executor, completer));
    }
}
