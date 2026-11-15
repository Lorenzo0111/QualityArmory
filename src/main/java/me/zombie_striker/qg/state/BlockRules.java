package me.zombie_striker.qg.state;

import org.bukkit.Material;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * Block sets checked on every shot, stored as EnumSets for constant-time lookups.
 */
public final class BlockRules {
    private final Set<Material> interactable = EnumSet.noneOf(Material.class);
    private final Set<Material> destructable = EnumSet.noneOf(Material.class);

    /**
     * Rebuilds both sets.
     *
     * @param destructableMaterials Materials from the destructable-materials setting.
     */
    public void reload(Collection<Material> destructableMaterials) {
        this.interactable.clear();
        for (Material material : Material.values()) {
            if (!material.isBlock()) continue;
            String name = material.name();
            if (name.contains("DOOR") || name.contains("BUTTON") || name.contains("LEVER"))
                this.interactable.add(material);
        }

        this.destructable.clear();
        for (Material material : destructableMaterials)
            if (material != null)
                this.destructable.add(material);
    }

    /**
     * @return Whether right clicking the block with a gun should use the block (doors, buttons, levers).
     */
    public boolean isInteractable(Material material) {
        return this.interactable.contains(material);
    }

    /**
     * @return Whether explosions and bullets can destroy the block.
     */
    public boolean isDestructable(Material material) {
        return this.destructable.contains(material);
    }
}
