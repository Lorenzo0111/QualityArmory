package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.ConfigKey;
import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.guns.Bullet;
import me.zombie_striker.qualityarmory.guns.Gun;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ControlHandler implements IHandler {

    public QAMain qaMain;

    public boolean prepareShoot(Player player, ItemStack itemStack, Gun gun){
      UUID gunserial =  qaMain.getGunDataHandler().getGunID(itemStack);
      int bulletCount = qaMain.getGunDataHandler().getGunBulletAmount(gunserial);
      if(bulletCount==0)
          return false;
      qaMain.getGunDataHandler().setBulletAmount(gunserial,bulletCount-1);
      return true;
    }
    public void shoot(Player player, ItemStack itemStack, Gun gun){
        Vector dir = player.getLocation().getDirection();
        dir = dir.normalize();
        dir=dir.add(qaMain.getBulletSwayHandler().getSway(player).multiply(0.1)).normalize();
        Bullet bullet = new Bullet(player.getEyeLocation(),dir,player.getUniqueId(), (float) gun.getData(ConfigKey.CUSTOMITEM_DAMAGE.getKey()), (float) gun.getData(ConfigKey.CUSTOMITEM_SPEED.getKey()));
        qaMain.getBulletHandler().registerBullet(bullet);
    }

    @Override
    public void init(QAMain main) {
        this.qaMain = main;
    }
}
