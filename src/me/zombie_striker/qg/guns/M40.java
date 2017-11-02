package me.zombie_striker.qg.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.ammo.AmmoType;
import me.zombie_striker.qg.handlers.IronSightsToggleItem;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class M40 extends DefaultGun implements Listener {

	List<UUID> time = new ArrayList<>();


	@SuppressWarnings("deprecation")
	@Override
	public void shoot(final Player player) {
		boolean offhand = player.getInventory().getItemInHand().getDurability()==IronSightsToggleItem.getData();
		if ((!offhand&&player.getInventory().getItemInHand().getAmount()>1)||(offhand&&player.getInventory().getItemInOffHand().getAmount()>1)) {
			if (time.contains(player.getUniqueId()))
				return;
			time.add(player.getUniqueId());
			GunUtil.basicShoot(offhand,this, player, 0.03);
			new BukkitRunnable() {
				@Override
				public void run() {
					try{
					player.getWorld().playSound(player.getLocation(),
							Sound.BLOCK_LEVER_CLICK, 5, 1);
					}catch(Error e){
						player.getWorld().playSound(player.getLocation(),
								Sound.valueOf("CLICK"), 5, 1);						
					}
				}
			}.runTaskLater(Main.getInstance(), 10);
			new BukkitRunnable() {
				@Override
				public void run() {
					try{
					player.getWorld().playSound(player.getLocation(),
							Sound.BLOCK_LEVER_CLICK, 5, 1);
				}catch(Error e){
					player.getWorld().playSound(player.getLocation(),
							Sound.valueOf("CLICK"), 5, 1);						
				}
					time.remove(player.getUniqueId());
				}
			}.runTaskLater(Main.getInstance(), 16);
		}
	}

	@EventHandler
	public void roggleshift(PlayerToggleSneakEvent e) {
		try{
		if (e.getPlayer().getInventory().getItemInOffHand() != null
				&& e.getPlayer().getInventory().getItemInOffHand().getType() == Main.guntype
				&& e.getPlayer().getInventory().getItemInOffHand().getDurability() == 13) {
			if (e.isSneaking())
				e.getPlayer()
						.addPotionEffect(
								new PotionEffect(PotionEffectType.SLOW,
										12000, 4));
		}
		if (!e.isSneaking())
			e.getPlayer().removePotionEffect(PotionEffectType.SLOW);
		}catch(Error e2){			
		}

	}

	public M40(int d,ItemStack[] ing, float damage) {
		super("M40", MaterialStorage.getMS(Main.guntype,13), GunType.SNIPER, true, AmmoType.Ammo556,  0.1,4, 8, damage,d,ing);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
}
