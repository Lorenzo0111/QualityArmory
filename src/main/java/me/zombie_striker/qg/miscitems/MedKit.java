package me.zombie_striker.qg.miscitems;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.handlers.HotbarMessager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.handlers.BulletWoundHandler;
import com.cryptomorin.xseries.XPotion;

public class MedKit extends CustomBaseObject implements ArmoryBaseObject {

	HashMap<UUID, Long> lastTimeHealed = new HashMap<>();
	HashMap<UUID, Double> PercentTimeHealed = new HashMap<>();

	public MedKit(MaterialStorage ms, String name, String displayname, ItemStack[] ings, int cost) {
		super(name, ms, displayname, null, false);
		super.setIngredients(ings);
		this.setPrice(cost);
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public boolean is18Support() {
		return false;
	}

	@Override
	public void set18Supported(boolean b) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onRMB(Player e, ItemStack usedItem) {
		Player healer = e.getPlayer();
		if (!BulletWoundHandler.bloodLevel.containsKey(healer.getUniqueId())) {

			if (healer.getHealth() < healer.getMaxHealth()) {

				if (!lastTimeHealed.containsKey(healer.getUniqueId())
						|| System.currentTimeMillis() - lastTimeHealed.get(healer.getUniqueId()) > 1500) {
					PercentTimeHealed.put(healer.getUniqueId(), 0.0);
				}
				lastTimeHealed.put(healer.getUniqueId(), System.currentTimeMillis());

				double percent = (100.0 / 3) / QAMain.S_MEDKIT_HEALDELAY;

				double p2 = PercentTimeHealed.get(healer.getUniqueId());

				if (p2 + percent < 100) {
					PercentTimeHealed.put(healer.getUniqueId(), p2 + percent);
				} else {
					healer.playSound(healer.getLocation(), getSoundOnEquip(), 1, 1);
					healer.setHealth(Math.min(healer.getMaxHealth(), healer.getHealth() + QAMain.S_MEDKIT_HEAL_AMOUNT));
					PercentTimeHealed.remove(healer.getUniqueId());
					lastTimeHealed.remove(healer.getUniqueId());
				}

				int totalBars = 25;
				double percentLoss = (p2 + percent) / 100;
				int healthBars = Math.min((int) (percentLoss * totalBars), totalBars);

				StringBuilder levelbar = new StringBuilder();
				levelbar.append(ChatColor.WHITE);
				levelbar.append(QualityArmory.repeat(":", healthBars));
				levelbar.append(ChatColor.BLACK);
				levelbar.append(QualityArmory.repeat(":", totalBars - healthBars));
				try {
					HotbarMessager.sendHotBarMessage(healer, ChatColor.RED + "[" + levelbar.toString() + ChatColor.RED
							+ "] " + new DecimalFormat("##0.#").format((p2 + percent)) + " percent!");
				} catch (Exception e2) {
				}

			} else {
				try {
					HotbarMessager.sendHotBarMessage(healer, QAMain.S_FULLYHEALED);
				} catch (Error | Exception e5) {
				}
			}
			return true;
		}
		double bloodlevel = BulletWoundHandler.bloodLevel.get(healer.getUniqueId());
		double percentBlood = Math.max(0, bloodlevel / QAMain.bulletWound_initialbloodamount);

		ChatColor severity = percentBlood > 75 ? ChatColor.WHITE
				: percentBlood > 50 ? ChatColor.GRAY : percentBlood > 25 ? ChatColor.RED : ChatColor.DARK_RED;
		if (BulletWoundHandler.bleedoutMultiplier.containsKey(healer.getUniqueId())
				&& BulletWoundHandler.bleedoutMultiplier.get(healer.getUniqueId()) < 0)
			BulletWoundHandler.bleedoutMultiplier.put(healer.getUniqueId(),
					Math.min(0, BulletWoundHandler.bleedoutMultiplier.get(healer.getUniqueId())
							+ QAMain.bulletWound_MedkitBloodlossHealRate));

		double newRate = BulletWoundHandler.bleedoutMultiplier.containsKey(healer.getUniqueId())
				? BulletWoundHandler.bleedoutMultiplier.get(healer.getUniqueId())
				: 0;

		if (newRate >= 0) {
			BulletWoundHandler.bleedoutMultiplier.remove(healer.getUniqueId());
			BulletWoundHandler.bloodLevel.remove(healer.getUniqueId());

			try {
				healer.removePotionEffect(XPotion.NAUSEA.getPotionEffectType());
				healer.removePotionEffect(PotionEffectType.BLINDNESS);
			} catch (Error | Exception e4) {
			}

			return true;
		}

		try {
			int totalBars = 25;
			int healthBars = (int) (percentBlood * totalBars);

			StringBuilder levelbar = new StringBuilder();
			levelbar.append(severity);
			levelbar.append(QualityArmory.repeat(":", healthBars));
			levelbar.append(ChatColor.BLACK);
			levelbar.append(QualityArmory.repeat(":", totalBars - healthBars));

			HotbarMessager.sendHotBarMessage(healer,
					ChatColor.RED + QAMain.S_MEDKIT_HEALING + "[" + levelbar.toString() + ChatColor.RED + "] "
							+ QAMain.S_MEDKIT_BLEEDING + " " + (newRate < 0 ? ChatColor.DARK_RED : ChatColor.GRAY)
							+ new DecimalFormat("##0.##").format(newRate) + ChatColor.GRAY + "+"
							+ QAMain.bulletWound_BloodIncreasePerSecond);
		} catch (Error | Exception e5) {
		}
		return true;
	}

	@Override
	public boolean onShift(Player shooter, ItemStack usedItem, boolean toggle) {
		return false;
	}

	@Override
	public boolean onLMB(Player e, ItemStack usedItem) {
		return false;
	}

	@Override
	public ItemStack getItemStack() {
		return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(),
				this.getItemData().getVariant());
	}

	@Override
	public boolean onSwapTo(Player shooter, ItemStack usedItem) {
		if (getSoundOnEquip() != null)
			shooter.getWorld().playSound(shooter.getLocation(), getSoundOnEquip(), 1, 1);
		return false;
	}

	@Override
	public boolean onSwapAway(Player shooter, ItemStack usedItem) {
		return false;
	}
}
