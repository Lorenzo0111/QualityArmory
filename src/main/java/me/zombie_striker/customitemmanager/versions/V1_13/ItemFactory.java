package me.zombie_striker.customitemmanager.versions.V1_13;

import me.zombie_striker.customitemmanager.*;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class ItemFactory extends AbstractItemFact {
	@Override
	public ItemStack getItem(MaterialStorage materialStorage, int amount) {
		CustomBaseObject base = QualityArmory.getCustomItem(materialStorage);

		if(base==null)
			return null;

		MaterialStorage ms = base.getItemData();
		String displayname = base.getDisplayName();
		if (ms == null || ms.getMat() == null)
			return new ItemStack(Material.AIR);

		ItemStack is = new ItemStack(ms.getMat(),1,(short)ms.getData());
		if (ms.getData() < 0)
			is.setDurability((short) 0);
		ItemMeta im = is.getItemMeta();
		if (im == null)
			im = Bukkit.getServer().getItemFactory().getItemMeta(ms.getMat());
		if (im != null) {
			im.setDisplayName(displayname);
			List<String> lore = base.getCustomLore()!=null?new ArrayList<>(base.getCustomLore()):new ArrayList<>();

			if(base instanceof  Gun)
				lore.addAll(Gun.getGunLore((Gun) base, null, ((Gun) base).getMaxBullets()));
			if (base instanceof ArmorObject)
				lore.addAll(OLD_ItemFact.getArmorLore((ArmorObject) base));

			OLD_ItemFact.addVariantData(im,lore,base);
			im.setLore(lore);
			if (QAMain.ITEM_enableUnbreakable) {
				try {
					im.setUnbreakable(true);
				} catch (Error | Exception e34) {
				}
			}
			try {
				if (QAMain.ITEM_enableUnbreakable) {
					im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
				}
				im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
				im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
			} catch (Error e) {

			}

			is.setItemMeta(im);
		} else {
			// Item meta is still null. Catch and report.
			QAMain.getInstance().getLogger()
					.warning(QAMain.prefix + " ItemMeta is null for " + base.getName() + ". I have");
		}
		is.setAmount(1);
		return is;
	}

	@Override
	public boolean isCustomItem(ItemStack is) {
		return false;
	}

}
