package me.zombie_striker.qg.miscitems;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;

public class Molotov extends Grenade {

    public Molotov(final ItemStack[] ingg, final double cost, final double damage, final double explosionreadius, final String name,
            final String displayname, final List<String> lore, final MaterialStorage ms) {
        super(ingg, cost, damage, explosionreadius, name, displayname, lore, ms);
    }

    @Override
    public boolean onPull(final Player e, final ItemStack usedItem) {
        final Player thrower = e.getPlayer();
        if (!QAMain.autoarm)
            if (ThrowableItems.throwItems.containsKey(thrower)) {
                thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
                thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
                return true;
            }
        thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
        final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
        h.setTimer(new BukkitRunnable() {

            int k = 0;

            @Override
            public void run() {
                try {
                    for (int i = 0; i < 8; i++) {
                        final double xoffset = ((Math.random() * 2) - 1) * Molotov.this.radius;
                        final double zoffset = ((Math.random() * 2) - 1) * Molotov.this.radius;
                        h.getHolder().getWorld().spawnParticle(Particle.FLAME, h.getHolder().getLocation().clone().add(xoffset, 0, zoffset),
                                0);
                    }
                    for (int i = 0; i < 4; i++) {
                        // TODO: Check: This goes in three directions, and one stays still
                        h.getHolder().getWorld().spawnParticle(org.bukkit.Particle.LAVA, h.getHolder().getLocation(), i);
                    }
                    h.getHolder().getWorld().playSound(h.getHolder().getLocation(), WeaponSounds.HISS.getSoundName(), 2f, 1f);
                } catch (final Error e3) {
                    h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
                    h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 3, 0.7f);
                }
                if (!(h.getHolder() instanceof Player) && (h.getHolder().isOnGround() || h.getHolder().isInWater()))
                    this.k++;
                QAMain.DEBUG("Fireticks");
                if (this.k == 40) {
                    if (h.getHolder() instanceof Item) {
                        Grenade.getGrenades().remove(h.getHolder());
                        h.getHolder().remove();
                    }
                    ThrowableItems.throwItems.remove(h.getHolder());
                    this.cancel();
                } else {
                    for (final Entity e : h.getHolder().getNearbyEntities(Molotov.this.radius, Molotov.this.radius, Molotov.this.radius))
                        if (e instanceof LivingEntity) {
                            QAMain.DEBUG("Firedamage to " + e.getName());
                            try {
                                if (ProtectionHandler.canPvp(e.getLocation())) {
                                    e.setFireTicks(20);
                                }
                            } catch (final Error error) {
                                e.setFireTicks(20);
                            }
                        }
                }
            }
        }.runTaskTimer(QAMain.getInstance(), 5 * 20, 10));
        ThrowableItems.throwItems.put(thrower, h);

        return true;
    }

}
