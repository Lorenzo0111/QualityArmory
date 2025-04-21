package me.zombie_striker.qg.miscitems;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;

public interface ThrowableItems extends ArmoryBaseObject {

    HashMap<Entity, ThrowableHolder> throwItems = new HashMap<>();

    class ThrowableHolder {
        private Entity holder;
        private final UUID owner;
        private Grenade grenade;

        private BukkitTask timer;

        public ThrowableHolder(final UUID owner, final Entity holder, final Grenade grenade) {
            this.holder = holder;
            this.owner = owner;
            this.grenade = grenade;
        }

        public void setHolder(final Entity e) { this.holder = e; }

        public Entity getHolder() { return this.holder; }

        public void setTimer(final BukkitTask bt) { this.timer = bt; }

        public BukkitTask getTask() { return this.timer; }

        public UUID getOwner() { return this.owner; }

        public Grenade getGrenade() { return this.grenade; }

        public void setGrenade(final Grenade grenade) { this.grenade = grenade; }
    }

    double getThrowSpeed();

    void setThrowSpeed(double throwspeed);
}
