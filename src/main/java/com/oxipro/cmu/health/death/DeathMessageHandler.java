package com.oxipro.cmu.health.death;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Loads messages from messages.yml (or a FileConfiguration provided by another plugin)
 * and resolves the correct death message for a DeathContext.
 *
 * Placeholders supported in messages.yml values:
 *   {victim}   → victim's name
 *   {killer}   → killer player's name  (PVP / PET)
 *   {mob}      → mob type name          (MOB / PET)
 *   {cause}    → DamageCause enum name  (environmental)
 */
public class DeathMessageHandler {

    private final Plugin plugin;
    private FileConfiguration messages;

    public DeathMessageHandler(Plugin plugin) {
        this.plugin = plugin;
        loadDefaultMessages();
    }

    // ── Loading ────────────────────────────────────────────────────

    private void loadDefaultMessages() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);

        // Merge with bundled defaults so missing keys are always covered
        InputStream defaults = plugin.getResource("messages.yml");
        if (defaults != null) {
            YamlConfiguration bundled = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaults, StandardCharsets.UTF_8));
            messages.setDefaults(bundled);
        }
    }

    /**
     * External plugins can inject their own FileConfiguration
     * if they want to fully control the messages.
     */
    public void setMessagesConfig(FileConfiguration config) {
        this.messages = config;
    }

    public void reload() {
        loadDefaultMessages();
    }

    /**
     * Sends the death message to the entire server (broadcast).
     *
     * @param ctx             the death context
     * @param overrideMessage if non-null, used instead of the yml template
     */
    public void sendDeathMessage(DeathContext ctx, String overrideMessage) {
        String raw = (overrideMessage != null) ? overrideMessage : resolveTemplate(ctx);
        if (raw == null || raw.equalsIgnoreCase("none")) return;
        String formatted = applyPlaceholders(raw, ctx);
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', formatted));
    }

    /** Returns the formatted message string without sending it. Useful for other plugins. */
    public String buildMessage(DeathContext ctx) {
        String raw = resolveTemplate(ctx);
        if (raw == null) return "";
        return ChatColor.translateAlternateColorCodes('&', applyPlaceholders(raw, ctx));
    }

    // ── Internal helpers ───────────────────────────────────────────

    private String resolveTemplate(DeathContext ctx) {
        DeathContext.Scenario s = ctx.getScenario();
        switch (s) {
            case PVP:
                return get("death.pvp");
            case MOB:
                return get("death.mob");
            case PET:
                return get("death.pet");
            case POTION_BY_PLAYER:
                return get("death.potion_by_player");
            case MAGIC:
                return get("death.magic");
            case PROJECTILE_INDIRECT:
                return get("death.projectile_indirect");
            case ENVIRONMENTAL:
                return resolveEnvironmental(ctx);
            default:
                return get("death.unknown");
        }
    }

    private String resolveEnvironmental(DeathContext ctx) {
        // Try a cause-specific key first, fall back to generic
        String causeKey = "death.environmental." + ctx.getCause().name().toLowerCase();
        String specific = messages.getString(causeKey);
        return (specific != null) ? specific : get("death.environmental.generic");
    }

    private String applyPlaceholders(String template, DeathContext ctx) {
        String result = template;

        // {victim}
        result = result.replace("{victim}", ctx.getVictim().getName());

        // {killer}
        Player kp = ctx.getKillerPlayer();
        result = result.replace("{killer}", kp != null ? kp.getName() : "Unknown");

        // {mob}
        String mob = ctx.getMobType() != null
                ? formatEntityType(ctx.getMobType().name())
                : "Unknown";
        result = result.replace("{mob}", mob);

        // {cause}
        result = result.replace("{cause}", friendlyCause(ctx.getCause().name()));

        return result;
    }

    private String get(String path) {
        return messages.getString(path, "&c{victim} &7died.");
    }

    /** "CAVE_SPIDER" → "Cave Spider" */
    private String formatEntityType(String name) {
        String[] parts = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    private String friendlyCause(String cause) {
        return cause.toLowerCase().replace('_', ' ');
    }
}
