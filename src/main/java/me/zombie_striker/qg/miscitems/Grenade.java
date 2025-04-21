package me.zombie_striker.qg.miscitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.particles.XParticle;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QAThrowableExplodeEvent;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;

public class Grenade extends CustomBaseObject implements ThrowableItems {
    private static final List<Entity> GRENADES = new ArrayList<>();

    @SuppressWarnings("unused")
    private final ItemStack[] ing = null;

    double dmageLevel = 10;
    double radius = 5;

    int craftingReturn;

    double throwspeed = 1.5;

    public Grenade(final ItemStack[] ingg, final double cost, final double damage, final double explosionreadius, final String name,
            final String displayname, final List<String> lore, final MaterialStorage ms) {
        super(name, ms, displayname, lore, false);
        super.setIngredients(ingg);
        this.setPrice(cost);
        this.radius = explosionreadius;
        this.dmageLevel = damage;
    }

    @Override
    public int getCraftingReturn() { return 1; }

    @SuppressWarnings("deprecation")
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
                thrower.setItemInHand(grenadeStack);
            }
            final Item grenade = holder.getHolder().getWorld().dropItem(holder.getHolder().getLocation().add(0, 1.5, 0), temp);
            grenade.setPickupDelay(Integer.MAX_VALUE);
            grenade.setVelocity(thrower.getLocation().getDirection().normalize().multiply(this.getThrowSpeed()));
            holder.setHolder(grenade);
            thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1.5f);

            if (!ProtectionHandler.canExplode(grenade.getLocation())) {
                return false;
            }

            ThrowableItems.throwItems.put(grenade, holder);
            Grenade.GRENADES.add(grenade);
            ThrowableItems.throwItems.remove(thrower);
            QAMain.DEBUG("Throw grenade");
        } else {
            thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PULLPIN);
        }
        return true;
    }

    @Override
    public boolean onShift(final Player shooter, final ItemStack usedItem, final boolean toggle) { return false; }

    @Override
    public boolean onLMB(final Player e, final ItemStack usedItem) {
        if (!QAMain.autoarm)
            return this.onPull(e, usedItem);
        return false;
    }

    public boolean onPull(final Player e, final ItemStack usedItem) {
        final Player thrower = e.getPlayer();
        if (!QAMain.autoarm)
            if (ThrowableItems.throwItems.containsKey(thrower)) {
                thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
                thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
                QAMain.DEBUG("Already pin out");
                return true;
            }

        thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
        final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
        h.setTimer(new BukkitRunnable() {
            @Override
            public void run() {
                if (h.getHolder() instanceof Player) {
                    QAMain.DEBUG("Player did not throw. Damaged for " + Grenade.this.dmageLevel);
                    Grenade.this.removeGrenade(((Player) h.getHolder()));
                    ((Player) h.getHolder()).damage(Grenade.this.dmageLevel);
                }
                if (h.getHolder() instanceof Item) {
                    Grenade.GRENADES.remove(h.getHolder());
                    h.getHolder().remove();
                }
                if (QAMain.enableExplosionDamage) {
                    final QAThrowableExplodeEvent event = new QAThrowableExplodeEvent(Grenade.this, h.getHolder().getLocation());
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled())
                        ExplosionHandler.handleExplosion(h.getHolder().getLocation(), 3, 1);
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
                    for (final Entity e : h.getHolder().getNearbyEntities(Grenade.this.radius, Grenade.this.radius, Grenade.this.radius)) {
                        if (e instanceof LivingEntity) {
                            final double dam = (Grenade.this.dmageLevel / e.getLocation().distance(h.getHolder().getLocation()));
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
            }
        }.runTaskLater(QAMain.getInstance(), 5 * 20));
        ThrowableItems.throwItems.put(thrower, h);
        return true;
    }

    @Override
    public boolean is18Support() { return false; }

    @Override
    public void set18Supported(final boolean b) {}

    @Override
    public ItemStack getItemStack() {
        return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(),
                this.getItemData().getVariant());
    }

    public void removeGrenade(final Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            int slot = -56;
            ItemStack stack = null;
            for (int i = 0; i < player.getInventory().getContents().length; i++) {
                if ((stack = player.getInventory().getItem(i)) != null && MaterialStorage.getMS(stack) == this.getItemData()) {
                    slot = i;
                    break;
                }
            }
            if (slot >= -1) {
                if (stack.getAmount() > 1) {
                    stack.setAmount(stack.getAmount() - 1);
                } else {
                    stack = null;
                }
                player.getInventory().setItem(slot, stack);
            }
        }
    }

    @Override
    public boolean onSwapTo(final Player shooter, final ItemStack usedItem) {
        if (this.getSoundOnEquip() != null)
            shooter.getWorld().playSound(shooter.getLocation(), this.getSoundOnEquip(), 1, 1);
        return false;
    }

    @Override
    public boolean onSwapAway(final Player shooter, final ItemStack usedItem) { return false; }

    @Override
    public double getThrowSpeed() { return this.throwspeed; }

    @Override
    public void setThrowSpeed(final double t) { this.throwspeed = t; }

    public static List<Entity> getGrenades() { return Grenade.GRENADES; }
}
