package me.zombie_striker.qg.miscitems;

import java.util.List;

import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class SmokeGrenade extends Grenade {

    public SmokeGrenade(ItemStack[] ingredients, double cost, double damage, double explosionRadius, String name,
                        String displayname, List<String> lore, MaterialStorage ms) {
        super(ingredients, cost, damage, explosionRadius, name, displayname, lore, ms);
    }

    @Override
    public boolean onPull(Player thrower, ItemStack usedItem) {
        if (!QAMain.autoarm)
            if (throwItems.containsKey(thrower)) {
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
                    h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(),
                            h.getHolder().getLocation(), 0);
                    if (k % 2 == 0)
                        h.getHolder().getWorld().playSound(h.getHolder().getLocation(),
                                WeaponSounds.HISS.getSoundName(), 2f, 1f);
                } catch (Error e3) {
                    h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
                    h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 3, 0.7f);
                }
                k++;
                if (k == 1) {
                    if (h.getHolder() instanceof Player) {
                        QAMain.DEBUG("Blinded player");
                        ((LivingEntity) h.getHolder())
                                .addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
                        removeGrenade(((Player) h.getHolder()));
                    }
                } else if (k == 80) {
                    if (h.getHolder() instanceof Item) {
                        Grenade.getGrenades().remove(h.getHolder());
                        h.getHolder().remove();
                    }
                    throwItems.remove(h.getHolder());
                    this.cancel();
                } else {
                    for (Entity e : h.getHolder().getNearbyEntities(radius, radius, radius))
                        if (e instanceof LivingEntity) {
                            QAMain.DEBUG("Blinding to " + e.getName());
                            try {
                                if (ProtectionHandler.canPvp(e.getLocation())) {
                                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
                                }
                            } catch (Error error) {
                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
                            }
                        }
                }
            }
        }.runTaskTimer(QAMain.getInstance(), 5 * 20, 5));
        throwItems.put(thrower, h);
        return true;

    }
}