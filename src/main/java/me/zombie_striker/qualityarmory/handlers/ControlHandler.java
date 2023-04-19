package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Bullet;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControlHandler implements IHandler, Listener {

    public QAMain qaMain;
    private HashMap<UUID, Long> lastHeldFireButton = new HashMap<>();
    private HashMap<UUID, Long> lastFired = new HashMap<>();
    private HashMap<UUID, Integer> bulletDelayBetweenShot = new HashMap<>();
    private HashMap<UUID, Integer> delaySinceShot = new HashMap<>();

    public void triggerShoot(Player player) {
        lastHeldFireButton.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void triggerRapidFire(Player player, int delayBetweeenShots) {
        triggerShoot(player);
        bulletDelayBetweenShot.put(player.getUniqueId(), delayBetweeenShots);
    }

    public boolean prepareShoot(Player player, ItemStack itemStack, Gun gun) {
        if (player.getGameMode() == GameMode.CREATIVE) return true;
        //TODO: Remove because this is just for testing.

        UUID gunserial = qaMain.getGunDataHandler().getGunID(itemStack);
        int bulletCount = qaMain.getGunDataHandler().getGunBulletAmount(gunserial);
        if (bulletCount == 0) return false;
        qaMain.getGunDataHandler().setBulletAmount(gunserial, bulletCount - 1);
        return true;
    }

    public void prepareShootBullet(Player player, ItemStack itemStack, Gun gun) {
        Vector dir = player.getLocation().getDirection();
        dir = dir.normalize();
        dir = dir.add(qaMain.getBulletSwayHandler().getSway(player).multiply(0.1)).normalize();
        Bullet bullet = new Bullet(player.getEyeLocation(), dir, player.getUniqueId(), (float) gun.getDataDouble(ConfigKey.CUSTOMITEM_DAMAGE.getKey()), gun.getDataDouble(ConfigKey.CUSTOMITEM_SPEED.getKey()));
        qaMain.getBulletHandler().registerBullet(bullet);
        player.getWorld().playSound(player.getLocation(), (String) gun.getData(ConfigKey.CUSTOMITEM_FIRING_SOUND.getKey()), 1, 1);
    }

    @Override
    public void init(QAMain main) {
        this.qaMain = main;
        Bukkit.getPluginManager().registerEvents(this, main);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Long> entry : lastHeldFireButton.entrySet()) {
                    UUID uuid = entry.getKey();
                    long time = entry.getValue();
                    if (System.currentTimeMillis() - time <= 50) {

                        Player player = Bukkit.getPlayer(uuid);

                        long t = lastFired.containsKey(uuid)?lastFired.get(uuid):0;
                        if (System.currentTimeMillis() - t < 1000 / (QualityArmory.getInstance().getGunInHand(player).getDataInt(ConfigKey.CUSTOMITEM_BULLETS_PER_SECOND.getKey())))
                        continue;


                        if (player != null) {
                            ItemStack hand = player.getInventory().getItemInMainHand();
                            Gun gun = QualityArmory.getInstance().getGun(player.getInventory().getItemInMainHand());
                            if (prepareShoot(player, hand, gun)) {
                                lastFired.put(uuid,System.currentTimeMillis());
                                prepareShootBullet(player, hand, gun);
                                    lastHeldFireButton.remove(uuid);

                            }
                        }
                    } else {
                        if (bulletDelayBetweenShot.containsKey(uuid)) {
                            bulletDelayBetweenShot.remove(uuid);
                            lastHeldFireButton.remove(uuid);
                        }
                    }
                }
            }
        }.runTaskTimer(main, 1, 1);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
        if (event.getHand() == EquipmentSlot.HAND)
            if (QualityArmory.getInstance().isGun(event.getPlayer().getInventory().getItemInMainHand())) {
                Gun gun = QualityArmory.getInstance().getGun(event.getPlayer().getInventory().getItemInMainHand());
                event.setCancelled(true);
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (lastHeldFireButton.containsKey(event.getPlayer().getUniqueId()))
                        if (System.currentTimeMillis() - lastHeldFireButton.get(event.getPlayer().getUniqueId()) < 200)
                            return;
                    boolean data = gun.getDataBoolean(ConfigKey.CUSTOMITEM_AUTOMATIC_FIRING.getKey());
                    if (data) {
                        triggerRapidFire(event.getPlayer(), 20 / (gun.getDataInt(ConfigKey.CUSTOMITEM_BULLETS_PER_SECOND.getKey())));
                    } else {
                        triggerShoot(event.getPlayer());
                    }
                } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    UUID gunuuid = qaMain.getGunDataHandler().getGunID(event.getPlayer().getInventory().getItemInMainHand());
                    if (qaMain.getGunDataHandler().isPlayingAnimation(gunuuid)) {
                        return;
                    }
                    /**
                     * TODO: Toggle Scope
                     */
                }
            }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
        if (QualityArmory.getInstance().isGun(event.getPlayer().getInventory().getItemInMainHand())) {
            Gun gun = QualityArmory.getInstance().getGun(event.getPlayer().getInventory().getItemInMainHand());
            event.setCancelled(true);
            UUID gunuuid = qaMain.getGunDataHandler().getGunID(event.getPlayer().getInventory().getItemInMainHand());
            if (qaMain.getGunDataHandler().isPlayingAnimation(gunuuid)) {
                return;
            }
            qaMain.getGunDataHandler().playAnimation(event.getPlayer(), gunuuid, (String) gun.getData(ConfigKey.CUSTOMITEM_RELOAD_ANIMATION.getKey()), event.getPlayer().getInventory().getHeldItemSlot());
        }
    }
}
