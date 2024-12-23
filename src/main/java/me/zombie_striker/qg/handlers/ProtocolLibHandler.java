package me.zombie_striker.qg.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.reflection.XReflection;
import com.mojang.datafixers.util.Pair;
import de.tr7zw.changeme.nbtapi.NBT;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.*;
import com.comphenix.protocol.events.*;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;

public class ProtocolLibHandler {

	private static ProtocolManager protocolManager;
	private static Class<?> nbtFactClass = null;
	private static Method nbtFactmethod = null;

	public static void initRemoveArmswing() {
		if (protocolManager == null)
			protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(
				new PacketAdapter(QAMain.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.ARM_ANIMATION) {

					@SuppressWarnings("deprecation")
					public void onPacketReceiving(PacketEvent event) {
						final Player player = event.getPlayer();

						boolean tempplayer =  false;
						try{
							player.getVehicle();
						}catch (UnsupportedOperationException e){
							tempplayer=true;
						}
						if(tempplayer)
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
							} catch (Error | Exception e) {
							}
						}
					}
				});

	}

	public static void initAimBow() {
		if (protocolManager == null)
			protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(
				new PacketAdapter(QAMain.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						final Player sender = event.getPlayer();
						int id = (int) event.getPacket().getModifier().read(0);
						Object slot;
						final Object ironsights;
						if(XMaterial.supports(16)){
							slot = event.getPacket().getModifier().read(1);
							ironsights = slot;//event.getPacket().getModifier().read(2);
						}else{
							slot = event.getPacket().getModifier().read(1);
							ironsights = event.getPacket().getModifier().read(2);
						}
						if ((id) == sender.getEntityId()) {
							return;
						}
						Player who = null;
						for (Player player : sender.getWorld().getPlayers()) {
							if (player.getEntityId() == (int) id) {
								who = player;
								break;
							}
						}
						if (who == null)
							return;
						if (!slot.toString().contains("MAINHAND")) {
							if (QualityArmory.isIronSights(who.getInventory().getItemInMainHand())) {
								event.setCancelled(true);
							}
							return;
						}
						if (who.getItemInHand() != null && who.getItemInHand().getType().name().equals("CROSSBOW") &&
								QualityArmory.isIronSights(who.getItemInHand()) &&
								ironsights.toString().contains("crossbow")) {
							Object is = null;

							Gun gun = QualityArmory.getGun(who.getInventory().getItemInOffHand());
							if (gun == null || !gun.hasBetterAimingAnimations())
								return;

							try {
								is = getCraftItemStack(who.getInventory().getItemInOffHand());
							} catch (NoSuchMethodException e) {}

							NBT.modify(who.getInventory().getItemInOffHand(), nbt -> {
								nbt.setBoolean("Charged",true);
							});

							if(XMaterial.supports(16)){
								List list = (List) slot;
								for(Object o : new ArrayList(list)){
									if(o.toString().contains("MAINHAND")) {
										Pair pair = (Pair) o;
										Pair newpair = new Pair(pair.getFirst(), is);
										list.set(list.indexOf(pair), newpair);
									}else if(o.toString().contains("OFFHAND")) {
										list.remove(list.indexOf(o));
									}
								}
								event.getPacket().getModifier().write(1, list);
							}else{
								event.getPacket().getModifier().write(2, is);
							}

							if(!XMaterial.supports(16))
							new BukkitRunnable() {
								public void run() {
									try {
										PacketContainer pc2 = protocolManager
												.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

										//EnumItemSlot e = EnumItemSlot.OFFHAND;

										Object neededSlot = null;
										Object[] enums = slot.getClass().getEnumConstants();
										for (Object k : enums) {
											String name = (String) k.getClass().getMethod("name").invoke(k, new Class[0]);
											if (name.contains("OFFHAND")) {
												neededSlot = k;
												break;
											}
										}
										if(XMaterial.supports(16)){
											pc2.getModifier().write(0, id)
													.write(1, ironsights);

										}else {
											pc2.getModifier().write(0, id)
													.write(1, neededSlot)
													.write(2, ironsights);
										}

										protocolManager.sendServerPacket(event.getPlayer(), pc2);
									} catch (Exception e) {
										e.printStackTrace();
									}


								}
							}.runTaskLater(QAMain.getInstance(), 1);
						}
					}
				});

	}

	private static Object getCraftItemStack(ItemStack is) throws NoSuchMethodException {
		if (nbtFactClass == null) {
			nbtFactClass = XReflection.getCraftClass("inventory.CraftItemStack");
			Class[] c = new Class[1];
			c[0] = ItemStack.class;
			nbtFactmethod = nbtFactClass.getMethod("asNMSCopy", c);
		}
		try {
			return nbtFactmethod.invoke(nbtFactClass, is);
		} catch (InvocationTargetException | IllegalAccessException e) {
			return null;
		}
	}


	public static void sendYawChange(Player player, Vector newDirection) {
		try {
			if (protocolManager == null)
				protocolManager = ProtocolLibrary.getProtocolManager();
			final PacketContainer yawpack = protocolManager.createPacket(PacketType.Play.Server.LOOK_AT, false);
			yawpack.getIntegers().write(0, 1);
			yawpack.getDoubles().write(0, player.getEyeLocation().getX() + newDirection.getX());
			yawpack.getDoubles().write(1, player.getEyeLocation().getY() + newDirection.getY());
			yawpack.getDoubles().write(2, player.getEyeLocation().getZ() + newDirection.getZ());
			yawpack.getBooleans().write(0, false);
			protocolManager.sendServerPacket(player, yawpack);
		} catch (Exception e) {
			QAMain.DEBUG("An error occurred while sending a yaw change packet to " + player.getName());
			QAMain.DEBUG(e.getMessage());
		}
	}
}
