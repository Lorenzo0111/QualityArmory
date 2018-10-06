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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;

public class Grenades implements ThrowableItems {

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

	public Grenades(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
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
	public void onRMB(PlayerInteractEvent e, ItemStack usedItem) {
		Player thrower = e.getPlayer();
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
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PULLPIN);
		}
	}

	@Override
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem) {
		Player thrower = e.getPlayer();
		if (grenadeHolder.containsKey(thrower)) {
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
			thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
			return;
		}
		thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
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
				if (QAMain.enableExplosionDamage) {
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
		}.runTaskLater(QAMain.getInstance(), 5 * 20));
		grenadeHolder.put(thrower, h);

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
		return ItemFact.getObject(this,1);
	}
}
