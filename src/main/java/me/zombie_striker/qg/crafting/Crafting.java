package me.zombie_striker.qg.crafting;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.customitemmanager.MaterialStorage;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Crafting ingredients: parsing them from item files, checking and taking them from inventories,
 * and their translated names.
 * <p>
 * An ingredient is either an {@link ItemStack} ({@code MATERIAL,durability,amount} in item files) or the
 * name of another custom item.
 */
public final class Crafting {

    private Crafting() {
    }

    /**
     * @return Whether the player has every ingredient of the item.
     */
    public static boolean hasIngredients(Player player, CustomBaseObject item) {
        return hasIngredients(player, item.getIngredientsRaw());
    }

    /**
     * @return Whether the player has every ingredient.
     */
    @SuppressWarnings("deprecation")
    public static boolean hasIngredients(Player player, Object[] ings) {
        if (ings == null)
            return true;
        boolean[] bb = new boolean[ings.length];
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                for (int i = 0; i < ings.length; i++) {
                    if (bb[i])
                        continue;
                    if (ings[i] instanceof ItemStack) {
                        ItemStack check = (ItemStack) ings[i];
                        if (is.getType() == check.getType()
                                && (check.getDurability() == 0 || is.getDurability() == check.getDurability())) {
                            if (is.getAmount() >= check.getAmount())
                                bb[i] = true;
                            break;
                        }
                    } else if (ings[i] instanceof String) {
                        CustomBaseObject base = QualityArmory.getCustomItemByName((String) ings[i]);
                        if (QualityArmory.getCustomItem(is) == base) {
                            bb[i] = true;
                            break;
                        }

                    }
                }
            }
        }
        for (boolean b : bb) {
            if (!b)
                return false;
        }
        return true;
    }

    /**
     * Takes the ingredients of the item from the player's inventory.
     *
     * @return Whether every ingredient was found.
     */
    public static boolean removeIngredients(Player player, CustomBaseObject item) {
        return removeIngredients(player, item.getIngredientsRaw());
    }

    /**
     * Takes the ingredients from the player's inventory.
     *
     * @return Whether every ingredient was found.
     */
    @SuppressWarnings("deprecation")
    public static boolean removeIngredients(Player player, Object[] ings) {
        if (ings == null)
            return true;
        boolean[] bb = new boolean[ings.length];
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null) {
                CustomBaseObject obj = QualityArmory.getCustomItem(is);
                for (int i = 0; i < ings.length; i++) {
                    if (bb[i])
                        continue;
                    if (obj != null) {
                        if (ings[i] instanceof String && QualityArmory.getCustomItemByName((String) ings[i]) == obj) {
                            for (int slot = 0; slot < player.getInventory().getContents().length; slot++) {
                                if (player.getInventory().getItem(slot).equals(is)) {
                                    player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                                    break;
                                }
                            }
                            bb[i] = true;
                            break;
                        }
                    } else if (ings[i] instanceof ItemStack) {
                        ItemStack check = (ItemStack) ings[i];
                        if (is.getType() == check.getType() && is.getDurability() == check.getDurability()) {
                            if (is.getAmount() > check.getAmount()) {
                                bb[i] = true;
                                int slot = player.getInventory().first(is);
                                is.setAmount(is.getAmount() - check.getAmount());
                                player.getInventory().setItem(slot, is);
                            } else if (is.getAmount() == check.getAmount()) {
                                bb[i] = true;
                                int slot = player.getInventory().first(is);
                                player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (boolean b : bb) {
            if (!b)
                return false;
        }
        return true;
    }

    /**
     * Parses ingredients from an item file. Entries without a comma are custom item names.
     */
    @SuppressWarnings("deprecation")
    public static Object[] parseIngredients(List<String> e) {
        Object[] list = new Object[e.size()];
        for (int i = 0; i < e.size(); i++) {
            ItemStack temp = null;
            if (!e.get(i).contains(",")) {
                list[i] = e.get(i);
                continue;
            }
            String[] k = e.get(i).split(",");

            Material material = Material.matchMaterial(k[0]);
            if (material == null) {
                QAMain.getInstance().getLogger().warning("Unknown material in crafting requirements: " + k[0]);
                continue;
            }
            temp = new ItemStack(material);
            if (k.length > 1)
                temp.setDurability(Short.parseShort(k[1]));
            if (k.length > 2)
                temp.setAmount(Integer.parseInt(k[2]));
            list[i] = temp;
        }
        return list;
    }

    /**
     * Parses ingredients from an item file, keeping only vanilla items.
     */
    public static ItemStack[] parseItemIngredients(List<String> e) {
        Object[] list = parseIngredients(e);
        ItemStack[] li = new ItemStack[list.length];
        for (int i = 0; i < list.length; i++) {
            if (list[i] instanceof ItemStack) {
                li[i] = (ItemStack) list[i];
            }
        }
        return li;

    }

    /**
     * @return The item file representation of a vanilla ingredient.
     */
    public static String toIngredientString(Material m, int durability, int amount) {
        return m.toString() + "," + durability + "," + amount;
    }

    /**
     * Caches the translated names of the vanilla ingredients used by the items.
     */
    public static void registerIngredientNames(Map<MaterialStorage, ?> regMaps) {
        if (null != regMaps && !regMaps.isEmpty()) {
            for (Object item : regMaps.values()) {
                Object[] ingredients = ((CustomBaseObject) item).getIngredientsRaw();
                if (ingredients == null) continue;
                for (Object ingredient : ingredients) {
                    // Ingredients can also be the names of other custom items
                    if (!(ingredient instanceof ItemStack)) continue;
                    String itemName = ((ItemStack) ingredient).getType().name();
                    QAMain.getItemRegistry().getCraftingNames().put(itemName, QAMain.getMessages().getMaterialName(itemName));
                }
            }
        }
    }

    /**
     * @return The translated name of a vanilla ingredient, or the default name.
     */
    public static String findIngredientName(String itemName, String defaultName) {
        Map<String, String> names = QAMain.getItemRegistry().getCraftingNames();
        String value = names.get(itemName);
        if (value == null || value.trim().isEmpty()) {
            names.put(itemName, defaultName);
            value = defaultName;
        }
        return value;
    }
}
