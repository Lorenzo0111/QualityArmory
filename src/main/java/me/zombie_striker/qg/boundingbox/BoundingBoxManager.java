package me.zombie_striker.qg.boundingbox;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.zombie_striker.qg.handlers.MultiVersionLookup;

public class BoundingBoxManager {

    // private static DefaultHumanoidBoundingBox DEFUALT = new
    // DefaultHumanoidBoundingBox();
    public static PlayerBoundingBox PLAYER = new PlayerBoundingBox();
    public static DefaultHumanoidBoundingBox HUMANOID = new DefaultHumanoidBoundingBox();
    public static DefaultHumanoidBoundingBox WITHER_SKELETON = new DefaultHumanoidBoundingBox(1.8, 0.45, 2.4);
    public static DefaultHumanoidBoundingBox VILLAGER = new DefaultHumanoidBoundingBox(1.4, 0.45, 2);
    public static DefaultHumanoidBoundingBox CREEPER = new DefaultHumanoidBoundingBox(1.2, 0.4, 1.7);
    public static DefaultHumanoidBoundingBox ENDERMAN = new DefaultHumanoidBoundingBox(2.4, 0.4, 3);
    public static ComplexHumanoidBoundingBox IRON_GOLEM = new ComplexHumanoidBoundingBox(2.1, 0.8, 2.9, 0.4);
    public static ComplexHumanoidBoundingBox WITHER = new ComplexHumanoidBoundingBox(2.5, 1, 3.5, 1.5);
    public static DefaultHumanoidBoundingBox BAT = new DefaultHumanoidBoundingBox(0.5, 0.4, 1);
    public static ComplexAnimalBoundingBox SILVERFISH = new ComplexAnimalBoundingBox(0.3, 0.2, 0, 0.3, 0.05, 0.15);
    public static ComplexAnimalBoundingBox CAT = new ComplexAnimalBoundingBox(0.4, 0.3, 0.15, 0.4, 0.1, 0.4);
    public static ComplexAnimalBoundingBox SPIDER = new ComplexAnimalBoundingBox(0.8, 0.8, 0.2, 0.8, 0.2, 0.6);
    public static ComplexAnimalBoundingBox CHICKEN = new ComplexAnimalBoundingBox(0.5, 0.25, 0.5, 0.9, 0.2, 0.1);
    public static ComplexAnimalBoundingBox COW = new ComplexAnimalBoundingBox(1.3, 0.5, 1, 1.3, 0.25, 0.6);
    public static ComplexAnimalBoundingBox PHANTOM = new ComplexAnimalBoundingBox(0.5, 1, 0, 0.5, 0.25, 0.15);
    public static ComplexAnimalBoundingBox PIG = new ComplexAnimalBoundingBox(0.8, 0.5, 0.5, 1, 0.25, 0.6);
    public static ComplexAnimalBoundingBox HORSE = new ComplexAnimalBoundingBox(1.3, 0.75, 1.3, 2, 0.25, 0.75);
    public static ComplexAnimalBoundingBox WOLF = new ComplexAnimalBoundingBox(0.5, 0.3, 0.3, 0.5, 0.1, 0.3);

    // TODO: Verify
    public static ComplexAnimalBoundingBox FISH = new ComplexAnimalBoundingBox(0.3, 0.3, 0, 0.3, 0.1, 0.21);
    public static ComplexAnimalBoundingBox ENDERDRAGON = new ComplexAnimalBoundingBox(4, 8, 0.8, 4, 1, -3);
    public static ComplexAnimalBoundingBox CAVE_SPIDER = new ComplexAnimalBoundingBox(0.5, 0.5, 0.15, 0.5, 0.2, 0.4);
    public static ComplexAnimalBoundingBox RAVAGER = new ComplexAnimalBoundingBox(2, 1, 1, 2.2, 1, 1.5);
    public static ComplexAnimalBoundingBox GHAST = new ComplexAnimalBoundingBox(4, 2, 0, 4, 2, 0);
    public static ComplexAnimalBoundingBox GUARDIAN = new ComplexAnimalBoundingBox(1.3, 0.6, 0, 1.3, 0.6, 0);

    public static NullBoundingBox NULL = new NullBoundingBox();

    // public static ComplexAnimalBoundingBox CAVE_SPIDER = new
    // ComplexAnimalBoundingBox(0.5,0.5,0.15,0.5,0.2,0.4);
    // TODO: SLIMES
    private static HashMap<UUID, AbstractBoundingBox> entityBoundbox = new HashMap<>();
    private static HashMap<EntityType, AbstractBoundingBox> entityTypeBoundingBox = new HashMap<>();

    public static AbstractBoundingBox getBoundingBox(final Entity base) {
        if (BoundingBoxManager.entityBoundbox.containsKey(base.getUniqueId()))
            return BoundingBoxManager.entityBoundbox.get(base.getUniqueId());
        if (BoundingBoxManager.entityTypeBoundingBox.containsKey(base.getType()))
            return BoundingBoxManager.entityTypeBoundingBox.get(base.getType());
        return BoundingBoxManager.HUMANOID;
    }

