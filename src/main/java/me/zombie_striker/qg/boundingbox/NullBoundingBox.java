package me.zombie_striker.qg.boundingbox;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class NullBoundingBox implements AbstractBoundingBox {

    @Override
    public boolean intersects(final Entity shooter, final Location check, final Entity base) { return false; }

    @Override
    public boolean allowsHeadshots() { return true; }

    @Override
    public boolean intersectsHead(final Location check, final Entity base) { return false; }

    @Override
    public boolean intersectsBody(final Location check, final Entity base) { return false; }

    @Override
    public double maximumCheckingDistance(final Entity base) { return 0; }

}
