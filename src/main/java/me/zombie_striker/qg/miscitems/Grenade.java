package me.zombie_striker.qg.miscitems;

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

public class Grenade implements ThrowableItems {

	private ItemStack[] ing = null;

	double dmageLevel = 10;
	double radius = 5;

	double cost;

	String name;
	String displayname;
	int craftingReturn;
	List<String> lore;
	MaterialStorage ms;

	public Grenade(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
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
		if (throwItems.containsKey(thrower)) {
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
			grenade.setVelocity(thrower.getLocation().getDirection().normalize().multiply(1.2));
			holder.setHolder(grenade);
			thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1.5f);

			throwItems.put(grenade, holder);
			throwItems.remove(thrower);
			QAMain.DEBUG("Throw grenade");
		} else {
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PULLPIN);
		}
	}

	@Override
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem) {
		Player thrower = e.getPlayer();
		if (throwItems.containsKey(thrower)) {
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
			thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
			QAMain.DEBUG("Already pin out");
			return;
		}

		thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower);
		h.setTimer(new BukkitRunnable() {
			@Override
			public void run() {
				if (h.getHolder() instanceof Player) {
					QAMain.DEBUG("Player did not throw. Damaged for " + dmageLevel);
					removeGrenade(((Player) h.getHolder()));
					((Player) h.getHolder()).damage(dmageLevel);
				}
				if (h.getHolder() instanceof Item) {
					h.getHolder().remove();
				}
				if (QAMain.enableExplosionDamage) {
					ExplosionHandler.handleExplosion(h.getHolder().getLocation(), 3, 1);
					QAMain.DEBUG("Using default explosions");
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
					Bukkit.broadcastMessage(
							"rad " + radius + " " + h.getHolder().getNearbyEntities(radius, radius, radius).size());
					for (Entity e : h.getHolder().getNearbyEntities(radius, radius, radius)) {
						if (e instanceof LivingEntity) {
							double dam = (dmageLevel / e.getLocation().distance(h.getHolder().getLocation()));
							QAMain.DEBUG("Grenade-Damaging " + e.getName() + " : " + dam + " DAM.");
							if (thro == null)
								((LivingEntity) e).damage(dam);
							else
								((LivingEntity) e).damage(dam, thro);
						}
					}
				} catch (Error e) {
					h.getHolder().getWorld().createExplosion(h.getHolder().getLocation(), 1);
					QAMain.DEBUG("Failed. Created default explosion");
				}
				throwItems.remove(h.getHolder());
			}
		}.runTaskLater(QAMain.getInstance(), 5 * 20));
		throwItems.put(thrower, h);

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
		return ItemFact.getObject(this, 1);
	}

	public void removeGrenade(Player player) {
		if (player.getGameMode() != GameMode.CREATIVE) {
			int slot = -56;
			ItemStack stack = null;
			for (int i = 0; i < player.getInventory().getContents().length; i++) {
				if ((stack = player.getInventory().getItem(i)) != null && MaterialStorage.getMS(stack) == ms) {
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
}
