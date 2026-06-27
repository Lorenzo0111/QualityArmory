package me.zombie_striker.qg.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Drop-in replacement for BukkitRunnable with Folia support.
 *
 * On Folia: sync tasks use GlobalRegionScheduler, async uses AsyncScheduler.
 * On Bukkit/Spigot/Paper: delegates to Bukkit.getScheduler().
 *
 * Entity-aware variants (runTaskLater(plugin, entity, delay)) use the
 * EntityScheduler on Folia for thread-safe entity access.
 *
 * Usage: replace "new BukkitRunnable()" with "new FoliaRunnable()".
 * For direct Bukkit.getScheduler() calls, use FoliaRunnable.runTask() static helpers.
 */
public abstract class FoliaRunnable implements Runnable {

    private static final boolean IS_FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;
    }

    private volatile Object task;
    private volatile boolean cancelled = false;

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public boolean isCancelled() {
        if (!IS_FOLIA && task instanceof BukkitTask) {
            return ((BukkitTask) task).isCancelled();
        }
        return cancelled;
    }

    public void cancel() {
        cancelled = true;
        if (task == null) return;
        if (!IS_FOLIA) {
            if (task instanceof BukkitTask) ((BukkitTask) task).cancel();
        } else {
            try {
                task.getClass().getMethod("cancel").invoke(task);
            } catch (Exception ignored) {}
        }
    }

    private void setTask(Object t) {
        this.task = t;
    }

    // ===================== Sync (global region on Folia) =====================

    public BukkitTask runTask(Plugin plugin) {
        if (!IS_FOLIA) {
            BukkitTask t = Bukkit.getScheduler().runTask(plugin, this);
            setTask(t);
            return t;
        }
        return scheduleGlobal(plugin, 1, -1);
    }

    public BukkitTask runTaskLater(Plugin plugin, long delay) {
        if (!IS_FOLIA) {
            BukkitTask t = Bukkit.getScheduler().runTaskLater(plugin, this, delay);
            setTask(t);
            return t;
        }
        return scheduleGlobal(plugin, Math.max(1, delay), -1);
    }

    public BukkitTask runTaskTimer(Plugin plugin, long delay, long period) {
        if (!IS_FOLIA) {
            BukkitTask t = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
            setTask(t);
            return t;
        }
        return scheduleGlobal(plugin, Math.max(1, delay), period);
    }

    // ===================== Async =====================

    public BukkitTask runTaskAsynchronously(Plugin plugin) {
        if (!IS_FOLIA) {
            BukkitTask t = Bukkit.getScheduler().runTaskAsynchronously(plugin, this);
            setTask(t);
            return t;
        }
        return scheduleAsync(plugin, 0, -1);
    }

    public BukkitTask runTaskLaterAsynchronously(Plugin plugin, long delay) {
        if (!IS_FOLIA) {
            BukkitTask t = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay);
            setTask(t);
            return t;
        }
        return scheduleAsync(plugin, delay, -1);
    }

    public BukkitTask runTaskTimerAsynchronously(Plugin plugin, long delay, long period) {
        if (!IS_FOLIA) {
            BukkitTask t = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
            setTask(t);
            return t;
        }
        return scheduleAsync(plugin, delay, period);
    }

    // ===================== Entity-aware (EntityScheduler on Folia) =====================

    public BukkitTask runTask(Plugin plugin, Entity entity) {
        if (!IS_FOLIA) return runTask(plugin);
        return scheduleEntity(plugin, entity, 1, -1);
    }

    public BukkitTask runTaskLater(Plugin plugin, Entity entity, long delay) {
        if (!IS_FOLIA) return runTaskLater(plugin, delay);
        return scheduleEntity(plugin, entity, Math.max(1, delay), -1);
    }

    public BukkitTask runTaskTimer(Plugin plugin, Entity entity, long delay, long period) {
        if (!IS_FOLIA) return runTaskTimer(plugin, delay, period);
        return scheduleEntity(plugin, entity, Math.max(1, delay), period);
    }

    // ===================== Location-aware (RegionScheduler on Folia) =====================

    public BukkitTask runTask(Plugin plugin, Location location) {
        if (!IS_FOLIA) return runTask(plugin);
        return scheduleRegion(plugin, location, 1, -1);
    }

    public BukkitTask runTaskLater(Plugin plugin, Location location, long delay) {
        if (!IS_FOLIA) return runTaskLater(plugin, delay);
        return scheduleRegion(plugin, location, Math.max(1, delay), -1);
    }

    public BukkitTask runTaskTimer(Plugin plugin, Location location, long delay, long period) {
        if (!IS_FOLIA) return runTaskTimer(plugin, delay, period);
        return scheduleRegion(plugin, location, Math.max(1, delay), period);
    }

    // ===================== Static helper methods =====================

    /** Run a task on the next tick (or global region tick on Folia). */
    public static void runTask(Plugin plugin, Runnable task) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().runTask(plugin, task);
            return;
        }
        invokeGlobal(plugin, task, 1, -1);
    }

    /** Run a task after a delay. */
    public static void runTaskLater(Plugin plugin, Runnable task, long delay) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
            return;
        }
        invokeGlobal(plugin, task, Math.max(1, delay), -1);
    }

    /** Run a task asynchronously. */
    public static void runTaskAsync(Plugin plugin, Runnable task) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            return;
        }
        invokeAsync(plugin, task, 0, -1);
    }

    /** Run a task asynchronously after a tick delay. */
    public static void runTaskAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
            return;
        }
        invokeAsync(plugin, task, delayTicks, -1);
    }

    /** Run on an entity's regional thread (falls back to global on Bukkit). */
    public static void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().runTask(plugin, task);
            return;
        }
        invokeEntity(plugin, entity, task, 1, -1);
    }

    /** Run delayed on an entity's regional thread. */
    public static void runEntityTaskLater(Plugin plugin, Entity entity, Runnable task, long delay) {
        if (!IS_FOLIA) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
            return;
        }
        invokeEntity(plugin, entity, task, Math.max(1, delay), -1);
    }

    // ===================== Private scheduling implementations =====================

    private BukkitTask scheduleGlobal(Plugin plugin, long delay, long period) {
        Consumer<Object> consumer = t -> { setTask(t); if (!cancelled) run(); };
        Object st = invokeGlobal(plugin, consumer, delay, period);
        if (st != null) setTask(st);
        return new FoliaTaskWrapper(st);
    }

    private BukkitTask scheduleAsync(Plugin plugin, long delayTicks, long periodTicks) {
        Consumer<Object> consumer = t -> { setTask(t); if (!cancelled) run(); };
        Object st = invokeAsync(plugin, consumer, delayTicks, periodTicks);
        if (st != null) setTask(st);
        return new FoliaTaskWrapper(st);
    }

    private BukkitTask scheduleEntity(Plugin plugin, Entity entity, long delay, long period) {
        Consumer<Object> consumer = t -> { setTask(t); if (!cancelled) run(); };
        Object st = invokeEntity(plugin, entity, consumer, delay, period);
        if (st != null) setTask(st);
        return new FoliaTaskWrapper(st);
    }

    private BukkitTask scheduleRegion(Plugin plugin, Location location, long delay, long period) {
        Consumer<Object> consumer = t -> { setTask(t); if (!cancelled) run(); };
        Object st = invokeRegion(plugin, location, consumer, delay, period);
        if (st != null) setTask(st);
        return new FoliaTaskWrapper(st);
    }

    private static Object invokeGlobal(Plugin plugin, Object consumer, long delay, long period) {
        try {
            Object scheduler = plugin.getServer().getClass()
                    .getMethod("getGlobalRegionScheduler").invoke(plugin.getServer());
            if (period >= 0) {
                return scheduler.getClass()
                        .getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class)
                        .invoke(scheduler, plugin, consumer, delay, period);
            } else if (delay > 1) {
                return scheduler.getClass()
                        .getMethod("runDelayed", Plugin.class, Consumer.class, long.class)
                        .invoke(scheduler, plugin, consumer, delay);
            } else {
                return scheduler.getClass()
                        .getMethod("run", Plugin.class, Consumer.class)
                        .invoke(scheduler, plugin, consumer);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[QualityArmory] Failed to schedule global Folia task: " + e.getMessage());
            return null;
        }
    }

    private static void invokeGlobal(Plugin plugin, Runnable task, long delay, long period) {
        invokeGlobal(plugin, (Consumer<Object>) t -> task.run(), delay, period);
    }

    private static Object invokeAsync(Plugin plugin, Object consumer, long delayTicks, long periodTicks) {
        try {
            Object scheduler = plugin.getServer().getClass()
                    .getMethod("getAsyncScheduler").invoke(plugin.getServer());
            if (periodTicks >= 0) {
                return scheduler.getClass()
                        .getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class)
                        .invoke(scheduler, plugin, consumer,
                                Math.max(1, delayTicks) * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS);
            } else if (delayTicks > 0) {
                return scheduler.getClass()
                        .getMethod("runDelayed", Plugin.class, Consumer.class, long.class, TimeUnit.class)
                        .invoke(scheduler, plugin, consumer, delayTicks * 50L, TimeUnit.MILLISECONDS);
            } else {
                return scheduler.getClass()
                        .getMethod("runNow", Plugin.class, Consumer.class)
                        .invoke(scheduler, plugin, consumer);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[QualityArmory] Failed to schedule async Folia task: " + e.getMessage());
            return null;
        }
    }

    private static void invokeAsync(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        invokeAsync(plugin, (Consumer<Object>) t -> task.run(), delayTicks, periodTicks);
    }

    private static Object invokeEntity(Plugin plugin, Entity entity, Object consumer, long delay, long period) {
        try {
            Object scheduler = entity.getClass().getMethod("getScheduler").invoke(entity);
            if (period >= 0) {
                return scheduler.getClass()
                        .getMethod("runAtFixedRate", Plugin.class, Consumer.class, Runnable.class, long.class, long.class)
                        .invoke(scheduler, plugin, consumer, (Runnable) null, delay, period);
            } else if (delay > 1) {
                return scheduler.getClass()
                        .getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class)
                        .invoke(scheduler, plugin, consumer, (Runnable) null, delay);
            } else {
                return scheduler.getClass()
                        .getMethod("run", Plugin.class, Consumer.class, Runnable.class)
                        .invoke(scheduler, plugin, consumer, (Runnable) null);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[QualityArmory] Failed to schedule entity Folia task: " + e.getMessage());
            return null;
        }
    }

    private static void invokeEntity(Plugin plugin, Entity entity, Runnable task, long delay, long period) {
        invokeEntity(plugin, entity, (Consumer<Object>) t -> task.run(), delay, period);
    }

    private static Object invokeRegion(Plugin plugin, Location location, Object consumer, long delay, long period) {
        try {
            Object scheduler = plugin.getServer().getClass()
                    .getMethod("getRegionScheduler").invoke(plugin.getServer());
            if (period >= 0) {
                return scheduler.getClass()
                        .getMethod("runAtFixedRate", Plugin.class, Location.class, Consumer.class, long.class, long.class)
                        .invoke(scheduler, plugin, location, consumer, delay, period);
            } else if (delay > 1) {
                return scheduler.getClass()
                        .getMethod("runDelayed", Plugin.class, Location.class, Consumer.class, long.class)
                        .invoke(scheduler, plugin, location, consumer, delay);
            } else {
                return scheduler.getClass()
                        .getMethod("run", Plugin.class, Location.class, Consumer.class)
                        .invoke(scheduler, plugin, location, consumer);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[QualityArmory] Failed to schedule region Folia task: " + e.getMessage());
            return null;
        }
    }

    // ===================== BukkitTask wrapper for Folia ScheduledTask =====================

    private final class FoliaTaskWrapper implements BukkitTask {
        private final Object scheduledTask;

        FoliaTaskWrapper(Object scheduledTask) {
            this.scheduledTask = scheduledTask;
        }

        @Override public int getTaskId() { return -1; }
        @Override public Plugin getOwner() { return null; }
        @Override public boolean isSync() { return true; }

        @Override
        public boolean isCancelled() {
            if (scheduledTask == null) return true;
            try {
                return (boolean) scheduledTask.getClass().getMethod("isCancelled").invoke(scheduledTask);
            } catch (Exception e) {
                return cancelled;
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
            if (scheduledTask == null) return;
            try {
                scheduledTask.getClass().getMethod("cancel").invoke(scheduledTask);
            } catch (Exception ignored) {}
        }
    }
}
