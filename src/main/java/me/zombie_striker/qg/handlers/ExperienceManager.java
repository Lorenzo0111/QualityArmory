package me.zombie_striker.qg.handlers;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Objects;

import org.bukkit.entity.Player;

/**
 * @author desht Adapted from ExperienceUtils code originally in
 *         ScrollingMenuSign. Credit to nisovin
 *         (http://forums.bukkit.org/threads/experienceutils-make-giving-taking-exp-a-bit-more-intuitive.54450/#post-1067480)
 *         for an implementation that avoids the problems of
 *         getTotalExperience(), which doesn't work properly after a player has
 *         enchanted something. Credit to comphenix for further contributions:
 *         See
 *         http://forums.bukkit.org/threads/experiencemanager-was-experienceutils-make-giving-taking-exp-a-bit-more-intuitive.54450/page-3#post-1273622
 */
public class ExperienceManager {
    // this is to stop the lookup table growing without control
    private static int hardMaxLevel = 100000;

    private static int xpTotalToReachLevel[];

    private final WeakReference<Player> player;
    private final String playerName;

    static {
        // 25 is an arbitrary value for the initial table size - the actual
        // value isn't critically important since the table is resized as needed.
        ExperienceManager.initLookupTables(25);
    }

    /**
     * Create a new ExperienceManager for the given player.
     *
     * @param player the player for this ExperienceManager object
     * @throws IllegalArgumentException if the player is null
     */
    public ExperienceManager(final Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        this.player = new WeakReference<Player>(player);
        this.playerName = player.getName();
    }

    /**
     * Get the current hard max level for which calculations will be done.
     *
     * @return the current hard max level
     */
    public static int getHardMaxLevel() { return ExperienceManager.hardMaxLevel; }

    /**
     * Set the current hard max level for which calculations will be done.
     *
     * @param hardMaxLevel the new hard max level
     */
    public static void setHardMaxLevel(final int hardMaxLevel) { ExperienceManager.hardMaxLevel = hardMaxLevel; }

    /**
     * Initialize the XP lookup table. See http://minecraft.gamepedia.com/Experience
     *
     * @param maxLevel The highest level handled by the lookup tables
     */
    private static void initLookupTables(final int maxLevel) {
        ExperienceManager.xpTotalToReachLevel = new int[maxLevel];

        for (int i = 0; i < ExperienceManager.xpTotalToReachLevel.length; i++) {
            ExperienceManager.xpTotalToReachLevel[i] = i >= 30 ? (int) (3.5 * i * i - 151.5 * i + 2220)
                    : i >= 16 ? (int) (1.5 * i * i - 29.5 * i + 360) : 17 * i;
        }
    }

    /**
     * Calculate the level that the given XP quantity corresponds to, without using
     * the lookup tables. This is needed if getLevelForExp() is called with an XP
     * quantity beyond the range of the existing lookup tables.
     *
     * @param exp
     * @return
     */
    private static int calculateLevelForExp(final int exp) {
        int level = 0;
        int curExp = 7; // level 1
        int incr = 10;

        while (curExp <= exp) {
            curExp += incr;
            level++;
            incr += (level % 2 == 0) ? 3 : 4;
        }
        return level;
    }

    /**
     * Get the Player associated with this ExperienceManager.
     *
     * @return the Player object
     * @throws IllegalStateException if the player is no longer online
     */
    public Player getPlayer() {
        final Player p = this.player.get();
        if (p == null) {
            throw new IllegalStateException("Player " + this.playerName + " is not online");
        }
        return p;
    }

    /**
     * Adjust the player's XP by the given amount in an intelligent fashion. Works
     * around some of the non-intuitive behaviour of the basic Bukkit
     * player.giveExp() method.
     *
     * @param amt Amount of XP, may be negative
     */
    public void changeExp(final int amt) { this.changeExp((double) amt); }

    /**
     * Adjust the player's XP by the given amount in an intelligent fashion. Works
     * around some of the non-intuitive behaviour of the basic Bukkit
     * player.giveExp() method.
     *
     * @param amt Amount of XP, may be negative
     */
    public void changeExp(final double amt) { this.setExp(this.getCurrentFractionalXP(), amt); }

    /**
     * Set the player's experience
     *
     * @param amt Amount of XP, should not be negative
     */
    public void setExp(final int amt) { this.setExp(0, amt); }

