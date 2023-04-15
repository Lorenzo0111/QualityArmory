package me.zombie_striker.qualityarmory.npcs.goals;

import net.citizensnpcs.api.ai.event.CancelReason;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.ai.PathStrategy;
import net.citizensnpcs.api.ai.TargetType;
import net.citizensnpcs.api.npc.NPC;

import java.util.function.Function;

public class GunnerLookAt implements Navigator{

	@Override
	public void cancelNavigation() {
		
	}

	@Override
	public void cancelNavigation(CancelReason cancelReason) {

	}

	@Override
	public boolean canNavigateTo(Location location) {
		return false;
	}

	@Override
	public boolean canNavigateTo(Location location, NavigatorParameters navigatorParameters) {
		return false;
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
	public void setStraightLineTarget(Entity entity, boolean b) {

	}

	@Override
	public void setStraightLineTarget(Location location) {

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

	@Override
	public void setTarget(Function<NavigatorParameters, PathStrategy> function) {

	}

}
