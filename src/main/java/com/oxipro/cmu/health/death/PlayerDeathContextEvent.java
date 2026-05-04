package com.oxipro.cmu.health.death;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;

public class PlayerDeathContextEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DeathContext context;
    private final Map<String, String> placeholders;
    private boolean cancelled = false;

    public PlayerDeathContextEvent(DeathContext context) {
        this.context      = context;
        this.placeholders = DeathPlaceholders.resolve(context);
    }

    // ── Context ────────────────────────────────────────────────────

    public DeathContext getContext() { return context; }

    // ── Key helpers ────────────────────────────────────────────────

    /**
     * Returns the standard death-side message key for the given channel.
     * Prepend your ADDON_PREFIX before looking up in your lang system.
     *
     * @param channel one of {@link DeathMessageKeys.Channel} constants
     * @return e.g. "death.pvp.chat", "death.mob.actionbar"
     */
    public String getDeathKey(String channel) {
        return DeathMessageKeys.death(context.getScenario(), channel);
    }

    /**
     * Returns the standard kill-side message key for the given channel.
     * Only meaningful when {@code getContext().getKillerPlayer() != null}.
     *
     * @param channel one of {@link DeathMessageKeys.Channel} constants
     * @return e.g. "kill.pvp.title", "kill.pet.actionbar"
     */
    public String getKillKey(String channel) {
        return DeathMessageKeys.kill(context.getScenario(), channel);
    }

    // ── Placeholder helpers ────────────────────────────────────────

    /**
     * Pre-resolved placeholder map.
     * Keys: {victim}, {killer}, {mob}, {mob_raw}, {cause}, {cause_raw}, {damage}, {scenario}
     * Useful if your lang system applies replacements itself (PAPI, MiniMessage resolver…).
     */
    public Map<String, String> getPlaceholders() { return placeholders; }

    /**
     * One-shot: applies all placeholders to a raw template string.
     * Compatible with §-codes, MiniMessage, or plain text —
     * just pass the raw string from your lang system.
     *
     * @param template raw message from your lang system (may be null → returns "")
     * @return         template with {placeholder} values substituted
     */
    public String applyPlaceholders(String template) {
        return DeathPlaceholders.apply(template, context);
    }

    // ── Cancellable ────────────────────────────────────────────────

    @Override public boolean isCancelled()        { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers()    { return HANDLERS; }
    public static HandlerList getHandlerList()    { return HANDLERS; }
}
