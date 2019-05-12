package me.zombie_striker.qg.npcs.goals;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.ai.PathStrategy;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.npc.NPC;

public class GunnerLookAt implements Navigator{

	@Override
	public void cancelNavigation() {
		
	}

	@Override
	public NavigatorParameters getDefaultParameters() {
		return null;
	}

	@Override
	public EntityTarget getEntityTarget() {
		return null;
	}

	@Override
	public NavigatorParameters getLocalParameters() {
		return null;
	}

	@Override
	public NPC getNPC() {
		return null;
	}

	@Override
	public PathStrategy getPathStrategy() {
		return null;
	}

	@Override
	public Location getTargetAsLocation() {
		return null;
	}

	@Override
	public TargetType getTargetType() {
		return null;
	}

	@Override
	public boolean isNavigating() {
		return false;
	}

	@Override
	public boolean isPaused() {
		return false;
	}

	@Override
	public void setPaused(boolean arg0) {
		
	}

	@Override
	public void setTarget(Iterable<Vector> arg0) {
		
	}

	@Override
	public void setTarget(Location arg0) {
		
	}

	@Override
	public void setTarget(Entity arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

}
