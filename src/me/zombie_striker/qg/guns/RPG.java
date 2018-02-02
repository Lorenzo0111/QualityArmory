package me.zombie_striker.qg.guns;

import java.util.List;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.RocketProjectile;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class RPG implements Gun {

	
	private double cost;
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean shoot(Player player) {
		boolean offhand = player.getInventory().getItemInHand().getDurability() == IronSightsToggleItem.getData();
		if ((!offhand && ItemFact.getAmount(player.getInventory().getItemInHand()) > 0)
				|| (offhand && Update19OffhandChecker.hasAmountOFfhandGreaterthan(player, 0))) {
			double sway = getSway();
			Location start = player.getLocation().clone().add(0, 1.5, 0);
			Vector go = player.getLocation().getDirection().normalize();
			go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
					(Math.random() * 2 * sway) - sway));
			Vector two = go.clone().multiply(2);

			new RocketProjectile(start, player, two);

			
			
			ItemStack temp = null;
			if (offhand)
				try {
					temp = player.getInventory().getItemInOffHand();
				} catch (Error | Exception ff) {
				}
			else
				temp = player.getItemInHand();

			ItemMeta im = temp.getItemMeta();

			if (Main.enableVisibleAmounts)
				temp.setAmount(temp.getAmount() - 1);
			int slot;
			if (offhand) {
				slot = -1;
			} else {
				slot = player.getInventory().getHeldItemSlot();
			}
			im.setLore(ItemFact.getGunLore(this, temp, ItemFact.getAmount(temp)));
			temp.setItemMeta(im);
			if (slot == -1) {
				try {
					player.getInventory().setItemInOffHand(temp);
				} catch (Error e) {
				}
			} else {
				player.getInventory().setItem(slot, temp);
			}
			
			
			
			

			try {
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 8, 2);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 8, 0.7f);
			} catch (Error e2) {
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("WITHER_SHOOT"), 8, 2);
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
			}
			return true;
		}
		return false;
	}
	@Override
	public double cost() {
		return cost;
	}

	@Override
	public int getMaxBullets() {
		return 2;
	}

	@Override
	public boolean playerHasAmmo(Player player) {
		if (WeaponType.isUnlimited(WeaponType.RPG))
			return true;
		return GunUtil.hasAmmo(player, this);
	}

	@Override
	public void reload(Player player) {
		GunUtil.basicReload(this, player, WeaponType.isUnlimited(WeaponType.RPG), 2.5);
	}

	@Override
	public String getName() {
		return "RPG";
	}

	@Override
	public double getDamage() {
		return 100;
	}

	public RPG(int d, ItemStack[] ing, double cost) {
		durability = d;
		this.ing = ing;
		this.cost = cost;
	}

	ItemStack[] ing;

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	private int durability;

	@Override
	public int getDurability() {
		return durability;
	}

	@Override
	public Ammo getAmmoType() {
		return AmmoType.getAmmo("rocket");
	}

	@Override
	public MaterialStorage getItemData() {
		return MaterialStorage.getMS(Main.guntype, 10);
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public boolean hasIronSights() {
		return false;
	}

	@Override
	public boolean hasUnlimitedAmmo() {
		return WeaponType.isUnlimited(WeaponType.RPG);
	}

	@Override
	public double getSway() {
		return 0.2;
	}

	@Override
	public double getMovementMultiplier() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public boolean isAutomatic() {
		return false;
	}

	@Override
	public WeaponSounds getWeaponSound() {
		return WeaponSounds.GUN_BIG;
	}

	@Override
	public List<String> getCustomLore() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return ChatColor.GOLD + getName();
	}
}
