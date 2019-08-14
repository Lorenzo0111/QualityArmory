package me.zombie_striker.qg.npcs.goals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.handlers.HeadShotUtil;
import me.zombie_striker.qg.npcs.Gunner;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;

public class Gunnergoal implements Goal {

	private Gunner gunner;
	private NPC npc;

	private Entity target;
	private Gun g;

	private int cuttoffDistance = 400;

	private int internalAmmoCount;
	private int maxReloadCooldown = 5;
	private int reloadcooldown = 0;
	private int maxShootCooldown = 5;
	private int shootcooldown = 0;

	private int searchCooldown = 0;
	private int searchCooldownMax = 35;

	public Gunnergoal(Gunner gunner2, Gun g) {
		gunner = gunner2;
		this.npc = gunner.gunner;
		this.g = g;
		internalAmmoCount = g.getMaxBullets();

		maxReloadCooldown = (int) (g.getReloadTime() * 20);
		maxShootCooldown = (int) (g.isAutomatic() ? 10.0*g.getBulletsPerShot()
				: g.getDelayBetweenShotsInSeconds() * 20);
	}

	@Override
	public void reset() {

	}

	private boolean inLineOfSight(Entity target, boolean setTargetNullifSolid) {
		double diste = target.getLocation().distance(npc.getEntity().getLocation());
		if (!npc.isSpawned())
			return false;
		if (npc.getEntity() == null)
			return false;
		try {
			Block btemp = ((Player) npc.getEntity()).getTargetBlock(null, (int) diste);
			if (btemp == null || btemp.getType() == Material.AIR) {
				return true;
			}
		} catch (Error | Exception e4) {
		}
		Location test = ((Player) npc.getEntity()).getEyeLocation().clone();
		int stepi = 4;
		Vector step = npc.getEntity().getLocation().getDirection().normalize().multiply(1.0 / stepi);
		for (int dist = 0; dist < diste * stepi; dist++) {
			test.add(step);
			if (HeadShotUtil.closeEnough(target, test)) {
				break;
			}
			boolean solid = GunUtil.isSolid(test.getBlock(), test);
			if (solid /* || GunUtil.isBreakable(test.getBlock(), test) */) {
				if (setTargetNullifSolid)
					target = null;
				return false;
			}
		}

		return true;
	}

	@Override
	public void run(GoalSelector goal) {
		if (target == null) {
			if (searchCooldown <= 0) {
				if (npc.isSpawned()) {
					double closestDis = cuttoffDistance;
					for (Entity e : npc.getEntity().getNearbyEntities(cuttoffDistance, cuttoffDistance,
							cuttoffDistance)) {
						if (e instanceof LivingEntity) {
							if (!CitizensAPI.getNPCRegistry().isNPC(e)) {
								double k = e.getLocation().distanceSquared(npc.getEntity().getLocation());
								if (target == null || closestDis > k) {
									if (inLineOfSight(e, false)) {
										target = e;
										closestDis = k;
									}
								}
							}
						}
					}
				}
				searchCooldown = searchCooldownMax;
			} else {
				searchCooldown--;
			}
		} else {
			if (target.isDead()
					|| target.getLocation().distanceSquared(npc.getEntity().getLocation()) >= cuttoffDistance) {
				target = null;
			} else {
				// Util.faceEntity(npc.getEntity(), target);
				Location tempc = npc.getEntity().getLocation();
				tempc.setDirection(new Vector(target.getLocation().getX() - tempc.getX(),
						target.getLocation().getY() - tempc.getY(), target.getLocation().getZ() - tempc.getZ()));
				npc.teleport(tempc, TeleportCause.PLUGIN);
				npc.faceLocation(target.getLocation());
				if (shootcooldown > 0) {
					shootcooldown--;
					return;
				} else if (reloadcooldown > 0) {
					reloadcooldown--;
					internalAmmoCount = g.getMaxBullets();
					return;
				}

				if (internalAmmoCount <= 0) {
					reloadcooldown = maxReloadCooldown;
					return;
				}
				if (!inLineOfSight(target, true)) {
					target=null;
					return;
				}
				internalAmmoCount--;
				shootcooldown = maxShootCooldown;
				GunUtil.shootHandler(g, (Player) npc.getEntity(), g.getBulletsPerShot());
				GunUtil.playShoot(g, (Player) npc.getEntity());
				if (target == null) {
					Bukkit.broadcastMessage("Shooting no target");
				}
				// Bukkit.broadcastMessage("run1");
				// new BukkitRunnable() {
				// public void run() {
				// GunUtil.shoot(g, (Player) npc.getEntity(), g.getSway(), g.getDamage(), 1,
				// g.getMaxDistance());
				// GunUtil.playShoot(g, null, (Player) npc.getEntity());
				// Bukkit.broadcastMessage("run");
				// }
				// }.runTaskTimer(Main.getInstance(), 10 / g.getBulletsPerShot(), 10 /
				// g.getBulletsPerShot());

			}
		}
	}

	@Override
	public boolean shouldExecute(GoalSelector goal) {
		return true;
	}

}
