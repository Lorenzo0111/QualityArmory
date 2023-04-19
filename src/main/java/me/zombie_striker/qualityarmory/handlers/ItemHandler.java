package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.MessageKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemHandler implements IHandler {

    public ItemStack createItemStack(CustomBaseObject customBaseObject){
        ItemStack is = new ItemStack(customBaseObject.getItemData().getMat());
        ItemMeta im = is.getItemMeta();
        im.setCustomModelData(customBaseObject.getItemData().getData());
        if(customBaseObject.containsData(ConfigKey.CUSTOMITEM_DISPLAYNAME.getKey()))
            im.setDisplayName((String) customBaseObject.getData(ConfigKey.CUSTOMITEM_DISPLAYNAME.getKey()));

        List<String> customLore = new ArrayList<>();
        if(customBaseObject.containsData(ConfigKey.CUSTOMITEM_LORE.getKey()))
            customLore.addAll((Collection<? extends String>) customBaseObject.getData(ConfigKey.CUSTOMITEM_LORE.getKey()));

        List<String> lore = new LinkedList<>();
        if(customBaseObject instanceof Gun) {
            lore.add((String) QualityArmory.getInstance().getPluginInstance().getMessagesYml().getOrSet(MessageKey.TUTORIAL_GUN_FIRE.getKey(), "Press [LMB] to fire."));
            lore.add((String) QualityArmory.getInstance().getPluginInstance().getMessagesYml().getOrSet(MessageKey.TUTORIAL_GUN_RELOAD.getKey(), "Press [F] to reload."));

            UUID randomGunUUID = UUID.randomUUID();
            lore.add(GunDataHandler.GUN_ID_LORE_STRING+randomGunUUID.toString());
            QualityArmory.getInstance().getPluginInstance().getGunDataHandler().registerGun((Gun) customBaseObject,randomGunUUID);
        }
        lore.addAll(customLore);
        im.setLore(lore);



        is.setItemMeta(im);
        return is;
    }


    @Override
    public void init(QAMain main) {

    }
}
