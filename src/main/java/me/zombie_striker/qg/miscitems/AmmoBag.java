package me.zombie_striker.qg.miscitems;

import me.zombie_striker.customitemmanager.ArmoryBaseObject;
import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.CustomItemManager;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.ammo.Ammo;
import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AmmoBag extends CustomBaseObject implements ArmoryBaseObject {
    private final int maxAmmo;

    public AmmoBag(final MaterialStorage ms, final String name, final String displayname, final ItemStack[] ings, final int max,
            final int cost) {
        super(name, ms, displayname, null, false);
        super.setIngredients(ings);
        this.setPrice(cost);
        this.maxAmmo = max;
    }

    @Override
    public int getCraftingReturn() { return 1; }

    @Override
    public boolean is18Support() { return false; }

    @Override
    public void set18Supported(final boolean b) {}

    @Override
    public boolean onRMB(final Player shooter, final ItemStack usedItem) {
        boolean needsEdit = false;
        Ammo ammoType = this.getAmmoType(usedItem);
        if (ammoType == null) {
            for (final ItemStack is : shooter.getInventory().getContents()) {
                if (QualityArmory.isAmmo(is)) {
                    ammoType = QualityArmory.getAmmo(is);
                    needsEdit = true;
                    break;
                }
            }
        }

        int newAmmoCount = this.getAmmo(usedItem);

        if (ammoType != null) {
            final int inInv = QualityArmory.getAmmoInInventory(shooter, ammoType, true);
            final int newCount = Math.min(inInv + newAmmoCount, this.maxAmmo);
            final int toRemove = Math.max(0, newCount - newAmmoCount);

            if (toRemove > 0) {
                QualityArmory.removeAmmoFromInventory(shooter, ammoType, toRemove);
                newAmmoCount = newCount;
                needsEdit = true;
            }
        }

        if (needsEdit) {
            this.updateTypeLore(usedItem, ammoType);
            this.updateAmmoLore(usedItem, newAmmoCount);
        }

        return true;
    }

    @Override
    public boolean onShift(final Player shooter, final ItemStack usedItem, final boolean toggle) { return false; }

    @Override
    public boolean onLMB(final Player shooter, final ItemStack usedItem) {
        final int ammo = this.getAmmo(usedItem);
        if (ammo > 0) {
            this.updateAmmoLore(usedItem, 0);
            final Ammo ammoType = this.getAmmoType(usedItem);
            if (ammoType != null) {
                QualityArmory.addAmmoToInventory(shooter, ammoType, ammo);
            }
        }

        return true;
    }

    @Override
    public ItemStack getItemStack() {
        return CustomItemManager.getItemType("gun").getItem(this.getItemData().getMat(), this.getItemData().getData(),
                this.getItemData().getVariant());
    }

    @Override
    public boolean onSwapTo(final Player shooter, final ItemStack usedItem) { return false; }

    @Override
    public boolean onSwapAway(final Player shooter, final ItemStack usedItem) { return false; }

    public void updateAmmoLore(@NotNull final ItemStack item, final int newAmmo) {
        final ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta);

        boolean foundLine = false;
        List<String> lore = new ArrayList<>();

        if (meta.hasLore()) {
            lore = meta.getLore();
            Objects.requireNonNull(lore);
        }

        for (int i = 0; i < lore.size(); i++) {
            if (ChatColor.stripColor(lore.get(i)).contains(ChatColor.stripColor(QAMain.bagAmmo))) {
                lore.set(i, QAMain.bagAmmo + newAmmo + "/" + this.maxAmmo);
                foundLine = true;
                break;
            }
        }

        if (!foundLine) {
            lore.add(QAMain.bagAmmo + newAmmo + "/" + this.maxAmmo);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void updateTypeLore(@NotNull final ItemStack item, final Ammo type) {
        final ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta);

        boolean foundLine = false;
        List<String> lore = new ArrayList<>();

        if (meta.hasLore()) {
            lore = meta.getLore();
            Objects.requireNonNull(lore);
        }

        for (int i = 0; i < lore.size(); i++) {
            if (ChatColor.stripColor(lore.get(i)).contains(ChatColor.stripColor(QAMain.bagAmmoType))) {
                lore.set(i, QAMain.bagAmmoType + type.getName());
                foundLine = true;
                break;
            }
        }

        if (!foundLine) {
            lore.add(QAMain.bagAmmoType + type.getName());
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public int getAmmo(@NotNull final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta);
        if (!meta.hasLore()) {
            return 0;
        }

        final List<String> lore = meta.getLore();
        Objects.requireNonNull(lore);

        for (final String value : lore) {
            if (ChatColor.stripColor(value).contains(ChatColor.stripColor(QAMain.bagAmmo))) {
                final String s = value.replace(QAMain.bagAmmo, "");
                final String[] split = s.split("/");
                return Integer.parseInt(split[0]);
            }
        }

        return 0;
    }

    public @Nullable Ammo getAmmoType(@NotNull final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta);
        if (!meta.hasLore()) {
            return null;
        }

        final List<String> lore = meta.getLore();
        Objects.requireNonNull(lore);

        for (final String value : lore) {
            if (ChatColor.stripColor(value).contains(ChatColor.stripColor(QAMain.bagAmmoType))) {
                final String s = value.replace(QAMain.bagAmmoType, "");
                return QualityArmory.getAmmoByName(s);
            }
        }

        return null;
    }
}
