package com.oxipro.cmu.health.lastHit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Snapshot of a single damage event received by a player.
 */
public class LastHitData {

    private final EntityDamageEvent.DamageCause cause;
    /** Resolved attacker (shooter behind a projectile, not the arrow itself). Null = environmental. */
    private final Entity attacker;
    private final double damage;
    private final long timestamp;

    public LastHitData(EntityDamageEvent.DamageCause cause, Entity attacker, double damage) {
        this.cause = cause;
        this.attacker = attacker;
        this.damage = damage;
        this.timestamp = System.currentTimeMillis();
    }

    public EntityDamageEvent.DamageCause getCause() { return cause; }
    public Entity getAttacker() { return attacker; }
    public double getDamage() { return damage; }
    public long getTimestamp() { return timestamp; }

    /** Convenience: true if attacker is an online player. */
    public boolean isAttackerPlayer() {
        return attacker instanceof Player && ((Player) attacker).isOnline();
    }

    public Player getAttackerAsPlayer() {
        return isAttackerPlayer() ? (Player) attacker : null;
    }
}

