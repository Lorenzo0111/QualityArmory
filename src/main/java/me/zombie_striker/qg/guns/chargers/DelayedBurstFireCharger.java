package me.zombie_striker.qg.guns.chargers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class DelayedBurstFireCharger implements ChargingHandler {

    public static HashMap<UUID, BukkitTask> shooters = new HashMap<>();

    public DelayedBurstFireCharger() { ChargingManager.add(this); }

    @Override
    public boolean isCharging(final Player player) { return DelayedBurstFireCharger.shooters.containsKey(player.getUniqueId()); }

    @Override
    public boolean shoot(final Gun g, final Player player, final ItemStack stack) {
        GunUtil.shootHandler(g, player);
        GunUtil.playShoot(g, player);

        DelayedBurstFireCharger.shooters.put(player.getUniqueId(), new BukkitRunnable() {
            int slotUsed = player.getInventory().getHeldItemSlot();
            boolean offhand = QualityArmory.isIronSights(player.getInventory().getItemInMainHand());
            int shotCurrently = 1;

            int currentRate = (int) (10 / g.getFireRate() / Math.pow(2, g.getBulletsPerShot()));
            int skippedTicks = 0;

            @Override
            public void run() {
                if (this.skippedTicks >= this.currentRate) {
                    this.skippedTicks = 0;
                    this.currentRate *= 2;
                } else {
                    this.skippedTicks++;
                    return;
                }
                int amount = Gun.getAmount(player);
                if (this.shotCurrently >= g.getBulletsPerShot() || this.slotUsed != player.getInventory().getHeldItemSlot()
                        || amount <= 0) {
                    if (DelayedBurstFireCharger.shooters.containsKey(player.getUniqueId()))
                        DelayedBurstFireCharger.shooters.remove(player.getUniqueId()).cancel();
                    return;
                }

                GunUtil.shootHandler(g, player);
                GunUtil.playShoot(g, player);
                if (QAMain.enableRecoil && g.getRecoil() > 0) {
                    GunUtil.addRecoil(player, g);
                }
                this.shotCurrently++;
                amount--;

                if (amount < 0)
                    amount = 0;

                // if (QAMain.enableVisibleAmounts) {
                // stack.setAmount(amount > 64 ? 64 : amount == 0 ? 1 : amount);
                // }
                final ItemMeta im = stack.getItemMeta();
                int slot;
                if (this.offhand) {
                    slot = -1;
                } else {
                    slot = player.getInventory().getHeldItemSlot();
                }
                Gun.updateAmmo(g, player, amount);
                stack.setItemMeta(im);
                if (slot == -1) {
                    try {
                        if (QualityArmory.isIronSights(player.getInventory().getItemInMainHand())) {
                            player.getInventory().setItemInOffHand(stack);
                        } else {
                            player.getInventory().setItemInMainHand(stack);
                        }

                    } catch (final Error e) {
                    }
                } else {
                    player.getInventory().setItem(slot, stack);
                }
                QualityArmory.sendHotbarGunAmmoCount(player, g, stack, false);
            }
        }.runTaskTimer(QAMain.getInstance(), 1, 1));
        return false;
    }

    @Override
    public String getName() {

        return ChargingManager.DelayedBURSTFIRE;
    }

    @Override
    public String getDefaultChargingSound() {
        return WeaponSounds.RELOAD_BULLET.getSoundName();
        // g.getChargingSound()
    }

}
