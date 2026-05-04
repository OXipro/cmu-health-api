package com.oxipro.cmu.health.listeners;

import com.oxipro.cmu.health.mobOwner.MobOwnerTracker;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Automatically tags mobs with their owner when they spawn.
 *
 * Handled spawn reasons:
 *   - TAME       → Wolf/Ocelot just tamed by a player (1.8 has Tameable)
 *   - BUILD_WITHER, BUILD_IRONGOLEM, BUILD_SNOWMAN → player-built mobs
 *
 * For mobs summoned by plugin code, call MobOwnerTracker.tagOwner() directly
 * right after spawning the entity.
 */
public class MobSpawnListener implements Listener {

    private final MobOwnerTracker tracker;

    public MobSpawnListener(MobOwnerTracker tracker) {
        this.tracker = tracker;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        // Tamed pets entity is already Tameable with an owner set at this point
        if (reason == CreatureSpawnEvent.SpawnReason.EGG) {
            Entity entity = event.getEntity();
            if (entity instanceof Tameable) {
                AnimalTamer owner = ((Tameable) entity).getOwner();
                if (owner instanceof Player) {
                    tracker.tagOwner(entity, (Player) owner);
                }
            }
        }

        // Player-built mobs (Wither, Iron Golem, Snow Golem) — no owner to resolve
        // at spawn time; tag them via plugin call instead if needed.
    }
}
