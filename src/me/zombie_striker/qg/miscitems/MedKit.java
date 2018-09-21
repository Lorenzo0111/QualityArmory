package me.zombie_striker.qg.miscitems;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.pluginconstructor.HotbarMessager;
import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.qg.ItemFact;
import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.handlers.BulletWoundHandler;

public class MedKit implements ArmoryBaseObject {

	MaterialStorage ms;
	String displayname;
	int cost;
	String name;

	List<UUID> medkitHeartUsage = new ArrayList<>();
	HashMap<UUID, Long> lastTimeHealed = new HashMap<>();
	HashMap<UUID, Double> PercentTimeHealed = new HashMap<>();

	ItemStack[] ing = null;

	public MedKit(MaterialStorage ms, String name, String displayname, ItemStack[] ings, int cost) {
		this.ms = ms;
		this.displayname = displayname;
		this.cost = cost;
		this.name = name;
		this.ing = ings;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ItemStack[] getIngredients() {
		return ing;
	}

	@Override
	public int getCraftingReturn() {
		return 1;
	}

	@Override
	public MaterialStorage getItemData() {
		return ms;
	}

	@Override
	public List<String> getCustomLore() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return displayname;
	}

	@Override
	public double cost() {
		return cost;
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
	public void onRMB(PlayerInteractEvent e, ItemStack usedItem) {
		Player healer = e.getPlayer();
		if (!BulletWoundHandler.bloodLevel.containsKey(healer.getUniqueId())) {
			/*
			 * if (medkitHeartUsage.contains(healer.getUniqueId())) { try {
			 * HotbarMessager.sendHotBarMessage(healer,
			 * Main.S_MEDKIT_WAIT.replaceAll("%seconds%",""+(Main.S_MEDKIT_HEARTDELAY-((
			 * System.currentTimeMillis()-lastTimeHealed.get(healer.getUniqueId()))/1000))))
			 * ; } catch (Error | Exception e5) { } return; }
			 */

			if (healer.getHealth() < healer.getMaxHealth()) {

				if (!lastTimeHealed.containsKey(healer.getUniqueId())
						|| System.currentTimeMillis() - lastTimeHealed.get(healer.getUniqueId()) > 1500) {
					PercentTimeHealed.put(healer.getUniqueId(), 0.0);
				}
				lastTimeHealed.put(healer.getUniqueId(), System.currentTimeMillis());

				double percent = (100.0 / 3) / Main.S_MEDKIT_HEALDELAY;

				double p2 = PercentTimeHealed.get(healer.getUniqueId());

				if (p2 + percent < 100) {
					PercentTimeHealed.put(healer.getUniqueId(), p2 + percent);

				} else {

					healer.setHealth(Math.min(healer.getMaxHealth(), healer.getHealth() + Main.S_MEDKIT_HEAL_AMOUNT));
					PercentTimeHealed.remove(healer.getUniqueId());
					lastTimeHealed.remove(healer.getUniqueId());
					/*
					 * try { HotbarMessager.sendHotBarMessage(healer, Main.S_MEDKIT_HEALINGHEARTS);
					 * } catch (Error | Exception e5) { }
					 */

					/*
					 * medkitHeartUsage.add(healer.getUniqueId()); new BukkitRunnable() {
					 * 
					 * @Override public void run() { medkitHeartUsage.remove(healer.getUniqueId());
					 * } }.runTaskLater(Main.getInstance(), (long) (20 * Main.S_MEDKIT_HEARTDELAY));
					 */
				}

				int totalBars = 25;
				double percentLoss = (p2 + percent) / 100;
				int healthBars = Math.min((int) (percentLoss * totalBars), totalBars);

				StringBuilder levelbar = new StringBuilder();
				levelbar.append(ChatColor.WHITE);
				levelbar.append(StringUtils.repeat(':', healthBars));
				levelbar.append(ChatColor.BLACK);
				levelbar.append(StringUtils.repeat(':', totalBars - healthBars));
				try {
					HotbarMessager.sendHotBarMessage(healer, ChatColor.RED + "[" + levelbar.toString() + ChatColor.RED
							+ "] " + new DecimalFormat("##0.#").format((p2 + percent)) + " percent!");
				} catch (Exception e2) {
				}

			} else {
				try {
					HotbarMessager.sendHotBarMessage(healer, Main.S_FULLYHEALED);
				} catch (Error | Exception e5) {
				}
			}
			return;
		}
		double bloodlevel = BulletWoundHandler.bloodLevel.get(healer.getUniqueId());
		double percentBlood = Math.max(0, bloodlevel / Main.bulletWound_initialbloodamount);

		ChatColor severity = percentBlood > 75 ? ChatColor.WHITE
				: percentBlood > 50 ? ChatColor.GRAY : percentBlood > 25 ? ChatColor.RED : ChatColor.DARK_RED;
		if (BulletWoundHandler.bleedoutMultiplier.containsKey(healer.getUniqueId())
				&& BulletWoundHandler.bleedoutMultiplier.get(healer.getUniqueId()) < 0)
			BulletWoundHandler.bleedoutMultiplier.put(healer.getUniqueId(),
					Math.min(0, BulletWoundHandler.bleedoutMultiplier.get(healer.getUniqueId())
							+ Main.bulletWound_MedkitBloodlossHealRate));

		double newRate = BulletWoundHandler.bleedoutMultiplier.containsKey(healer.getUniqueId())
				? BulletWoundHandler.bleedoutMultiplier.get(healer.getUniqueId())
				: 0;

		try {
			int totalBars = 25;
			int healthBars = (int) (percentBlood * totalBars);

			StringBuilder levelbar = new StringBuilder();
			levelbar.append(severity);
			levelbar.append(StringUtils.repeat(':', healthBars));
			levelbar.append(ChatColor.BLACK);
			levelbar.append(StringUtils.repeat(':', totalBars - healthBars));

			HotbarMessager.sendHotBarMessage(healer,
					ChatColor.RED + Main.S_MEDKIT_HEALING + "[" + levelbar.toString() + ChatColor.RED + "] "
							+ Main.S_MEDKIT_BLEEDING + " " + (newRate < 0 ? ChatColor.DARK_RED : ChatColor.GRAY)
							+ new DecimalFormat("##0.##").format(newRate) + ChatColor.GRAY + "+"
							+ Main.bulletWound_BloodIncreasePerSecond);
		} catch (Error | Exception e5) {
		}
	}

	@Override
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public ItemStack getItemStack() {
		return ItemFact.getObject(this, 1);
	}

}
