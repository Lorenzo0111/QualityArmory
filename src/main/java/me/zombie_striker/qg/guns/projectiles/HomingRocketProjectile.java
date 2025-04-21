package me.zombie_striker.qg.guns.projectiles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.particles.XParticle;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QAProjectileExplodeEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import me.zombie_striker.qg.handlers.ParticleHandlers;

public class HomingRocketProjectile implements RealtimeCalculationProjectile {
    public HomingRocketProjectile() { ProjectileManager.add(this); }

    @Override
    public void spawn(final Gun g, final Location starting, final Player player, final Vector dir) {
        new BukkitRunnable() {
            Location RPGLOCATION = starting.clone();
            int distance = g.getMaxDistance();
            Vector vect = dir;

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for (int tick = 0; tick < g.getVelocityForRealtimeCalculations(); tick++) {
                    this.distance--;
                    this.RPGLOCATION.add(this.vect);
                    final Block lookat = player.getTargetBlock(null, 300);
                    if (lookat != null && lookat.getType() != Material.AIR) {
                        if (QualityArmory.isGun(player.getInventory().getItemInMainHand())) {
                            final Gun g = me.zombie_striker.qg.api.QualityArmory.getGun(player.getInventory().getItemInMainHand());
                            if (g.usesCustomProjctiles() && g.getCustomProjectile() instanceof HomingRocketProjectile) {
                                final Vector newDir = lookat.getLocation().clone().subtract(this.RPGLOCATION).toVector().normalize();
                                this.vect = newDir;
                            }
                        }
                    }
                    ParticleHandlers.spawnGunParticles(g, this.RPGLOCATION);
                    try {
                        player.getWorld().playSound(this.RPGLOCATION, MultiVersionLookup.getDragonGrowl(), 1, 2f);

                    } catch (final Error e2) {
                        this.RPGLOCATION.getWorld().playEffect(this.RPGLOCATION, Effect.valueOf("CLOUD"), 0);
                        player.getWorld().playSound(this.RPGLOCATION, Sound.valueOf("ENDERDRAGON_GROWL"), 1, 2f);
                    }
                    boolean entityNear = false;
                    try {
                        final List<Entity> e2 = new ArrayList<>(this.RPGLOCATION.getWorld().getNearbyEntities(this.RPGLOCATION, 1, 1, 1));
                        for (final Entity e : e2) {
                            if (e != player && (!(e instanceof Player) || ((Player) e).getGameMode() != GameMode.SPECTATOR))
                                entityNear = true;
                        }
                    } catch (final Error e) {
                    }

                    if (GunUtil.isSolid(this.RPGLOCATION.getBlock(), this.RPGLOCATION) || entityNear || this.distance < 0) {
                        if (QAMain.enableExplosionDamage) {
                            final QAProjectileExplodeEvent event = new QAProjectileExplodeEvent(HomingRocketProjectile.this,
                                    this.RPGLOCATION);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled())
                                ExplosionHandler.handleExplosion(this.RPGLOCATION, 4, 2);
                        }
                        try {
                            player.getWorld().playSound(this.RPGLOCATION, WeaponSounds.WARHEAD_EXPLODE.getName(), 10, 0.9f);
                            player.getWorld().playSound(this.RPGLOCATION, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
                            this.RPGLOCATION.getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(), this.RPGLOCATION, 0);

                        } catch (final Error e3) {
                            this.RPGLOCATION.getWorld().playEffect(this.RPGLOCATION, Effect.valueOf("CLOUD"), 0);
                            player.getWorld().playSound(this.RPGLOCATION, Sound.valueOf("EXPLODE"), 8, 0.7f);
                        }
                        ExplosionHandler.handleAOEExplosion(player, this.RPGLOCATION, g.getDamage(), g.getExplosionRadius());
                        this.cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(QAMain.getInstance(), 0, 1);
    }

    @Override
    public String getName() { return ProjectileManager.HOMING_RPG; }
}
