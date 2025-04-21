package me.zombie_striker.qg.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.cryptomorin.xseries.particles.XParticle;
import com.cryptomorin.xseries.reflection.XReflection;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;

public class ParticleHandlers {

    public static boolean is13 = true;

    public static void initValues() { ParticleHandlers.is13 = XReflection.supports(13); }

    public static void spawnExplosion(final Location loc) {
        try {
            loc.getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(), loc, 1);
        } catch (Error | Exception e4) {
        }
        // TODO: Do lights n stuff
        try {
            if (Bukkit.getPluginManager().getPlugin("LightAPI") != null) {
                final Location loc2 = loc;
                LightAPI.get().setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 15);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        LightAPI.get().setLightLevel(loc2.getWorld().getName(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), 0);
                    }
                }.runTaskLater(QAMain.getInstance(), 10);
            }
        } catch (Error | Exception e5) {
        }
    }

    public static void spawnMushroomCloud(final Location loc) {
        try {
            for (double d = 0; d < 2 * Math.PI; d += Math.PI / 48) {
                double radius = 2;

                ParticleHandlers.spawnParticle(1.0, 1.0, 1.0,
                        new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius), loc.getY(), loc.getZ() + (Math.cos(d) * radius)));
                radius = 1.8;
                ParticleHandlers.spawnParticle(1.0, 0.0, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 0.5, loc.getZ() + (Math.cos(d) * radius)));
                radius = 1.6;
                ParticleHandlers.spawnParticle(1.0, 0.2, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 1, loc.getZ() + (Math.cos(d) * radius)));
                radius = 1.3;
                ParticleHandlers.spawnParticle(1.0, 0.2, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 1.5, loc.getZ() + (Math.cos(d) * radius)));
                radius = 1.1;
                ParticleHandlers.spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 2, loc.getZ() + (Math.cos(d) * radius)));
                radius = 1;
                ParticleHandlers.spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 2.5, loc.getZ() + (Math.cos(d) * radius)));
                radius = 3;
                ParticleHandlers.spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 3, loc.getZ() + (Math.cos(d) * radius)));
                radius = 2.8;
                ParticleHandlers.spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 3.5, loc.getZ() + (Math.cos(d) * radius)));
                radius = 2.5;
                ParticleHandlers.spawnParticle(1.0, 1.0, 1.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 4, loc.getZ() + (Math.cos(d) * radius)));
                radius = 2;
                ParticleHandlers.spawnParticle(1.0, 1.0, 1.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 4.5, loc.getZ() + (Math.cos(d) * radius)));
                radius = 1.5;
                ParticleHandlers.spawnParticle(1.0, 0.2, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 5, loc.getZ() + (Math.cos(d) * radius)));
                radius = 0.8;
                ParticleHandlers.spawnParticle(1.0, 0.5, 0.0, new Location(loc.getWorld(), loc.getX() + (Math.sin(d) * radius),
                        loc.getY() + 5.5, loc.getZ() + (Math.cos(d) * radius)));
            }
        } catch (Error | Exception e4) {
        }

        try {
            if (Bukkit.getPluginManager().getPlugin("LightAPI") != null) {
                final Location loc2 = loc;
                LightAPI.get().setLightLevel(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 15);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        LightAPI.get().setLightLevel(loc2.getWorld().getName(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), 0);
                    }
                }.runTaskLater(QAMain.getInstance(), 20);
            }
        } catch (Error | Exception e5) {
        }
    }

    public static void spawnGunParticles(final Gun g, final Location loc) {
        try {
            if (g.getParticle() != null)
                if (g.getParticle() == XParticle.DUST.get()) {
                    ParticleHandlers.spawnParticle(g.getParticleR(), g.getParticleG(), g.getParticleB(), loc);
                } else if (g.getParticle() == XParticle.BLOCK.get() || g.getParticle() == Particle.BLOCK
                        || g.getParticle() == Particle.FALLING_DUST) {
                    loc.getWorld().spawnParticle(g.getParticle(), loc, 1, g.getParticleMaterial().createBlockData());
                } else {
                    loc.getWorld().spawnParticle(g.getParticle(), loc, g.getParticleData());
                }
        } catch (Error | Exception e4) {
        }
    }

    public static void spawnParticle(final double r, final double g, final double b, final Location loc) {
        try {
            if (ParticleHandlers.is13) {
                final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB((int) (r * 255), (int) (g * 255), (int) (b * 255)),
                        1);
                for (final Player player : loc.getWorld().getPlayers()) {
                    if (player.getLocation().distanceSquared(loc) < 60 * 60)
                        player.spawnParticle(XParticle.DUST.get(), loc.getX(), loc.getY(), loc.getZ(), 0, 0, 0, 0, dust);
                }
                /*
                 * Particle.DustOptions dust = new Particle.DustOptions( Color.fromRGB((int) (r
                 * * 255), (int) (g * 255), (int) (b * 255)), 1);
                 * loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(),
                 * loc.getZ(), 0, 0, 0, 0, dust);
                 */
            } else {

                for (final Player player : loc.getWorld().getPlayers()) {
                    if (player.getLocation().distanceSquared(loc) < 60 * 60)
                        player.spawnParticle(XParticle.DUST.get(), loc.getX(), loc.getY(), loc.getZ(), 0, r, g, b, 1);
                }
                // loc.getWorld().spawnParticle(Particle.REDSTONE, loc.getX(), loc.getY(),
                // loc.getZ(), 0, r, g, b, 1);
            }
        } catch (Error | Exception e45) {
            e45.printStackTrace();
        }
    }

    public static void spawnMuzzleSmoke(final Player shooter, final Location loc) {
        try {
            double theta = Math.atan2(shooter.getLocation().getDirection().getX(), shooter.getLocation().getDirection().getZ());

            theta -= (Math.PI / 8);

            final double x = Math.sin(theta);
            final double z = Math.cos(theta);

            final Location l = loc.clone().add(x, 0, z);

            for (int i = 0; i < 2; i++)
                loc.getWorld().spawnParticle(XParticle.EFFECT.get(), l, 0);
        } catch (Error | Exception e4) {
        }
    }
}
