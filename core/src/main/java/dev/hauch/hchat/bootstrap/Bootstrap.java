package dev.hauch.hchat.bootstrap;

import dev.hauch.hchat.api.HChatProvider;
import dev.hauch.hchat.api.platform.Platform;
import dev.hauch.hchat.api.platform.PlatformAdapter;
import dev.hauch.hchat.api.premium.PremiumChatEnhancer;
import dev.hauch.hchat.api.premium.PremiumModule;
import dev.hauch.hchat.command.AfkCommand;
import dev.hauch.hchat.command.BroadcastCommand;
import dev.hauch.hchat.command.ChannelCommand;
import dev.hauch.hchat.command.ChatlockCommand;
import dev.hauch.hchat.command.ClearCommand;
import dev.hauch.hchat.command.DndCommand;
import dev.hauch.hchat.command.HChatCommand;
import dev.hauch.hchat.command.IgnoreCommand;
import dev.hauch.hchat.command.MessageCommand;
import dev.hauch.hchat.command.PingCommand;
import dev.hauch.hchat.command.ReplyCommand;
import dev.hauch.hchat.command.SeenCommand;
import dev.hauch.hchat.command.SlowmodeCommand;
import dev.hauch.hchat.command.SpyCommand;
import dev.hauch.hchat.command.StaffChatCommand;
import dev.hauch.hchat.config.PluginConfig;
import dev.hauch.hchat.config.PluginMessages;
import dev.hauch.hchat.listener.AfkActivityListener;
import dev.hauch.hchat.listener.ChatListener;
import dev.hauch.hchat.listener.DeathMessageListener;
import dev.hauch.hchat.listener.PlayerJoinListener;
import dev.hauch.hchat.listener.PlayerQuitListener;
import dev.hauch.hchat.manager.AfkManager;
import dev.hauch.hchat.manager.AutoBroadcastManager;
import dev.hauch.hchat.manager.ChannelManager;
import dev.hauch.hchat.manager.ChatLockManager;
import dev.hauch.hchat.manager.ChatReplay;
import dev.hauch.hchat.manager.DndManager;
import dev.hauch.hchat.manager.IgnoreManager;
import dev.hauch.hchat.manager.LanguageManager;
import dev.hauch.hchat.manager.MentionManager;
import dev.hauch.hchat.manager.MessageHistory;
import dev.hauch.hchat.manager.OfflineMessageStore;
import dev.hauch.hchat.manager.PlayerLangManager;
import dev.hauch.hchat.manager.SlowmodeManager;
import dev.hauch.hchat.manager.SpyManager;
import dev.hauch.hchat.manager.StaffChatManager;
import dev.hauch.hchat.placeholder.HChatPlaceholderExpansion;
import dev.hauch.hchat.registry.CommandHolder;
import dev.hauch.hchat.registry.CommandRegistry;
import dev.hauch.hchat.registry.ListenerRegistry;
import dev.hauch.hchat.registry.ManagerRegistry;
import dev.hauch.hchat.registry.ServiceRegistry;
import dev.hauch.hchat.service.BroadcastService;
import dev.hauch.hchat.service.ConfigurationService;
import dev.hauch.hchat.service.DynamicPlaceholderResolver;
import dev.hauch.hchat.storage.IgnoreStorage;
import dev.hauch.hchat.storage.PlayerChannelStorage;
import dev.hauch.hchat.storage.PlayerLangStorage;
import dev.hauch.hchat.storage.YamlIgnoreStorage;
import dev.hauch.hchat.update.UpdateChecker;
import dev.hauch.hchat.utils.ChatLogger;
import dev.hauch.hchat.utils.WordFilter;
import dev.hauch.hchat.utils.filter.AntiAdFilter;
import dev.hauch.hchat.utils.filter.AntiCapsFilter;
import dev.hauch.hchat.utils.filter.AntiSpamFilter;
import dev.hauch.hchat.utils.filter.AntiUnicodeFilter;
import dev.hauch.hchat.utils.filter.FilterChain;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

// class Bootstrap
public final class Bootstrap {

    private static volatile PluginContext context;
    private static volatile PremiumModule premiumModule;
    private static final Logger LOG = Bukkit.getLogger();

    // make Bootstrap
    private Bootstrap() {
        throw new UnsupportedOperationException("Utility class");
    }

    // initialize data
    public static synchronized void initialize(Plugin plugin) {
        initialize(plugin, null);
    }

