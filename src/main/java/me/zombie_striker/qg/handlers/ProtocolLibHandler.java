package me.zombie_striker.qg.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.reflection.XReflection;
import com.mojang.datafixers.util.Pair;

import de.tr7zw.changeme.nbtapi.NBT;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;

public class ProtocolLibHandler {

    private static ProtocolManager protocolManager;

    private static Object enumArgumentAnchor_EYES = null;
    private static Class<?> class_ArgumentAnchor = null;
    // org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
    private static Class<?> nbtFactClass = null;
    private static Method nbtFactmethod = null;

    public static void initRemoveArmswing() {
        if (ProtocolLibHandler.protocolManager == null)
            ProtocolLibHandler.protocolManager = ProtocolLibrary.getProtocolManager();
        ProtocolLibHandler.protocolManager
                .addPacketListener(new PacketAdapter(QAMain.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.ARM_ANIMATION) {

                    @Override
                    public void onPacketReceiving(final PacketEvent event) {
                        final Player player = event.getPlayer();

                        boolean tempplayer = false;
                        try {
                            player.getVehicle();
                        } catch (final UnsupportedOperationException e) {
                            tempplayer = true;
                        }
                        if (tempplayer)
                            return;

                        if (event.getPacketType() == PacketType.Play.Client.ARM_ANIMATION && player.getVehicle() != null) {
                            try {

                                final byte state = event.getPacket().getBytes().readSafely(0);
                                final int entityID = event.getPacket().getIntegers().readSafely(0);
                                Player targ = null;
                                for (final Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getEntityId() == entityID) {
                                        targ = p;
                                        break;
                                    }
                                }
                                if (targ == null) {
                                    Bukkit.broadcastMessage("The ID for the entity is incorrect");
                                    return;
                                }
                                if (state == 0) {
                                    if (QualityArmory.isGun(targ.getInventory().getItemInMainHand())
                                            || QualityArmory.isIronSights(targ.getInventory().getItemInMainHand())) {
                                        event.setCancelled(true);
                                    }
                                }
                            } catch (Error | Exception e) {
                            }
                        }
                    }
                });

    }

    public static void initAimBow() {
        if (ProtocolLibHandler.protocolManager == null)
            ProtocolLibHandler.protocolManager = ProtocolLibrary.getProtocolManager();
        ProtocolLibHandler.protocolManager.addPacketListener(
                new PacketAdapter(QAMain.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    @Override
                    public void onPacketSending(final PacketEvent event) {
                        final Player sender = event.getPlayer();
                        final int id = (int) event.getPacket().getModifier().read(0);
                        Object slot;
                        final Object ironsights;
                        // https://wiki.vg/Protocol#Set_Equipment
                        if (XMaterial.supports(16)) {
                            slot = event.getPacket().getModifier().read(1);
                            ironsights = slot;// event.getPacket().getModifier().read(2);
                        } else {
                            slot = event.getPacket().getModifier().read(1);
                            ironsights = event.getPacket().getModifier().read(2);
                        }
                        if ((id) == sender.getEntityId()) {
                            return;
                        }
                        Player who = null;
                        for (final Player player : sender.getWorld().getPlayers()) {
                            if (player.getEntityId() == (int) id) {
                                who = player;
                                break;
                            }
                        }
                        if (who == null)
                            return;
                        // if equipment is in OFFHAND
                        if (!slot.toString().contains("MAINHAND")) {
                            if (QualityArmory.isIronSights(who.getInventory().getItemInMainHand())) {
                                event.setCancelled(true);
                            }
                            return;
                        }
                        if (who.getInventory().getItemInMainHand() != null
                                && who.getInventory().getItemInMainHand().getType().name().equals("CROSSBOW")
                                && QualityArmory.isIronSights(who.getInventory().getItemInMainHand())
                                && ironsights.toString().contains("crossbow")) {
                            Object is = null;

                            final Gun gun = QualityArmory.getGun(who.getInventory().getItemInOffHand());
                            if (gun == null || !gun.hasBetterAimingAnimations())
                                return;

                            try {
                                is = ProtocolLibHandler.getCraftItemStack(who.getInventory().getItemInOffHand());
                            } catch (final NoSuchMethodException e) {
                            }

                            NBT.modify(who.getInventory().getItemInOffHand(), nbt -> {
                                nbt.setBoolean("Charged", true);
                            });

                            if (XMaterial.supports(16)) {
                                final List list = (List) slot;
                                for (final Object o : new ArrayList(list)) {
                                    if (o.toString().contains("MAINHAND")) {
                                        final Pair pair = (Pair) o;
                                        final Pair newpair = new Pair(pair.getFirst(), is);
                                        list.set(list.indexOf(pair), newpair);
                                    } else if (o.toString().contains("OFFHAND")) {
                                        list.remove(list.indexOf(o));
                                    }
                                }
                                event.getPacket().getModifier().write(1, list);
                            } else {
                                event.getPacket().getModifier().write(2, is);
                            }

                            if (!XMaterial.supports(16))
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final PacketContainer pc2 = ProtocolLibHandler.protocolManager
                                                    .createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

                                            // EnumItemSlot e = EnumItemSlot.OFFHAND;

                                            Object neededSlot = null;
                                            final Object[] enums = slot.getClass().getEnumConstants();
                                            for (final Object k : enums) {
                                                @SuppressWarnings("all")
                                                final String name = (String) k.getClass().getMethod("name").invoke(k, new Class[0]);
                                                if (name.contains("OFFHAND")) {
                                                    neededSlot = k;
                                                    break;
                                                }
                                            }
                                            if (XMaterial.supports(16)) {
                                                pc2.getModifier().write(0, id).write(1, ironsights);

                                            } else {
                                                pc2.getModifier().write(0, id).write(1, neededSlot).write(2, ironsights);
                                            }

                                            ProtocolLibHandler.protocolManager.sendServerPacket(event.getPlayer(), pc2);
                                        } catch (final Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }.runTaskLater(QAMain.getInstance(), 1);
                        }
                    }
                });

    }

    private static Object getCraftItemStack(final ItemStack is) throws NoSuchMethodException {
        if (ProtocolLibHandler.nbtFactClass == null) {
            ProtocolLibHandler.nbtFactClass = XReflection.getCraftClass("inventory.CraftItemStack");
            final Class[] c = new Class[1];
            c[0] = ItemStack.class;
            ProtocolLibHandler.nbtFactmethod = ProtocolLibHandler.nbtFactClass.getMethod("asNMSCopy", c);
        }
        try {
            return ProtocolLibHandler.nbtFactmethod.invoke(ProtocolLibHandler.nbtFactClass, is);
        } catch (InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    public static void sendYawChange(final Player player, final Vector newDirection) {
        if (ProtocolLibHandler.protocolManager == null)
            ProtocolLibHandler.protocolManager = ProtocolLibrary.getProtocolManager();
        final PacketContainer yawpack = ProtocolLibHandler.protocolManager.createPacket(PacketType.Play.Server.LOOK_AT, false);
        if (ProtocolLibHandler.enumArgumentAnchor_EYES == null) {
            ProtocolLibHandler.class_ArgumentAnchor = XReflection.getNMSClass("commands.arguments", "ArgumentAnchor$Anchor");
            ProtocolLibHandler.enumArgumentAnchor_EYES = ReflectionsUtil.getEnumConstant(ProtocolLibHandler.class_ArgumentAnchor, "EYES");
        }
        yawpack.getModifier().write(4, ProtocolLibHandler.enumArgumentAnchor_EYES);
        yawpack.getDoubles().write(0, player.getEyeLocation().getX() + newDirection.getX());
        yawpack.getDoubles().write(1, player.getEyeLocation().getY() + newDirection.getY());
        yawpack.getDoubles().write(2, player.getEyeLocation().getZ() + newDirection.getZ());
        yawpack.getBooleans().write(0, false);
        ProtocolLibHandler.protocolManager.sendServerPacket(player, yawpack);
    }
}
