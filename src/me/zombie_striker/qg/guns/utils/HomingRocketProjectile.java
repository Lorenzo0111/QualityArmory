package me.zombie_striker.qg.guns.utils;

import java.util.ArrayList;
import java.util.List;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
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

	//private final float turnRate = 0.6f;

	public HomingRocketProjectile(final Location starting, final Player player, final Vector dir) {
		new BukkitRunnable() {
			Location RPGLOCATION = starting.clone();
			int distance = 220;
			Vector vect = dir;

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				distance--;
				RPGLOCATION.add(vect);
				Block lookat = player.getTargetBlock(null, 300);
				if (lookat != null && lookat.getType() != Material.AIR) {
					if (Main.isGun(player.getItemInHand())) {
						Gun g = Main.getGun(player.getItemInHand());
						if (g.getChargingVal() != null && g.getChargingVal() instanceof HomingRPGCharger) {
							/*
							 * Vector newVect = new Vector(lookat.getX() - RPGLOCATION.getBlockX(),
							 * lookat.getY() - RPGLOCATION.getBlockY(), lookat.getZ() -
							 * RPGLOCATION.getBlockZ()).normalize();
							 * 
							 * double newX = newVect.getX() - vect.getBlockX() > turnRate ? vect.getBlockX()
							 * + turnRate : newVect.getX() - vect.getBlockX() < -turnRate ? vect.getBlockX()
							 * - turnRate : newVect.getX(); double newY = newVect.getY() - vect.getBlockY()
							 * > turnRate ? vect.getBlockY() + turnRate : newVect.getY() - vect.getBlockY()
							 * < -turnRate ? vect.getBlockY() - turnRate : newVect.getY(); double newZ =
							 * newVect.getZ() - vect.getBlockZ() > turnRate ? vect.getBlockZ() + turnRate :
							 * newVect.getZ() - vect.getBlockZ() < -turnRate ? vect.getBlockZ() - turnRate :
							 * newVect.getZ();
							 * 
							 * vect = new Vector(newX, newY, newZ);
							 */
							Vector newDir = lookat.getLocation().clone().subtract(RPGLOCATION).toVector().normalize();
							vect = newDir;
						}
					}
				}
				try {
					RPGLOCATION.getWorld().spawnParticle(org.bukkit.Particle.SMOKE_LARGE, RPGLOCATION, 0);
					RPGLOCATION.getWorld().spawnParticle(org.bukkit.Particle.FIREWORKS_SPARK, RPGLOCATION, 0);
					player.getWorld().playSound(RPGLOCATION, MultiVersionLookup.getDragonGrowl(), 1, 2f);

				} catch (Error e2) {
					RPGLOCATION.getWorld().playEffect(RPGLOCATION, Effect.valueOf("CLOUD"), 0);
					player.getWorld().playSound(RPGLOCATION, Sound.valueOf("ENDERDRAGON_GROWL"), 1, 2f);
				}
				boolean entityNear = false;
				try {
					List<Entity> e2 = new ArrayList<>(RPGLOCATION.getWorld().getNearbyEntities(RPGLOCATION, 1, 1, 1));
					if (!e2.isEmpty())
						if (e2.size() > 1 || e2.get(0) != player)
							entityNear = true;
				} catch (Error e) {
				}

				if (GunUtil.isSolid(RPGLOCATION.getBlock(), RPGLOCATION) || entityNear || distance < 0) {
					if (Main.enableExplosionDamage) {
						ExplosionHandler.handleExplosion(RPGLOCATION, 4, 2);
					}
					try {
						player.getWorld().playSound(RPGLOCATION, WeaponSounds.WARHEAD_EXPLODE.getName(), 10, 0.9f);
						player.getWorld().playSound(RPGLOCATION, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
						RPGLOCATION.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE, RPGLOCATION, 0);

					} catch (Error e3) {
						RPGLOCATION.getWorld().playEffect(RPGLOCATION, Effect.valueOf("CLOUD"), 0);
						player.getWorld().playSound(RPGLOCATION, Sound.valueOf("EXPLODE"), 8, 0.7f);
					}
					try {
						for (Entity e : RPGLOCATION.getWorld().getNearbyEntities(RPGLOCATION, 10, 10, 10)) {
							if (e instanceof LivingEntity) {
								((LivingEntity) e).damage((20 * 4 / e.getLocation().distance(RPGLOCATION)), player);
							}
						}
					} catch (Error e) {
						RPGLOCATION.getWorld().createExplosion(RPGLOCATION, 1);
					}
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
	}
}
