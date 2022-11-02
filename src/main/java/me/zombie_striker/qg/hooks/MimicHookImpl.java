package me.zombie_striker.qg.hooks;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.Mimic;
import ru.endlesscode.mimic.MimicApiLevel;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.Collection;
import java.util.stream.Collectors;

public class MimicHookImpl implements BukkitItemsRegistry {

    @NotNull
    @Override
    public Collection<String> getKnownIds() {
        return QualityArmory.getCustomItemsAsList().stream()
                .map(CustomBaseObject::getName)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ItemStack getItem(@NotNull String itemId, @Nullable Object payload, int amount) {
        ItemStack item = QualityArmory.getCustomItemAsItemStack(itemId);
        if (item == null) return null;

        item.setAmount(amount);
        return item;
    }

    @Nullable
    @Override
    public String getItemId(@NotNull ItemStack item) {
        CustomBaseObject i = QualityArmory.getCustomItem(item);
        return i == null ? null : i.getName();
    }

    @Override
    public boolean isItemExists(@NotNull String itemId) {
        return QualityArmory.getCustomItemByName(itemId) != null;
    }

    public void register() {
        try {
            Mimic.getInstance().registerItemsRegistry(this, MimicApiLevel.CURRENT, QAMain.getInstance(), ServicePriority.Normal);
        } catch (Throwable ignored) {}
    }
}
