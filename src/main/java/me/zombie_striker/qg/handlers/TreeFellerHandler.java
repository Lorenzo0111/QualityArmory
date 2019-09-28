package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.UUID;

import me.zombie_striker.customitemmanager.CustomItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;


public class TreeFellerHandler implements Listener {

	public HashMap<UUID,Long> lastClicked = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST,ignoreCancelled=true)
	public void onClickFirst(PlayerInteractEvent e) {
		if(CustomItemManager.isUsingCustomData())
			return;
		if(!QualityArmory.isCustomItem(e.getPlayer().getItemInHand())) {
			lastClicked.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onBlock(BlockBreakEvent e) {
		if(CustomItemManager.isUsingCustomData())
			return;
		if(QualityArmory.isCustomItem(e.getPlayer().getItemInHand()) && e.getPlayer().getItemOnCursor().getType().name().endsWith ("AXE")
				&& System.currentTimeMillis()-lastClicked.get(e.getPlayer().getUniqueId())<1000) {
			lastClicked.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
			int durib = QualityArmory.findSafeSpot(e.getPlayer().getItemInHand(), true,QAMain.overrideURL);
			ItemStack temp = e.getPlayer().getItemInHand();
			temp.setDurability((short) (durib+(QAMain.overrideURL?0:4)));
			e.getPlayer().setItemInHand(temp);
		}
	}
}
