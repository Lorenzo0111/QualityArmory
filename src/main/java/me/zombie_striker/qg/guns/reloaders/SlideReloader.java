package me.zombie_striker.qg.guns.reloaders;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import org.bukkit.entity.Player;
import me.zombie_striker.qg.util.FoliaRunnable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SlideReloader implements ReloadingHandler{


	public SlideReloader() {
		ReloadingManager.add(this);
	}

	Set<UUID> timeR = ConcurrentHashMap.newKeySet();
	@Override
	public boolean isReloading(Player player) {
		return timeR.contains(player.getUniqueId());
	}

	@Override
	public double reload(Player player, Gun g, int amountReloading) {
		timeR.add(player.getUniqueId());
		player.getWorld().playSound(player.getLocation(), WeaponSounds.RELOAD_CLICK.getSoundName(), 1, 1f);
			new FoliaRunnable() {
				@Override
				public void run() {
						player.getWorld().playSound(player.getLocation(), g.getReloadingSound(), 1, 1f);
						timeR.remove(player.getUniqueId());
				}
			}.runTaskLater(QAMain.getInstance(), player, Math.max((int) ((g.getReloadTime()* 20.0) - 10.0),10));
		return g.getReloadTime();
	}

	@Override
	public String getName() {
		return ReloadingManager.SLIDE_RELOAD;
	}

	@Override
	public String getDefaultReloadingSound() {
		return WeaponSounds.RELOAD_MAG_CLICK.getSoundName();
	}
}
