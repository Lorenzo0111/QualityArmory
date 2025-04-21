package me.zombie_striker.qg.armor;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.zombie_striker.qg.api.QualityArmory;

public class BulletProtectionUtil {

    public static boolean stoppedBullet(final Player p, final Location bullet, final Vector velocity) {
        if (p.getInventory().getHelmet() != null) {
            if (me.zombie_striker.qg.api.QualityArmory.isArmor(p.getInventory().getHelmet())) {
                final ArmorObject armor = me.zombie_striker.qg.api.QualityArmory.getArmor(p.getInventory().getHelmet());
                final double offset = (p.isSneaking() ? armor.getShifitngHeightOffset() : 0);
                if (bullet.getY() - p.getLocation().getY() > armor.getMinH() + offset) {
                    if (bullet.getY() - p.getLocation().getY() < armor.getMaxH() + offset) {
                        // Within kevlar range
                        return true;
                    } else {
                        if (velocity.getY() < 0) {
                            final Vector fourth = velocity.normalize().multiply(0.25);
                            if (bullet.clone().add(fourth).getY() < armor.getMaxH() + offset) {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    if (velocity.getY() > 0) {

                        final Vector fourth = velocity.normalize().multiply(0.25);
                        if (bullet.clone().add(fourth).getY() > armor.getMinH() + offset) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static boolean negatesHeadshot(final Player p) {
        if (p.getInventory().getHelmet() != null) {
            if (QualityArmory.isArmor(p.getInventory().getHelmet())) {
                final ArmorObject armor = QualityArmory.getArmor(p.getInventory().getHelmet());
                return armor.getNegateHeadshots();
            }
        }
        return false;
    }
}
