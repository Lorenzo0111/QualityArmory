package me.zombie_striker.qg.hooks;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import ru.endlesscode.mimic.Mimic;
import ru.endlesscode.mimic.MimicApiLevel;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

public class MimicHookImpl implements BukkitItemsRegistry {

    @NotNull
    @Override
    public Collection<String> getKnownIds() {
        return QualityArmory.getCustomItemsAsList().stream().map(CustomBaseObject::getName).collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ItemStack getItem(@NotNull final String itemId, @Nullable final Object payload, final int amount) {
        final ItemStack item = QualityArmory.getCustomItemAsItemStack(itemId);
        if (item == null)
            return null;

        item.setAmount(amount);
        return item;
    }

    @Nullable
    @Override
    public String getItemId(@NotNull final ItemStack item) {
        final CustomBaseObject i = QualityArmory.getCustomItem(item);
        return i == null ? null : i.getName();
    }

    @Override
    public boolean isItemExists(@NotNull final String itemId) { return QualityArmory.getCustomItemByName(itemId) != null; }

    public void register() {
        try {
            Mimic.getInstance().registerItemsRegistry(this, MimicApiLevel.CURRENT, QAMain.getInstance(), ServicePriority.Normal);
        } catch (final Throwable ignored) {
        }
    }
}