    public static void setEntityTypeBoundingBox(final EntityType type, final AbstractBoundingBox box) {
        BoundingBoxManager.entityTypeBoundingBox.put(type, box);
    }

    public static void setEntityBoundingBox(final Entity base, final AbstractBoundingBox box) {
        BoundingBoxManager.entityBoundbox.put(base.getUniqueId(), box);
    }

    public static void initEntityTypeBoundingBoxes() {
        try {
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.PLAYER, BoundingBoxManager.PLAYER);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ENDER_DRAGON, BoundingBoxManager.ENDERDRAGON);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ARMOR_STAND, BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SKELETON, BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ZOMBIE, BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.BLAZE, BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(MultiVersionLookup.getZombiePig(), BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SNOW_GOLEM, BoundingBoxManager.HUMANOID);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ENDERMAN, BoundingBoxManager.ENDERMAN);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.WITHER_SKELETON, BoundingBoxManager.WITHER_SKELETON);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.IRON_GOLEM, BoundingBoxManager.IRON_GOLEM);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.CREEPER, BoundingBoxManager.CREEPER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SQUID, BoundingBoxManager.CREEPER);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.BAT, BoundingBoxManager.BAT);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.RABBIT, BoundingBoxManager.BAT);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SPIDER, BoundingBoxManager.SPIDER);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.CAVE_SPIDER, BoundingBoxManager.CAVE_SPIDER);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SILVERFISH, BoundingBoxManager.SILVERFISH);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.VILLAGER, BoundingBoxManager.VILLAGER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ZOMBIE_VILLAGER, BoundingBoxManager.VILLAGER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.WITCH, BoundingBoxManager.VILLAGER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ILLUSIONER, BoundingBoxManager.VILLAGER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.EVOKER, BoundingBoxManager.VILLAGER);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.PIG, BoundingBoxManager.PIG);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.OCELOT, BoundingBoxManager.CAT);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.CHICKEN, BoundingBoxManager.CHICKEN);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.WOLF, BoundingBoxManager.WOLF);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.COW, BoundingBoxManager.COW);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.MOOSHROOM, BoundingBoxManager.COW);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SHEEP, BoundingBoxManager.COW);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.HORSE, BoundingBoxManager.HORSE);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.DONKEY, BoundingBoxManager.HORSE);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SKELETON_HORSE, BoundingBoxManager.HORSE);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ZOMBIE_HORSE, BoundingBoxManager.HORSE);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.MULE, BoundingBoxManager.HORSE);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.GHAST, BoundingBoxManager.GHAST);

            // Now, these are updates past 1.8, so we need to order by compat

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.LLAMA, BoundingBoxManager.HORSE);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.WITHER, BoundingBoxManager.WITHER);

            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.GUARDIAN, BoundingBoxManager.GUARDIAN);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ELDER_GUARDIAN, BoundingBoxManager.GUARDIAN);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.VEX, BoundingBoxManager.BAT);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.PARROT, BoundingBoxManager.BAT);

            // idk. 1.12 ish?
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.ENDERMITE, BoundingBoxManager.SILVERFISH);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.STRAY, BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.DROWNED, BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.HUSK, BoundingBoxManager.HUMANOID);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.POLAR_BEAR, BoundingBoxManager.COW);

            // 1.13
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.SALMON, BoundingBoxManager.FISH);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.TROPICAL_FISH, BoundingBoxManager.FISH);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.PUFFERFISH, BoundingBoxManager.FISH);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.COD, BoundingBoxManager.FISH);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.DOLPHIN, BoundingBoxManager.WOLF);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.PHANTOM, BoundingBoxManager.PHANTOM);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.TURTLE, BoundingBoxManager.WOLF);

            // 1.14
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.TRADER_LLAMA, BoundingBoxManager.HORSE);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.RAVAGER, BoundingBoxManager.RAVAGER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.PANDA, BoundingBoxManager.COW);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.FOX, BoundingBoxManager.WOLF);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.CAT, BoundingBoxManager.CAT);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.WANDERING_TRADER, BoundingBoxManager.VILLAGER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.PILLAGER, BoundingBoxManager.VILLAGER);
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.VINDICATOR, BoundingBoxManager.VILLAGER);

            // 1.15
            BoundingBoxManager.setEntityTypeBoundingBox(EntityType.BEE, BoundingBoxManager.BAT);
        } catch (Error | Exception e4) {
        }

        // Just make sure that all new mobs will have a bounding box
        for (final EntityType e : EntityType.values()) {
            if (e.isAlive()) {
                if (!BoundingBoxManager.entityTypeBoundingBox.containsKey(e))
                    BoundingBoxManager.setEntityTypeBoundingBox(e, BoundingBoxManager.HUMANOID);
            }
        }

    }
}
