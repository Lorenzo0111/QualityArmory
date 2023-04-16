package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.boundingbox.AbstractBoundingBox;
import me.zombie_striker.qualityarmory.boundingbox.BoundingBoxManager;
import me.zombie_striker.qualityarmory.guns.Bullet;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import me.zombie_striker.qualityarmory.interfaces.ISettingsReloader;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BulletHandler implements IHandler, ISettingsReloader {

    private final QAMain qaMain;
    private float headshotMultiplier;
    private List<Bullet> bullets = new ArrayList<>();

    public BulletHandler(QAMain qaMain) {
        this.qaMain = qaMain;
        new BukkitRunnable() {
            @Override
            public void run() {
                tickAll();
            }
        }.runTaskTimer(qaMain, 1, 1);
    }

    public void registerBullet(Bullet bullet) {
        this.bullets.add(bullet);
    }

    public boolean tick(Bullet bullet) {
        Vector directionToCenter = bullet.getDirection().clone().multiply(bullet.getSpeed() / 2);
        Location center = bullet.getBulletLocation().clone().add(directionToCenter);
        Entity closestEntity = null;
        double closestDistance = bullet.getSpeed();
        boolean headshot = false;
        for (Entity entity : center.getWorld().getNearbyEntities(center, bullet.getSpeed(), bullet.getSpeed(), bullet.getSpeed())) {
            if (!(entity instanceof Damageable))
                continue;
            AbstractBoundingBox abstractBoundingBox = BoundingBoxManager.getBoundingBox(entity);

            double distanceToEntity = bullet.getBulletLocation().distance(entity.getLocation());
            Vector distanceToMob = bullet.getDirection().clone().multiply(distanceToEntity);
            Location proposedLocationOfMob = bullet.getBulletLocation().clone().add(distanceToMob);
            if (closestDistance > distanceToEntity) {
                if (proposedLocationOfMob.distanceSquared(entity.getLocation()) < abstractBoundingBox.maximumCheckingDistance(entity)) {
                    if (abstractBoundingBox.intersects(null, proposedLocationOfMob, entity)) {
                        headshot = abstractBoundingBox.allowsHeadshots()&&abstractBoundingBox.intersectsHead(proposedLocationOfMob,entity);
                        closestEntity = entity;
                        closestDistance = distanceToEntity;
                    }
                }
            }
        }

        if (closestEntity != null) {
            if(headshot){
                ((Damageable) closestEntity).damage(bullet.getDamage()*headshotMultiplier);
            }else {
                ((Damageable) closestEntity).damage(bullet.getDamage());
            }
        }

        Location particleLocation = bullet.getBulletLocation().clone();
        Vector shortDistance = bullet.getDirection().multiply(0.1);
        double stepBeforeParticle = 0.5;
        Block lastBlock = null;
        for (double i = 0; i < closestDistance; i += 0.1) {
            particleLocation.add(shortDistance);

            //TODO: Spawn BulletTrail of the ammo used

            double res = lastBlock != particleLocation.getBlock() ? qaMain.getBlockCollisionHandler().getResistance(particleLocation.getBlock(), particleLocation) : 0.0;
            if (bullet.getInternalVelocity() - res <= 0) {
                return true;
            } else {
                bullet.setInternalVelocity((float) (bullet.getInternalVelocity() - res));
            }
            lastBlock = particleLocation.getBlock();
        }
        return closestEntity != null;
    }

    public void tickAll() {
        List<Bullet> bullets = new ArrayList<>(this.bullets);
        for (Bullet bullet : bullets) {
            if (this.tick(bullet))
                this.bullets.remove(bullet);
        }
    }

    @Override
    public void init(QAMain main) {

    }

    @Override
    public void reloadSettings(QAMain main) {
        this.headshotMultiplier= (float) qaMain.getSettingIfPresent(ConfigKey.SETTING_HEADSHOT_MULTIPLIER.getKey(),2.0);
    }
}
