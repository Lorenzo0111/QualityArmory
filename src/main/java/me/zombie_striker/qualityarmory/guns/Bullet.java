package me.zombie_striker.qualityarmory.guns;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Bullet {

    private Location bulletLocation;
    private Vector direction;
    private UUID shooter;
    private float damage;
    private double speed;
    private float internalVelocity = 1.0F;
    private double distanceTraveled = 0;

    public Bullet(Location starting, Vector direction, UUID shooter, float damage, double speed) {
        this.bulletLocation = starting;
        this.damage = damage;
        this.direction = direction;
        this.shooter = shooter;
        this.speed = speed;
    }

    public void setBulletLocation(Location bulletLocation) {
        this.bulletLocation = bulletLocation;
    }

    public float getDamage() {
        return damage;
    }

    public Vector getDirection() {
        return direction;
    }

    public UUID getShooter() {
        return shooter;
    }

    public Location getBulletLocation() {
        return bulletLocation;
    }

    public double getSpeed() {
    return speed;
    }

    public float getInternalVelocity() {
        return internalVelocity;
    }

    public void setInternalVelocity(float internalVelocity) {
        this.internalVelocity = internalVelocity;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }
}
