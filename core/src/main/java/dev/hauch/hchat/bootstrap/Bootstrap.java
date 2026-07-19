package dev.hauch.hchat.bootstrap;

import dev.hauch.hchat.api.HChatProvider;
import dev.hauch.hchat.api.platform.Platform;
import dev.hauch.hchat.api.platform.PlatformAdapter;
import dev.hauch.hchat.command.BroadcastCommand;
import dev.hauch.hchat.command.ClearCommand;
import dev.hauch.hchat.command.HChatCommand;
import dev.hauch.hchat.command.IgnoreCommand;
import dev.hauch.hchat.command.MessageCommand;
import dev.hauch.hchat.command.ReplyCommand;
import dev.hauch.hchat.command.SpyCommand;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.listener.ChatListener;
import dev.hauch.hchat.listener.PlayerJoinListener;
import dev.hauch.hchat.listener.PlayerQuitListener;
import dev.hauch.hchat.manager.AutoBroadcastManager;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.manager.SpyManager;
import dev.hauch.hchat.placeholder.HChatPlaceholderExpansion;
import dev.hauch.hchat.registry.CommandHolder;
import dev.hauch.hchat.registry.CommandRegistry;
import dev.hauch.hchat.registry.ListenerRegistry;
import dev.hauch.hchat.registry.ManagerRegistry;
import dev.hauch.hchat.registry.ServiceRegistry;
import dev.hauch.hchat.service.BroadcastService;
import dev.hauch.hchat.service.ConfigurationService;
import dev.hauch.hchat.storage.IgnoreStorage;
import dev.hauch.hchat.storage.PlayerLangStorage;
import dev.hauch.hchat.storage.YamlIgnoreStorage;
import dev.hauch.hchat.utils.ChatLogger;
import dev.hauch.hchat.utils.WordFilter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.logging.Logger;

// class Bootstrap
public final class Bootstrap {

    private static volatile PluginContext context;
    private static final Logger LOG = Bukkit.getLogger();

    // make Bootstrap
    private Bootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

    // initialize data
    public static synchronized void initialize(Plugin plugin) {
        if (context != null) {
            LOG.warning("[Bootstrap] initialize() called twice - ignoring.");
            return;
        }

        PluginContext ctx = assemble(plugin);
        context = ctx;

        bindCommands(ctx);
        bindListeners(ctx);
        scheduleTasks(ctx);

        HChatProvider.install(new HChatProvider(
                ctx.plugin,
                ctx.config,
                ctx.messages,
                ctx.ignoreManager,
                ctx.spyManager,
                ctx.offlineMessageStore,
                ctx.playerLangManager,
                ctx.placeholderExpansion));

        ctx.chatLogger.purgeOldLogs();

        LOG.info(String.format(Locale.ROOT,
                "[Bootstrap] hChat %s ready on platform %s.",
                ctx.plugin.getDescription().getVersion(),
                ctx.platformAdapter.getDisplayName()));
    }

    // reload data
    public static synchronized void reload() {
        PluginContext ctx = context;
        if (ctx == null) {
            LOG.warning("[Bootstrap] reload() called before initialize()");
            return;
        }
        ctx.config.reloadConfig();
        ctx.messages.reload();
        ctx.ignoreManager.reload();
        ctx.playerLangManager.saveAll();
        ctx.autoBroadcastManager.reload();
        ctx.chatLogger.purgeOldLogs();
        LOG.info("[Bootstrap] hChat reloaded.");
    }

    // shutdown data
    public static synchronized void shutdown() {
        PluginContext ctx = context;
        if (ctx == null) return;

        try {
            ctx.autoBroadcastManager.shutdown();
            ctx.chatLogger.purgeOldLogs();
            ctx.playerLangManager.saveAll();
        } catch (Exception e) {
            LOG.warning("[Bootstrap] shutdown raised: " + e.getMessage());
        } finally {
            HChatProvider.uninstall();
            context = null;
        }
    }

    // assemble data
    private static PluginContext assemble(Plugin plugin) {
        PlatformAdapter adapter = VersionLoader.bootstrap(plugin.getLogger());

        plugin.saveDefaultConfig();

        PluginConfig config = new PluginConfig(plugin);
        PluginMessages messages = new PluginMessages(plugin);

        plugin.saveResource("lang/lang_en.yml", false);

        IgnoreStorage ignoreStorage = new YamlIgnoreStorage(plugin);
        PlayerLangStorage playerLangStorage = new PlayerLangStorage(plugin);

        PlayerLangManager playerLangManager = new PlayerLangManager(playerLangStorage);
        LanguageManager languageManager = new LanguageManager(plugin, playerLangManager);
        BroadcastService broadcastService = new BroadcastService(config);
        ChatLogger chatLogger = new ChatLogger(plugin, config);
        ConfigurationService configurationService =
                new ConfigurationService(config, messages);

        IgnoreManager ignoreManager = new IgnoreManager(ignoreStorage);
        SpyManager spyManager = new SpyManager();
        MessageHistory messageHistory = new MessageHistory();
        OfflineMessageStore offlineMessageStore = new OfflineMessageStore(
                config.getOfflineMessagesMaxPending());
        AutoBroadcastManager autoBroadcastManager =
                new AutoBroadcastManager(plugin, config, broadcastService);
        DndManager dndManager = new DndManager();

        CommandRegistry commandRegistry = new CommandRegistry();
        ListenerRegistry listenerRegistry = new ListenerRegistry();
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        ManagerRegistry managerRegistry = new ManagerRegistry();

        serviceRegistry.registerConfigurationService(configurationService);
        serviceRegistry.registerBroadcastService(broadcastService);
        serviceRegistry.registerAutoBroadcastManager(autoBroadcastManager);
        serviceRegistry.registerChatLogger(chatLogger);

        managerRegistry.registerIgnoreManager(ignoreManager);
        managerRegistry.registerSpyManager(spyManager);
        managerRegistry.registerMessageHistory(messageHistory);
        managerRegistry.registerOfflineMessageStore(offlineMessageStore);
        managerRegistry.registerPlayerLangManager(playerLangManager);
        managerRegistry.registerLanguageManager(languageManager);
        managerRegistry.registerDndManager(dndManager);

        HChatPlaceholderExpansion placeholderExpansion =
                new HChatPlaceholderExpansion(plugin, messages,
                        ignoreManager, spyManager, dndManager, messageHistory);

        boolean discordSrvEnabled =
                Bukkit.getPluginManager().getPlugin("DiscordSRV") != null;
        if (discordSrvEnabled) {
            LOG.info("[Bootstrap] DiscordSRV detected - bridge available.");
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                placeholderExpansion.register();
                LOG.info("[Bootstrap] PlaceholderAPI expansion registered.");
            } catch (Exception t) {
                LOG.warning("[Bootstrap] PlaceholderAPI registration failed: "
                        + t.getMessage());
            }
        } else {
            LOG.info("[Bootstrap] PlaceholderAPI not found - placeholders disabled.");
        }

