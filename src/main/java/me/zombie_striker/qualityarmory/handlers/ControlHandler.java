package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.api.QualityArmory;
import me.zombie_striker.qualityarmory.guns.Bullet;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControlHandler implements IHandler, Listener {

    public QAMain qaMain;
    private HashMap<UUID, Long> lastHeldFireButton = new HashMap<>();
    private HashMap<UUID, Integer> bulletDelayBetweenShot = new HashMap<>();
    private HashMap<UUID, Integer> delaySinceShot = new HashMap<>();

    public void triggerShoot(Player player){
        lastHeldFireButton.put(player.getUniqueId(),System.currentTimeMillis());
    }
    public void triggerRapidFire(Player player, int delayBetweeenShots){
        triggerShoot(player);
        bulletDelayBetweenShot.put(player.getUniqueId(),delayBetweeenShots);
    }

    public boolean prepareShoot(Player player, ItemStack itemStack, Gun gun) {
        UUID gunserial = qaMain.getGunDataHandler().getGunID(itemStack);
        int bulletCount = qaMain.getGunDataHandler().getGunBulletAmount(gunserial);
        if (bulletCount == 0)
            return false;
        qaMain.getGunDataHandler().setBulletAmount(gunserial, bulletCount - 1);
        return true;
    }

    public void prepareShootBullet(Player player, ItemStack itemStack, Gun gun) {
        Vector dir = player.getLocation().getDirection();
        dir = dir.normalize();
        dir = dir.add(qaMain.getBulletSwayHandler().getSway(player).multiply(0.1)).normalize();
        Bullet bullet = new Bullet(player.getEyeLocation(), dir, player.getUniqueId(), (float) gun.getData(ConfigKey.CUSTOMITEM_DAMAGE.getKey()), (float) gun.getData(ConfigKey.CUSTOMITEM_SPEED.getKey()));
        qaMain.getBulletHandler().registerBullet(bullet);
    }

    @Override
    public void init(QAMain main) {
        this.qaMain = main;
        Bukkit.getPluginManager().registerEvents(this,main);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Long> entry : lastHeldFireButton.entrySet()) {
                    UUID uuid = entry.getKey();
                    long time = entry.getValue();
                    if (System.currentTimeMillis() - time <= 350) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            ItemStack hand = player.getInventory().getItemInMainHand();
                            Gun gun = QualityArmory.getInstance().getGun(player.getInventory().getItemInMainHand());
                            /**
                             * TODO: Add way to add delayt between shots while allowing players to shoot multiple times
                             * by clicking
                             */
                            if (prepareShoot(player, hand, gun)) {
                                prepareShootBullet(player, hand, gun);
                                if(!bulletDelayBetweenShot.containsKey(uuid)){
                                    lastHeldFireButton.remove(uuid);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(main, 1, 1);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getPlayer().getInventory().getItemInMainHand()==null)
            return;
        if(QualityArmory.getInstance().isGun(event.getPlayer().getInventory().getItemInMainHand())){
            Gun gun = QualityArmory.getInstance().getGun(event.getPlayer().getInventory().getItemInMainHand());
            if(event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK) {
                if ((boolean) gun.getData(ConfigKey.CUSTOMITEM_AUTOMATIC_FIRING.getKey())) {
                    triggerRapidFire(event.getPlayer(), 20 / ((int) gun.getData(ConfigKey.CUSTOMITEM_BULLETS_PER_SECOND.getKey())));
                } else {
                    triggerShoot(event.getPlayer());
                }
            }
        }
    }
}