    /**
     * Set the player's fractional experience.
     *
     * @param amt Amount of XP, should not be negative
     */
    public void setExp(final double amt) { this.setExp(0, amt); }

    private void setExp(final double base, final double amt) {
        final int xp = (int) Math.max(base + amt, 0);

        final Player player = this.getPlayer();
        final int curLvl = player.getLevel();
        final int newLvl = this.getLevelForExp(xp);

        // Increment level
        if (curLvl != newLvl) {
            player.setLevel(newLvl);
        }
        // Increment total experience - this should force the server to send an update
        // packet
        if (xp > base) {
            player.setTotalExperience(player.getTotalExperience() + xp - (int) base);
        }

        final double pct = (base - this.getXpForLevel(newLvl) + amt) / (double) (this.getXpNeededToLevelUp(newLvl));
        player.setExp((float) pct);
    }

    /**
     * Get the player's current XP total.
     *
     * @return the player's total XP
     */
    public int getCurrentExp() {
        final Player player = this.getPlayer();

        final int lvl = player.getLevel();
        final int cur = this.getXpForLevel(lvl) + (int) Math.round(this.getXpNeededToLevelUp(lvl) * player.getExp());
        return cur;
    }

    /**
     * Get the player's current fractional XP.
     *
     * @return The player's total XP with fractions.
     */
    private double getCurrentFractionalXP() {
        final Player player = this.getPlayer();

        final int lvl = player.getLevel();
        final double cur = this.getXpForLevel(lvl) + (double) (this.getXpNeededToLevelUp(lvl) * player.getExp());
        return cur;
    }

    /**
     * Checks if the player has the given amount of XP.
     *
     * @param amt The amount to check for.
     * @return true if the player has enough XP, false otherwise
     */
    public boolean hasExp(final int amt) { return this.getCurrentExp() >= amt; }

    /**
     * Checks if the player has the given amount of fractional XP.
     *
     * @param amt The amount to check for.
     * @return true if the player has enough XP, false otherwise
     */
    public boolean hasExp(final double amt) { return this.getCurrentFractionalXP() >= amt; }

    /**
     * Get the level that the given amount of XP falls within.
     *
     * @param exp the amount to check for
     * @return the level that a player with this amount total XP would be
     * @throws IllegalArgumentException if the given XP is less than 0
     */
    public int getLevelForExp(final int exp) {
        if (exp <= 0) {
            return 0;
        }
        if (exp > ExperienceManager.xpTotalToReachLevel[ExperienceManager.xpTotalToReachLevel.length - 1]) {
            // need to extend the lookup tables
            final int newMax = ExperienceManager.calculateLevelForExp(exp) * 2;
            if (newMax > ExperienceManager.hardMaxLevel)
                throw new IllegalStateException("Level for exp " + exp + " > hard max level " + ExperienceManager.hardMaxLevel);
            ExperienceManager.initLookupTables(newMax);
        }
        final int pos = Arrays.binarySearch(ExperienceManager.xpTotalToReachLevel, exp);
        return pos < 0 ? -pos - 2 : pos;
    }

    /**
     * Retrieves the amount of experience the experience bar can hold at the given
     * level.
     *
     * @param level the level to check
     * @return the amount of experience at this level in the level bar
     * @throws IllegalArgumentException if the level is less than 0
     */
    public int getXpNeededToLevelUp(final int level) {
        if (level < 0)
            throw new IllegalStateException("Level may not be negative.");
        return level > 30 ? 62 + (level - 30) * 7 : level >= 16 ? 17 + (level - 15) * 3 : 17;
    }

    /**
     * Return the total XP needed to be the given level.
     *
     * @param level The level to check for.
     * @return The amount of XP needed for the level.
     * @throws IllegalArgumentException if the level is less than 0 or greater than
     *                                      the current hard maximum
     */
    public int getXpForLevel(final int level) {
        if (!(level >= 0 && level <= ExperienceManager.hardMaxLevel))
            throw new IllegalStateException("Invalid level " + level + "(must be in range 0.." + ExperienceManager.hardMaxLevel + ")");
        if (level >= ExperienceManager.xpTotalToReachLevel.length) {
            ExperienceManager.initLookupTables(level * 2);
        }
        return ExperienceManager.xpTotalToReachLevel[level];
    }
}