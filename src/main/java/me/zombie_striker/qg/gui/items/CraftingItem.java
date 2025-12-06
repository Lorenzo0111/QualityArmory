package me.zombie_striker.qg.gui.items;

import com.cryptomorin.xseries.XSound;
import de.themoep.inventorygui.StaticGuiElement;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.OLD_ItemFact;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QAGunGiveEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CraftingItem {
    private final CustomBaseObject item;

    public CraftingItem(CustomBaseObject item) {
        this.item = item;
    }

    public StaticGuiElement createElement(char slot) {
        try {
            ItemStack is = CustomItemManager.getItemType("gun").getItem(
                    item.getItemData().getMat(),
                    item.getItemData().getData(),
                    item.getItemData().getVariant()
            );

            is.setAmount(item.getCraftingReturn());
            if (is.getAmount() <= 0) {
                is.setAmount(1);
            }

            ItemMeta im = is.getItemMeta();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            lore.addAll(OLD_ItemFact.getCraftingLore(item));
            im.setLore(lore);
            is.setItemMeta(im);

            return new StaticGuiElement(slot, is, click -> {
                Player player = (Player) click.getWhoClicked();

                if (item.getIngredientsRaw() == null) return true;

                if (!QAMain.lookForIngre(player, item) && player.getGameMode() != GameMode.CREATIVE) {
                    player.closeInventory();
                    player.sendMessage(QAMain.prefix + QAMain.S_missingIngredients);
                    XSound.BLOCK_ANVIL_BREAK.play(player, 1, 1);
                    return true;
                }

                if (player.getGameMode() != GameMode.CREATIVE) {
                    QAMain.removeForIngre(player, item);
                }

                if (item instanceof Ammo) {
                    QualityArmory.addAmmoToInventory(player, (Ammo) item, item.getCraftingReturn());
                } else {
                    CustomBaseObject finalItem = item;
                    if (item instanceof Gun) {
                        QAGunGiveEvent giveEvent = new QAGunGiveEvent(player, (Gun) item, QAGunGiveEvent.Cause.SHOP);
                        Bukkit.getPluginManager().callEvent(giveEvent);
                        if (giveEvent.isCancelled()) return true;
                        finalItem = giveEvent.getGun();
                    }

                    ItemStack s = CustomItemManager.getItemType("gun").getItem(
                            finalItem.getItemData().getMat(),
                            finalItem.getItemData().getData(),
                            finalItem.getItemData().getVariant()
                    );
                    QualityArmory.giveOrDrop(player, s);
                }

                QAMain.shopsSounds(player, false);
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            ItemStack errorItem = new ItemStack(Material.BARRIER);
            ItemMeta meta = errorItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§cError loading item");
                errorItem.setItemMeta(meta);
            }
            return new StaticGuiElement(slot, errorItem, click -> true);
        }
    }

}
