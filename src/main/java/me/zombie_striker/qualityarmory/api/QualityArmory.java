package me.zombie_striker.qualityarmory.api;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.ammo.Ammo;
import me.zombie_striker.qualityarmory.armor.ArmorObject;
import me.zombie_striker.qualityarmory.attachments.AttachmentBase;
import me.zombie_striker.qualityarmory.config.GunYML;
import me.zombie_striker.qualityarmory.config.GunYMLCreator;
import me.zombie_striker.qualityarmory.config.GunYMLLoader;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.guns.WeaponSounds;
import me.zombie_striker.qualityarmory.guns.utils.WeaponType;
import me.zombie_striker.qualityarmory.hooks.protection.ProtectionHandler;
import me.zombie_striker.qualityarmory.miscitems.AmmoBag;
import me.zombie_striker.qualityarmory.utils.HotbarMessagerUtil;
import me.zombie_striker.qualityarmory.utils.IronsightsUtil;
import me.zombie_striker.qualityarmory.utils.LocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Map.Entry;

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
        if (warning) {
            try {
                player.sendTitle(LocalUtils.colorize(ChatColor.RED + QAMain.S_NORES1), LocalUtils.colorize(QAMain.S_NORES2));
            } catch (Error e2) {
                player.sendMessage(LocalUtils.colorize(ChatColor.RED + QAMain.S_NORES1));
                player.sendMessage(LocalUtils.colorize(ChatColor.RED + QAMain.S_NORES2));
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    try {
                        main.DEBUG("Sending resourcepack : " + (QAMain.AutoDetectResourcepackVersion) + " || "
                                + QAMain.MANUALLYSELECT18 + " || " + QAMain.isVersionHigherThan(1, 9) + " || ");
                        try {
                            if (QAMain.hasViaVersion) {
                                main.DEBUG(
                                        "Has Viaversion: " + us.myles.ViaVersion.bukkit.util.ProtocolSupportUtil
                                                .getProtocolVersion(player) + " 1.8=" + QAMain.ViaVersionIdfor_1_8);

                            }
                        } catch (Error | Exception re4) {
                        }
                        player.setResourcePack(CustomItemManager.getResourcepack(), null, "Accept this resourcepack to see custom item models.", true);

                    } catch (Error | Exception e4) {

                        player.setResourcePack(CustomItemManager.getResourcepack());
                    }
                } catch (Exception e) {

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
        if (isGun(is))
            return getGun(is);
        if (isAmmo(is))
            return getAmmo(is);
        if (isMisc(is))
            return getMisc(is);
        return null;
    }

    @SuppressWarnings("deprecation")
    public boolean isCustomItem(ItemStack is, int dataOffset) {
        if (is == null)
            return false;
        ItemStack itemstack = is.clone();
        if (dataOffset != 0) {
            try {
                ItemMeta im = itemstack.getItemMeta();
                int modeldata = 0;
                if (im.hasCustomModelData()) {
                    modeldata = im.getCustomModelData();
                } else if (modeldata + dataOffset > 0) {
                    return false;
                }
                im.setCustomModelData(modeldata + dataOffset);
                itemstack.setItemMeta(im);
            } catch (Error | Exception ed4) {
                itemstack.setDurability((short) (is.getDurability() + dataOffset));
            }
        }
        if (isIronSights(itemstack))
            return true;
        if (isGun(itemstack))
            return true;
        if (isAmmo(itemstack))
            return true;
        if (isMisc(itemstack))
            return true;
        return false;
    }


    @SuppressWarnings("deprecation")
    public boolean isMisc(ItemStack is) {
        if (is == null)
            return false;
        int var = MaterialStorage.getVariant(is);
        return (is != null
                && (QAMain.miscRegister.containsKey(MaterialStorage.getMS(is, var))
                || QAMain.miscRegister.containsKey(MaterialStorage.getMS(is, var))));
    }

    public CustomBaseObject getMisc(ItemStack is) {
        return QAMain.miscRegister.get(MaterialStorage.getMS(is));
    }

    public Gun getGun(ItemStack is) {
        return QAMain.gunRegister.get(MaterialStorage.getMS(is));
    }

    @Nullable
    public Gun getGunInHand(@NotNull HumanEntity entity) {
        ItemStack stack = entity.getInventory().getItemInHand();
        if (stack == null || stack.getType().equals(Material.AIR)) return null;

        if (isGun(stack)) return getGun(stack);
        if (isIronSights(stack)) {
            try {
                ItemStack offHand = entity.getInventory().getItemInOffHand();
                if (isGun(offHand)) {
                    return getGun(offHand);
                }
            } catch (NoSuchMethodError ignored) {
            }
        }

        return null;
    }

    public boolean isGun(ItemStack is) {
        return (is != null && QAMain.gunRegister.containsKey(MaterialStorage.getMS(is)));
    }

    @SuppressWarnings("deprecation")
    public Ammo getAmmo(ItemStack is) {
        int var = MaterialStorage.getVariant(is);
        if (QAMain.ammoRegister
                .containsKey(MaterialStorage.getMS(is, var)))
            return QAMain.ammoRegister
                    .get(MaterialStorage.getMS(is, var));
        return QAMain.ammoRegister.get(MaterialStorage.getMS(is, var));
    }

    public Ammo getAmmoByName(String name) {
        for (Entry<MaterialStorage, Ammo> e : QAMain.ammoRegister.entrySet())
            if (e.getValue().getName().equalsIgnoreCase(name)) {
                return e.getValue();
            }
        return null;
    }

    public int getAmmoInBag(@NotNull Player player, Ammo a) {
        int amount = 0;

        for (ItemStack is : player.getInventory().getContents()) {
            if (is == null || is.getType().equals(Material.AIR)) continue;

            CustomBaseObject customItem = getCustomItem(is);
            if (customItem instanceof AmmoBag) {
                Ammo ammoType = ((AmmoBag) customItem).getAmmoType(is);
                if (ammoType == null || !ammoType.equals(a)) continue;

                amount += ((AmmoBag) customItem).getAmmo(is);
            }
        }

        return amount;
    }


    @SuppressWarnings("deprecation")
    public boolean isAmmo(ItemStack is) {
        if (is == null)
            return false;
        int var = MaterialStorage.getVariant(is);

        MaterialStorage storage = MaterialStorage.getMS(is, var);

        return QAMain.ammoRegister.containsKey(storage);
    }

    public boolean isAmmoBag(ItemStack is) {
        if (is == null)
            return false;
        int var = MaterialStorage.getVariant(is);

        MaterialStorage storage = MaterialStorage.getMS(is, var);

        return QAMain.miscRegister.containsKey(storage) && QAMain.miscRegister.get(storage) instanceof AmmoBag;
    }



    public ItemStack getCustomItemAsItemStack(String name) {
        return getCustomItemAsItemStack(getCustomItemByName(name));
    }

    public ItemStack getCustomItemAsItemStack(CustomBaseObject obj) {
        if (obj == null) return null;
        return CustomItemManager.getItemType("gun").getItem(obj.getItemData().getMat(), obj.getItemData().getData(), obj.getItemData().getVariant());
    }


    public int getAmmoInInventory(Player player, Ammo a) {
        return getAmmoInInventory(player, a, false);
    }

    public int getAmmoInInventory(Player player, Ammo a, boolean ignoreBag) {
        int amount = 0;
        if (player.getGameMode() == GameMode.CREATIVE)
            return 99999;
        for (ItemStack is : player.getInventory().getContents()) {
            if (isAmmo(is) && getAmmo(is).equals(a)) {
                amount += is.getAmount();
            }
        }
        return ignoreBag ? amount : amount + getAmmoInBag(player, a);
    }

    public boolean addAmmoToInventory(Player player, Ammo a, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack is = player.getInventory().getItem(i);
            if (is != null && QualityArmory.isAmmo(is) && QualityArmory.getAmmo(is).equals(a)) {
                if (is.getAmount() + remaining <= a.getMaxItemStack()) {
                    is.setAmount(is.getAmount() + remaining);
                    remaining = 0;
                } else {
                    remaining -= a.getMaxItemStack() - is.getAmount();
                    is.setAmount(a.getMaxItemStack());
                }
                player.getInventory().setItem(i, is);
                if (remaining <= 0)
                    break;
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
        return Gun.getAmount(player);
    }

    public boolean removeAmmoFromInventory(Player player, Ammo a, int amount) {
        int remaining = amount;
        if (player.getGameMode() == GameMode.CREATIVE)
            return true;
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
                if (remaining <= 0)
                    break;

            }
        }

        if (remaining > 0) {
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
        }
        return remaining <= 0;
    }

    @Deprecated
    public void DEBUG(String s) {
        main.DEBUG(s);
    }

    public CustomBaseObject getCustomItemByName(String itemId) {
        for(CustomBaseObject cbo : main.getCustomItems()){
            if(cbo.getName().equalsIgnoreCase(itemId))
                return cbo;
        }
        return null;
    }

    public Collection<CustomBaseObject> getCustomItemsAsList() {
        return main.getCustomItems();
    }
}
