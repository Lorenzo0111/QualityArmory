package me.zombie_striker.qualityarmory.utils;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BlockCollisionUtil implements IHandler {
    private BlockCollisionEntry DEFAULT;
    private HashMap<Material, BlockCollisionEntry> customBlocks = new HashMap<>();
    private FileConfiguration blockdata_config = null;

    public double getResistance(Block b, Location l) {
        BlockCollisionEntry entry = DEFAULT;
        if(customBlocks.containsKey(b.getType())){
            entry=customBlocks.get(b.getType());
        }

        if(entry.isDoor()){
            if(b.getState() instanceof Door) {
                Door door = (Door) b.getState();
                /**
                 * TODO: Write door open or close
                 */
                if (door.isOpen())
                    return 0;
            }
            return entry.getResistance();
        }
        if(entry.isTrapdoor()){
            if(b.getBlockData() instanceof TrapDoor) {
                TrapDoor door = (TrapDoor) b.getBlockData();
                /**
                 * TODO: Write trapdoor open or close
                 */
                if (door.isOpen()) {
                    return 0;
                }else if (l.getY()-l.getBlockY()<=0.2)
                    return entry.getResistance();
            }
            return entry.getResistance();
        }
        if(entry.isSlab()){
            if(b.getBlockData() instanceof Slab) {
                switch ((((Slab) b.getBlockData()).getType())){
                    case BOTTOM:
                        if(l.getY()-l.getBlockY()<=0.5)
                            return entry.getResistance();
                        return 0.0;
                    case TOP:
                        if(l.getY()-l.getBlockY()>=0.5)
                            return entry.getResistance();
                        return 0.0;
                    case DOUBLE:
                        return entry.getResistance();
                }
            }
        }


        if (entry.isStairs()) {
            if (b.getData() < 4 && (l.getY() - l.getBlockY() < 0.5))
                return entry.getResistance();
            if (b.getData() >= 4 && (l.getY() - l.getBlockY() > 0.5))
                return entry.getResistance();
            switch (b.getData()) {
                case 0:
                case 4:
                    if(l.getX() - (0.5 + l.getBlockX()) > 0)
                        return entry.getResistance();
                    break;
                case 1:
                case 5:
                    if( l.getX() - (0.5 + l.getBlockX()) < 0)
                        return entry.getResistance();
                    break;
                case 2:
                case 6:
                    if( l.getZ() - (0.5 + l.getBlockZ()) > 0)
                        return entry.getResistance();
                    break;
                case 3:
                case 7:
                    if( l.getZ() - (0.5 + l.getBlockZ()) < 0)
                        return entry.getResistance();
                    break;

            }
            return 0.0;
        }
        return entry.getResistance();
    }

    @Override
    public void init(QAMain main) {
        File blockData = new File(main.getDataFolder(), "materialdata.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(blockData);
        if (!blockData.exists() || config.getKeys(false).size() == 0) {
            config.set("DEFAULT.resistance", 1.0);
            config.set("DEFAULT.blockheight", 1);
            config.set("DEFAULT.blockwidth", 1);
            config.set("DEFAULT.isStair", false);
            config.set("DEFAULT.isDoor", false);
            config.set("DEFAULT.isTrapdoor", false);
            config.set("DEFAULT.isSlab", false);

            for (Material material : Material.values()) {
                if (!material.isBlock())
                    continue;
                if(material.name().endsWith("_DOOR"))
                    config.set(material.name()+".isDoor", true);
                if(material.name().endsWith("_TRAPDOOR"))
                    config.set(material.name()+".isTrapdoor", true);
                if(material.name().endsWith("_STAIRS"))
                    config.set(material.name()+".isStair", true);
                if(material.name().endsWith("_SLAB"))
                    config.set(material.name()+".isSlab", true);
                if(material.name().endsWith("_CARPET"))
                    config.set(material.name()+".blockheight", 0.1);
                if(material.name().endsWith("_BED"))
                    config.set(material.name()+".blockheight", 0.45);
                if(material.name().contains("FENCE")||
                        material.name().contains("FENCE_GATE")
                )
                    config.set(material.name()+".resistance", 0.5);
                if(material.name().endsWith("PLANKS")||
                        material.name().endsWith("WOOD")||
                        material.name().endsWith("LOG")
                )
                    config.set(material.name()+".resistance", 0.5);

                if(material.name().endsWith("GLASS")||
                        material.name().endsWith("GLASS_PANE")||
                        material.name().endsWith("SIGN")||
                        material.name().endsWith("LEAVES")||
                        material.name().endsWith("GRASS")||
                        material.name().endsWith("FERN")||
                        material.name().endsWith("LEAVES")||
                        material.name().endsWith("AIR")
                )
                    config.set(material.name()+".resistance", 0.0);
                if(material.name().endsWith("WATER")
                )
                    config.set(material.name()+".resistance", 0.1);
            }
            try {
                config.save(blockData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        DEFAULT = new BlockCollisionEntry(
                config.getDouble("DEFAULT.resistance"),
                config.getDouble("DEFAULT.blockheight"),
                config.getDouble("DEFAULT.blockwidth"),
                config.getBoolean("DEFAULT.isStair"),
                config.getBoolean("DEFAULT.isDoor"),
                config.getBoolean("DEFAULT.isTrapdoor"),
                config.getBoolean("DEFAULT.isSlab")
        );

        for (String key : config.getKeys(false)) {
            if (Material.matchMaterial(key) != null) {
                double passthrough = config.contains(key + ".resistance") ? config.getDouble(key + ".resistance") : DEFAULT.getResistance();
                double height = config.contains(key + ".blockheight") ? config.getDouble(key + ".blockheight") : DEFAULT.getHeight();
                double width = config.contains(key + ".blockwidth") ? config.getDouble(key + ".blockwidth") : DEFAULT.getWidth();
                boolean stair = config.contains(key + ".isStair") ? config.getBoolean(key + ".isStair") : DEFAULT.isStairs();
                boolean door = config.contains(key + ".isDoor") ? config.getBoolean(key + ".isDoor") : DEFAULT.isDoor();
                boolean trapdoor = config.contains(key + ".isTrapdoor") ? config.getBoolean(key + ".isTrapdoor") : DEFAULT.isTrapdoor();
                boolean slab = config.contains(key + ".isSlab") ? config.getBoolean(key + ".isSlab") : DEFAULT.isSlab();
                this.customBlocks.put(Material.matchMaterial(key), new BlockCollisionEntry(passthrough, height, width, stair, door, trapdoor,slab));
            }
        }

        this.blockdata_config = config;
    }

    public class BlockCollisionEntry {
        private final double resistance;
        private final double height;
        private final double width;
        private final boolean stairs;
        private final boolean trapdoor;
        private final boolean door;
        private final boolean slab;

        public BlockCollisionEntry(double resistance, double height, double width, boolean stairs, boolean trappdoor, boolean door, boolean slab) {
            this.resistance = resistance;
            this.height = height;
            this.width = width;
            this.trapdoor = trappdoor;
            this.stairs = stairs;
            this.door = door;
            this.slab = slab;
        }

        public boolean isSlab() {
            return slab;
        }

        public boolean isDoor() {
            return door;
        }

        public double getResistance() {
            return resistance;
        }

        public boolean isStairs() {
            return stairs;
        }

        public boolean isTrapdoor() {
            return trapdoor;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }
}
