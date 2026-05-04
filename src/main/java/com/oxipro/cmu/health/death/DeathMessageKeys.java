package com.oxipro.cmu.health.death;

public final class DeathMessageKeys {

    private DeathMessageKeys() {}

    // ── Channel constants ──────────────────────────────────────────

    public static final class Channel {
        private Channel() {}
        public static final String CHAT      = "chat";
        public static final String TITLE     = "title";
        public static final String SUBTITLE  = "subtitle";
        public static final String ACTIONBAR = "actionbar";
        public static final String SOUND     = "sound";
    }

    // ── Scenario constants (mirrors DeathContext.Scenario names) ──

    public static final class Scenario {
        private Scenario() {}
        public static final String PVP                  = "pvp";
        public static final String MOB                  = "mob";
        public static final String PET                  = "pet";
        public static final String POTION_BY_PLAYER     = "potion_by_player";
        public static final String MAGIC                = "magic";
        public static final String PROJECTILE_INDIRECT  = "projectile_indirect";
        public static final String ENVIRONMENTAL        = "environmental";
        public static final String UNKNOWN              = "unknown";

        /** Converts a DeathContext.Scenario enum to its string key. */
        public static String from(com.oxipro.cmu.health.death.DeathContext.Scenario scenario) {
            switch (scenario) {
                case PVP:                 return PVP;
                case MOB:                 return MOB;
                case PET:                 return PET;
                case POTION_BY_PLAYER:    return POTION_BY_PLAYER;
                case MAGIC:               return MAGIC;
                case PROJECTILE_INDIRECT: return PROJECTILE_INDIRECT;
                case ENVIRONMENTAL:       return ENVIRONMENTAL;
                default:                  return UNKNOWN;
            }
        }
    }

    // ── Key builders ───────────────────────────────────────────────

    /**
     * Returns the key for the DEATH side (victim).
     * e.g. death("pvp", "chat") → "death.pvp.chat"
     */
    public static String death(String scenario, String channel) {
        return "death." + scenario + "." + channel;
    }

    /** Convenience overload accepting the enum directly. */
    public static String death(com.oxipro.cmu.health.death.DeathContext.Scenario scenario, String channel) {
        return death(Scenario.from(scenario), channel);
    }

    /**
     * Returns the key for the KILL side (killer — PVP / PET only).
     * e.g. kill("pvp", "title") → "kill.pvp.title"
     */
    public static String kill(String scenario, String channel) {
        return "kill." + scenario + "." + channel;
    }

    /** Convenience overload accepting the enum directly. */
    public static String kill(com.oxipro.cmu.health.death.DeathContext.Scenario scenario, String channel) {
        return kill(Scenario.from(scenario), channel);
    }

    // ── Pre-built constants for the most common keys ───────────────
    // (mirrors the pattern in your existing addon — ADDON_PREFIX excluded,
    //  the consumer plugin prepends its own prefix)

    // Death side
    public static final String DEATH_PVP_CHAT              = "death.pvp.chat";
    public static final String DEATH_PVP_TITLE             = "death.pvp.title";
    public static final String DEATH_PVP_SUBTITLE          = "death.pvp.subtitle";
    public static final String DEATH_PVP_ACTIONBAR         = "death.pvp.actionbar";
    public static final String DEATH_PVP_SOUND             = "death.pvp.sound";

    public static final String DEATH_MOB_CHAT              = "death.mob.chat";
    public static final String DEATH_MOB_TITLE             = "death.mob.title";
    public static final String DEATH_MOB_SUBTITLE          = "death.mob.subtitle";
    public static final String DEATH_MOB_ACTIONBAR         = "death.mob.actionbar";
    public static final String DEATH_MOB_SOUND             = "death.mob.sound";

    public static final String DEATH_PET_CHAT              = "death.pet.chat";
    public static final String DEATH_PET_TITLE             = "death.pet.title";
    public static final String DEATH_PET_SUBTITLE          = "death.pet.subtitle";
    public static final String DEATH_PET_ACTIONBAR         = "death.pet.actionbar";
    public static final String DEATH_PET_SOUND             = "death.pet.sound";

    public static final String DEATH_POTION_CHAT           = "death.potion_by_player.chat";
    public static final String DEATH_POTION_TITLE          = "death.potion_by_player.title";
    public static final String DEATH_POTION_SUBTITLE       = "death.potion_by_player.subtitle";
    public static final String DEATH_POTION_ACTIONBAR      = "death.potion_by_player.actionbar";
    public static final String DEATH_POTION_SOUND          = "death.potion_by_player.sound";

    public static final String DEATH_MAGIC_CHAT            = "death.magic.chat";
    public static final String DEATH_MAGIC_TITLE           = "death.magic.title";
    public static final String DEATH_MAGIC_SUBTITLE        = "death.magic.subtitle";
    public static final String DEATH_MAGIC_ACTIONBAR       = "death.magic.actionbar";
    public static final String DEATH_MAGIC_SOUND           = "death.magic.sound";

    public static final String DEATH_ENVIRONMENTAL_CHAT    = "death.environmental.chat";
    public static final String DEATH_ENVIRONMENTAL_TITLE   = "death.environmental.title";
    public static final String DEATH_ENVIRONMENTAL_SUBTITLE= "death.environmental.subtitle";
    public static final String DEATH_ENVIRONMENTAL_ACTIONBAR="death.environmental.actionbar";
    public static final String DEATH_ENVIRONMENTAL_SOUND   = "death.environmental.sound";

    public static final String DEATH_UNKNOWN_CHAT          = "death.unknown.chat";
    public static final String DEATH_UNKNOWN_TITLE         = "death.unknown.title";
    public static final String DEATH_UNKNOWN_SUBTITLE      = "death.unknown.subtitle";
    public static final String DEATH_UNKNOWN_ACTIONBAR     = "death.unknown.actionbar";
    public static final String DEATH_UNKNOWN_SOUND         = "death.unknown.sound";

    // Kill side (only PVP and PET have a meaningful killer)
    public static final String KILL_PVP_TITLE              = "kill.pvp.title";
    public static final String KILL_PVP_SUBTITLE           = "kill.pvp.subtitle";
    public static final String KILL_PVP_ACTIONBAR          = "kill.pvp.actionbar";
    public static final String KILL_PVP_SOUND              = "kill.pvp.sound";

    public static final String KILL_PET_TITLE              = "kill.pet.title";
    public static final String KILL_PET_SUBTITLE           = "kill.pet.subtitle";
    public static final String KILL_PET_ACTIONBAR          = "kill.pet.actionbar";
    public static final String KILL_PET_SOUND              = "kill.pet.sound";
}