    // initialize data
    public static synchronized void initialize(Plugin plugin,
                                               PremiumModule premium) {
        if (context != null) {
            LOG.warning("[Bootstrap] initialize() called twice - ignoring.");
            return;
        }

        PluginContext ctx = assemble(plugin);
        context = ctx;

        // The premium module registers its commands, listeners and
        // managers right after the core is assembled, so they take part in
        // the normal bind flow below. A failing premium module must never
        // take the chat plugin down: fall back to free mode gracefully.
        premiumModule = premium;
        if (premium != null) {
            try {
                premium.install(ctx);
                LOG.info("[Bootstrap] " + premium.edition() + " active.");
            } catch (Exception t) {
                LOG.warning("[Bootstrap] Premium module failed to start: "
                        + t.getMessage() + " - continuing in free mode.");
                premiumModule = null;
            }
        }

        bindCommands(ctx);
        bindListeners(ctx, premiumModule == null
                ? List.of() : premiumModule.chatEnhancers());
        scheduleTasks(ctx);

        HChatProvider.install(new HChatProvider(
                ctx.plugin,
                ctx.config,
                ctx.messages,
                ctx.ignoreManager,
                ctx.spyManager,
                ctx.offlineMessageStore,
                ctx.playerLangManager,
                ctx.placeholderExpansion,
                ctx.channelManager,
                ctx.dndManager,
                ctx.messageHistory,
                ctx.serviceRegistry.broadcastService()));

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
        ctx.channelManager.reload();
        ctx.slowmodeManager.reload();
        ctx.afkManager.reload(ctx.plugin);
        ctx.ignoreManager.reload();
        ctx.playerLangManager.saveAll();
        ctx.autoBroadcastManager.reload();
        ctx.chatLogger.purgeOldLogs();
        if (premiumModule != null) {
            try {
                premiumModule.reload();
            } catch (Exception t) {
                LOG.warning("[Bootstrap] premium reload failed: "
                        + t.getMessage());
            }
        }
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
            if (premiumModule != null) {
                premiumModule.shutdown();
            }
        } catch (Exception e) {
            LOG.warning("[Bootstrap] shutdown raised: " + e.getMessage());
        } finally {
            HChatProvider.uninstall();
            premiumModule = null;
            context = null;
        }
    }

    // assemble data
    private static PluginContext assemble(Plugin plugin) {
        PlatformAdapter adapter = VersionLoader.bootstrap(plugin.getLogger());

        plugin.saveDefaultConfig();

        PluginConfig config = new PluginConfig(plugin);
        PluginMessages messages = new PluginMessages(plugin);

        UpdateChecker updateChecker = new UpdateChecker(plugin);

        plugin.saveResource("lang/lang_en.yml", false);

        IgnoreStorage ignoreStorage = new YamlIgnoreStorage(plugin);
        PlayerLangStorage playerLangStorage = new PlayerLangStorage(plugin);
        PlayerChannelStorage playerChannelStorage = new PlayerChannelStorage(plugin);

        PlayerLangManager playerLangManager = new PlayerLangManager(playerLangStorage);
        LanguageManager languageManager = new LanguageManager(plugin, playerLangManager);
        BroadcastService broadcastService = new BroadcastService(config);
        ChatLogger chatLogger = new ChatLogger(plugin, config);
        ConfigurationService configurationService =
                new ConfigurationService(config, messages);

        ChannelManager channelManager = new ChannelManager(config, playerChannelStorage);

        IgnoreManager ignoreManager = new IgnoreManager(ignoreStorage);
        SpyManager spyManager = new SpyManager();
        MessageHistory messageHistory = new MessageHistory();
        OfflineMessageStore offlineMessageStore = new OfflineMessageStore(
                config.getOfflineMessagesMaxPending());
        AutoBroadcastManager autoBroadcastManager =
                new AutoBroadcastManager(plugin, config, broadcastService);
        DndManager dndManager = new DndManager();
        SlowmodeManager slowmodeManager = new SlowmodeManager(config);
        ChatLockManager chatLockManager = new ChatLockManager(config);
        MentionManager mentionManager = new MentionManager();
        StaffChatManager staffChatManager = new StaffChatManager(config);
        ChatReplay chatReplay = new ChatReplay(config.getReplayCapacity());
        AfkManager afkManager = new AfkManager(config);

        CommandRegistry commandRegistry = new CommandRegistry();
        ListenerRegistry listenerRegistry = new ListenerRegistry();
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        ManagerRegistry managerRegistry = new ManagerRegistry();

        DynamicPlaceholderResolver placeholderResolver =
                new DynamicPlaceholderResolver(config, afkManager);

        serviceRegistry.registerConfigurationService(configurationService);
        serviceRegistry.registerBroadcastService(broadcastService);
        serviceRegistry.registerAutoBroadcastManager(autoBroadcastManager);
        serviceRegistry.registerChatLogger(chatLogger);
        serviceRegistry.registerPlaceholderResolver(placeholderResolver);

        managerRegistry.registerChannelManager(channelManager);
        managerRegistry.registerIgnoreManager(ignoreManager);
        managerRegistry.registerSpyManager(spyManager);
        managerRegistry.registerMessageHistory(messageHistory);
        managerRegistry.registerOfflineMessageStore(offlineMessageStore);
        managerRegistry.registerPlayerLangManager(playerLangManager);
        managerRegistry.registerLanguageManager(languageManager);
        managerRegistry.registerDndManager(dndManager);
        managerRegistry.registerSlowmodeManager(slowmodeManager);
        managerRegistry.registerChatLockManager(chatLockManager);
        managerRegistry.registerMentionManager(mentionManager);
        managerRegistry.registerStaffChatManager(staffChatManager);
        managerRegistry.registerChatReplay(chatReplay);
        managerRegistry.registerAfkManager(afkManager);

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
                playerChannelStorage,
                channelManager,
                ignoreManager,
                spyManager,
                messageHistory,
                offlineMessageStore,
                playerLangManager,
                languageManager,
                autoBroadcastManager,
                chatLogger,
                dndManager,
                slowmodeManager,
                chatLockManager,
                mentionManager,
                staffChatManager,
                chatReplay,
                afkManager,
                commandRegistry,
                listenerRegistry,
                serviceRegistry,
                managerRegistry,
                placeholderExpansion,
                updateChecker,
                discordSrvEnabled);
    }

    // bind commands
    private static void bindCommands(PluginContext ctx) {
        Plugin plugin = ctx.plugin;

        attachCommand(ctx, "message",
                new MessageCommand(plugin, ctx.config, ctx.messages,
                        ctx.ignoreManager, ctx.spyManager,
                        ctx.messageHistory, ctx.chatLogger,
                        ctx.offlineMessageStore, ctx.dndManager));
        attachCommand(ctx, "reply",
                new ReplyCommand(plugin, ctx.config, ctx.messages, ctx.messageHistory));
        attachCommand(ctx, "hchat",
                new HChatCommand(plugin, ctx.config, ctx.messages, ctx.playerLangManager,
                        ctx.languageManager, ctx.updateChecker, ctx.ignoreManager,
                        ctx.offlineMessageStore, ctx.chatReplay));
        attachCommand(ctx, "channel",
                new ChannelCommand(ctx.messages, ctx.channelManager, ctx.config));
        attachCommand(ctx, "afk",
                new AfkCommand(ctx.messages, ctx.afkManager));
        attachCommand(ctx, "clear",
                new ClearCommand(plugin, ctx.config, ctx.messages));
        attachCommand(ctx, "ignore",
                new IgnoreCommand(plugin, ctx.config, ctx.messages, ctx.ignoreManager));
        attachCommand(ctx, "spy",
                new SpyCommand(plugin, ctx.config, ctx.messages, ctx.spyManager));
        attachCommand(ctx, "broadcast",
                new BroadcastCommand(plugin, ctx.config, ctx.messages, broadcastOf(ctx),
                        ctx.channelManager));
        attachCommand(ctx, "slowmode",
                new SlowmodeCommand(ctx.messages, ctx.slowmodeManager));
        attachCommand(ctx, "chatlock",
                new ChatlockCommand(ctx.messages, ctx.chatLockManager));
        attachCommand(ctx, "dnd",
                new DndCommand(ctx.messages, ctx.dndManager));
        attachCommand(ctx, "sc",
                new StaffChatCommand(plugin, ctx.messages, ctx.staffChatManager));
        attachCommand(ctx, "ping",
                new PingCommand(ctx.messages));
        attachCommand(ctx, "seen",
                new SeenCommand(ctx.messages, ctx.messageHistory));

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
    private static void bindListeners(PluginContext ctx,
                                      List<PremiumChatEnhancer> chatEnhancers) {
        Plugin plugin = ctx.plugin;
        PluginManager pm = plugin.getServer().getPluginManager();

        FilterChain filterChain = new FilterChain(List.of(
                new WordFilter(ctx.config),
                new AntiUnicodeFilter(ctx.config),
                new AntiCapsFilter(ctx.config),
                new AntiAdFilter(ctx.config),
                new AntiSpamFilter(ctx.config)));
        ctx.listenerRegistry.register("chat",
                new ChatListener(plugin, ctx.config, ctx.messages, filterChain,
                        ctx.channelManager, ctx.slowmodeManager,
                        ctx.chatLockManager, ctx.mentionManager,
                        ctx.serviceRegistry.placeholderResolver(),
                        ctx.chatReplay,
                        chatEnhancers));
        ctx.listenerRegistry.register("death",
                new DeathMessageListener(ctx.config));
        ctx.listenerRegistry.register("join",
                new PlayerJoinListener(plugin, ctx.config, ctx.messages,
                        ctx.offlineMessageStore, ctx.updateChecker,
                        ctx.channelManager, ctx.afkManager));
        ctx.listenerRegistry.register("quit",
                new PlayerQuitListener(plugin, ctx.config, ctx.messages));
        ctx.listenerRegistry.register("afk-activity",
                new AfkActivityListener(plugin, ctx.config, ctx.messages,
                        ctx.afkManager));

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
        ctx.afkManager.startAutoCheck(ctx.plugin);

        if (ctx.config.isUpdateCheckerEnabled()) {
            ctx.updateChecker.checkAsync(() -> {
                if (ctx.updateChecker.isUpdateAvailable()) {
                    LOG.info("[UpdateChecker] A new version is available: "
                            + ctx.updateChecker.latestVersion()
                            + " (current: " + ctx.updateChecker.currentVersion()
                            + ") - " + ctx.updateChecker.releaseUrl());
                }
            });
        }
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
