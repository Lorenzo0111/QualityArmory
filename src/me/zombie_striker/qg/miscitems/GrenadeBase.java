package me.zombie_striker.qg.miscitems;

import java.util.HashMap;
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

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.miscitems.ThrowableItems.ThrowableHolder;

public class GrenadeBase implements InteractableMisc {

	private ItemStack[] ing = null;

	double dmageLevel = 10;
	double radius = 5;

	public HashMap<Entity, ThrowableHolder> grenadeHolder = new HashMap<>();

	double cost;

	String name;
	String displayname;
	int craftingReturn;
	List<String> lore;
	MaterialStorage ms;

	public GrenadeBase(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
			String displayname, List<String> lore, MaterialStorage ms) {
		this.ing = ingg;
		this.cost = cost;
		this.radius = explosionreadius;
		this.dmageLevel = damage;
		this.name = name;
		this.displayname = displayname;
		this.lore = lore;
		this.ms = ms;
	}

	@Override
	public double cost() {
		return cost;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public MaterialStorage getItemData() {
		return ms;
	}

	@Override
	public List<String> getCustomLore() {
		return lore;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRightClick(Player thrower) {
		if (grenadeHolder.containsKey(thrower)) {
			ThrowableHolder holder = grenadeHolder.get(thrower);
			ItemStack g = thrower.getItemInHand();
			if (thrower.getGameMode() != GameMode.CREATIVE) {
				thrower.setItemInHand(null);
			}
			g.setAmount(1);
			Item grenade = holder.getHolder().getWorld().dropItem(holder.getHolder().getLocation().add(0, 1.5, 0), g);
			grenade.setPickupDelay(Integer.MAX_VALUE);
			grenade.setVelocity(thrower.getLocation().getDirection().normalize().multiply(1.2));
			holder.setHolder(grenade);
			thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1.5f);

			grenadeHolder.put(grenade, holder);
			grenadeHolder.remove(thrower);
		} else {
			thrower.sendMessage(Main.prefix + " Pull the pin first...");
		}
	}

	@Override
	public void onLeftClick(Player thrower) {
		if (grenadeHolder.containsKey(thrower)) {
			thrower.sendMessage(Main.prefix + "You already pulled the pin!");
			thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getName(), 1, 1);
			return;
		}
		thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getName(), 2, 1);
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower);
		h.setTimer(new BukkitRunnable() {
			@Override
			public void run() {
				if (h.getHolder() instanceof Player) {
					((Player) h.getHolder()).damage(100);
				}
				if (h.getHolder() instanceof Item) {
					h.getHolder().remove();
				}
				if (Main.enableExplosionDamage) {
					ExplosionHandler.handleExplosion(h.getHolder().getLocation(), 3, 1);
				}
				try {
					h.getHolder().getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE,
							h.getHolder().getLocation(), 0);
					h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 8,
							0.7f);
				} catch (Error e3) {
					h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
					h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
				}
				Player thro = Bukkit.getPlayer(h.getOwner());
				try {
					for (Entity e : h.getHolder().getNearbyEntities(radius, radius, radius)) {
						if (e instanceof LivingEntity) {
							if (thro == null)
								((LivingEntity) e).damage(
										(dmageLevel * radius / e.getLocation().distance(h.getHolder().getLocation())));
							else
								((LivingEntity) e).damage(
										(dmageLevel * radius / e.getLocation().distance(h.getHolder().getLocation())),
										thro);
						}
					}
				} catch (Error e) {
					h.getHolder().getWorld().createExplosion(h.getHolder().getLocation(), 1);
				}
				grenadeHolder.remove(h.getHolder());
			}
		}.runTaskLater(Main.getInstance(), 5 * 20));
		grenadeHolder.put(thrower, h);

	}

}
