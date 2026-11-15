package me.zombie_striker.qg.miscitems;

import com.cryptomorin.xseries.XPotion;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
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
	public boolean handlesEquipSound() {
		return true;
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

		if (!QAMain.getPlayerState().getBloodLevel().containsKey(patientId)) {

			if (patient.getHealth() < patient.getMaxHealth()) {

				if (!lastTimeHealed.containsKey(patientId)
						|| System.currentTimeMillis() - lastTimeHealed.get(patientId) > 1500)
					PercentTimeHealed.put(patientId, 0.0);

				lastTimeHealed.put(patientId, System.currentTimeMillis());

				double percent = (100.0 / 3) / QAMain.getConfiguration().medkit.healDelaySeconds;
				double p2 = PercentTimeHealed.get(patientId);

				if (p2 + percent < 100) {
					PercentTimeHealed.put(patientId, p2 + percent);
				} else {
					patient.playSound(patient.getLocation(), getSoundOnEquip(), getSoundOnEquipVolume(), 1);
					patient.setHealth(Math.min(patient.getMaxHealth(), patient.getHealth() + QAMain.getConfiguration().medkit.healAmount));
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
						message = QAMain.getMessages().medkit.fullyHealed;
					} else {
						message = QAMain.getMessages().medkit.fullyHealedOther.replace("%player%", patient.getName());
					}

					HotbarMessager.sendHotBarMessage(healer, message);
				} catch (Error | Exception ignored) {
				}
			}
			return true;
		}

		double bloodLevel = QAMain.getPlayerState().getBloodLevel().get(patientId);
		double percentBlood = Math.max(0, bloodLevel / QAMain.getConfiguration().combat.bleeding.initialBloodLevel);

		ChatColor severity = percentBlood > 0.75 ? ChatColor.WHITE
				: percentBlood > 0.5 ? ChatColor.GRAY : percentBlood > 0.25 ? ChatColor.RED : ChatColor.DARK_RED;
		if (QAMain.getPlayerState().getBleedoutMultiplier().containsKey(patientId)
				&& QAMain.getPlayerState().getBleedoutMultiplier().get(patientId) < 0)
			QAMain.getPlayerState().getBleedoutMultiplier().put(patientId,
					Math.min(0, QAMain.getPlayerState().getBleedoutMultiplier().get(patientId)
							+ QAMain.getConfiguration().combat.bleeding.medkitHealBloodlossRate));

		double newRate = QAMain.getPlayerState().getBleedoutMultiplier().containsKey(patientId)
				? QAMain.getPlayerState().getBleedoutMultiplier().get(patientId)
				: 0;

		if (newRate >= 0) {
			QAMain.getPlayerState().getBleedoutMultiplier().remove(patientId);
			QAMain.getPlayerState().getBloodLevel().remove(patientId);

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

			String bleedingLabel = QAMain.getMessages().medkit.bleedingRate;
			if (!healer.equals(patient))
				bleedingLabel = patient.getName() + " - " + bleedingLabel;

			String bar = ChatColor.RED + QAMain.getMessages().medkit.healing + "[" + levelBar + ChatColor.RED + "] "
					+ bleedingLabel + " " + (newRate < 0 ? ChatColor.DARK_RED : ChatColor.GRAY)
					+ new DecimalFormat("##0.##").format(newRate) + ChatColor.GRAY + "+"
					+ QAMain.getConfiguration().combat.bleeding.bloodIncreasePerSecond;

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
