package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.utils.LocalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public class HotbarMessager {

	// These are the Class instances. Needed to get fields or methods for classes.
	private static Class<?> CRAFTPLAYERCLASS, PACKET_PLAYER_CHAT_CLASS, ICHATCOMP, CHATMESSAGE, PACKET_CLASS,
			CHAT_MESSAGE_TYPE_CLASS;

	private static Field PLAYERCONNECTION;
	private static Method GETHANDLE, SENDPACKET;

	// These are the constructors for those classes. Need to create new objects.
	private static Constructor<?> PACKET_PLAYER_CHAT_CONSTRUCTOR, CHATMESSAGE_CONSTRUCTOR;

	// Used in 1.12+. Bytes are replaced with this enum
	private static Object CHAT_MESSAGE_TYPE_ENUM_OBJECT;

	private static Class CHAT_MESSAGE_TYPE;

	// This is the server version. This is how we know the server version.
	private static String SERVER_VERSION;
	private static int PacketConstructorType = 0;
	private static QAMain plugin = QAMain.getInstance();
	static {
		// This gets the server version.
		String name = Bukkit.getServer().getClass().getName();
		name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
		name = name.substring(0, name.indexOf("."));
		SERVER_VERSION = name;
		try {
			// This here sets the class fields.
			CRAFTPLAYERCLASS = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
			//PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
			PACKET_PLAYER_CHAT_CLASS = ReflectionsUtil.getMinecraftClass("PacketPlayOutChat","network.protocol.game.PacketPlayOutChat");
			//PACKET_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
			PACKET_CLASS = ReflectionsUtil.getMinecraftClass("Packet","network.protocol.Packet");
			//ICHATCOMP = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
			ICHATCOMP = ReflectionsUtil.getMinecraftClass("IChatBaseComponent","network.chat.IChatBaseComponent");
			GETHANDLE = CRAFTPLAYERCLASS.getMethod("getHandle");
			Class<?> PLAYERCONNECTIONCLAZZ = ReflectionsUtil.getMinecraftClass("PlayerConnection","server.network.PlayerConnection");
			try {
				PLAYERCONNECTION = GETHANDLE.getReturnType().getField("playerConnection");
			}catch (Throwable e) {
				for (Field field : GETHANDLE.getReturnType().getFields()) {
					LocalUtils.logp("Checking field '"+field.getName()+"' return type: '"+field.getType().getName()+"'");
					if(field.getType().isAssignableFrom(PLAYERCONNECTIONCLAZZ)) {
						LocalUtils.logp("PlayerConnection field found is: '"+field.getName()+"'");
						PLAYERCONNECTION = field;
						break;
					}
				}
				if(PLAYERCONNECTION == null) {
					LocalUtils.logp("PlayerConnection field couldn't be found!");
				}
			}
			SENDPACKET = PLAYERCONNECTION.getType().getMethod("sendPacket", PACKET_CLASS);
			try {
				//CHAT_MESSAGE_TYPE_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessageType");
				CHAT_MESSAGE_TYPE_CLASS = ReflectionsUtil.getMinecraftClass("ChatMessageType","network.chat.ChatMessageType");
				CHAT_MESSAGE_TYPE_ENUM_OBJECT = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];
				try {
					PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
							CHAT_MESSAGE_TYPE_CLASS, UUID.class);
					PacketConstructorType = 2;
				}catch (NoSuchMethodException noerror){
					PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
							CHAT_MESSAGE_TYPE_CLASS);
					PacketConstructorType = 1;
				}
			} catch (NullPointerException | NoSuchMethodException e) {
					PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
					PacketConstructorType = 0;
			}
			//CHATMESSAGE = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");
			CHATMESSAGE = ReflectionsUtil.getMinecraftClass("ChatMessage","network.chat.ChatMessage");
			CHATMESSAGE_CONSTRUCTOR = CHATMESSAGE.getConstructor(String.class, Object[].class);
			LocalUtils.logp("&cSupport for 1.17+ added by &6AlonsoAliaga&c.");
			LocalUtils.logp("&cVisit https://alonsoaliaga.com/QualityArmory");
			LocalUtils.logp("&cMore plugins on https://alonsoaliaga.com/plugins");
		} catch (Throwable e) {
			LocalUtils.loge("Error loading HotBarMessager handler.. Report it on github: "+e.getMessage());
			if(QAMain.DEBUG)
				e.printStackTrace();
		}
	}

	/**
	 * Sends the hotbar message 'message' to the player 'player'
	 *
	 * @param player
	 * @param message
	 * @throws Exception
	 */
	public static void sendHotBarMessage(Player player, String message) throws Exception {
		try {
			// This creates the IChatComponentBase instance
			Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(message, new Object[0]);
			// This creates the packet
			Object packet=null;
			if (PacketConstructorType==0)
				packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, (byte) 2);
			else if (PacketConstructorType==1)
				packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT);
			else if (PacketConstructorType==2)
				packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT, UUID.randomUUID());
			// This casts the player to a craftplayer
			Object craftplayerInst = CRAFTPLAYERCLASS.cast(player);
			// This invokes the method above.
			Object methodhHandle = GETHANDLE.invoke(craftplayerInst);
			// This gets the player's connection
			Object playerConnection = PLAYERCONNECTION.get(methodhHandle);
			// This sends the packet.
			SENDPACKET.invoke(playerConnection, packet);

		} catch (Exception e) {
			failsafe("sendHotBarMessage = "+e.getMessage());
			throw e;
		}
	}

	private static void failsafe(String message) {
		Bukkit.getLogger().log(Level.WARNING,
				"[PluginConstructorAPI] HotBarMessager disabled! Something went wrong with: " + message);
		Bukkit.getLogger().log(Level.WARNING, "[PluginConstructorAPI] Report this to Zombie_Striker");
		Bukkit.getLogger().log(Level.WARNING, "[PluginConstructorAPI] Needed Information: " + Bukkit.getName() + ", "
				+ Bukkit.getVersion() + ", " + Bukkit.getBukkitVersion());
		Bukkit.getLogger().log(Level.WARNING,
				"[PluginConstructorAPI] https://github.com/ZombieStriker/PluginConstructorAPI");
	}
}