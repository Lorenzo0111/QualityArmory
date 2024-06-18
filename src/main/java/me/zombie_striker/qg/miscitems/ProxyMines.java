package me.zombie_striker.qg.miscitems;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.particles.XParticle;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QAThrowableExplodeEvent;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;

public class ProxyMines extends Grenade {

    public ProxyMines(final ItemStack[] ingg, final double cost, final double damage, final double explosionreadius, final String name,
            final String displayname, final List<String> lore, final MaterialStorage ms) {
        super(ingg, cost, damage, explosionreadius, name, displayname, lore, ms);
    }

    @Override
    public boolean onPull(final Player thrower, final ItemStack usedItem) {
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
            BlockFace sticky = null;

            @Override
            public void run() {
                // TODO: Write this bit that detect how close a player is.
                if (this.sticky == null) {
                    if (h.getHolder().getLocation().add(0.3, 0, 0).getBlock().getType().isSolid()) {
                        this.sticky = BlockFace.EAST;
                    } else if (h.getHolder().getLocation().add(-0.3, 0, 0).getBlock().getType().isSolid()) {
                        this.sticky = BlockFace.WEST;
                    } else if (h.getHolder().getLocation().add(0, 0, 0.3).getBlock().getType().isSolid()) {
                        this.sticky = BlockFace.SOUTH;
                    } else if (h.getHolder().getLocation().add(0, 0, -0.3).getBlock().getType().isSolid()) {
                        this.sticky = BlockFace.NORTH;
                    }
                }
                if (this.sticky != null) {
                    h.getHolder().setVelocity(new Vector(0, 0.0, 0));
                }
                if (!(h.getHolder() instanceof Player)) {
                    this.k++;
                }
                if (this.k >= 20) {
                    boolean det = false;
                    for (final Entity e : h.getHolder().getNearbyEntities(ProxyMines.this.radius, ProxyMines.this.radius,
                            ProxyMines.this.radius)) {
                        if (e instanceof Player) {
                            det = true;
                            break;
                        }
                    }
                    if (det) {
                        if (h.getHolder() instanceof Item) {
                            Grenade.getGrenades().remove(h.getHolder());
                            h.getHolder().remove();
                        }
                        if (QAMain.enableExplosionDamage) {
                            final QAThrowableExplodeEvent event = new QAThrowableExplodeEvent(ProxyMines.this, h.getHolder().getLocation());
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled())
                                ExplosionHandler.handleExplosion(h.getHolder().getLocation(),
                                        Math.toIntExact(Math.round(ProxyMines.this.radius / 2)), 1);
                            QAMain.DEBUG("Using default explosions");
                        }
                        try {
                            h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(), h.getHolder().getLocation(), 0);
                            h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
                        } catch (final Error e3) {
                            h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
                            h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
                        }
                        final Player thro = Bukkit.getPlayer(h.getOwner());
                        try {
                            for (final Entity e : h.getHolder().getNearbyEntities(ProxyMines.this.radius, ProxyMines.this.radius,
                                    ProxyMines.this.radius)) {
                                if (e instanceof LivingEntity) {
                                    final double dam = (ProxyMines.this.dmageLevel / e.getLocation().distance(h.getHolder().getLocation()));
                                    QAMain.DEBUG("Grenade-Damaging " + e.getName() + " : " + dam + " DAM.");
                                    if (thro == null)
                                        ((LivingEntity) e).damage(dam);
                                    else
                                        ((LivingEntity) e).damage(dam, thro);
                                }
                            }
                        } catch (final Error e) {
                            h.getHolder().getWorld().createExplosion(h.getHolder().getLocation(), 1);
                            QAMain.DEBUG("Failed. Created default explosion");
                        }
                        ThrowableItems.throwItems.remove(h.getHolder());
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(QAMain.getInstance(), 5, 1));
        ThrowableItems.throwItems.put(thrower, h);
        return true;
    }

}
