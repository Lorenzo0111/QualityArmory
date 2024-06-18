package me.zombie_striker.qg.miscitems;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.particles.XParticle;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QAThrowableExplodeEvent;
import me.zombie_striker.qg.handlers.ExplosionHandler;

public class StickyGrenades extends Grenade {

    public StickyGrenades(final ItemStack[] ingg, final double cost, final double damage, final double explosionreadius, final String name,
            final String displayname, final List<String> lore, final MaterialStorage ms) {
        super(ingg, cost, damage, explosionreadius, name, displayname, lore, ms);
    }

    @Override
    public boolean onRMB(final Player thrower, final ItemStack usedItem) {
        if (QAMain.autoarm)
            this.onPull(thrower, usedItem);
        if (ThrowableItems.throwItems.containsKey(thrower) && ThrowableItems.throwItems.get(thrower).getGrenade().equals(this)) {
            final ThrowableHolder holder = ThrowableItems.throwItems.get(thrower);
            ItemStack grenadeStack = thrower.getInventory().getItemInMainHand();
            final ItemStack temp = grenadeStack.clone();
            temp.setAmount(1);
            if (thrower.getGameMode() != GameMode.CREATIVE) {
                if (grenadeStack.getAmount() > 1) {
                    grenadeStack.setAmount(grenadeStack.getAmount() - 1);
                } else {
                    grenadeStack = null;
                }
                thrower.getInventory().setItemInMainHand(grenadeStack);
            }

            ThrowableItems.throwItems.remove(holder.getHolder());
            final Arrow arrow = ((Player) holder.getHolder()).launchProjectile(Arrow.class,
                    holder.getHolder().getLocation().getDirection().normalize().multiply(this.getThrowSpeed()));
            holder.setHolder(arrow);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            ThrowableItems.throwItems.put(holder.getHolder(), holder);
            holder.setTimer(new BukkitRunnable() {
                @Override
                public void run() {
                    if (thrower.isSneaking()) {
                        if (holder.getHolder() instanceof Arrow) {
                            holder.getHolder().remove();
                        }
                        if (QAMain.enableExplosionDamage) {
                            final QAThrowableExplodeEvent event = new QAThrowableExplodeEvent(StickyGrenades.this,
                                    holder.getHolder().getLocation());
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled())
                                ExplosionHandler.handleExplosion(holder.getHolder().getLocation(), 3, 1);
                            QAMain.DEBUG("Using default explosions");
                        }
                        try {
                            holder.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(), holder.getHolder().getLocation(),
                                    0);
                            holder.getHolder().getWorld().playSound(holder.getHolder().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 8,
                                    0.7f);
                        } catch (final Error e3) {
                            holder.getHolder().getWorld().playEffect(holder.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
                            holder.getHolder().getWorld().playSound(holder.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
                        }
                        final Player thro = Bukkit.getPlayer(holder.getOwner());
                        try {
                            for (final Entity e : holder.getHolder().getNearbyEntities(StickyGrenades.this.radius,
                                    StickyGrenades.this.radius, StickyGrenades.this.radius)) {
                                if (e instanceof LivingEntity) {
                                    final double dam = (StickyGrenades.this.dmageLevel
                                            / e.getLocation().distance(holder.getHolder().getLocation()));
                                    QAMain.DEBUG("Grenade-Damaging " + e.getName() + " : " + dam + " DAM.");
                                    if (thro == null)
                                        ((LivingEntity) e).damage(dam);
                                    else
                                        ((LivingEntity) e).damage(dam, thro);
                                }
                            }
                        } catch (final Error e) {
                            holder.getHolder().getWorld().createExplosion(holder.getHolder().getLocation(), 1);
                            QAMain.DEBUG("Failed. Created default explosion");
                        }
                        ThrowableItems.throwItems.remove(holder.getHolder());
                        this.cancel();
                    }
                }
            }.runTaskTimer(QAMain.getInstance(), 0, 2));
            // thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT,
            // 1, 1.5f);

            QAMain.DEBUG("Throw grenade");
        } else {
            thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PULLPIN);
        }
        return true;
    }

    @Override
    public boolean onPull(final Player thrower, final ItemStack usedItem) {
        if (!QAMain.autoarm)
            if (ThrowableItems.throwItems.containsKey(thrower)) {
                thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
                thrower.playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
                return true;
            }
        thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 2, 1);
        final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
        ThrowableItems.throwItems.put(thrower, h);
        return true;

    }

    @Override
    public boolean onShift(final Player shooter, final ItemStack usedItem, final boolean toggle) {
        return super.onShift(shooter, usedItem, toggle);
    }
}
