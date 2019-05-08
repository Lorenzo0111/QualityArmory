package me.zombie_striker.qg.handlers;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.protocol.*;
import com.comphenix.protocol.events.*;

import me.zombie_striker.pluginconstructor.reflection.ReflectionUtil;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;

public class ProtocolLibHandler {

	private static ProtocolManager protocolManager;

	private static Object enumArgumentAnchor_EYES = null;
	private static Class<?> class_ArgumentAnchor = null;

	public static void initRemoveArmswing() {
		if (protocolManager == null)
			protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(
				new PacketAdapter(QAMain.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.ARM_ANIMATION) {

					@SuppressWarnings("deprecation")
					public void onPacketReceiving(PacketEvent event) {
						final Player player = event.getPlayer();
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
							}catch (Error|Exception e) {
							}
						}
					}
				});

	}

	public static void sendYawChange(Player player, Vector newDirection) {
		if (protocolManager == null)
			protocolManager = ProtocolLibrary.getProtocolManager();
		final PacketContainer yawpack = protocolManager.createPacket(PacketType.Play.Server.LOOK_AT, false);
		if (enumArgumentAnchor_EYES == null) {
			class_ArgumentAnchor = ReflectionUtil.getNMSClass("ArgumentAnchor$Anchor");
			enumArgumentAnchor_EYES = ReflectionUtil.getEnumConstant(class_ArgumentAnchor, "EYES");
		}
		yawpack.getModifier().write(4, enumArgumentAnchor_EYES);
		yawpack.getDoubles().write(0, player.getEyeLocation().getX() + newDirection.getX());
		yawpack.getDoubles().write(1, player.getEyeLocation().getY() + newDirection.getY());
		yawpack.getDoubles().write(2, player.getEyeLocation().getZ() + newDirection.getZ());
		yawpack.getBooleans().write(0, false);
		try {
			protocolManager.sendServerPacket(player, yawpack);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
