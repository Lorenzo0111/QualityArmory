package me.zombie_striker.qg.miscitems;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;

public interface ThrowableItems extends ArmoryBaseObject {

	Map<Entity, ThrowableHolder> throwItems = new ConcurrentHashMap<>();
	
	class ThrowableHolder {
		private volatile Entity holder;
		private UUID owner;
		private Grenade grenade;

		private volatile BukkitTask timer;
		/** Per-throw tick counter (only touched on the holder's region thread). */
		private int ticks = 0;
		/** Per-throw stick state used by ProxyMines. */
		private BlockFace sticky;

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

		public int getTicks() {
			return ticks;
		}

		public void setTicks(int ticks) {
			this.ticks = ticks;
		}

		public BlockFace getSticky() {
			return sticky;
		}

		public void setSticky(BlockFace sticky) {
			this.sticky = sticky;
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
