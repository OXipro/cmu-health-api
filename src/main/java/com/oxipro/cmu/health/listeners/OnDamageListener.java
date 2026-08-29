package com.oxipro.cmu.health.listeners;

import com.oxipro.cmu.health.HealthAPI;
import com.oxipro.cmu.health.lastHit.LastHitManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
        this.lastHitManager = healthAPI.getLastHitManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity();
        if (healthAPI.isDeathIntercepted(player)) {
            e.setCancelled(true);
            return;
        }
        if (player.getHealth() - e.getFinalDamage() >= 0.5D) {
            return;
        }
        if (!healthAPI.shouldInterceptDeath(player)) {
            return;
        }

        if (healthAPI.getTotemUtil().wouldVanillaTotemSave(player, e.getCause())) {
            return;
        }

        e.setCancelled(true);
        healthAPI.markDeathIntercepted(player);
        player.setLastDamageCause(e);

        Entity attacker = null;
        if (e instanceof EntityDamageByEntityEvent) {
            attacker = resolveAttacker(((EntityDamageByEntityEvent) e).getDamager());
        }
        lastHitManager.record(player, e.getCause(), attacker, e.getFinalDamage());

        List<ItemStack> drops = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
        healthAPI.getPlayerUtilsSupport().fakeDamage(player);
        healthAPI.getPlayerUtilsSupport().callPlayerDeathEvent(player, drops, 0, 0, "");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) event.getEntity();
        Entity attacker = resolveAttacker(event.getDamager());
        lastHitManager.record(victim, event.getCause(), attacker, event.getFinalDamage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnvironmentalDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) event.getEntity();
        lastHitManager.record(victim, event.getCause(), null, event.getFinalDamage());
    }

    private Entity resolveAttacker(Entity raw) {
        if (raw instanceof Projectile) {
            ProjectileSource src = ((Projectile) raw).getShooter();
            if (src instanceof Entity) {
                return (Entity) src;
            }
        }
        return raw;
    }
}
