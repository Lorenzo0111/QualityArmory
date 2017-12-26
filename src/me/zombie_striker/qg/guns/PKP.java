package me.zombie_striker.qg.guns;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;
import me.zombie_striker.qg.handlers.Update19OffhandChecker;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PKP extends DefaultGun {

	//List<UUID> time = new ArrayList<>();

	@SuppressWarnings("deprecation")
	@Override
	public void shoot(final Player player) {
		final boolean offhand = player.getInventory().getItemInHand().getDurability()==IronSightsToggleItem.getData();
		if ((!offhand&&player.getInventory().getItemInHand().getAmount()>3)||(offhand&&Update19OffhandChecker.hasAmountOFfhandGreaterthan(player,3))) {
			final Gun g = this;
			GunUtil.basicShoot(offhand,g, player, getSway());
		}
	}
	public PKP(int d,ItemStack[] ing, float damage) {
		super("PKP", MaterialStorage.getMS(Main.guntype,3), WeaponType.RIFLE,false, AmmoType.Ammo556,  0.3,2, 60, damage,true,d,WeaponSounds.GUN_BIG,ing);
	}
}
