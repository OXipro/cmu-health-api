package com.oxipro.cmu.health.listeners;

import com.oxipro.cmu.health.HealthAPI;
import com.oxipro.cmu.health.lastHit.LastHitManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnDamageListener implements Listener {

    private final HealthAPI healthAPI;
    private final LastHitManager lastHitManager;

    public OnDamageListener(HealthAPI healthAPI) {
        this.healthAPI = healthAPI;
        this.lastHitManager = new LastHitManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            double finalHealth = player.getHealth() - e.getFinalDamage();
            if (finalHealth < 0.5) {
                e.setCancelled(true);

                player.setLastDamageCause(e);
                List<ItemStack> drops = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
                player.setLastDamageCause(e);
                healthAPI.getPlayerUtilsSupport().fakeDamage(player);
                healthAPI.getPlayerUtilsSupport().callPlayerDeathEvent(player, drops, 0, 0, "");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player victim   = (Player) event.getEntity();
        Entity attacker = resolveAttacker(event.getDamager());
        lastHitManager.record(victim, event.getCause(), attacker, event.getFinalDamage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnvironmentalDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        lastHitManager.record(victim, event.getCause(), null, event.getFinalDamage());
    }

    /**
     * Resolves the true attacker behind a projectile.
     * Arrow shot by player → returns the player.
     * Splash potion thrown by player → returns the player.
     */
    private Entity resolveAttacker(Entity raw) {
        if (raw instanceof Projectile) {
            ProjectileSource src = ((Projectile) raw).getShooter();
            if (src instanceof Entity) return (Entity) src;
        }
        return raw;
    }
}
