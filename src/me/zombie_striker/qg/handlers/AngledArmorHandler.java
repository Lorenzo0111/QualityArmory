package me.zombie_striker.qg.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.armor.angles.AngledArmor;

public class AngledArmorHandler {

	private static List<UUID> angledPlayers = new ArrayList<>();
	
	private static List<UUID> remove = new ArrayList<>();

	private static BukkitTask check1 = null;
	private static BukkitTask check2 = null;

	public static void init() {
		check1 = new BukkitRunnable() {

			@Override
			public void run() {
				for(UUID uuid : remove) {
					angledPlayers.remove(uuid);
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (angledPlayers.contains(p.getUniqueId())) {
						if (p.getInventory().getHelmet() == null || (!Main.isArmor(p.getInventory().getHelmet())
								&& !Main.isAngledArmor(p.getInventory().getHelmet()))) {
							angledPlayers.remove(p.getUniqueId());
						}
					} else {
						if (p.getInventory().getHelmet() != null && (Main.isArmor(p.getInventory().getHelmet())
								|| Main.isAngledArmor(p.getInventory().getHelmet()))) {
							angledPlayers.add(p.getUniqueId());
						}
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20 * 5);
		check2 = new BukkitRunnable() {

			@Override
			public void run() {
				for (UUID uuid : angledPlayers) {
					Player p = Bukkit.getPlayer(uuid);
					if(p==null) {
						if(!remove.contains(uuid))
								remove.add(uuid);
						continue;
					}

					if (p.getInventory().getHelmet() == null || (!Main.isAngledArmor(p.getInventory().getHelmet())
							&& !Main.isArmor(p.getInventory().getHelmet())))
						continue;

					float angle = p.getLocation().getPitch() + 90;
					// 90 = foward. 180 = up. 0 = down
					double close = 10000;
					AngledArmor closest = null;
					ArmorObject base = null;
					for (AngledArmor ag : Main.angledArmor.values()) {
						double k = Math.abs(ag.getAngle() - angle);
						if (k < close) {
							close = k;
							closest = ag;
						}
					}
					if (Math.abs(90 - angle) < close) {
						base = Main.armorRegister.get(closest.getBase());
						closest = null;
					}
					if (closest != null || base != null) {
						ItemStack is;
						if (base != null)
							is = ItemFact.getArmor(base);
						else
							is = ItemFact.getArmor(closest);
						// p.getInventory().get
						//REally bad hack
						ItemStack helmet = p.getInventory().getHelmet();
						helmet.setDurability(is.getDurability());
						p.getInventory().setHelmet(helmet);
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 5);
	}

	public static void stopTasks() {
		if (check1 != null)
			check1.cancel();
		if (check2 != null)
			check2.cancel();
	}
}
