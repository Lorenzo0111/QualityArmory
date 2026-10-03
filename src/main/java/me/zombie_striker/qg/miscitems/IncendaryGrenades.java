package me.zombie_striker.qg.miscitems;

import java.util.List;

import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.zombie_striker.qg.util.FoliaRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class IncendaryGrenades extends Grenade {

	public IncendaryGrenades(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
			String displayname, List<String> lore, MaterialStorage ms) {
		super(ingg, cost, damage, explosionreadius, name, displayname, lore, ms);
	}

	@Override
	public boolean onPull(Player e, ItemStack usedItem) {
		Player thrower = e.getPlayer();
		if(!QAMain.autoarm)
		if (throwItems.containsKey(thrower)) {
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
			thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
			return true;
		}
		thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
		h.setTimer(new FoliaRunnable() {
			@Override
			public void run() {
				Entity holderEntity = h.getHolder();
				if (holderEntity == null) {
					cancel();
					return;
				}
				FoliaRunnable.runEntityTask(QAMain.getInstance(), holderEntity,
						() -> handleThrowableTick(h),
						() -> {
							throwItems.remove(holderEntity);
							BukkitTask t = h.getTask();
							if (t != null) t.cancel();
						});
			}
		}.runTaskTimer(QAMain.getInstance(), 5*20, 10));
		throwItems.put(thrower, h);

		return true;
	}

	private void handleThrowableTick(ThrowableHolder h) {
		try {
			h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(),
					h.getHolder().getLocation(), 0);
			for(int i = 0; i < 4; i ++) {
				//TODO: Check: This goes in three directions, and one stays still
				h.getHolder().getWorld().spawnParticle(org.bukkit.Particle.LAVA,
						h.getHolder().getLocation(), i);
			}
			h.getHolder().getWorld().playSound(h.getHolder().getLocation(), WeaponSounds.HISS.getSoundName(), 2f,
					1f);
		} catch (Error e3) {
			h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
			h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 3, 0.7f);
		}
		h.setTicks(h.getTicks() + 1);
		QAMain.DEBUG("Fireticks");
		if (h.getTicks() == 1) {
			if (h.getHolder() instanceof Player) {
				((LivingEntity) h.getHolder()).setFireTicks(h.getHolder().getMaxFireTicks()/5);
				removeGrenade(((Player) h.getHolder()));
			}
		} else if (h.getTicks() == 40) {
			if (h.getHolder() instanceof Item) {
				Grenade.getGrenades().remove(h.getHolder());
				h.getHolder().remove();
			}
			throwItems.remove(h.getHolder());
			if (h.getTask() != null) h.getTask().cancel();
		} else {
			for(Entity e : h.getHolder().getNearbyEntities(radius, radius, radius))
				if(e instanceof LivingEntity) {
					final Entity target = e;
					QAMain.DEBUG("Firedamage to "+target.getName());
					FoliaRunnable.runEntityTask(QAMain.getInstance(), target, () -> {
						try {
							if (ProtectionHandler.canPvp(target.getLocation())) {
								target.setFireTicks(20);
							}
						}catch (Error error){
							target.setFireTicks(20);
						}
					});
				}
		}
	}

}
