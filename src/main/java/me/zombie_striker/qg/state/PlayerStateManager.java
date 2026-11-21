package me.zombie_striker.qg.state;

import me.zombie_striker.qg.guns.utils.GunRefillerRunnable;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Transient per-player state. Collections are concurrent, since Folia runs players on different threads.
 Everything that only matters while a player is online is removed in
 * {@link #onQuit(UUID)}, and running tasks are cancelled.
 * <p>
 * Bleeding and scoping are deliberately kept across a relog: a player who logs out while bleeding is still
 * bleeding when they come back, and the scoping marker is what removes the zoom effects they still have.
 */
public final class PlayerStateManager {
    private final Map<UUID, Long> lastWeaponSwitch = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastMoveLocation = new ConcurrentHashMap<>();
    private final Map<UUID, List<GunRefillerRunnable>> reloadingTasks = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> rapidFireTasks = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> burstFireTasks = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> delayedBurstFireTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Double> recoilCounter = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> slotBeforeIronsights = new ConcurrentHashMap<>();
    private final Set<UUID> scoping = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Double> bleedoutMultiplier = new ConcurrentHashMap<>();
    private final Map<UUID, Double> bloodLevel = new ConcurrentHashMap<>();

    /**
     * @return The time (millis) each player last switched weapon.
     */
    public Map<UUID, Long> getLastWeaponSwitch() {
        return lastWeaponSwitch;
    }

    /**
     * @return The last location each player moved to, used to apply recoil relative to movement.
     */
    public Map<UUID, Location> getLastMoveLocation() {
        return lastMoveLocation;
    }

    public Map<UUID, List<GunRefillerRunnable>> getReloadingTasks() {
        return reloadingTasks;
    }

    public Map<UUID, BukkitTask> getRapidFireTasks() {
        return rapidFireTasks;
    }

    public Map<UUID, BukkitTask> getBurstFireTasks() {
        return burstFireTasks;
    }

    public Map<UUID, BukkitTask> getDelayedBurstFireTasks() {
        return delayedBurstFireTasks;
    }

    /**
     * @return Recoil accumulated by guns with high recoil, applied when the burst ends.
     */
    public Map<UUID, Double> getRecoilCounter() {
        return recoilCounter;
    }

    /**
     * @return The hotbar slot each player had selected before aiming, to restore the offhand item.
     */
    public Map<UUID, Integer> getSlotBeforeIronsights() {
        return slotBeforeIronsights;
    }

    /**
     * @return Players with zoom or night vision effects from a scope.
     */
    public Set<UUID> getScoping() {
        return scoping;
    }

    public Map<UUID, Double> getBleedoutMultiplier() {
        return bleedoutMultiplier;
    }

    public Map<UUID, Double> getBloodLevel() {
        return bloodLevel;
    }

    /**
     * Removes the state that only matters while the player is online and cancels their tasks.
     */
    public void onQuit(UUID player) {
        lastWeaponSwitch.remove(player);
        lastMoveLocation.remove(player);
        recoilCounter.remove(player);
        slotBeforeIronsights.remove(player);
        cancel(rapidFireTasks.remove(player));
        cancel(burstFireTasks.remove(player));
        cancel(delayedBurstFireTasks.remove(player));

        List<GunRefillerRunnable> reloads = reloadingTasks.remove(player);
        if (reloads != null) {
            for (GunRefillerRunnable reload : reloads)
                cancel(reload.getTask());
        }
    }

    /**
     * Cancels every task and clears all state, when the plugin is disabled.
     */
    public void shutdown() {
        for (UUID player : new ArrayList<>(reloadingTasks.keySet()))
            onQuit(player);
        for (Map<UUID, BukkitTask> tasks : Arrays.asList(rapidFireTasks, burstFireTasks, delayedBurstFireTasks)) {
            for (BukkitTask task : tasks.values())
                cancel(task);
            tasks.clear();
        }
        lastWeaponSwitch.clear();
        lastMoveLocation.clear();
        recoilCounter.clear();
        slotBeforeIronsights.clear();
        scoping.clear();
        bleedoutMultiplier.clear();
        bloodLevel.clear();
    }

    private static void cancel(BukkitTask task) {
        if (task != null)
            task.cancel();
    }
}
