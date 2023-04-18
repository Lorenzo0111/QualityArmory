package me.zombie_striker.qualityarmory.api;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.guns.Ammo;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHandler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class QualityArmory {


    private static QualityArmory inst;

    public static void setInstance(QAMain maininstance) {
        inst = new QualityArmory(maininstance);
    }


    private QAMain main;


    private QualityArmory(QAMain main) {
        this.main = main;
    }

    public static QualityArmory getInstance() {
        return inst;
    }


    @SuppressWarnings("deprecation")
    public void sendResourcepack(final Player player, final boolean warning) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    player.setResourcePack(CustomItemManager.getResourcepack(), null, "Accept this resourcepack to see custom item models.", true);

                } catch (Error | Exception e4) {

                    player.setResourcePack(CustomItemManager.getResourcepack());
                }
            }
        }.runTaskLater(main, 20 * (warning ? 1 : 5));
    }

    public boolean allowGunsInRegion(Location loc) {
        try {
            return ProtectionHandler.canPvp(loc);
        } catch (Error e) {
        }
        return true;
    }

    public boolean isCustomItem(ItemStack is) {
        return isCustomItem(is, 0);
    }


    public CustomBaseObject getCustomItem(ItemStack is) {
        if (is.getItemMeta().hasCustomModelData())
            for (CustomBaseObject cbo : main.getCustomItems()) {
                if (cbo.getItemData().getMat() == is.getType() && cbo.getItemData().getData() == is.getItemMeta().getCustomModelData())
                    return cbo;
            }
        return null;
    }

    @SuppressWarnings("deprecation")
    public boolean isCustomItem(ItemStack is, int dataOffset) {
        if (is == null) return false;
        ItemStack itemstack = is.clone();
        int custommodeldata = 0;
        if (dataOffset != 0) {
            ItemMeta im = itemstack.getItemMeta();
            int modeldata = 0;
            if (im.hasCustomModelData()) {
                modeldata = im.getCustomModelData();
            } else if (modeldata + dataOffset > 0) {
                return false;
            }
            im.setCustomModelData(modeldata + dataOffset);
            itemstack.setItemMeta(im);
            custommodeldata = itemstack.getItemMeta().getCustomModelData();
        } else {
            if (itemstack.getItemMeta().hasCustomModelData())
                custommodeldata = itemstack.getItemMeta().getCustomModelData();
        }
        for (CustomBaseObject cbo : main.getCustomItems()) {
            if (cbo.getItemData().getMat() == itemstack.getType() && cbo.getItemData().getData() == custommodeldata)
                return true;
        }
        return false;
    }


    public Gun getGun(ItemStack is) {
        CustomBaseObject cbo;
        if ((cbo = getCustomItem(is)) instanceof Gun)
            return (Gun) cbo;
        return null;
    }

    @Nullable
    public Gun getGunInHand(@NotNull HumanEntity entity) {
        ItemStack stack = entity.getInventory().getItemInHand();
        if (stack == null || stack.getType().equals(Material.AIR)) return null;

        if (isGun(stack)) return getGun(stack);

        return null;
    }

    public boolean isGun(ItemStack is) {
        return (is != null && getGun(is) != null);
    }

    @SuppressWarnings("deprecation")
    public Ammo getAmmo(ItemStack is) {
        CustomBaseObject cbo;
        if ((cbo = getCustomItem(is)) instanceof Ammo)
            return (Ammo) cbo;
        return null;
    }

    public Ammo getAmmoByName(String name) {
        for (CustomBaseObject e : main.getCustomItems()) {
            if (e instanceof Ammo)
                if (e.getName().equalsIgnoreCase(name)) {
                    return (Ammo) e;
                }
        }
        return null;
    }

    public int getAmmoInBag(@NotNull Player player, Ammo a) {
        int amount = 0;

        for (ItemStack is : player.getInventory().getContents()) {
            if (is == null || is.getType().equals(Material.AIR)) continue;

            CustomBaseObject customItem = getCustomItem(is);
            /*if (customItem instanceof AmmoBag) {
                Ammo ammoType = ((AmmoBag) customItem).getAmmoType(is);
                if (ammoType == null || !ammoType.equals(a)) continue;

                amount += ((AmmoBag) customItem).getAmmo(is);
            }*/
        }

        return amount;
    }


    public boolean isAmmo(ItemStack is) {
        return (is != null && getAmmo(is) != null);
    }


    public ItemStack getCustomItemAsItemStack(String name) {
        return getCustomItemAsItemStack(getCustomItemByName(name));
    }

    public ItemStack getCustomItemAsItemStack(CustomBaseObject obj) {
        if (obj == null) return null;
        return CustomItemManager.getItemType("gun").getItem(obj.getItemData().getMat(), obj.getItemData().getData(), obj.getItemData().getVariant());
    }


    public int getAmmoInInventory(Player player, String a) {
        return getAmmoInInventory(player, a, false);
    }

    public int getAmmoInInventory(Player player, String a, boolean ignoreBag) {
        int amount = 0;
        if (player.getGameMode() == GameMode.CREATIVE) return 99999;
        for (ItemStack is : player.getInventory().getContents()) {
            if (isAmmo(is) && ((String) getAmmo(is).getData(ConfigKey.CUSTOMITEM_AMMOTYPE.getKey())).equals(a)) {
                amount += is.getAmount();
            }
        }
        return ignoreBag ? amount : amount /*+ getAmmoInBag(player, a)*/;
    }

    public boolean addAmmoToInventory(Player player, Ammo a, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack is = player.getInventory().getItem(i);
            if (is != null && QualityArmory.getInstance().isAmmo(is) && QualityArmory.getInstance().getAmmo(is).equals(a)) {
                if (is.getAmount() + remaining <= (int) a.getData(ConfigKey.CUSTOMITEM_MAX_ITEM_STACK.getKey())) {
                    is.setAmount(is.getAmount() + remaining);
                    remaining = 0;
                } else {
                    remaining -= (int) a.getData(ConfigKey.CUSTOMITEM_MAX_ITEM_STACK.getKey()) - is.getAmount();
                    is.setAmount((int) a.getData(ConfigKey.CUSTOMITEM_MAX_ITEM_STACK.getKey()));
                }
                player.getInventory().setItem(i, is);
                if (remaining <= 0) break;
            }
        }
        if (remaining > 0) {
            if (player.getInventory().firstEmpty() >= 0) {
                ItemStack is = getCustomItemAsItemStack(a);
                is.setAmount(remaining);
                player.getInventory().addItem(is);
                remaining = 0;
            }
        }
        return remaining <= 0;
    }

    public int getBulletsInHand(Player player) {
        if (player.getInventory().getItemInMainHand() != null)
            return main.getGunDataHandler().getGunBulletAmount(player.getInventory().getItemInMainHand());
        return 0;
    }

    public boolean removeAmmoFromInventory(Player player, Ammo a, int amount) {
        int remaining = amount;
        if (player.getGameMode() == GameMode.CREATIVE) return true;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack is = player.getInventory().getItem(i);
            if (is != null && isAmmo(is) && getAmmo(is).equals(a)) {
                int temp = is.getAmount();
                if (remaining < is.getAmount()) {
                    is.setAmount(is.getAmount() - remaining);
                } else {
                    is.setType(Material.AIR);
                }
                remaining -= temp;
                player.getInventory().setItem(i, is);
                if (remaining <= 0) break;

            }
        }
        /*if (remaining > 0) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack is = player.getInventory().getItem(i);
                if (isAmmoBag(is)) {
                    AmmoBag ab = (AmmoBag) getCustomItem(is);
                    if (ab == null) continue;

                    Ammo ammoType = ab.getAmmoType(is);
                    if (ammoType != null && ammoType.equals(a)) {
                        int amountInBag = ab.getAmmo(is);

                        if (amountInBag >= remaining) {
                            ab.updateAmmoLore(is, amountInBag - remaining);
                            remaining = 0;
                        } else {
                            ab.updateAmmoLore(is, 0);
                            remaining -= amountInBag;
                        }
                    }

                }
            }
        }*/
        return remaining <= 0;
    }

    @Deprecated
    public void DEBUG(String s) {
        main.DEBUG(s);
    }

    public CustomBaseObject getCustomItemByName(String itemId) {
        for (CustomBaseObject cbo : main.getCustomItems()) {
            if (cbo.getName().equalsIgnoreCase(itemId)) return cbo;
        }
        return null;
    }

    public Collection<CustomBaseObject> getCustomItemsAsList() {
        return main.getCustomItems();
    }

}
