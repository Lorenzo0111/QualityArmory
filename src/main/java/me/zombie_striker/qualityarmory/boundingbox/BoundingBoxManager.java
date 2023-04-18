package me.zombie_striker.qualityarmory.boundingbox;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.UUID;

public class BoundingBoxManager {

	//private static DefaultHumanoidBoundingBox DEFUALT = new DefaultHumanoidBoundingBox();
	private static PlayerBoundingBox PLAYER = new PlayerBoundingBox();
	private static DefaultHumanoidBoundingBox HUMANOID = new DefaultHumanoidBoundingBox(1.4,0.45,0.5);
	private static DefaultHumanoidBoundingBox WITHER_SKELETON = new DefaultHumanoidBoundingBox(1.8, 0.45, 2.4);
	private static DefaultHumanoidBoundingBox VILLAGER = new DefaultHumanoidBoundingBox(1.4, 0.45, 2);
	private static DefaultHumanoidBoundingBox CREEPER = new DefaultHumanoidBoundingBox(1.2, 0.4, 1.7);
	private static DefaultHumanoidBoundingBox ENDERMAN = new DefaultHumanoidBoundingBox(2.4, 0.4, 3);
	private static ComplexHumanoidBoundingBox IRON_GOLEM = new ComplexHumanoidBoundingBox(2.1, 0.8, 2.9, 0.4);
	private static ComplexHumanoidBoundingBox WITHER = new ComplexHumanoidBoundingBox(2.5, 1, 3.5, 1.5);
	private static DefaultHumanoidBoundingBox BAT = new DefaultHumanoidBoundingBox(0.5, 0.4, 1);
	private static ComplexAnimalBoundingBox SILVERFISH = new ComplexAnimalBoundingBox(0.3, 0.2, 0, 0.3, 0.05, 0.15);
	private static ComplexAnimalBoundingBox CAT = new ComplexAnimalBoundingBox(0.4, 0.3, 0.15, 0.4, 0.1, 0.4);
	private static ComplexAnimalBoundingBox SPIDER = new ComplexAnimalBoundingBox(0.8, 0.8, 0.2, 0.8, 0.2, 0.6);
	private static ComplexAnimalBoundingBox CHICKEN = new ComplexAnimalBoundingBox(0.5, 0.25, 0.5, 0.9, 0.2, 0.1);
	private static ComplexAnimalBoundingBox COW = new ComplexAnimalBoundingBox(1.3, 0.5, 1, 1.3, 0.25, 0.6);
	private static ComplexAnimalBoundingBox PHANTOM = new ComplexAnimalBoundingBox(0.5, 1, 0, 0.5, 0.25, 0.15);
	private static ComplexAnimalBoundingBox PIG = new ComplexAnimalBoundingBox(0.8, 0.5, 0.5, 1, 0.25, 0.6);
	private static ComplexAnimalBoundingBox HORSE = new ComplexAnimalBoundingBox(1.3, 0.75, 1.3, 2, 0.25, 0.75);
	private static ComplexAnimalBoundingBox WOLF = new ComplexAnimalBoundingBox(0.5, 0.3, 0.3, 0.5, 0.1, 0.3);


	//TODO: Verify
	public static ComplexAnimalBoundingBox FISH = new ComplexAnimalBoundingBox(0.3, 0.3, 0, 0.3, 0.1, 0.21);
	public static ComplexAnimalBoundingBox ENDERDRAGON = new ComplexAnimalBoundingBox(4, 8, 0.8, 4, 1, -3);
	public static ComplexAnimalBoundingBox CAVE_SPIDER = new ComplexAnimalBoundingBox(0.5, 0.5, 0.15, 0.5, 0.2, 0.4);
	public static ComplexAnimalBoundingBox RAVAGER = new ComplexAnimalBoundingBox(2, 1, 1, 2.2, 1, 1.5);
	public static ComplexAnimalBoundingBox GHAST = new ComplexAnimalBoundingBox(4, 2, 0, 4, 2, 0);
	public static ComplexAnimalBoundingBox GUARDIAN = new ComplexAnimalBoundingBox(1.3, 0.6, 0, 1.3, 0.6, 0);

	public static NullBoundingBox NULL = new NullBoundingBox();

	//public static ComplexAnimalBoundingBox CAVE_SPIDER = new ComplexAnimalBoundingBox(0.5,0.5,0.15,0.5,0.2,0.4);
	//TODO: SLIMES
	private static HashMap<UUID, AbstractBoundingBox> entityBoundbox = new HashMap<>();
	private static HashMap<EntityType, AbstractBoundingBox> entityTypeBoundingBox = new HashMap<>();

	public static AbstractBoundingBox getBoundingBox(Entity base) {
		if (entityBoundbox.containsKey(base.getUniqueId()))
			return entityBoundbox.get(base.getUniqueId());
		if (entityTypeBoundingBox.containsKey(base.getType()))
			return entityTypeBoundingBox.get(base.getType());
		return HUMANOID;
	}

	public static void setEntityTypeBoundingBox(EntityType type, AbstractBoundingBox box) {
		entityTypeBoundingBox.put(type, box);
	}

	public static void setEntityBoundingBox(Entity base, AbstractBoundingBox box) {
		entityBoundbox.put(base.getUniqueId(), box);
	}

