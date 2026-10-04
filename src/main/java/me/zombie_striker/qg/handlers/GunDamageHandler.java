package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Keeps track of the damage that is dealt by QualityArmory (bullets and
 * explosives), so it can be told apart from regular melee attacks.
 */
public class GunDamageHandler implements Listener {

	// Nesting depth of the weapon damage calls. Per thread, as Folia ticks regions on different threads
	private static final ThreadLocal<int[]> depth = ThreadLocal.withInitial(() -> new int[1]);
	private static boolean projectileDamageSupported = true;

	/**
	 * Has to be called before damaging an entity with a weapon, followed by
	 * {@link #end()} in a finally block.
	 */
	public static void begin() {
		depth.get()[0]++;
	}

	public static void end() {
		int[] current = depth.get();
		if (current[0] > 0)
			current[0]--;
	}

	/**
	 * @return true if the damage currently being dealt comes from a QualityArmory weapon.
	 */
	public static boolean isWeaponDamage() {
		return depth.get()[0] > 0;
	}

	/**
	 * Damages an entity with a weapon.
	 *
	 * @param source the entity that used the weapon, or null if there is none.
	 * @param bullet if the damage is dealt by a bullet.
	 */
	public static void damage(Damageable target, double damage, Entity source, boolean bullet) {
		begin();
		try {
			if (source == null) {
				target.damage(damage);
				return;
			}
			if (bullet && QAMain.projectileDamageType && projectileDamageSupported) {
				try {
					ProjectileDamage.damage(target, damage, source);
					return;
				} catch (LinkageError e) {
					// The damage source API does not exist on this version
					projectileDamageSupported = false;
				}
			}
			target.damage(damage, source);
		} finally {
			end();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onWeaponDamage(EntityDamageEvent e) {
		if (!isWeaponDamage() || QAMain.armorEffectiveness == 1.0)
			return;
		try {
			if (!e.isApplicable(DamageModifier.ARMOR))
				return;
			// The armor modifier is negative: it is the amount of damage the armor absorbs
			double armor = e.getDamage(DamageModifier.ARMOR);
			e.setDamage(DamageModifier.ARMOR, armor * QAMain.armorEffectiveness);
			if (e.getFinalDamage() < 0)
				e.setDamage(DamageModifier.ARMOR, e.getDamage(DamageModifier.ARMOR) - e.getFinalDamage());
		} catch (Error | Exception ignored) {
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onArmorDamage(PlayerItemDamageEvent e) {
		if (!isWeaponDamage() || QAMain.armorDurabilityDamageMultiplier == 1.0)
			return;
		if (!isWearing(e.getPlayer(), e.getItem()))
			return;

		double damage = e.getDamage() * QAMain.armorDurabilityDamageMultiplier;
		int rounded = (int) Math.floor(damage);
		// Randomly round the remainder, so multipliers below 1 still work for small amounts
		if (ThreadLocalRandom.current().nextDouble() < damage - rounded)
			rounded++;

		if (rounded <= 0) {
			e.setCancelled(true);
		} else {
			e.setDamage(rounded);
		}
	}

	private static boolean isWearing(Player player, ItemStack item) {
		for (ItemStack armor : player.getInventory().getArmorContents()) {
			if (armor != null && armor.equals(item))
				return true;
		}
		return false;
	}

	// Kept in a separate class so the damage source classes are only loaded when they exist.
	private static class ProjectileDamage {

		private static void damage(Damageable target, double damage, Entity source) {
			target.damage(damage, DamageSource.builder(DamageType.ARROW)
					.withCausingEntity(source).withDirectEntity(source).build());
		}
	}
}
