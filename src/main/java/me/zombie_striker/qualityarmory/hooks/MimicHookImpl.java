package me.zombie_striker.qualityarmory.hooks;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.Mimic;
import ru.endlesscode.mimic.MimicApiLevel;
import ru.endlesscode.mimic.items.BukkitItemsRegistry;

import java.util.Collection;
import java.util.stream.Collectors;

public class MimicHookImpl implements BukkitItemsRegistry, IHandler {

    @NotNull
    @Override
    public Collection<String> getKnownIds() {
        return QualityArmory.getInstance().getCustomItemsAsList().stream()
                .map(CustomBaseObject::getName)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ItemStack getItem(@NotNull String itemId, @Nullable Object payload, int amount) {
        ItemStack item = QualityArmory.getInstance().getCustomItemAsItemStack(itemId);
        if (item == null) return null;

        item.setAmount(amount);
        return item;
    }

    @Nullable
    @Override
    public String getItemId(@NotNull ItemStack item) {
        CustomBaseObject i = QualityArmory.getInstance().getCustomItem(item);
        return i == null ? null : i.getName();
    }

    @Override
    public boolean isItemExists(@NotNull String itemId) {
        return QualityArmory.getInstance().getCustomItemByName(itemId) != null;
    }

    @Override
    public void init(QAMain main) {
        Mimic.getInstance().registerItemsRegistry(this, MimicApiLevel.CURRENT, main, ServicePriority.Normal);

    }
}