	public static void initEntityTypeBoundingBoxes() {
		try {
			setEntityTypeBoundingBox(EntityType.PLAYER, PLAYER);

			setEntityTypeBoundingBox(EntityType.ENDER_DRAGON, ENDERDRAGON);

			setEntityTypeBoundingBox(EntityType.ARMOR_STAND, HUMANOID);
			setEntityTypeBoundingBox(EntityType.SKELETON, HUMANOID);
			setEntityTypeBoundingBox(EntityType.ZOMBIE, HUMANOID);
			setEntityTypeBoundingBox(EntityType.BLAZE, HUMANOID);
			setEntityTypeBoundingBox(EntityType.SNOWMAN, HUMANOID);

			setEntityTypeBoundingBox(EntityType.ENDERMAN, ENDERMAN);

			setEntityTypeBoundingBox(EntityType.WITHER_SKELETON, WITHER_SKELETON);

			setEntityTypeBoundingBox(EntityType.IRON_GOLEM, IRON_GOLEM);

			setEntityTypeBoundingBox(EntityType.CREEPER, CREEPER);
			setEntityTypeBoundingBox(EntityType.SQUID, CREEPER);

			setEntityTypeBoundingBox(EntityType.BAT, BAT);
			setEntityTypeBoundingBox(EntityType.RABBIT, BAT);

			setEntityTypeBoundingBox(EntityType.SPIDER, SPIDER);

			setEntityTypeBoundingBox(EntityType.CAVE_SPIDER, CAVE_SPIDER);

			setEntityTypeBoundingBox(EntityType.SILVERFISH, SILVERFISH);

			setEntityTypeBoundingBox(EntityType.VILLAGER, VILLAGER);
			setEntityTypeBoundingBox(EntityType.ZOMBIE_VILLAGER, VILLAGER);
			setEntityTypeBoundingBox(EntityType.WITCH, VILLAGER);
			setEntityTypeBoundingBox(EntityType.ILLUSIONER, VILLAGER);
			setEntityTypeBoundingBox(EntityType.EVOKER, VILLAGER);

			setEntityTypeBoundingBox(EntityType.PIG, PIG);

			setEntityTypeBoundingBox(EntityType.OCELOT, CAT);

			setEntityTypeBoundingBox(EntityType.CHICKEN, CHICKEN);

			setEntityTypeBoundingBox(EntityType.WOLF, WOLF);

			setEntityTypeBoundingBox(EntityType.COW, COW);
			setEntityTypeBoundingBox(EntityType.MUSHROOM_COW, COW);
			setEntityTypeBoundingBox(EntityType.SHEEP, COW);


			setEntityTypeBoundingBox(EntityType.HORSE, HORSE);
			setEntityTypeBoundingBox(EntityType.DONKEY, HORSE);
			setEntityTypeBoundingBox(EntityType.SKELETON_HORSE, HORSE);
			setEntityTypeBoundingBox(EntityType.ZOMBIE_HORSE, HORSE);
			setEntityTypeBoundingBox(EntityType.MULE, HORSE);
			setEntityTypeBoundingBox(EntityType.GHAST, GHAST);


			//Now, these are updates past 1.8, so we need to order by compat

			setEntityTypeBoundingBox(EntityType.LLAMA, HORSE);

			setEntityTypeBoundingBox(EntityType.WITHER, WITHER);


			setEntityTypeBoundingBox(EntityType.GUARDIAN, GUARDIAN);
			setEntityTypeBoundingBox(EntityType.ELDER_GUARDIAN, GUARDIAN);
			setEntityTypeBoundingBox(EntityType.VEX, BAT);
			setEntityTypeBoundingBox(EntityType.PARROT, BAT);

			//idk. 1.12 ish?
			setEntityTypeBoundingBox(EntityType.ENDERMITE, SILVERFISH);
			setEntityTypeBoundingBox(EntityType.STRAY, HUMANOID);
			setEntityTypeBoundingBox(EntityType.DROWNED, HUMANOID);
			setEntityTypeBoundingBox(EntityType.HUSK, HUMANOID);
			setEntityTypeBoundingBox(EntityType.POLAR_BEAR, COW);

			//1.13
			setEntityTypeBoundingBox(EntityType.SALMON, FISH);
			setEntityTypeBoundingBox(EntityType.TROPICAL_FISH, FISH);
			setEntityTypeBoundingBox(EntityType.PUFFERFISH, FISH);
			setEntityTypeBoundingBox(EntityType.COD, FISH);
			setEntityTypeBoundingBox(EntityType.DOLPHIN, WOLF);
			setEntityTypeBoundingBox(EntityType.PHANTOM, PHANTOM);
			setEntityTypeBoundingBox(EntityType.TURTLE, WOLF);

			//1.14
			setEntityTypeBoundingBox(EntityType.TRADER_LLAMA, HORSE);
			setEntityTypeBoundingBox(EntityType.RAVAGER, RAVAGER);
			setEntityTypeBoundingBox(EntityType.PANDA, COW);
			setEntityTypeBoundingBox(EntityType.FOX, WOLF);
			setEntityTypeBoundingBox(EntityType.CAT, CAT);
			setEntityTypeBoundingBox(EntityType.WANDERING_TRADER, VILLAGER);
			setEntityTypeBoundingBox(EntityType.PILLAGER, VILLAGER);
			setEntityTypeBoundingBox(EntityType.VINDICATOR, VILLAGER);

			//1.15
			setEntityTypeBoundingBox(EntityType.BEE, BAT);

			//1.16
			setEntityTypeBoundingBox(EntityType.PANDA, COW);

			//1.17
			setEntityTypeBoundingBox(EntityType.ZOMBIFIED_PIGLIN, HUMANOID);
			setEntityTypeBoundingBox(EntityType.PIGLIN, HUMANOID);
			setEntityTypeBoundingBox(EntityType.PIGLIN_BRUTE, HUMANOID);
		} catch (Error | Exception e4) {
		}

		//Just make sure that all new mobs will have a bounding box
		for (EntityType e : EntityType.values()) {
			if (e.isAlive()) {
				if (!entityTypeBoundingBox.containsKey(e))
					setEntityTypeBoundingBox(e, HUMANOID);
			}
		}


	}
}
