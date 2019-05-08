package me.zombie_striker.qg.miscitems;

import org.bukkit.entity.Player;

import me.zombie_striker.qg.ArmoryBaseObject;

public interface InteractableMisc extends ArmoryBaseObject {
	public void onRightClick(Player thrower);

	public void onLeftClick(Player thrower);

}
