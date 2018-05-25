package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.handlers.gunvalues.HomingRPGCharger;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HomingRocketProjectile {

	private final float turnRate = 0.3f;

	public HomingRocketProjectile(final Location s, final Player player, final Vector dir) {
		new BukkitRunnable() {
			int distance = 220;
			Vector vect = dir;

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				distance--;
				s.add(vect);
				Block lookat = player.getTargetBlock(null, 300);
				if (lookat != null && lookat.getType() != Material.AIR) {
					if (Main.isGun(player.getItemInHand())) {
						Gun g = Main.getGun(player.getItemInHand());
						if (g.getChargingVal() != null && g.getChargingVal() instanceof HomingRPGCharger) {
							Vector newVect = new Vector(lookat.getX() - s.getBlockX(), lookat.getY() - s.getBlockY(),
									lookat.getZ() - s.getBlockZ()).normalize();

							double newX = newVect.getX() - vect.getBlockX() > turnRate ? vect.getBlockX() + turnRate
									: newVect.getX() - vect.getBlockX() < -turnRate ? vect.getBlockX() - turnRate
											: newVect.getX();
							double newY = newVect.getY() - vect.getBlockY() > turnRate ? vect.getBlockY() + turnRate
									: newVect.getY() - vect.getBlockY() < -turnRate ? vect.getBlockY() - turnRate
											: newVect.getY();
							double newZ = newVect.getZ() - vect.getBlockZ() > turnRate ? vect.getBlockZ() + turnRate
									: newVect.getZ() - vect.getBlockZ() < -turnRate ? vect.getBlockZ() - turnRate
											: newVect.getZ();

							vect = new Vector(newX, newY, newZ);
						}
					}
				}
				try {
					s.getWorld().spawnParticle(org.bukkit.Particle.SMOKE_LARGE, s, 0);
					s.getWorld().spawnParticle(org.bukkit.Particle.FIREWORKS_SPARK, s, 0);
					player.getWorld().playSound(s, Sound.ENTITY_ENDERDRAGON_GROWL, 1, 2f);

				} catch (Error e2) {
					s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
					player.getWorld().playSound(s, Sound.valueOf("ENDERDRAGON_GROWL"), 1, 2f);
				}
				boolean entityNear = false;
				try {
					List<Entity> e2 = new ArrayList<>(s.getWorld().getNearbyEntities(s, 1, 1, 1));
					if (!e2.isEmpty())
						if (e2.size() > 1 || e2.get(0) != player)
							entityNear = true;
				} catch (Error e) {
				}

				if (GunUtil.isSolid(s.getBlock(), s) || entityNear || distance < 0) {
					if (Main.enableExplosionDamage) {
						ExplosionHandler.handleExplosion(s, 4, 1);
					}
					try {
						player.getWorld().playSound(s, WeaponSounds.WARHEAD_EXPLODE.getName(), 10, 0.9f);
						player.getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
						s.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE, s, 0);

					} catch (Error e3) {
						s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
						player.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
					}
					try {
						for (Entity e : s.getWorld().getNearbyEntities(s, 10, 10, 10)) {
							if (e instanceof LivingEntity) {
								((LivingEntity) e).damage((20 * 4 / e.getLocation().distance(s)), player);
							}
						}
					} catch (Error e) {
						s.getWorld().createExplosion(s, 1);
					}
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
}
