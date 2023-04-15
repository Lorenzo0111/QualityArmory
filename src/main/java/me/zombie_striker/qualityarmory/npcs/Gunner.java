package me.zombie_striker.qualityarmory.npcs;

import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.npcs.goals.Gunnergoal;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;

public class Gunner {

	public NPC gunner;

	@SuppressWarnings("deprecation")
	public static Gunner createGunner(Location loc, final String gun) {
		final Gunner gunner = new Gunner();
		gunner.gunner = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "GunnerTest");
		gunner.gunner.spawn(loc);
		QAMain.gunners.add(gunner);
		SkinnableEntity se = ((SkinnableEntity)gunner.gunner.getEntity());
		se.setSkinName("army");
		// gunner.gunner.addTrait(GunnerTrait.class);
		// new BukkitRunnable() {
		// @Override
		// public void run() {
		Gun g = me.zombie_striker.qualityarmory.api.QualityArmory.getGunByName(gun);
		if (g == null) {
			Bukkit.broadcastMessage("gun is null");
		} else {
			((Player) gunner.gunner.getEntity()).setItemInHand(CustomItemManager.getItemType("gun").getItem(g.getItemData().getMat(),g.getItemData().getData(),g.getItemData().getVariant()));
		}
		gunner.gunner.getDefaultGoalController().addGoal(new Gunnergoal(gunner, g), 1);
		
		//LookClose lookclose = new LookClose();
		//lookclose.setRange(64);
		//lookclose.setRealisticLooking(true);
		//lookclose.linkToNPC(gunner.gunner);
		//gunner.gunner.addTrait(lookclose);

		// }
		// }.runTaskLater(Main.getInstance(), 1);

		return gunner;
	}
	
	public void dispose() {
		gunner.despawn();
		//TODO: Check if destroying is best option, or if multiple entities are okay.
		gunner.destroy();
	}

}
