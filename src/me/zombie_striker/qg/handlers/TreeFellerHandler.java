package me.zombie_striker.qg.handlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.QualityArmory;

public class TreeFellerHandler implements Listener {

	public HashMap<UUID,Long> lastClicked = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onClickFirst(PlayerInteractEvent e) {
		if(!QualityArmory.isCustomItem(e.getPlayer().getItemInHand())) {
			lastClicked.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlock(BlockBreakEvent e) {
		if(QualityArmory.isCustomItem(e.getPlayer().getItemInHand()) && (System.currentTimeMillis()-lastClicked.get(e.getPlayer().getUniqueId())<1000)) {
			lastClicked.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
			int durib = QualityArmory.findSafeSpot(e.getPlayer().getItemInHand(), true);
			ItemStack temp = e.getPlayer().getItemInHand();
			temp.setDurability((short) (durib+4));
			e.getPlayer().setItemInHand(temp);
		}
	}
}
