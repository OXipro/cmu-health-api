package com.oxipro.cmu.health.utils;

import com.oxipro.cmu.versionsupport.PlayerUtilsSupport;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class TotemUtil {

    private final PlayerUtilsSupport playerUtilsSupport;

    public TotemUtil(PlayerUtilsSupport playerUtilsSupport) {
        this.playerUtilsSupport = playerUtilsSupport;
    }

    // Super sketchy way to know if a player has a totem and would save the death from it
    public boolean wouldVanillaTotemSave(Player player, EntityDamageEvent.DamageCause cause) {
        return hasTotemOfUndying(player) && totemProtectsAgainst(cause);
    }

    public boolean hasTotemOfUndying(Player player) {
        if (player == null) return false;

        PlayerInventory inventory = player.getInventory();

        if (isTotem(inventory.getItemInHand())) return true;
        return isTotem(playerUtilsSupport.getOffHandItem(player));
    }

    private boolean isTotem(ItemStack item) {
        if (item == null) return false;

        String name = item.getType().name();
        return "TOTEM".equals(name) || "TOTEM_OF_UNDYING".equals(name);
    }

    private boolean totemProtectsAgainst(EntityDamageEvent.DamageCause cause) {
        if (cause == null) {
            return true;
        }
        if (cause == EntityDamageEvent.DamageCause.VOID) {
            return false;
        }
        String name = cause.name();
        return !"SUICIDE".equals(name) && !"KILL".equals(name) && !"WORLD_BORDER".equals(name);
    }


}
