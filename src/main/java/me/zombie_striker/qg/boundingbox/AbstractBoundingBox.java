package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface AbstractBoundingBox {

    public boolean intersects(Entity shooter, Location check, Entity base);

    public boolean allowsHeadshots();

    public boolean intersectsHead(Location check, Entity base);

    public boolean intersectsBody(Location check, Entity base);

    public double maximumCheckingDistance(Entity base);
}
