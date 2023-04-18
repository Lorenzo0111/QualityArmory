package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.data.AnimationGunAction;
import me.zombie_striker.qualityarmory.data.AnimationKeyFrame;
import me.zombie_striker.qualityarmory.guns.Ammo;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GunDataHandler implements IHandler, Listener {

    private final HashMap<UUID, Integer> gunBulletCount = new HashMap<>();
    private final HashMap<UUID, Ammo> ammoUsedPerGun = new HashMap<>();
    private final HashMap<UUID, Gun> gunType = new HashMap<>();



    private final HashMap<UUID, Integer> ticksPlayingAnimation = new HashMap<>();
    private final HashMap<UUID, String> animationPlaying = new HashMap<>();
    private final HashMap<UUID, Integer> animationSlot = new HashMap<>();
    private final HashMap<UUID, UUID> playerHoldingGun = new HashMap<>();


    private final HashMap<UUID, Integer> reloadAmount = new HashMap<>();


    public final static String GUN_ID_LORE_STRING = ChatColor.GRAY + "Gun Serial Number: ";

    private QAMain main;

    public int getGunBulletAmount(UUID gun) {
        return gunBulletCount.containsKey(gun) ? gunBulletCount.get(gun) : 0;
    }

    public void setBulletAmount(UUID gun, int bulletCount) {
        this.gunBulletCount.put(gun, bulletCount);
    }

    public Ammo getAmmoUsed(ItemStack is) {
        UUID gunserial = getGunID(is);
        if (gunserial == null)
            return null;
        return ammoUsedPerGun.get(gunserial);
    }

    public int getGunBulletAmount(ItemStack is) {
        UUID gunserial = getGunID(is);
        if (gunserial == null)
            return 0;
        return gunBulletCount.containsKey(gunserial) ? gunBulletCount.get(gunserial) : 0;
    }

    public UUID getGunID(ItemStack is) {
        if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
            for (String lore : is.getItemMeta().getLore()) {
                if (lore.startsWith(GUN_ID_LORE_STRING)) {
                    return UUID.fromString(lore.substring(GUN_ID_LORE_STRING.length()));
                }
            }
        }
        return null;
    }
    public void registerGun(Gun gun, ItemStack is){
        UUID gunuuid = getGunID(is);
        if(gunuuid==null){
            //TODO: Generate UUID
        }
        this.gunBulletCount.put(gunuuid,0);
        this.gunType.put(gunuuid,gun);
    }

    public boolean isPlayingAnimation(UUID gun) {
        return ticksPlayingAnimation.containsKey(gun);
    }

    public String getAnimationName(UUID gun) {
        return animationPlaying.get(gun);
    }

    public Integer getAnimationSlot(UUID gun) {
        return animationSlot.get(gun);
    }

    public void playAnimation(Player player, UUID gun, String animation, int gunslot) {
        playerHoldingGun.put(gun,player.getUniqueId());
        animationPlaying.put(gun, animation);
        animationSlot.put(gun, gunslot);
        ticksPlayingAnimation.put(gun, 0);
    }

    @Override
    public void init(QAMain main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this,main);
        new BukkitRunnable() {
            public void run() {
                for (Map.Entry<UUID, Integer> entry : ticksPlayingAnimation.entrySet()) {
                    int tick = 0;
                    String anim = animationPlaying.get(entry.getKey());
                    if (entry.getValue() == main.getKeyFrameGunHandler().getMaxKeyFrame(anim)) {
                        tick = ticksPlayingAnimation.remove(entry.getKey());
                    } else {
                        tick = ticksPlayingAnimation.put(entry.getKey(), entry.getValue() + 1);
                    }
                    AnimationKeyFrame keyframe = main.getKeyFrameGunHandler().getAnimationKeyFrame(anim, tick);
                    if (keyframe != null) {
                        Player who = Bukkit.getPlayer(playerHoldingGun.get(entry.getKey()));
                        who.getWorld().playSound(who.getLocation(), keyframe.getSound(), 1, 1);
                        if(keyframe.getAction()==AnimationGunAction.CANCEL_IF_AMMO_FULL){
                          Gun guntype = gunType.get(entry.getKey());
                          int maxAmmo = (int) guntype.getData(ConfigKey.CUSTOMITEM_MAXBULLETS.getKey());
                          if(gunBulletCount.get(entry.getKey())>=maxAmmo) {
                              animationSlot.remove(entry.getKey());
                              animationPlaying.remove(entry.getKey());
                              ticksPlayingAnimation.remove(entry.getKey());
                          }
                        } else if(keyframe.getAction()== AnimationGunAction.INCREMENT_BULLET){
                            gunBulletCount.put(entry.getKey(),gunBulletCount.get(entry.getKey())+1);
                        } else if(keyframe.getAction()== AnimationGunAction.DROP_ALL_AMMO){
                            gunBulletCount.put(entry.getKey(),0);
                        } else if (keyframe.getAction()==AnimationGunAction.RELOAD_ALL){
                            gunBulletCount.put(entry.getKey(),gunBulletCount.get(entry.getKey())+reloadAmount.get(entry.getKey()));
                        }
                    }
                }
            }
        }.runTaskTimer(main, 1, 1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem()!=null){
            if(QualityArmory.getInstance().isGun(event.getCurrentItem())){
                UUID gun = getGunID(event.getCurrentItem());
                if(gun!=null){
                    if(isPlayingAnimation(gun))
                        event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(event.getItemDrop().getItemStack()!=null){
            if(QualityArmory.getInstance().isGun(event.getItemDrop().getItemStack())){
                UUID gun = getGunID(event.getItemDrop().getItemStack());
                if(gun!=null){
                    if(isPlayingAnimation(gun))
                        event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event){
        if(event.getMainHandItem()!=null){
            if(QualityArmory.getInstance().isGun(event.getMainHandItem())){
                UUID gun = getGunID(event.getMainHandItem());
                if(gun!=null){
                    if(isPlayingAnimation(gun))
                        event.setCancelled(true);
                }
            }
        }
    }
}
