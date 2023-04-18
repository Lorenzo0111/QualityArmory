package me.zombie_striker.qualityarmory.guns;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Bullet {

    private Location bulletLocation;
    private Vector direction;
    private UUID shooter;
    private float damage;
    private float speed;
    private float internalVelocity = 1.0F;

    public Bullet(Location starting, Vector direction, UUID shooter, float damage, float speed) {
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

    public float getSpeed() {
    return speed;
    }

    public float getInternalVelocity() {
        return internalVelocity;
    }

    public void setInternalVelocity(float internalVelocity) {
        this.internalVelocity = internalVelocity;
    }
}
