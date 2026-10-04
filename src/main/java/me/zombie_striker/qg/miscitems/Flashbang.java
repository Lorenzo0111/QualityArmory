package me.zombie_striker.qg.miscitems;

import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.utils.FoliaRunnable;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class Flashbang extends Grenade {

	public Flashbang(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
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
		}.runTaskLater(QAMain.getInstance(), getFuseTicks()));
		throwItems.put(thrower, h);
		return true;
	}

	private void handleThrowableTick(ThrowableHolder h) {
		try {
			h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(),
					h.getHolder().getLocation(), 0);
			h.getHolder().getWorld().playSound(h.getHolder().getLocation(), WeaponSounds.FLASHBANG.getSoundName(),
					3f, 1f);
		} catch (Error e3) {
			h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
			h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
		}
		try {
			for (Entity e : h.getHolder().getNearbyEntities(radius, radius, radius)) {
				if (e instanceof LivingEntity) {
					final LivingEntity target = (LivingEntity) e;
					QAMain.DEBUG("Flashbaned "+target.getName());
					FoliaRunnable.runEntityTask(QAMain.getInstance(), target, () ->
							target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2)));
				}
			}
		} catch (Error e) {
		}
		if (h.getHolder() instanceof Player) {
			QAMain.DEBUG("Blinded player");
			((LivingEntity) h.getHolder())
					.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
			removeGrenade(((Player) h.getHolder()));
		}
		if (h.getHolder() instanceof Item) {
			Grenade.getGrenades().remove(h.getHolder());
			h.getHolder().remove();
		}

		throwItems.remove(h.getHolder());
	}

}
