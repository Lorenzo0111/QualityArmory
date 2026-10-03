package me.zombie_striker.qg.miscitems;

import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.handlers.GunDamageHandler;
import me.zombie_striker.qg.api.QAThrowableExplodeEvent;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import me.zombie_striker.qg.utils.FoliaRunnable;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Grenade extends CustomBaseObject implements ThrowableItems {
	private static final List<Entity> GRENADES = new CopyOnWriteArrayList<>();

	private ItemStack[] ing = null;

	double dmageLevel = 10;
	double radius = 5;

	int craftingReturn;

	double throwspeed = 1.5;

	int fuseTicks = 5 * 20;

	public Grenade(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
			String displayname, List<String> lore, MaterialStorage ms) {
		super(name,ms,displayname,lore,false);
		super.setIngredients(ingg);
		this.setPrice(cost);
		this.radius = explosionreadius;
		this.dmageLevel = damage;
	}


	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public boolean onRMB(Player thrower, ItemStack usedItem) {
		if(QAMain.autoarm) onPull(thrower,usedItem);
		if (throwItems.containsKey(thrower) && throwItems.get(thrower).getGrenade().equals(this)) {
			return this.throwGrenade(thrower, thrower.getLocation().getDirection().normalize().multiply(getThrowSpeed()));
		} else {
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PULLPIN);
		}
		return true;
	}

    @SuppressWarnings("deprecation")
    public boolean throwGrenade(Player thrower, @Nullable Vector velocity) {
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
        Item grenade = holder.getHolder().getWorld().dropItem(holder.getHolder().getLocation().add(0, 1.5, 0),
                temp);
        grenade.setPickupDelay(Integer.MAX_VALUE);
        if (velocity != null)
            grenade.setVelocity(velocity);
        holder.setHolder(grenade);
        thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1.5f);

        if (!ProtectionHandler.canExplode(grenade.getLocation())) {
            return false;
        }

        throwItems.put(grenade, holder);
        GRENADES.add(grenade);
        throwItems.remove(thrower);
        QAMain.DEBUG("Throw grenade");
        return true;
    }

	@Override
	public boolean onShift(Player shooter, ItemStack usedItem, boolean toggle) {
		return false;
	}

	@Override
	public boolean onLMB(Player e, ItemStack usedItem) {
		if(!QAMain.autoarm)
			return onPull(e,usedItem);
		return false;
	}

	public boolean onPull(Player e, ItemStack usedItem) {
		Player thrower = e.getPlayer();
		if(!QAMain.autoarm)
		if (throwItems.containsKey(thrower)) {
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
			thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
			QAMain.DEBUG("Already pin out");
			return true;
		}

		thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
		h.setTimer(new FoliaRunnable() {
			@Override
			public void run() {
				Entity holderEntity = h.getHolder();
				if (holderEntity == null) {
					cancel();
					return;
				}
				FoliaRunnable.runEntityTask(QAMain.getInstance(), holderEntity,
						() -> handleThrowableTick(h),
						() -> {
							throwItems.remove(holderEntity);
							BukkitTask t = h.getTask();
							if (t != null) t.cancel();
						});
			}
		}.runTaskLater(QAMain.getInstance(), getFuseTicks()));
		throwItems.put(thrower, h);
		return true;
	}

	private void handleThrowableTick(ThrowableHolder h) {
		if (h.getHolder() instanceof Player) {
			QAMain.DEBUG("Player did not throw. Damaged for " + dmageLevel);
			removeGrenade(((Player) h.getHolder()));
			((Player) h.getHolder()).damage(dmageLevel);
		}
		if (h.getHolder() instanceof Item) {
			GRENADES.remove(h.getHolder());
			h.getHolder().remove();
		}
		if (QAMain.enableExplosionDamage) {
			QAThrowableExplodeEvent event = new QAThrowableExplodeEvent(Grenade.this, h.getHolder().getLocation());
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) ExplosionHandler.handleExplosion(h.getHolder().getLocation(), 3, 1);
			QAMain.DEBUG("Using default explosions");
		}
		try {
			h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(),
					h.getHolder().getLocation(), 0);
			h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 8,
					0.7f);
		} catch (Error e3) {
			h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
			h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
		}
		Player thro = Bukkit.getPlayer(h.getOwner());
		final Location holderLoc = h.getHolder().getLocation();
		try {
			for (Entity e : h.getHolder().getNearbyEntities(radius, radius, radius)) {
				if (e instanceof LivingEntity) {
					final LivingEntity target = (LivingEntity) e;
					FoliaRunnable.runEntityTask(QAMain.getInstance(), target, () -> {
						double dam = (dmageLevel / target.getLocation().distance(holderLoc));
						QAMain.DEBUG("Grenade-Damaging " + target.getName() + " : " + dam + " DAM.");
						GunDamageHandler.damage(target, dam, thro, false);
					});
				}
			}
		} catch (Error e) {
			h.getHolder().getWorld().createExplosion(h.getHolder().getLocation(), 1);
			QAMain.DEBUG("Failed. Created default explosion");
		}
		throwItems.remove(h.getHolder());
	}

	@Override
	public boolean is18Support() {
		return false;
	}

	@Override
	public void set18Supported(boolean b) {
	}

	@Override
	public ItemStack getItemStack() {
		return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(),this.getItemData().getData(),this.getItemData().getVariant());
	}

	public void removeGrenade(Player player) {
		if (player.getGameMode() != GameMode.CREATIVE) {
			int slot = -56;
			ItemStack stack = null;
			for (int i = 0; i < player.getInventory().getContents().length; i++) {
				if ((stack = player.getInventory().getItem(i)) != null && MaterialStorage.getMS(stack) == getItemData()) {
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
	public boolean onSwapTo(Player shooter, ItemStack usedItem) {
		if (getSoundOnEquip() != null)
			shooter.getWorld().playSound(shooter.getLocation(), getSoundOnEquip(), getSoundOnEquipVolume(), 1);
		return false;
	}

	@Override
	public boolean onSwapAway(Player shooter, ItemStack usedItem) {
		return false;
	}

	@Override
	public double getThrowSpeed() {
		return throwspeed;
	}

	@Override
	public void setThrowSpeed(double t) {
		throwspeed = t;
	}

	public int getFuseTicks() {
		return fuseTicks;
	}

	public void setFuseTicks(int fuseTicks) {
		this.fuseTicks = Math.max(1, fuseTicks);
	}

	public static List<Entity> getGrenades() {
		return GRENADES;
	}
}
