package me.zombie_striker.customitemmanager.versions.V1_14;

import me.zombie_striker.customitemmanager.AbstractItemFact;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.qg.ArmoryBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.ArmorObject;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.handlers.SkullHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;


public class ItemFactory extends AbstractItemFact {
	@Override
	public ItemStack getItem(MaterialStorage materialStorage, int amount) {
		ArmoryBaseObject base = QualityArmory.getCustomItem(materialStorage);


		MaterialStorage ms = base.getItemData();
		String displayname = base.getDisplayName();
		if (ms == null || ms.getMat() == null)
			return new ItemStack(Material.AIR);

		ItemStack is = new ItemStack(ms.getMat());
		if (ms.getData() < 0)
			is.setDurability((short) 0);





		ItemMeta im = is.getItemMeta();
		if (im == null)
			im = Bukkit.getServer().getItemFactory().getItemMeta(ms.getMat());
		if (im != null) {
			im.setDisplayName(displayname);
			List<String> lore = null;

			if(base instanceof  Ammo){
				boolean setSkull = false;
				if (((Ammo)base).isSkull() && ((Ammo)base).hasCustomSkin()) {
					setSkull = true;
					is = SkullHandler.getCustomSkull64(((Ammo)base).getCustomSkin().getBytes());
				}
				if (((Ammo)base).isSkull() && !setSkull) {
					((SkullMeta) im).setOwner(((Ammo)base).getSkullOwner());
				}
			}

			if (base instanceof Gun)
				lore = Gun.getGunLore((Gun) base, null, ((Gun) base).getMaxBullets());
			if (base instanceof ArmorObject)
				lore = OLD_ItemFact.getArmorLore((ArmorObject) base,is);


			im.setLore(lore);
			if (QAMain.ITEM_enableUnbreakable) {
				try {
					im.setUnbreakable(true);
				} catch (Error | Exception e34) {
					try {
						im.spigot().setUnbreakable(true);
					} catch (Error | Exception e344) {
					}
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

			im.setCustomModelData(ms.getData());
			/*if(is.getType()==Material.CROSSBOW){
				//Now the player will hold the crossbow like a gun
				CrossbowMeta im2 = (CrossbowMeta) im;
				im2.addChargedProjectile(new ItemStack(Material.ARROW));
			}*/

			is.setItemMeta(im);
		} else {
			// Item meta is still null. Catch and report.
			QAMain.getInstance().getLogger()
					.warning(QAMain.prefix + " ItemMeta is null for " + base.getName() + ". I have");
		}
		is.setAmount(1);
		return is;
	}
}
