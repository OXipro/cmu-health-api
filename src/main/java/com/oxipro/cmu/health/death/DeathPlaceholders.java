package com.oxipro.cmu.health.death;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DeathPlaceholders {

    private DeathPlaceholders() {}

    /**
     * Returns an ordered map of placeholder → resolved value.
     * All values are non-null (unknown fields default to "?").
     */
    public static Map<String, String> resolve(DeathContext ctx) {
        Map<String, String> map = new LinkedHashMap<>();

        // Victim
        map.put("{victim}",       ctx.getVictim().getDisplayName());
        map.put("{victim_name}",  ctx.getVictim().getName());

        // Killer player (PVP / PET)
        String killerDisplay = ctx.getKillerPlayer() != null
                ? ctx.getKillerPlayer().getDisplayName() : "?";
        String killerName = ctx.getKillerPlayer() != null
                ? ctx.getKillerPlayer().getName() : "?";
        map.put("{killer}",       killerDisplay);
        map.put("{killer_name}",  killerName);

        // Mob
        EntityType mobType = ctx.getMobType();
        String mobRaw      = mobType != null ? mobType.name() : "?";
        map.put("{mob}",          mobType != null ? formatEntityType(mobRaw) : "?");
        map.put("{mob_raw}",      mobRaw);

        // Cause
        EntityDamageEvent.DamageCause cause = ctx.getCause();
        map.put("{cause}",        cause != null ? friendlyCause(cause.name()) : "?");
        map.put("{cause_raw}",    cause != null ? cause.name() : "?");

        // Damage
        map.put("{damage}",       String.format("%.1f", ctx.getFinalDamage()));

        // Scenario
        map.put("{scenario}",     DeathMessageKeys.Scenario.from(ctx.getScenario()));

        return map;
    }

    /**
     * Applies all resolved placeholders to a raw message string.
     * Works with any format: §-codes, MiniMessage tags, raw text.
     *
     * The consumer plugin calls this AFTER its own language system
     * has returned the template string.
     *
     * Example:
     * <pre>
     *   String template = lang.get(ADDON_PREFIX + DeathMessageKeys.DEATH_PVP_CHAT);
     *   String ready    = DeathPlaceholders.apply(template, ctx);
     *   player.sendMessage(ready);
     * </pre>
     */
    public static String apply(String template, DeathContext ctx) {
        if (template == null) return "";
        Map<String, String> values = resolve(ctx);
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    // ── Helpers ────────────────────────────────────────────────────

    /** "CAVE_SPIDER" → "Cave Spider" */
    private static String formatEntityType(String raw) {
        String[] parts = raw.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    /** "FIRE_TICK" → "fire tick" */
    private static String friendlyCause(String raw) {
        return raw.toLowerCase().replace('_', ' ');
    }
}