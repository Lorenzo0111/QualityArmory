package me.zombie_striker.qualityarmory.handlers;

import me.zombie_striker.qualityarmory.QAMain;
import me.zombie_striker.qualityarmory.ammo.Ammo;
import me.zombie_striker.qualityarmory.interfaces.IHandler;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class GunDataHandler implements IHandler {

    private final HashMap<UUID, Integer> gunBulletCount = new HashMap<>();
    private final HashMap<UUID, Ammo> ammoUsedPerGun = new HashMap<>();

    public final static String GUN_ID_LORE_STRING = ChatColor.GRAY + "Gun Serial Number: ";

    public int getGunBulletAmount(UUID gun){
        return gunBulletCount.containsKey(gun)?gunBulletCount.get(gun):0;
    }
    public void setBulletAmount(UUID gun, int bulletCount){
        this.gunBulletCount.put(gun,bulletCount);
    }
    public Ammo getAmmoUsed(ItemStack is){
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

    @Override
    public void init(QAMain main) {

    }
}
