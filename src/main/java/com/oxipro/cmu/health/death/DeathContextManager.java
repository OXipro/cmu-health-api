package com.oxipro.cmu.health.death;

import com.oxipro.cmu.health.lastHit.LastHitData;
import com.oxipro.cmu.health.lastHit.LastHitManager;
import com.oxipro.cmu.health.mobOwner.MobOwnerTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Builds a {@link DeathContext} for a dying player and fires {@link PlayerDeathContextEvent}.
 * That is the full responsibility of this class — no message sending, no display logic.
 */
public class DeathContextManager {

    private final LastHitManager  lastHitManager;
    private final MobOwnerTracker mobOwnerTracker;

    public DeathContextManager(LastHitManager lastHitManager, MobOwnerTracker mobOwnerTracker) {
        this.lastHitManager  = lastHitManager;
        this.mobOwnerTracker = mobOwnerTracker;
    }

    /**
     * Entry point called by {@link com.oxipro.cmu.health.listeners.OnDeathListener}.
     * Builds the context, fires the event, cleans up.
     *
     * @return the built DeathContext (for callers who need it synchronously)
     */
    public DeathContext handleDeath(Player victim) {
        LastHitData hit = lastHitManager.get(victim);
        DeathContext ctx = buildContext(victim, hit);

        PlayerDeathContextEvent event = new PlayerDeathContextEvent(ctx);
        Bukkit.getPluginManager().callEvent(event);

        lastHitManager.remove(victim);
        return ctx;
    }

    // ── Context resolution ─────────────────────────────────────────

    private DeathContext buildContext(Player victim, LastHitData hit) {
        DeathContext.Builder b = new DeathContext.Builder(victim);

        if (hit == null) {
            return b.scenario(DeathContext.Scenario.UNKNOWN).build();
        }

        b.cause(hit.getCause()).finalDamage(hit.getDamage());
        Entity attacker = hit.getAttacker();

        // Environmental — no attacker entity at all
        if (attacker == null) {
            return b.scenario(DeathContext.Scenario.ENVIRONMENTAL).build();
        }

        // PVP — direct player attacker
        if (attacker instanceof Player) {
            return b.scenario(DeathContext.Scenario.PVP)
                    .killerEntity(attacker)
                    .killerPlayer((Player) attacker)
                    .build();
        }

        // Mob — check if it has a player owner (pet / summon)
        if (attacker instanceof LivingEntity) {
            Player owner = mobOwnerTracker.getOwner(attacker);
            if (owner != null) {
                return b.scenario(DeathContext.Scenario.PET)
                        .killerEntity(attacker)
                        .killerPlayer(owner)
                        .mobType(attacker.getType())
                        .build();
            }
            return b.scenario(DeathContext.Scenario.MOB)
                    .killerEntity(attacker)
                    .mobType(attacker.getType())
                    .build();
        }

        // Damage-cause based fallbacks (projectile, magic, potion)
        EntityDamageEvent.DamageCause cause = hit.getCause();

        if (cause == EntityDamageEvent.DamageCause.MAGIC || cause == EntityDamageEvent.DamageCause.WITHER) {
            return b.scenario(DeathContext.Scenario.MAGIC).killerEntity(attacker).build();
        }
        if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            return b.scenario(DeathContext.Scenario.PROJECTILE_INDIRECT).killerEntity(attacker).build();
        }

        return b.scenario(DeathContext.Scenario.UNKNOWN).killerEntity(attacker).build();
    }
}
