package me.zombie_striker.qg.miscitems;

import me.zombie_striker.qg.utils.Compat;
import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.handlers.GunDamageHandler;
import me.zombie_striker.qg.api.QAThrowableExplodeEvent;
import me.zombie_striker.qg.handlers.ExplosionHandler;
import me.zombie_striker.qg.utils.FoliaRunnable;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class StickyGrenades extends Grenade {

	public StickyGrenades(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
                          String displayname, List<String> lore, MaterialStorage ms) {
		super(ingg, cost, damage, explosionreadius, name, displayname, lore, ms);
	}

	@Override
	public boolean onRMB(Player thrower, ItemStack usedItem) {
		if(QAMain.getConfiguration().features.enableAutoArmGrenades)
			onPull(thrower,usedItem);
		if (ThrowableItemRegistry.containsKey(thrower) && ThrowableItemRegistry.get(thrower).getGrenade().equals(this)) {
			ThrowableHolder holder = ThrowableItemRegistry.get(thrower);
			ItemStack grenadeStack = thrower.getItemInHand();
			ItemStack temp = grenadeStack.clone();
			temp.setAmount(1);
			if (thrower.getGameMode() != GameMode.CREATIVE) {
				if (grenadeStack.getAmount() > 1) {
					grenadeStack.setAmount(grenadeStack.getAmount() - 1);
				} else {
					grenadeStack = null;
				}
				thrower.setItemInHand(grenadeStack);
			}

			ThrowableItemRegistry.remove(holder.getHolder());
			Arrow arrow = ((Player)holder.getHolder()).launchProjectile(Arrow.class,holder.getHolder().getLocation().getDirection().normalize().multiply(getThrowSpeed()));
			holder.setHolder(arrow);
			arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
			ThrowableItemRegistry.put(holder.getHolder(),holder);
			holder.setTimer(new FoliaRunnable(){
				public void run(){
					Entity holderEntity = holder.getHolder();
					if (holderEntity == null) {
						cancel();
						return;
					}
					final Runnable retired = () -> {
						ThrowableItemRegistry.remove(holderEntity);
						BukkitTask t = holder.getTask();
						if (t != null) t.cancel();
					};
					FoliaRunnable.runEntityTask(QAMain.getInstance(), thrower, () -> {
						final boolean sneaking = thrower.isSneaking();
						FoliaRunnable.runEntityTask(QAMain.getInstance(), holderEntity,
								() -> handleThrowableTick(holder, sneaking),
								retired);
					}, retired);
				}
			}.runTaskTimer(QAMain.getInstance(), 0, 2));
			//thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1.5f);

			QAMain.DEBUG("Throw grenade");
		} else {
			thrower.sendMessage(QAMain.getMessages().general.prefix + QAMain.getMessages().grenade.pullPin);
		}
		return true;
	}

	private void handleThrowableTick(ThrowableHolder h, boolean sneaking) {
		if(sneaking) {
			if (h.getHolder() instanceof Arrow) {
				h.getHolder().remove();
			}
			if (QAMain.getConfiguration().features.enableExplosionDamage) {
				QAThrowableExplodeEvent event = new QAThrowableExplodeEvent(StickyGrenades.this, h.getHolder().getLocation());
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) ExplosionHandler.handleExplosion(h.getHolder().getLocation(), 3, 1);
				QAMain.DEBUG("Using default explosions");
			}
			if (Compat.PARTICLES) {
				h.getHolder().getWorld().spawnParticle(XParticle.EXPLOSION_EMITTER.get(),
						h.getHolder().getLocation(), 0);
				h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 8,
						0.7f);
			} else {
				h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
				h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
			}
			Player thro = Bukkit.getPlayer(h.getOwner());
			final Location holderLoc = h.getHolder().getLocation();
			for (Entity e : h.getHolder().getNearbyEntities(radius, radius, radius)) {
				if (e instanceof LivingEntity) {
					final LivingEntity target = (LivingEntity) e;
					FoliaRunnable.runEntityTask(QAMain.getInstance(), target, () -> {
						double dam = (dmageLevel / target.getLocation().distance(holderLoc));
						QAMain.DEBUG("Grenade-Damaging " + target.getName() + " : " + dam + " DAM.");
						GunDamageHandler.damage(target, dam, thro, false);
					});
				}
			}
			ThrowableItemRegistry.remove(h.getHolder());
			if (h.getTask() != null) h.getTask().cancel();
		}
	}

	@Override
	public boolean onPull(Player thrower, ItemStack usedItem) {
		if(!QAMain.getConfiguration().features.enableAutoArmGrenades)
		if (ThrowableItemRegistry.containsKey(thrower)) {
			thrower.sendMessage(QAMain.getMessages().general.prefix + QAMain.getMessages().grenade.alreadyPulled);
			thrower.playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
			return true;
		}
		thrower.getWorld().playSound(thrower.getLocation(), Sound.ENTITY_ARROW_SHOOT, 2, 1);
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower, this);
		ThrowableItemRegistry.put(thrower, h);
		return true;

	}

	@Override
	public boolean onShift(Player shooter, ItemStack usedItem, boolean toggle) {
		return super.onShift(shooter, usedItem, toggle);
	}
}
