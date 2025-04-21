package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.api.WeaponInteractEvent;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.IronsightsHandler;

public class GunRefillerRunnable {

    private static List<GunRefillerRunnable> allGunRefillers = new ArrayList<>();

    public static boolean hasItemReloaded(final Player reloader, final ItemStack is) {
        for (final GunRefillerRunnable s : GunRefillerRunnable.allGunRefillers) {
            if (is.isSimilar(s.reloadedItem))
                if (reloader == null || reloader.equals(s.reloader)) {
                    if (!s.getTask().isCancelled())
                        return true;
                }
        }
        return false;
    }

    public static boolean hasItemReloaded(final ItemStack is) { return GunRefillerRunnable.hasItemReloaded(null, is); }

    public static boolean isReloading(final Player reloader) {
        for (final GunRefillerRunnable s : GunRefillerRunnable.allGunRefillers) {
            if (reloader == null || reloader.equals(s.reloader)) {
                if (!s.getTask().isCancelled())
                    return true;
            }
        }
        return false;
    }

    private BukkitTask r;
    private ItemStack reloadedItem;
    private int originalAmount = 0;
    private int addedAmount = 0;
    private Player reloader = null;

    public int getOriginalAmount() { return this.originalAmount; }

    public int getAddedAmount() { return this.addedAmount; }

    public BukkitTask getTask() { return this.r; }

    public ItemStack getItem() { return this.reloadedItem; }

    public GunRefillerRunnable(final Player player, final ItemStack modifiedOriginalItem, final Gun g, final int slot,
            final int originalAmount, final int reloadAmount, final double seconds, final Ammo ammo, final int subtractAmount,
            final boolean removeAmmo) {
        final GunRefillerRunnable gg = this;
        gg.reloader = player;

        this.originalAmount = originalAmount;
        this.addedAmount = reloadAmount - originalAmount;

        this.reloadedItem = modifiedOriginalItem.clone();

        this.r = new BukkitRunnable() {
            @Override
            public void run() {
                final ItemMeta newim = modifiedOriginalItem.getItemMeta();
                boolean shouldContinue = player.getInventory().getHeldItemSlot() == slot;

                if (shouldContinue && removeAmmo) {
                    // Check if player still have ammo and remove it
                    if (!player.isDead() && g.playerHasAmmo(player) && QualityArmory.getAmmoInInventory(player, ammo) >= subtractAmount)
                        QualityArmory.removeAmmoFromInventory(player, ammo, subtractAmount);
                    else
                        shouldContinue = false;
                }

                if (shouldContinue) {
                    try {
                        player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 1, 1f);
                        if (!QAMain.isVersionHigherThan(1, 9)) {
                            try {
                                player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
                            } catch (Error | Exception e3) {
                                player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
                            }
                        }
                    } catch (final Error e2) {
                        try {
                            player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
                        } catch (Error | Exception e3) {
                            player.getWorld().playSound(player.getLocation(), Sound.valueOf("BLOCK_LEVER_CLICK"), 5, 1);
                        }
                    }
                }

                newim.setDisplayName(g.getDisplayName());
                modifiedOriginalItem.setItemMeta(newim);
                if (shouldContinue)
                    Gun.updateAmmo(g, modifiedOriginalItem, reloadAmount);

                final ItemStack current = player.getInventory().getItem(slot);
                int newSlot = slot;
                boolean different = false;
                if (current == null || !current.equals(GunRefillerRunnable.this.reloadedItem)) {
                    newSlot = -8;
                    different = true;
                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                        final ItemStack check = player.getInventory().getItem(i);
                        if (check != null) {
                            final Gun g2 = QualityArmory.getGun(check);
                            if (g2 != null && g2 == g) {
                                if (check.getItemMeta().getDisplayName().contains(QAMain.S_RELOADING_MESSAGE)) {
                                    newSlot = i;
                                    break;
                                }
                            }
                        }
                    }
                }
                QAMain.DEBUG("Reloading to slot " + newSlot + "(org=" + slot + ")");
                if (newSlot > -2) {
                    player.getInventory().setItem(newSlot, modifiedOriginalItem);

                    if (!different && player.isSneaking() && g.hasIronSights() && !QAMain.enableIronSightsON_RIGHT_CLICK) {
                        IronsightsHandler.aim(player);
                        QAMain.toggleNightvision(player, g, true);
                    }

                    QualityArmory.sendHotbarGunAmmoCount(player, g, modifiedOriginalItem, false);

                    if (QAMain.showAmmoInXPBar && shouldContinue) {
                        GunUtil.updateXPBar(player, g, reloadAmount);
                    }
                }

                Bukkit.getPluginManager().callEvent(new WeaponInteractEvent(player, g, WeaponInteractEvent.InteractType.RELOAD));
                if (!QAMain.reloadingTasks.containsKey(player.getUniqueId())) {
                    return;
                }
                final List<GunRefillerRunnable> rr = QAMain.reloadingTasks.get(player.getUniqueId());
                rr.remove(GunRefillerRunnable.this);
                GunRefillerRunnable.this.reloadedItem = null;
                GunRefillerRunnable.allGunRefillers.remove(gg);

                if (rr.isEmpty()) {
                    QAMain.reloadingTasks.remove(player.getUniqueId());
                } else {
                    QAMain.reloadingTasks.put(player.getUniqueId(), rr);
                }
            }
        }.runTaskLater(QAMain.getInstance(), (long) (20 * seconds));

        GunRefillerRunnable.allGunRefillers.add(gg);

        if (!QAMain.reloadingTasks.containsKey(player.getUniqueId())) {
            QAMain.reloadingTasks.put(player.getUniqueId(), new ArrayList<GunRefillerRunnable>());
        }
        final List<GunRefillerRunnable> rr = QAMain.reloadingTasks.get(player.getUniqueId());
        rr.add(this);
        QAMain.reloadingTasks.put(player.getUniqueId(), rr);
    }

}
