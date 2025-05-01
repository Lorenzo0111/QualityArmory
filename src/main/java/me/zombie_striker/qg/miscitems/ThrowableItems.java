package me.zombie_striker.qg.miscitems;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public interface ThrowableItems {

	HashMap<Entity, ThrowableHolder> throwItems = new HashMap<>();
	
	class ThrowableHolder {
		private Entity holder;
		private UUID owner;
		private Grenade grenade;

		private BukkitTask timer;

		public ThrowableHolder(UUID owner, Entity holder, Grenade grenade) {
			this.holder = holder;
			this.owner = owner;
			this.grenade = grenade;
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

		public Grenade getGrenade() {
			return grenade;
		}

		public void setGrenade(Grenade grenade) {
			this.grenade = grenade;
		}
	}


	double getThrowSpeed();
	void setThrowSpeed(double throwspeed);
}
