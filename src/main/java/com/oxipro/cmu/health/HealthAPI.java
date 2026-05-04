package com.oxipro.cmu.health;

import com.oxipro.cmu.health.death.DeathContextManager;
import com.oxipro.cmu.health.death.DeathMessageHandler;
import com.oxipro.cmu.health.lastHit.LastHitManager;
import com.oxipro.cmu.health.listeners.MobSpawnListener;
import com.oxipro.cmu.health.listeners.OnDamageListener;
import com.oxipro.cmu.health.listeners.OnDeathListener;
import com.oxipro.cmu.health.mobOwner.MobOwnerTracker;
import com.oxipro.cmu.versionsupport.PlayerUtilsSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

import static org.bukkit.Bukkit.getLogger;

public class HealthAPI {

    private Plugin plugin;

    private PlayerUtilsSupport playerUtilsSupport;

    // Death messages
    private LastHitManager lastHitManager;
    private MobOwnerTracker mobOwnerTracker;
    private DeathContextManager deathContextManager;
    private DeathMessageHandler messageHandler;

    public boolean register(Plugin plugin) {
        if (plugin == null) return false;
        this.plugin = plugin;
        if (!initDeps()) return false;
        registerListeners();
        return true;
    }

    private boolean initDeps() {
        playerUtilsSupport = PlayerUtilsSupport.SupportBuilder.load();

        if (playerUtilsSupport == null){
            getLogger().severe("HEALTH-API: Server version not supported");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
        return true;
    }

    private void initManagers() {
        lastHitManager = new LastHitManager();
        mobOwnerTracker = new MobOwnerTracker(plugin);
        messageHandler = new DeathMessageHandler(plugin);
        deathContextManager = new DeathContextManager(lastHitManager, mobOwnerTracker);
    }

    private void registerListeners() {
        registerEvents(new OnDeathListener(this), new OnDamageListener(this), new MobSpawnListener(mobOwnerTracker));
    }

    public void registerEvents(Listener... listeners) {
        Arrays.stream(listeners).forEach(l -> plugin.getServer().getPluginManager().registerEvents(l, plugin));
    }

    public Plugin getPlugin() {
        return  plugin;
    }


    public PlayerUtilsSupport getPlayerUtilsSupport() { return playerUtilsSupport; }

    public LastHitManager getLastHitManager() { return lastHitManager; }
    public MobOwnerTracker getMobOwnerTracker() { return mobOwnerTracker; }
    public DeathContextManager getDeathContextManager() { return deathContextManager; }
    public DeathMessageHandler getMessageHandler() { return messageHandler; }
}
