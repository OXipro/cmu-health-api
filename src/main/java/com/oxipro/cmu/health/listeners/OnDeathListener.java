package com.oxipro.cmu.health.listeners;

import com.oxipro.cmu.health.HealthAPI;
import com.oxipro.cmu.health.death.DeathContextManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnDeathListener implements Listener {

    private final HealthAPI healthAPI;
    private final DeathContextManager deathContextManager;

    public OnDeathListener(HealthAPI healthAPI) {
        this.healthAPI = healthAPI;
        this.deathContextManager = healthAPI.getDeathContextManager();
    }

    // LOWEST: we override the vanilla death message first.
    // Other plugins listening at NORMAL+ can still override via PlayerDeathContextEvent.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        // Suppress vanilla message ours comes from DeathContextManager
        event.setDeathMessage(null);

        if (deathContextManager != null) {
            deathContextManager.handleDeath(victim);
        }
    }
}
