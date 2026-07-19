package dev.hauch.hchat.registry;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

// command holder
public record CommandHolder(String label,
                            CommandExecutor executor,
                            @Nullable TabCompleter tabCompleter) {

    public CommandHolder {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Command label cannot be blank");
        }
        if (executor == null) {
            throw new IllegalArgumentException("CommandExecutor cannot be null");
        }
        
        if (tabCompleter == null && executor instanceof TabCompleter tc) {
            tabCompleter = tc;
        }
    }

    // has tab completer
    public boolean hasTabCompleter() {
        return tabCompleter != null;
    }
}
