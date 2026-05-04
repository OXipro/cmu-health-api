package com.oxipro.cmu.health.mobOwner;


import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class MobOwnerTracker {

    public static final String TAG_KEY = "deathapi_owner";

    // Your NMS VersionSupport is accessed via a plugin instance.
    // We keep the plugin reference so callers can cast to their own type
    // and call setTag / getTag themselves OR we proxy through here.
    //
    // Pattern: we assume your VersionSupport is accessible as a static singleton
    // or through your main plugin. Adjust the two private helpers below to match.

    private final Plugin plugin;

    public MobOwnerTracker(Plugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Tag an entity as owned by a player.
     * Call this when a pet is tamed, a mob is summoned, etc.
     */
    public void tagOwner(Entity entity, Player owner) {
        nmsSetTag(entity, TAG_KEY, owner.getUniqueId().toString());
    }

    /**
     * Returns the owning Player, or null if the entity has no owner
     * or the owner is offline.
     */
    public Player getOwner(Entity entity) {
        String raw = nmsGetTag(entity, TAG_KEY);
        if (raw == null || raw.isEmpty()) return null;

        try {
            UUID ownerUUID = UUID.fromString(raw);
            return plugin.getServer().getPlayer(ownerUUID); // null if offline
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean hasOwner(Entity entity) {
        return getOwner(entity) != null;
    }

    public void clear() {
        // Tags live on entities themselves via NMS nothing to clean up in memory.
    }

    // NMS bridge swap these two methods to match your VersionSupport API

    /**
     * Replace with: YourVersionSupport.setTag(entity, key, value)
     */
    private void nmsSetTag(Entity entity, String key, String value) {
        // TODO: replace body with your VersionSupport call
        // Example: VersionSupport.getInstance().setTag(entity, key, value);
        entity.setMetadata(key, new org.bukkit.metadata.FixedMetadataValue(plugin, value));
    }

    /**
     * Replace with: YourVersionSupport.getTag(entity, key)
     */
    private String nmsGetTag(Entity entity, String key) {
        // TODO: replace body with your VersionSupport call
        // Example: return VersionSupport.getInstance().getTag(entity, key);
        if (!entity.hasMetadata(key)) return null;
        return entity.getMetadata(key).get(0).asString();
    }
}
