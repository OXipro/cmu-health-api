package com.oxipro.cmu.health.lastHit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores the most recent damage event for each player.
 * Intentionally simple — just a UUID → LastHitData map.
 */
public class LastHitManager {

    private final Map<UUID, LastHitData> hits = new HashMap<>();

    public void record(Player victim, EntityDamageEvent.DamageCause cause, Entity attacker, double damage) {
        hits.put(victim.getUniqueId(), new LastHitData(cause, attacker, damage));
    }

    /** Returns null if no hit was ever recorded. Does NOT expire — call this at death time. */
    public LastHitData get(Player victim) {
        return hits.get(victim.getUniqueId());
    }

    public void remove(Player victim) {
        hits.remove(victim.getUniqueId());
    }

    public void clear() {
        hits.clear();
    }
}
