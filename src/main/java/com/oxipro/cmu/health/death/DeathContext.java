package com.oxipro.cmu.health.death;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class DeathContext {
    /** All possible death scenarios. */
    public enum Scenario {
        /** Killed by another player in melee or with a bow */
        PVP,
        /** Killed by a mob that was NOT owned by a player */
        MOB,
        /** Killed by a mob owned/summoned by another player */
        PET,
        /** Killed by a potion thrown or splashed by a player */
        POTION_BY_PLAYER,
        /** Killed by indirect magic (wither skull, evoker fang, etc.) */
        MAGIC,
        /** Projectile whose shooter could not be resolved to a player/mob we know */
        PROJECTILE_INDIRECT,
        /** Fall, fire, lava, void, drowning, starvation, etc. */
        ENVIRONMENTAL,
        /** Catch-all when nothing matches */
        UNKNOWN
    }

    private final Player victim;
    private final Scenario   scenario;
    private final EntityDamageEvent.DamageCause cause;
    /** The entity that dealt the killing blow (may be null for environmental). */
    private final Entity killerEntity;
    /** If scenario is PVP or PET, the owning player. */
    private final Player     killerPlayer;
    /** Entity type of the mob (for MOB / PET scenarios). */
    private final EntityType mobType;
    private final double     finalDamage;
    private final long       timestamp;

    private DeathContext(Builder b) {
        this.victim       = b.victim;
        this.scenario     = b.scenario;
        this.cause        = b.cause;
        this.killerEntity = b.killerEntity;
        this.killerPlayer = b.killerPlayer;
        this.mobType      = b.mobType;
        this.finalDamage  = b.finalDamage;
        this.timestamp    = System.currentTimeMillis();
    }

    // ── Accessors ──────────────────────────────────────────────────

    public Player     getVictim()       { return victim; }
    public Scenario   getScenario()     { return scenario; }
    public EntityDamageEvent.DamageCause getCause()       { return cause; }
    public Entity     getKillerEntity() { return killerEntity; }
    public Player     getKillerPlayer() { return killerPlayer; }
    public EntityType getMobType()      { return mobType; }
    public double     getFinalDamage()  { return finalDamage; }
    public long       getTimestamp()    { return timestamp; }

    // ── Builder ────────────────────────────────────────────────────

    public static class Builder {
        private final Player victim;
        private Scenario   scenario   = Scenario.UNKNOWN;
        private EntityDamageEvent.DamageCause cause     = EntityDamageEvent.DamageCause.CUSTOM;
        private Entity     killerEntity;
        private Player     killerPlayer;
        private EntityType mobType;
        private double     finalDamage;

        public Builder(Player victim)                  { this.victim = victim; }
        public Builder scenario(Scenario s)            { this.scenario = s; return this; }
        public Builder cause(EntityDamageEvent.DamageCause c)            { this.cause = c; return this; }
        public Builder killerEntity(Entity e)          { this.killerEntity = e; return this; }
        public Builder killerPlayer(Player p)          { this.killerPlayer = p; return this; }
        public Builder mobType(EntityType t)           { this.mobType = t; return this; }
        public Builder finalDamage(double d)           { this.finalDamage = d; return this; }
        public DeathContext build()                    { return new DeathContext(this); }
    }
}
