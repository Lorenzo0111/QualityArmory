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
import me.zombie_striker.qg.handlers.EconHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    private final CustomBaseObject item;

    public ShopItem(CustomBaseObject item) {
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
            lore.addAll(OLD_ItemFact.addShopLore(item));
            im.setLore(lore);
            is.setItemMeta(im);

            return new StaticGuiElement(slot, is, click -> {
                click.getRawEvent().setCancelled(true);
                Player player = (Player) click.getRawEvent().getWhoClicked();

                if (!QAMain.enableEconomy) {
                    try {
                        QAMain.enableEconomy = EconHandler.setupEconomy();
                    } catch (Exception | Error ignored) {
                    }
                }

                if (!QAMain.enableEconomy) {
                    player.closeInventory();
                    player.sendMessage(QAMain.prefix + QAMain.S_noEcon);
                    return true;
                }

                if (item.getPrice() < 0 || !item.isEnableShop()) return true;

                if (!EconHandler.hasEnough(item, player)) {
                    player.closeInventory();
                    player.sendMessage(QAMain.prefix + QAMain.S_noMoney);
                    XSound.BLOCK_ANVIL_BREAK.play(player, 1, 1);
                    return true;
                }

                EconHandler.pay(item, player);
                player.sendMessage(QAMain.S_BUYCONFIRM
                        .replaceAll("%item%", ChatColor.stripColor(item.getDisplayName()))
                        .replaceAll("%cost%", "" + item.getPrice()));

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

                QAMain.shopsSounds(player, true);
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
