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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class Flashbang extends Grenade {

    public Flashbang(final ItemStack[] ingg, final double cost, final double damage, final double explosionreadius, final String name,
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
            @Override
            public void run() {
                try {
                    h.getHolder().getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, h.getHolder().getLocation(), 0);
                    h.getHolder().getWorld().playSound(h.getHolder().getLocation(), WeaponSounds.FLASHBANG.getSoundName(), 3f, 1f);
                } catch (final Error e3) {
                    h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
                    h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
                }
                try {
                    for (final Entity e : h.getHolder().getNearbyEntities(Flashbang.this.radius, Flashbang.this.radius,
                            Flashbang.this.radius)) {
                        if (e instanceof LivingEntity) {
                            QAMain.DEBUG("Flashbaned " + e.getName());
                            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
                        }
                    }
                } catch (final Error e) {
                }
                if (h.getHolder() instanceof Player) {
                    QAMain.DEBUG("Blinded player");
                    ((LivingEntity) h.getHolder()).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
                    Flashbang.this.removeGrenade(((Player) h.getHolder()));
                }
                if (h.getHolder() instanceof Item) {
                    Grenade.getGrenades().remove(h.getHolder());
                    h.getHolder().remove();
                }

                ThrowableItems.throwItems.remove(h.getHolder());
            }
        }.runTaskLater(QAMain.getInstance(), 5 * 20));
        ThrowableItems.throwItems.put(thrower, h);
        return true;
    }

}
