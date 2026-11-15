package me.zombie_striker.qg.commands;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.state.ItemRegistry;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Item name listing and completion for {@code /qa give} and {@code /qa drop}.
 */
final class ItemNames {

    private ItemNames() {
    }

    /**
     * @return All item names, grouped by type with alternating colors.
     */
    static String validItemsMessage() {
        ItemRegistry items = QAMain.getItemRegistry();
        StringBuilder sb = new StringBuilder(QAMain.getMessages().commands.validItems);
        append(sb, items.getGuns().values());
        sb.append(ChatColor.GRAY);
        append(sb, items.getAmmo().values());
        sb.append(ChatColor.WHITE);
        append(sb, items.getMisc().values());
        sb.append(ChatColor.GRAY);
        append(sb, items.getArmor().values());
        sb.append(ChatColor.WHITE);
        return sb.toString();
    }

    /**
     * @return Item names starting with the typed text, ignoring case.
     */
    static List<String> complete(String typed) {
        ItemRegistry items = QAMain.getItemRegistry();
        String prefix = typed.toLowerCase(Locale.ROOT);
        List<String> names = new ArrayList<>();
        addMatching(names, items.getGuns().values(), prefix);
        addMatching(names, items.getAmmo().values(), prefix);
        addMatching(names, items.getMisc().values(), prefix);
        addMatching(names, items.getArmor().values(), prefix);
        return names;
    }

    private static void append(StringBuilder sb, Collection<? extends CustomBaseObject> items) {
        for (CustomBaseObject item : items)
            sb.append(item.getName()).append(", ");
    }

    private static void addMatching(List<String> names, Collection<? extends CustomBaseObject> items, String prefix) {
        for (CustomBaseObject item : items)
            if (item.getName().toLowerCase(Locale.ROOT).startsWith(prefix))
                names.add(item.getName());
    }
}