        return new PluginContext(
                plugin,
                adapter,
                config,
                messages,
                configurationService,
                ignoreStorage,
                playerLangStorage,
                ignoreManager,
                spyManager,
                messageHistory,
                offlineMessageStore,
                playerLangManager,
                languageManager,
                autoBroadcastManager,
                chatLogger,
                dndManager,
                commandRegistry,
                listenerRegistry,
                serviceRegistry,
                managerRegistry,
                placeholderExpansion,
                discordSrvEnabled);
    }

    // bind commands
    private static void bindCommands(PluginContext ctx) {
        Plugin plugin = ctx.plugin;

        attachCommand(ctx, "message",
                new MessageCommand(plugin, ctx.config, ctx.messages,
                        ctx.ignoreManager, ctx.spyManager,
                        ctx.messageHistory, ctx.chatLogger,
                        ctx.offlineMessageStore));
        attachCommand(ctx, "reply",
                new ReplyCommand(plugin, ctx.config, ctx.messages, ctx.messageHistory));
        attachCommand(ctx, "hchat",
                new HChatCommand(plugin, ctx.config, ctx.messages, ctx.playerLangManager));
        attachCommand(ctx, "clear",
                new ClearCommand(plugin, ctx.config, ctx.messages));
        attachCommand(ctx, "ignore",
                new IgnoreCommand(plugin, ctx.config, ctx.messages, ctx.ignoreManager));
        attachCommand(ctx, "spy",
                new SpyCommand(plugin, ctx.config, ctx.messages, ctx.spyManager));
        attachCommand(ctx, "broadcast",
                new BroadcastCommand(plugin, ctx.config, ctx.messages, broadcastOf(ctx)));

        for (CommandHolder holder : ctx.commandRegistry.all()) {
            try {
                PluginCommand cmd = ((JavaPlugin) plugin).getCommand(holder.label());
                if (cmd == null) {
                    LOG.warning("[Bootstrap] command '" + holder.label()
                            + "' not declared in plugin.yml - skipped.");
                    continue;
                }
                cmd.setExecutor(holder.executor());
                if (holder.hasTabCompleter()) {
                    cmd.setTabCompleter(holder.tabCompleter());
                }
            } catch (Exception t) {
                LOG.warning("[Bootstrap] failed to bind command '"
                        + holder.label() + "': " + t.getMessage());
            }
        }
    }

    // attach command
    private static void attachCommand(PluginContext ctx,
                                      String label,
                                      org.bukkit.command.CommandExecutor executor) {
        ctx.commandRegistry.register(label, executor, null);
    }

    // broadcast of
    private static BroadcastService broadcastOf(PluginContext ctx) {
        return ctx.serviceRegistry.broadcastService();
    }

    // bind listeners
    private static void bindListeners(PluginContext ctx) {
        Plugin plugin = ctx.plugin;
        PluginManager pm = plugin.getServer().getPluginManager();

        WordFilter wordFilter = new WordFilter(ctx.config);

        ctx.listenerRegistry.register("chat",
                new ChatListener(plugin, ctx.config, ctx.messages, wordFilter));
        ctx.listenerRegistry.register("join",
                new PlayerJoinListener(plugin, ctx.config, ctx.messages,
                        ctx.offlineMessageStore));
        ctx.listenerRegistry.register("quit",
                new PlayerQuitListener(plugin, ctx.config, ctx.messages));

        for (String entry : ctx.listenerRegistry.names()) {
            Listener listener = ctx.listenerRegistry.get(entry);
            try {
                pm.registerEvents(listener, plugin);
            } catch (Exception t) {
                LOG.warning("[Bootstrap] failed to register listener '" + entry
                        + "': " + t.getMessage());
            }
        }
    }

    // schedule tasks
    private static void scheduleTasks(PluginContext ctx) {
        
        ctx.autoBroadcastManager.reload();
    }

    // context data
    public static PluginContext context() {
        PluginContext ctx = context;
        if (ctx == null) {
            throw new IllegalStateException("Bootstrap was not initialized");
        }
        return ctx;
    }

    // clear for testing
    static void resetForTesting() {
        context = null;
        Platform.reset();
    }
}
