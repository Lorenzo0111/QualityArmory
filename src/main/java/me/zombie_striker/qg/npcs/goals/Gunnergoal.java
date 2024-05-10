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
import me.zombie_striker.qg.npcs.Gunner;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;

public class Gunnergoal implements Goal {

    private final Gunner gunner;
    private final NPC npc;

    private Entity target;
    private final Gun g;

    private final int cuttoffDistance = 400;

    private int internalAmmoCount;
    private int maxReloadCooldown = 5;
    private int reloadcooldown = 0;
    private int maxShootCooldown = 5;
    private int shootcooldown = 0;

    private int searchCooldown = 0;
    private final int searchCooldownMax = 35;

    public Gunnergoal(final Gunner gunner2, final Gun g) {
        this.gunner = gunner2;
        this.npc = this.gunner.gunner;
        this.g = g;
        this.internalAmmoCount = g.getMaxBullets();

        this.maxReloadCooldown = (int) (g.getReloadTime() * 20);
        this.maxShootCooldown = (int) (g.isAutomatic() ? 10.0 * g.getBulletsPerShot() : g.getDelayBetweenShotsInSeconds() * 20);
    }

    @Override
    public void reset() {

    }

    @SuppressWarnings("unused")
    private boolean inLineOfSight(final Entity target, final boolean setTargetNullifSolid) {
        final double diste = target.getLocation().distance(this.npc.getEntity().getLocation());
        if (!this.npc.isSpawned())
            return false;
        if (this.npc.getEntity() == null)
            return false;
        try {
            final Block btemp = ((Player) this.npc.getEntity()).getTargetBlock(null, (int) diste);
            if (btemp == null || btemp.getType() == Material.AIR) {
                return true;
            }
        } catch (Error | Exception e4) {
        }
        final Location test = ((Player) this.npc.getEntity()).getEyeLocation().clone();
        final int stepi = 4;
        final Vector step = this.npc.getEntity().getLocation().getDirection().normalize().multiply(1.0 / stepi);
        /*
         * for (int dist = 0; dist < diste * stepi; dist++) { test.add(step); if
         * (BoundingBoxUtil.closeEnough(target, test)) { break; } boolean solid =
         * GunUtil.isSolid(test.getBlock(), test); if (solid /* ||
         * GunUtil.isBreakable(test.getBlock(), test) * /) { if (setTargetNullifSolid)
         * target = null; return false; } }
         */

        return true;
    }

    @Override
    public void run(final GoalSelector goal) {
        if (this.target == null) {
            if (this.searchCooldown <= 0) {
                if (this.npc.isSpawned()) {
                    double closestDis = this.cuttoffDistance;
                    for (final Entity e : this.npc.getEntity().getNearbyEntities(this.cuttoffDistance, this.cuttoffDistance,
                            this.cuttoffDistance)) {
                        if (e instanceof LivingEntity) {
                            if (!CitizensAPI.getNPCRegistry().isNPC(e)) {
                                final double k = e.getLocation().distanceSquared(this.npc.getEntity().getLocation());
                                if (this.target == null || closestDis > k) {
                                    if (this.inLineOfSight(e, false)) {
                                        this.target = e;
                                        closestDis = k;
                                    }
                                }
                            }
                        }
                    }
                }
                this.searchCooldown = this.searchCooldownMax;
            } else {
                this.searchCooldown--;
            }
        } else {
            if (this.target.isDead()
                    || this.target.getLocation().distanceSquared(this.npc.getEntity().getLocation()) >= this.cuttoffDistance) {
                this.target = null;
            } else {
                // Util.faceEntity(npc.getEntity(), target);
                final Location tempc = this.npc.getEntity().getLocation();
                tempc.setDirection(new Vector(this.target.getLocation().getX() - tempc.getX(),
                        this.target.getLocation().getY() - tempc.getY(), this.target.getLocation().getZ() - tempc.getZ()));
                this.npc.teleport(tempc, TeleportCause.PLUGIN);
                this.npc.faceLocation(this.target.getLocation());
                if (this.shootcooldown > 0) {
                    this.shootcooldown--;
                    return;
                } else if (this.reloadcooldown > 0) {
                    this.reloadcooldown--;
                    this.internalAmmoCount = this.g.getMaxBullets();
                    return;
                }

                if (this.internalAmmoCount <= 0) {
                    this.reloadcooldown = this.maxReloadCooldown;
                    return;
                }
                if (!this.inLineOfSight(this.target, true)) {
                    this.target = null;
                    return;
                }
                this.internalAmmoCount--;
                this.shootcooldown = this.maxShootCooldown;
                GunUtil.shootHandler(this.g, (Player) this.npc.getEntity());
                GunUtil.playShoot(this.g, (Player) this.npc.getEntity());
                if (this.target == null) {
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
    public boolean shouldExecute(final GoalSelector goal) { return true; }

}
