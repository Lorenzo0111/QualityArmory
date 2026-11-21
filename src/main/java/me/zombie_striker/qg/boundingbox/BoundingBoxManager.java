package me.zombie_striker.qg.boundingbox;

import java.util.HashMap;
import java.util.UUID;

import com.cryptomorin.xseries.XEntityType;
import me.zombie_striker.qg.handlers.MultiVersionLookup;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BoundingBoxManager {

	//private static DefaultHumanoidBoundingBox DEFUALT = new DefaultHumanoidBoundingBox();
	public static final PlayerBoundingBox PLAYER = new PlayerBoundingBox();
	public static final DefaultHumanoidBoundingBox HUMANOID = new DefaultHumanoidBoundingBox();
	public static final DefaultHumanoidBoundingBox WITHER_SKELETON = new DefaultHumanoidBoundingBox(1.8, 0.45, 2.4);
	public static final DefaultHumanoidBoundingBox VILLAGER = new DefaultHumanoidBoundingBox(1.4, 0.45, 2);
	public static final DefaultHumanoidBoundingBox CREEPER = new DefaultHumanoidBoundingBox(1.2, 0.4, 1.7);
	public static final DefaultHumanoidBoundingBox ENDERMAN = new DefaultHumanoidBoundingBox(2.4, 0.4, 3);
	public static final ComplexHumanoidBoundingBox IRON_GOLEM = new ComplexHumanoidBoundingBox(2.1, 0.8, 2.9, 0.4);
	public static final ComplexHumanoidBoundingBox WITHER = new ComplexHumanoidBoundingBox(2.5, 1, 3.5, 1.5);
	public static final DefaultHumanoidBoundingBox BAT = new DefaultHumanoidBoundingBox(0.5, 0.4, 1);
	public static final ComplexAnimalBoundingBox SILVERFISH = new ComplexAnimalBoundingBox(0.3, 0.2, 0, 0.3, 0.05, 0.15);
	public static final ComplexAnimalBoundingBox CAT = new ComplexAnimalBoundingBox(0.4, 0.3, 0.15, 0.4, 0.1, 0.4);
	public static final ComplexAnimalBoundingBox SPIDER = new ComplexAnimalBoundingBox(0.8, 0.8, 0.2, 0.8, 0.2, 0.6);
	public static final ComplexAnimalBoundingBox CHICKEN = new ComplexAnimalBoundingBox(0.5, 0.25, 0.5, 0.9, 0.2, 0.1);
	public static final ComplexAnimalBoundingBox COW = new ComplexAnimalBoundingBox(1.3, 0.5, 1, 1.3, 0.25, 0.6);
	public static final ComplexAnimalBoundingBox PHANTOM = new ComplexAnimalBoundingBox(0.5, 1, 0, 0.5, 0.25, 0.15);
	public static final ComplexAnimalBoundingBox PIG = new ComplexAnimalBoundingBox(0.8, 0.5, 0.5, 1, 0.25, 0.6);
	public static final ComplexAnimalBoundingBox HORSE = new ComplexAnimalBoundingBox(1.3, 0.75, 1.3, 2, 0.25, 0.75);
	public static final ComplexAnimalBoundingBox WOLF = new ComplexAnimalBoundingBox(0.5, 0.3, 0.3, 0.5, 0.1, 0.3);


	//TODO: Verify
	public static final ComplexAnimalBoundingBox FISH = new ComplexAnimalBoundingBox(0.3, 0.3, 0, 0.3, 0.1, 0.21);
	public static final ComplexAnimalBoundingBox ENDERDRAGON = new ComplexAnimalBoundingBox(4, 8, 0.8, 4, 1, -3);
	public static final ComplexAnimalBoundingBox CAVE_SPIDER = new ComplexAnimalBoundingBox(0.5, 0.5, 0.15, 0.5, 0.2, 0.4);
	public static final ComplexAnimalBoundingBox RAVAGER = new ComplexAnimalBoundingBox(2, 1, 1, 2.2, 1, 1.5);
	public static final ComplexAnimalBoundingBox GHAST = new ComplexAnimalBoundingBox(4, 2, 0, 4, 2, 0);
	public static final ComplexAnimalBoundingBox GUARDIAN = new ComplexAnimalBoundingBox(1.3, 0.6, 0, 1.3, 0.6, 0);

	public static final NullBoundingBox NULL = new NullBoundingBox();

	//public static final ComplexAnimalBoundingBox CAVE_SPIDER = new ComplexAnimalBoundingBox(0.5,0.5,0.15,0.5,0.2,0.4);
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

	/**
	 * Registers a bounding box by entity type name. Types that don't exist on this version are skipped.
	 */
	private static void setEntityTypeBoundingBox(String type, AbstractBoundingBox box) {
		try {
			setEntityTypeBoundingBox(EntityType.valueOf(type), box);
		} catch (IllegalArgumentException missingOnThisVersion) {
			// The entity type was added in a newer Minecraft version
		}
	}

	public static void setEntityTypeBoundingBox(EntityType type, AbstractBoundingBox box) {
		entityTypeBoundingBox.put(type, box);
	}

	public static void setEntityBoundingBox(Entity base, AbstractBoundingBox box) {
		entityBoundbox.put(base.getUniqueId(), box);
	}

	public static void initEntityTypeBoundingBoxes() {
		// By name, so types missing on older versions only skip themselves
		setEntityTypeBoundingBox("PLAYER", PLAYER);

		setEntityTypeBoundingBox("ENDER_DRAGON", ENDERDRAGON);

		setEntityTypeBoundingBox("ARMOR_STAND", HUMANOID);
		setEntityTypeBoundingBox("SKELETON", HUMANOID);
		setEntityTypeBoundingBox("ZOMBIE", HUMANOID);
		setEntityTypeBoundingBox("BLAZE", HUMANOID);
		setEntityTypeBoundingBox(MultiVersionLookup.getZombiePig(), HUMANOID);
		setEntityTypeBoundingBox(XEntityType.SNOW_GOLEM.get(), HUMANOID);

		setEntityTypeBoundingBox("ENDERMAN", ENDERMAN);

		setEntityTypeBoundingBox("WITHER_SKELETON", WITHER_SKELETON);

		setEntityTypeBoundingBox("IRON_GOLEM", IRON_GOLEM);

		setEntityTypeBoundingBox("CREEPER", CREEPER);
		setEntityTypeBoundingBox("SQUID", CREEPER);

		setEntityTypeBoundingBox("BAT", BAT);
		setEntityTypeBoundingBox("RABBIT", BAT);

		setEntityTypeBoundingBox("SPIDER", SPIDER);

		setEntityTypeBoundingBox("CAVE_SPIDER", CAVE_SPIDER);

		setEntityTypeBoundingBox("SILVERFISH", SILVERFISH);

		setEntityTypeBoundingBox("VILLAGER", VILLAGER);
		setEntityTypeBoundingBox("ZOMBIE_VILLAGER", VILLAGER);
		setEntityTypeBoundingBox("WITCH", VILLAGER);
		setEntityTypeBoundingBox("ILLUSIONER", VILLAGER);
		setEntityTypeBoundingBox("EVOKER", VILLAGER);

		setEntityTypeBoundingBox("PIG", PIG);

		setEntityTypeBoundingBox("OCELOT", CAT);

		setEntityTypeBoundingBox("CHICKEN", CHICKEN);

		setEntityTypeBoundingBox("WOLF", WOLF);

		setEntityTypeBoundingBox("COW", COW);
		setEntityTypeBoundingBox(XEntityType.MOOSHROOM.get(), COW);
		setEntityTypeBoundingBox("SHEEP", COW);


		setEntityTypeBoundingBox("HORSE", HORSE);
		setEntityTypeBoundingBox("DONKEY", HORSE);
		setEntityTypeBoundingBox("SKELETON_HORSE", HORSE);
		setEntityTypeBoundingBox("ZOMBIE_HORSE", HORSE);
		setEntityTypeBoundingBox("MULE", HORSE);
		setEntityTypeBoundingBox("GHAST", GHAST);


		//Now, these are updates past 1.8, so we need to order by compat

		setEntityTypeBoundingBox("LLAMA", HORSE);

		setEntityTypeBoundingBox("WITHER", WITHER);


		setEntityTypeBoundingBox("GUARDIAN", GUARDIAN);
		setEntityTypeBoundingBox("ELDER_GUARDIAN", GUARDIAN);
		setEntityTypeBoundingBox("VEX", BAT);
		setEntityTypeBoundingBox("PARROT", BAT);

		//idk. 1.12 ish?
		setEntityTypeBoundingBox("ENDERMITE", SILVERFISH);
		setEntityTypeBoundingBox("STRAY", HUMANOID);
		setEntityTypeBoundingBox("DROWNED", HUMANOID);
		setEntityTypeBoundingBox("HUSK", HUMANOID);
		setEntityTypeBoundingBox("POLAR_BEAR", COW);

		//1.13
		setEntityTypeBoundingBox("SALMON", FISH);
		setEntityTypeBoundingBox("TROPICAL_FISH", FISH);
		setEntityTypeBoundingBox("PUFFERFISH", FISH);
		setEntityTypeBoundingBox("COD", FISH);
		setEntityTypeBoundingBox("DOLPHIN", WOLF);
		setEntityTypeBoundingBox("PHANTOM", PHANTOM);
		setEntityTypeBoundingBox("TURTLE", WOLF);

		//1.14
		setEntityTypeBoundingBox("TRADER_LLAMA", HORSE);
		setEntityTypeBoundingBox("RAVAGER", RAVAGER);
		setEntityTypeBoundingBox("PANDA", COW);
		setEntityTypeBoundingBox("FOX", WOLF);
		setEntityTypeBoundingBox("CAT", CAT);
		setEntityTypeBoundingBox("WANDERING_TRADER", VILLAGER);
		setEntityTypeBoundingBox("PILLAGER", VILLAGER);
		setEntityTypeBoundingBox("VINDICATOR", VILLAGER);

		//1.15
		setEntityTypeBoundingBox("BEE", BAT);


		//Just make sure that all new mobs will have a bounding box
		for (EntityType e : EntityType.values()) {
			if (e.isAlive()) {
				if (!entityTypeBoundingBox.containsKey(e))
					setEntityTypeBoundingBox(e, HUMANOID);
			}
		}


	}
}
