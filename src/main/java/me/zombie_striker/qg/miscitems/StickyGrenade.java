package me.zombie_striker.qg.miscitems;

import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QAThrowableExplodeEvent;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class StickyGrenade extends Grenade {

    public StickyGrenade(ItemStack[] ingredients, double cost, double damage, double explosionRadius, String name,
                         String displayname, List<String> lore, MaterialStorage ms) {
        super(ingredients, cost, damage, explosionRadius, name, displayname, lore, ms);
    }

    @Override
    public boolean onRMB(Player thrower, ItemStack usedItem) {
        if (QAMain.autoarm)
            onPull(thrower, usedItem);
        if (throwItems.containsKey(thrower) && throwItems.get(thrower).getGrenade().equals(this)) {
            ThrowableHolder holder = throwItems.get(thrower);
            ItemStack grenadeStack = thrower.getItemInHand();
            ItemStack temp = grenadeStack.clone();
            temp.setAmount(1);
            if (thrower.getGameMode() != GameMode.CREATIVE) {
                if (grenadeStack.getAmount() > 1) {
                    grenadeStack.setAmount(grenadeStack.getAmount() - 1);
                } else {
                    grenadeStack = null;
                }
                thrower.setItemInHand(grenadeStack);
            }

            throwItems.remove(holder.getHolder());
            Arrow arrow = ((Player) holder.getHolder()).launchProjectile(Arrow.class, holder.getHolder().getLocation().getDirection().normalize().multiply(getThrowSpeed()));
            holder.setHolder(arrow);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            throwItems.put(holder.getHolder(), holder);
            holder.setTimer(new BukkitRunnable() {
                public void run() {
                    if (thrower.isSneaking()) {
                        if (holder.getHolder() instanceof Arrow) {
                            holder.getHolder().remove();
                        }
                        if (QAMain.enableExplosionDamage) {
                            QAThrowableExplodeEvent event = new QAThrowableExplodeEvent(StickyGrenade.this, holder.getHolder().getLocation());
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled())
                                ExplosionHandler.handleExplosion(holder.getHolder().getLocation(), 3, 1);
                            QAMain.DEBUG("Using default explosions");
                        }
                        try {
                            holder.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(),
                                    holder.getHolder().getLocation(), 0);
                            holder.getHolder().getWorld().playSound(holder.getHolder().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 8,
                                    0.7f);
                        } catch (Error e3) {
                            holder.getHolder().getWorld().playEffect(holder.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
                            holder.getHolder().getWorld().playSound(holder.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
                        }
                        Player thro = Bukkit.getPlayer(holder.getOwner());
                        try {
                            for (Entity e : holder.getHolder().getNearbyEntities(radius, radius, radius)) {
                                if (e instanceof LivingEntity) {
                                    double dam = (damageLevel / e.getLocation().distance(holder.getHolder().getLocation()));
                                    QAMain.DEBUG("Grenade-Damaging " + e.getName() + " : " + dam + " DAM.");
                                    if (thro == null)
                                        ((LivingEntity) e).damage(dam);
                                    else
                                        ((LivingEntity) e).damage(dam, thro);
                                }
                            }
                        } catch (Error e) {
                            holder.getHolder().getWorld().createExplosion(holder.getHolder().getLocation(), 1);
                            QAMain.DEBUG("Failed. Created default explosion");
                        }
                        throwItems.remove(holder.getHolder());
                        this.cancel();
                    }
                }
            }.runTaskTimer(QAMain.getInstance(), 0, 2));
            //thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1.5f);

            QAMain.DEBUG("Throw grenade");
        } else {
            thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PULLPIN);
        }
        return true;
    }

    @Override
    public boolean onPull(Player thrower, ItemStack usedItem) {
        if (!QAMain.autoarm)
            if (throwItems.containsKey(thrower)) {
                thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
                thrower.playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
                return true;
            }
        thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 2, 1);
        final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
        throwItems.put(thrower, h);
        return true;

    }

    @Override
    public boolean onShift(Player shooter, ItemStack usedItem, boolean toggle) {
        return super.onShift(shooter, usedItem, toggle);
    }
}