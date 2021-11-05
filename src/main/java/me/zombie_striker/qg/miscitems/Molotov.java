package me.zombie_striker.qg.miscitems;

import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.hooks.protection.ProtectionHandler;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Molotov extends Grenade {

	public Molotov(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
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
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower);
		h.setTimer(new BukkitRunnable() {

			int k = 0;

			@Override
			public void run() {
				try {
					for(int i = 0; i < 8; i++) {
						double xoffset = ((Math.random() * 2) - 1)*radius;
						double zoffset = ((Math.random() * 2) - 1)*radius;
						h.getHolder().getWorld().spawnParticle(Particle.FLAME,
								h.getHolder().getLocation().clone().add(xoffset,0,zoffset), 0);
					}
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
				if(!(h.getHolder() instanceof Player)&&h.getHolder().isOnGround())
				k++;
				QAMain.DEBUG("Fireticks");
				if (k == 40) {
					if (h.getHolder() instanceof Item) {
						h.getHolder().remove();
					}
					throwItems.remove(h.getHolder());
					this.cancel();
				} else {
					for(Entity e : h.getHolder().getNearbyEntities(radius, radius, radius)) 
						if(e instanceof LivingEntity) {
							QAMain.DEBUG("Firedamage to "+e.getName());
							try {
								if (ProtectionHandler.canPvp(e.getLocation())) {
									e.setFireTicks(20);
								}
							}catch (Error error){
								e.setFireTicks(20);
							}
						}
				}
			}
		}.runTaskTimer(QAMain.getInstance(),5*20,10));
		throwItems.put(thrower, h);

		return true;
	}

}
