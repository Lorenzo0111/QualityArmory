package me.zombie_striker.qg.handlers;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

public class MultiVersionLookup {

    private static Material wood;
    private static Material glasspane;
    private static Material gunpowderr;
    private static Material skull;
    private static Material mycil;
    private static Material wool;
    private static Material grasspath;
    private static Material goldpickaxe;
    private static Material goldshovel;
    private static Material goldhoe;
    private static Material ironshovel;
    private static Material ink;

    private static Material carrotstick;

    private static Sound noteHarp;
    private static Sound woolsound;
    private static Sound enderdrag;
    private static Sound pliung;

    private static EntityType zombiePig;

    public static Sound getPling() {
        if (MultiVersionLookup.pliung == null) {
            try {
                MultiVersionLookup.pliung = Sound.valueOf("BLOCK_NOTE_PLING");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.pliung == null)
                MultiVersionLookup.pliung = Sound.BLOCK_NOTE_BLOCK_PLING;
        }
        return MultiVersionLookup.pliung;
    }

    public static Sound getDragonGrowl() {
        if (MultiVersionLookup.enderdrag == null) {
            try {
                MultiVersionLookup.enderdrag = Sound.valueOf("ENTITY_ENDERDRAGON_GROWL");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.enderdrag == null)
                MultiVersionLookup.enderdrag = Sound.ENTITY_ENDER_DRAGON_GROWL;
        }
        return MultiVersionLookup.enderdrag;
    }

    public static Sound getWoolSound() {
        if (MultiVersionLookup.woolsound == null) {
            try {
                MultiVersionLookup.woolsound = Sound.valueOf("BLOCK_CLOTH_BREAK");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.woolsound == null)
                MultiVersionLookup.woolsound = Sound.BLOCK_WOOL_BREAK;
        }
        return MultiVersionLookup.woolsound;
    }

    public static Sound getHarp() {
        if (MultiVersionLookup.noteHarp == null) {
            try {
                MultiVersionLookup.noteHarp = Sound.valueOf("BLOCK_NOTE_HARP");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.noteHarp == null)
                MultiVersionLookup.noteHarp = Sound.BLOCK_NOTE_BLOCK_HARP;
        }
        return MultiVersionLookup.noteHarp;
    }

    public static Material getINKSAC() {
        if (MultiVersionLookup.ink == null) {
            try {
                MultiVersionLookup.ink = Material.matchMaterial("INK_SACK");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.ink == null)
                MultiVersionLookup.ink = Material.INK_SAC;
        }
        return MultiVersionLookup.ink;
    }

    public static Material getIronShovel() {
        if (MultiVersionLookup.ironshovel == null) {
            try {
                MultiVersionLookup.ironshovel = Material.matchMaterial("IRON_SPADE");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.ironshovel == null)
                MultiVersionLookup.ironshovel = Material.IRON_SHOVEL;
        }
        return MultiVersionLookup.ironshovel;
    }

    public static Material getCarrotOnAStick() {
        if (MultiVersionLookup.carrotstick == null) {
            try {
                MultiVersionLookup.carrotstick = Material.matchMaterial("CARROT_STICK");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.carrotstick == null)
                MultiVersionLookup.carrotstick = Material.CARROT_ON_A_STICK;
        }
        return MultiVersionLookup.carrotstick;
    }

    public static Material getGoldHoe() {
        if (MultiVersionLookup.goldhoe == null) {
            try {
                MultiVersionLookup.goldhoe = Material.matchMaterial("GOLD_HOE");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.goldhoe == null)
                MultiVersionLookup.goldhoe = Material.GOLDEN_HOE;
        }
        return MultiVersionLookup.goldhoe;
    }

    public static Material getGoldShovel() {
        if (MultiVersionLookup.goldshovel == null) {
            try {
                MultiVersionLookup.goldshovel = Material.matchMaterial("GOLD_SPADE");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.goldshovel == null)
                MultiVersionLookup.goldshovel = Material.GOLDEN_SHOVEL;
        }
        return MultiVersionLookup.goldshovel;
    }

    public static Material getGoldPick() {
        if (MultiVersionLookup.goldpickaxe == null) {
            try {
                MultiVersionLookup.goldpickaxe = Material.matchMaterial("GOLD_PICKAXE");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.goldpickaxe == null)
                MultiVersionLookup.goldpickaxe = Material.GOLDEN_PICKAXE;
        }
        return MultiVersionLookup.goldpickaxe;
    }

    public static Material getGrassPath() {
        if (MultiVersionLookup.grasspath == null) {
            try {
                MultiVersionLookup.grasspath = Material.matchMaterial("GRASS_PATH");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.grasspath == null)
                MultiVersionLookup.grasspath = Material.DIRT_PATH;
        }
        return MultiVersionLookup.grasspath;
    }

    public static Material getWool() {
        if (MultiVersionLookup.wool == null) {
            try {
                MultiVersionLookup.wool = Material.matchMaterial("WOOL");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.wool == null)
                MultiVersionLookup.wool = Material.WHITE_WOOL;
        }
        return MultiVersionLookup.wool;
    }

    public static Material getWood() {
        if (MultiVersionLookup.wood == null) {
            try {
                MultiVersionLookup.wood = Material.matchMaterial("WOOD");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.wood == null)
                MultiVersionLookup.wood = Material.OAK_PLANKS;
        }
        return MultiVersionLookup.wood;
    }

    public static Material getGlass() {
        if (MultiVersionLookup.glasspane == null) {
            try {
                MultiVersionLookup.glasspane = Material.matchMaterial("STAINED_GLASS_PANE");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.glasspane == null)
                MultiVersionLookup.glasspane = Material.YELLOW_STAINED_GLASS_PANE;
        }
        return MultiVersionLookup.glasspane;
    }

    public static Material getGunpowder() {
        if (MultiVersionLookup.gunpowderr == null) {
            try {
                MultiVersionLookup.gunpowderr = Material.matchMaterial("SULPHUR");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.gunpowderr == null)
                try {
                    MultiVersionLookup.gunpowderr = Material.GUNPOWDER;
                } catch (Error | Exception e45) {
                }
        }
        return MultiVersionLookup.gunpowderr;
    }

    public static Material getSkull() {
        if (MultiVersionLookup.skull == null) {
            try {
                MultiVersionLookup.skull = Material.matchMaterial("SKULL_ITEM");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.skull == null)
                MultiVersionLookup.skull = Material.PLAYER_HEAD;
        }
        return MultiVersionLookup.skull;
    }

    public static Material getMycil() {
        if (MultiVersionLookup.mycil == null) {
            try {
                MultiVersionLookup.mycil = Material.matchMaterial("MYCEL");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.mycil == null)
                MultiVersionLookup.mycil = Material.MYCELIUM;
        }
        return MultiVersionLookup.mycil;
    }

    public static EntityType getZombiePig() {
        if (MultiVersionLookup.zombiePig == null) {
            try {
                MultiVersionLookup.zombiePig = EntityType.valueOf("PIG_ZOMBIE");
            } catch (Error | Exception e) {
            }
            if (MultiVersionLookup.zombiePig == null)
                MultiVersionLookup.zombiePig = EntityType.ZOMBIFIED_PIGLIN;
        }
        return MultiVersionLookup.zombiePig;
    }
}
