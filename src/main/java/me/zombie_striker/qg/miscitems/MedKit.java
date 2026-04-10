package me.zombie_striker.qg.miscitems;

import com.cryptomorin.xseries.XPotion;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.handlers.BulletWoundHandler;
import me.zombie_striker.qg.handlers.HotbarMessager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class MedKit extends CustomBaseObject {

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

	@Override
	public boolean onRMB(Player e, ItemStack usedItem) {
		return useOn(e, e, usedItem);
	}

	@SuppressWarnings("deprecation")
	public boolean useOn(Player healer, Player patient, ItemStack usedItem) {
		if (healer == null || patient == null)
			return false;

		UUID patientId = patient.getUniqueId();

		if (!BulletWoundHandler.bloodLevel.containsKey(patientId)) {

			if (patient.getHealth() < patient.getMaxHealth()) {

				if (!lastTimeHealed.containsKey(patientId)
						|| System.currentTimeMillis() - lastTimeHealed.get(patientId) > 1500)
					PercentTimeHealed.put(patientId, 0.0);

				lastTimeHealed.put(patientId, System.currentTimeMillis());

				double percent = (100.0 / 3) / QAMain.S_MEDKIT_HEALDELAY;
				double p2 = PercentTimeHealed.get(patientId);

				if (p2 + percent < 100) {
					PercentTimeHealed.put(patientId, p2 + percent);
				} else {
					patient.playSound(patient.getLocation(), getSoundOnEquip(), getSoundOnEquipVolume(), 1);
					patient.setHealth(Math.min(patient.getMaxHealth(), patient.getHealth() + QAMain.S_MEDKIT_HEAL_AMOUNT));
					PercentTimeHealed.remove(patientId);
					lastTimeHealed.remove(patientId);
				}

				int totalBars = 25;
				double percentLoss = (p2 + percent) / 100;
				int healthBars = Math.min((int) (percentLoss * totalBars), totalBars);

				StringBuilder levelBar = new StringBuilder();
				levelBar.append(ChatColor.WHITE);
				levelBar.append(QualityArmory.repeat(":", healthBars));
				levelBar.append(ChatColor.BLACK);
				levelBar.append(QualityArmory.repeat(":", totalBars - healthBars));

				String progressText = new DecimalFormat("##0.#").format((p2 + percent)) + " percent!";
				if (!healer.equals(patient)) {
					progressText = patient.getName() + ": " + progressText;
				}

				// Show progress to both healer and patient
				try {
					String bar = ChatColor.RED + "[" + levelBar + ChatColor.RED + "] " + progressText;
					HotbarMessager.sendHotBarMessage(healer, bar);
					if (!healer.equals(patient)) HotbarMessager.sendHotBarMessage(patient, bar);
				} catch (Exception ignored) {
				}

			} else {
				try {
					String message;
					if (healer.equals(patient)) {
						message = QAMain.S_FULLYHEALED;
					} else {
						message = QAMain.S_FULLYHEALED_OTHER.replace("%player%", patient.getName());
					}

					HotbarMessager.sendHotBarMessage(healer, message);
				} catch (Error | Exception ignored) {
				}
			}
			return true;
		}

		double bloodLevel = BulletWoundHandler.bloodLevel.get(patientId);
		double percentBlood = Math.max(0, bloodLevel / QAMain.bulletWound_initialbloodamount);

		ChatColor severity = percentBlood > 75 ? ChatColor.WHITE
				: percentBlood > 50 ? ChatColor.GRAY : percentBlood > 25 ? ChatColor.RED : ChatColor.DARK_RED;
		if (BulletWoundHandler.bleedoutMultiplier.containsKey(patientId)
				&& BulletWoundHandler.bleedoutMultiplier.get(patientId) < 0)
			BulletWoundHandler.bleedoutMultiplier.put(patientId,
					Math.min(0, BulletWoundHandler.bleedoutMultiplier.get(patientId)
							+ QAMain.bulletWound_MedkitBloodlossHealRate));

		double newRate = BulletWoundHandler.bleedoutMultiplier.containsKey(patientId)
				? BulletWoundHandler.bleedoutMultiplier.get(patientId)
				: 0;

		if (newRate >= 0) {
			BulletWoundHandler.bleedoutMultiplier.remove(patientId);
			BulletWoundHandler.bloodLevel.remove(patientId);

			try {
				patient.removePotionEffect(XPotion.NAUSEA.getPotionEffectType());
				patient.removePotionEffect(PotionEffectType.BLINDNESS);
			} catch (Error | Exception ignored) {
			}

			return true;
		}

		try {
			int totalBars = 25;
			int healthBars = (int) (percentBlood * totalBars);

			String levelBar = severity +
					QualityArmory.repeat(":", healthBars) +
					ChatColor.BLACK +
					QualityArmory.repeat(":", totalBars - healthBars);

			String bleedingLabel = QAMain.S_MEDKIT_BLEEDING;
			if (!healer.equals(patient))
				bleedingLabel = patient.getName() + " - " + bleedingLabel;

			String bar = ChatColor.RED + QAMain.S_MEDKIT_HEALING + "[" + levelBar + ChatColor.RED + "] "
					+ bleedingLabel + " " + (newRate < 0 ? ChatColor.DARK_RED : ChatColor.GRAY)
					+ new DecimalFormat("##0.##").format(newRate) + ChatColor.GRAY + "+"
					+ QAMain.bulletWound_BloodIncreasePerSecond;

			HotbarMessager.sendHotBarMessage(healer, bar);
			if (!healer.equals(patient)) {
				HotbarMessager.sendHotBarMessage(patient, bar);
			}
		} catch (Error | Exception ignored) {
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
			shooter.getWorld().playSound(shooter.getLocation(), getSoundOnEquip(), getSoundOnEquipVolume(), 1);
		return false;
	}

	@Override
	public boolean onSwapAway(Player shooter, ItemStack usedItem) {
		return false;
	}
}
