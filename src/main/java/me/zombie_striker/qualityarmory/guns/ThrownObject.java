package me.zombie_striker.qualityarmory.guns;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class ThrownObject {


    private Entity holder;
    private UUID owner;
    private ThrownObject thrownObject;

    private BukkitTask timer;

    public ThrownObject(UUID owner, Entity holder, ThrownObject thrownObject) {
        this.holder = holder;
        this.owner = owner;
        this.thrownObject = thrownObject;
    }

    public void setHolder(Entity e) {
        this.holder = e;
    }

    public Entity getHolder() {
        return holder;
    }

    public void setTimer(BukkitTask bt) {
        this.timer = bt;
    }

    public BukkitTask getTask() {
        return timer;
    }

    public UUID getOwner() {
        return owner;
    }

    public ThrownObject getThrownObject() {
        return thrownObject;
    }

    public void setThrownObject(ThrownObject thrownObject) {
        this.thrownObject = thrownObject;
    }
}
