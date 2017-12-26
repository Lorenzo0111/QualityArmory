package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.ammo.AmmoType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class RPG implements Gun {

	@SuppressWarnings("deprecation")
	@Override
	public void shoot(Player player) {
		final ItemStack temp = player.getInventory().getItemInHand();
		ItemMeta im = temp.getItemMeta();
		if (ItemFact.getAmount(temp) > 0) {
			double sway = getSway();
			Location start = player.getLocation().clone().add(0, 1.5, 0);
			Vector go = player.getLocation().getDirection().normalize();
			go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
					(Math.random() * 2 * sway) - sway));
			Vector two = go.clone().multiply(2);

			new RocketProjectile(start, player, two);

			temp.setAmount(temp.getAmount() - 1);
			int slot = player.getInventory().getHeldItemSlot();
			im.setLore(ItemFact.getGunLore(this, temp, 0));
			temp.setItemMeta(im);
			player.getInventory().setItem(slot, temp);
			try {
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 5, 1);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 8, 2);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 8, 0.7f);
			} catch (Error e2) {
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("CLICK"), 5, 1);
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("WITHER_SHOOT"), 8, 2);
				player.getWorld().playSound(player.getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
			}
		}

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

	public RPG(int d, ItemStack[] ing) {
		durability = d;
		this.ing = ing;
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
		return AmmoType.AmmoRPG.getType();
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
