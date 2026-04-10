package me.zombie_striker.qg.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ProtocolLibHandler {

    private static ProtocolManager protocolManager;

    public static void initRemoveArmswing() {
        if (protocolManager == null)
            protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
                new PacketAdapter(QAMain.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.ARM_ANIMATION) {

                    @SuppressWarnings("deprecation")
                    public void onPacketReceiving(PacketEvent event) {
                        final Player player = event.getPlayer();

                        boolean tempplayer = false;
                        try {
                            player.getVehicle();
                        } catch (UnsupportedOperationException e) {
                            tempplayer = true;
                        }
                        if (tempplayer)
                            return;

                        if (event.getPacketType() == PacketType.Play.Client.ARM_ANIMATION
                                && player.getVehicle() != null) {
                            try {

                                byte state = event.getPacket().getBytes().readSafely(0);
                                int entityID = event.getPacket().getIntegers().readSafely(0);
                                Player targ = null;
                                for (Player p : Bukkit.getOnlinePlayers()) {
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
                                    if (QualityArmory.isGun(targ.getItemInHand())
                                            || QualityArmory.isIronSights(targ.getItemInHand())) {
                                        event.setCancelled(true);
                                    }
                                }
                            } catch (Error | Exception ignored) {
                            }
                        }
                    }
                });
    }

    @SuppressWarnings("deprecation")
    public static void initAimBow() {
        if (protocolManager == null)
            protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(
                new PacketAdapter(QAMain.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        QAMain.DEBUG("Processing ENTITY_EQUIPMENT packet");
                        final Player sender = event.getPlayer();
                        final int entityId = event.getPacket().getIntegers().read(0);

                        // Ignore own entity packet
                        if (entityId == sender.getEntityId()) {
                            QAMain.DEBUG("Ignoring own entity packet");
                            return;
                        }

                        // Find target player from entity ID
                        Player targetPlayer = null;

                        for (Player player : sender.getWorld().getPlayers()) {
                            if (player.getEntityId() == entityId) {
                                targetPlayer = player;
                                break;
                            }
                        }

                        if (targetPlayer == null) {
                            QAMain.DEBUG("Target player not found for entity ID: " + entityId);
                            return;
                        }

                        // Check if player has crossbow with iron sights
                        ItemStack mainHandItem = targetPlayer.getItemInHand();
                        if (mainHandItem == null || !mainHandItem.getType().name().equals("CROSSBOW") ||
                                !QualityArmory.isIronSights(mainHandItem)) {
                            QAMain.DEBUG("Player doesn't have crossbow with iron sights");
                            return;
                        }

                        // Check for gun in offhand
                        Gun gun = QualityArmory.getGun(targetPlayer.getInventory().getItemInOffHand());
                        if (gun == null || !gun.hasBetterAimingAnimations()) {
                            QAMain.DEBUG("No valid gun found in offhand with better aiming animations");
                            return;
                        }

                        // Handle different Minecraft versions
                        if (XReflection.supports(16)) {
                            handleModern1_16PacketFormat(event, targetPlayer);
                        } else {
                            handleLegacyPacketFormat(event, targetPlayer, entityId);
                        }
                   }

                    /**
                     * Handle Minecraft 1.16+ packet format
                     */
                    private void handleModern1_16PacketFormat(PacketEvent event, Player targetPlayer) {
                        QAMain.DEBUG("Using Minecraft 1.16+ packet handling");
                        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList =
                                event.getPacket().getSlotStackPairLists().read(0);

                        boolean hasMainHandEntry = false;
                        for (Pair<EnumWrappers.ItemSlot, ItemStack> entry : equipmentList) {
                            if (entry.getFirst().equals(EnumWrappers.ItemSlot.MAINHAND)) {
                                hasMainHandEntry = true;
                                break;
                            }
                        }

                        if (!hasMainHandEntry) {
                            QAMain.DEBUG("No MAINHAND entry found in equipment list");
                            return;
                        }

                        // Modify the equipment list
                        List<Pair<EnumWrappers.ItemSlot, ItemStack>> modifiableList = new ArrayList<>(equipmentList);
                        boolean modified = false;

                        for (int i = 0; i < modifiableList.size(); i++) {
                            Pair<EnumWrappers.ItemSlot, ItemStack> pair = modifiableList.get(i);
                            if (pair.getFirst().equals(EnumWrappers.ItemSlot.MAINHAND)) {
                                // Replace mainhand item with offhand gun
                                ItemStack offhandItem = prepareOffhandItemForPacket(targetPlayer);
                                Pair<EnumWrappers.ItemSlot, ItemStack> newPair = new Pair<>(pair.getFirst(), offhandItem);
                                modifiableList.set(i, newPair);
                                QAMain.DEBUG("Replaced MAINHAND entry with offhand item");
                                modified = true;
                            } else if (pair.getFirst().equals(EnumWrappers.ItemSlot.OFFHAND)) {
                                // Remove offhand entry
                                modifiableList.remove(i);
                                i--; // Adjust index after removal
                                QAMain.DEBUG("Removed OFFHAND entry");
                                modified = true;
                            }
                        }

                        if (modified) {
                            QAMain.DEBUG("Writing modified equipment list to packet");
                            event.getPacket().getSlotStackPairLists().write(0, modifiableList);
                        }
                    }

                    /**
                     * Handle pre-1.16 packet format
                     */
                    private void handleLegacyPacketFormat(PacketEvent event, Player targetPlayer, int entityId) {
                        QAMain.DEBUG("Using pre-1.16 packet handling");
                        EnumWrappers.ItemSlot slot = event.getPacket().getItemSlots().read(1);
                        ItemStack itemStack = event.getPacket().getItemModifier().read(2);

                        // Check if not main hand and iron sights are active
                        if (!slot.toString().contains("MAINHAND")) {
                            QAMain.DEBUG("Not main hand slot: " + slot);
                            if (QualityArmory.isIronSights(targetPlayer.getInventory().getItemInMainHand())) {
                                QAMain.DEBUG("Player using iron sights, cancelling packet");
                                event.setCancelled(true);
                            }
                            return;
                        }

                        // Only proceed if the packet contains a crossbow
                        if (!itemStack.toString().contains("crossbow")) {
                            QAMain.DEBUG("Packet doesn't contain crossbow");
                            return;
                        }

                        event.getPacket().getItemModifier().write(2, prepareOffhandItemForPacket(targetPlayer));

                        // Schedule task to send offhand packet
                        final ItemStack finalItemStack = itemStack;

                        new BukkitRunnable() {
                            public void run() {
                                try {
                                    PacketContainer pc2 = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

                                    pc2.getIntegers().write(0, entityId);
                                    pc2.getItemSlots().write(1, slot);
                                    pc2.getItemModifier().write(2, finalItemStack);

                                    protocolManager.sendServerPacket(event.getPlayer(), pc2);
                                } catch (Exception e) {
                                    QAMain.DEBUG("Error sending offhand packet: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }.runTaskLater(QAMain.getInstance(), 1);
                    }

                    /**
                     * Prepare the offhand item for the packet (add charged projectile if crossbow)
                     */
                    private ItemStack prepareOffhandItemForPacket(Player player) {
                        ItemStack clone = player.getInventory().getItemInOffHand().clone();

                        ItemMeta meta = clone.getItemMeta();
                        if (meta instanceof CrossbowMeta) {
                            QAMain.DEBUG("Item is a crossbow, adding charged projectile");
                            CrossbowMeta crossbowMeta = (CrossbowMeta) meta;
                            crossbowMeta.addChargedProjectile(new ItemStack(Material.ARROW));
                            clone.setItemMeta(crossbowMeta);
                        }

                        QAMain.DEBUG("Prepared offhand item for packet: " + clone);
                        return clone;
                    }
                });
    }


    public static void sendYawChange(Player player, Vector newDirection) {
        try {
            if (protocolManager == null) protocolManager = ProtocolLibrary.getProtocolManager();

            final PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.LOOK_AT, false);
            if (!setLookAtAnchor(packet)) {
                QAMain.DEBUG("Could not set anchor for LOOK_AT packet");
                return;
            }

            packet.getDoubles().write(0, player.getEyeLocation().getX() + newDirection.getX());
            packet.getDoubles().write(1, player.getEyeLocation().getY() + newDirection.getY());
            packet.getDoubles().write(2, player.getEyeLocation().getZ() + newDirection.getZ());
            packet.getBooleans().write(0, false);
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            QAMain.DEBUG("An error occurred while sending a yaw change packet to " + player.getName() + ": " + e.getMessage());
        }
    }

    private static boolean setLookAtAnchor(PacketContainer packet) {
        try {
            StructureModifier<Object> modifier = packet.getModifier();
            for (int i = 0; i < modifier.size(); i++) {
                Class<?> fieldType = modifier.getField(i).getType();
                if (fieldType.isEnum() && fieldType.getSimpleName().toLowerCase().contains("anchor")) {
                    Object[] enumConstants = fieldType.getEnumConstants();
                    if (enumConstants != null && enumConstants.length > 1) {
                        modifier.write(i, enumConstants[1]);
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        try {
            packet.getIntegers().write(0, 1);
            return true;
        } catch (Exception ignored) {
            QAMain.DEBUG("Could not set anchor for LOOK_AT packet");
            return false;
        }
    }
}
