package com.oxipro.cmu.health;

import com.oxipro.cmu.health.death.DeathContextManager;
import com.oxipro.cmu.health.death.DeathMessageHandler;
import com.oxipro.cmu.health.lastHit.LastHitManager;
import com.oxipro.cmu.health.listeners.MobSpawnListener;
import com.oxipro.cmu.health.listeners.OnDamageListener;
import com.oxipro.cmu.health.listeners.OnDeathListener;
import com.oxipro.cmu.health.mobOwner.MobOwnerTracker;
import com.oxipro.cmu.health.utils.TotemUtil;
import com.oxipro.cmu.versionsupport.PlayerUtilsSupport;
import com.oxipro.cmu.versionsupport.VersionMapping;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.bukkit.Bukkit.getLogger;

public class HealthAPI {

    private Plugin plugin;

    private String mcVersion;

    private TotemUtil totemUtil;

    private PlayerUtilsSupport playerUtilsSupport;

    private LastHitManager lastHitManager;
    private MobOwnerTracker mobOwnerTracker;
    private DeathContextManager deathContextManager;
    private DeathMessageHandler messageHandler;

    private Predicate<Player> deathInterceptFilter;
    private final Set<UUID> interceptedDeaths = new HashSet<>();

    public boolean register(Plugin plugin) {
        return register(plugin, null);
    }

    public boolean register(Plugin plugin, PlayerUtilsSupport playerUtilsSupport) {
        if (plugin == null) {
            return false;
        }
        this.plugin = plugin;
        this.playerUtilsSupport = playerUtilsSupport;
        if (this.playerUtilsSupport == null) {
            this.playerUtilsSupport = PlayerUtilsSupport.SupportBuilder.load();
        }
        if (this.playerUtilsSupport == null) {
            getLogger().severe("HEALTH-API: Server version not supported");
            return false;
        }

        this.totemUtil = new TotemUtil(playerUtilsSupport);

        initManagers();
        registerListeners();
        return true;
    }

    private void initManagers() {
        lastHitManager = new LastHitManager();
        mobOwnerTracker = new MobOwnerTracker(plugin);
        deathContextManager = new DeathContextManager(lastHitManager, mobOwnerTracker);
    }

    private void registerListeners() {
        registerEvents(new OnDeathListener(this), new OnDamageListener(this), new MobSpawnListener(mobOwnerTracker));
    }

    public void registerEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(l -> plugin.getServer().getPluginManager().registerEvents(l, plugin));
    }

    public void setDeathInterceptFilter(Predicate<Player> filter) {
        this.deathInterceptFilter = filter;
    }

    public boolean shouldInterceptDeath(Player player) {
        return player != null && deathInterceptFilter != null && deathInterceptFilter.test(player);
    }

    public boolean isDeathIntercepted(Player player) {
        return player != null && interceptedDeaths.contains(player.getUniqueId());
    }

    public void markDeathIntercepted(Player player) {
        if (player != null) {
            interceptedDeaths.add(player.getUniqueId());
        }
    }

    public void clearDeathIntercepted(Player player) {
        if (player != null) {
            interceptedDeaths.remove(player.getUniqueId());
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public TotemUtil getTotemUtil() { return  totemUtil; }

    public PlayerUtilsSupport getPlayerUtilsSupport() { return playerUtilsSupport; }

    public LastHitManager getLastHitManager() { return lastHitManager; }
    public MobOwnerTracker getMobOwnerTracker() { return mobOwnerTracker; }
    public DeathContextManager getDeathContextManager() { return deathContextManager; }
    public DeathMessageHandler getMessageHandler() { return messageHandler; }
}
