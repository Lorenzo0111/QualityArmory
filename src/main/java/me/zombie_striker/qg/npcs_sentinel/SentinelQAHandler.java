package me.zombie_striker.qg.npcs_sentinel;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.mcmonkey.sentinel.SentinelIntegration;
import org.mcmonkey.sentinel.SentinelTrait;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;

public class SentinelQAHandler extends SentinelIntegration {

    @Override
    public String getTargetHelp() { return ""; }

    private final HashMap<UUID, Long> lastTimeShot = new HashMap<>();

    @SuppressWarnings("deprecation")
    @Override
    public boolean tryAttack(final SentinelTrait st, final LivingEntity ent) {
        QAMain.DEBUG("Sentinel about to shoot!");
        if (!(st.getLivingEntity() instanceof Player)) {
            return false;
        }
        final ItemStack itm = ((Player) st.getLivingEntity()).getInventory().getItemInMainHand();
        final Gun g = QualityArmory.getGun(itm);
        QAMain.DEBUG("Getting gun! gun = " + g);
        if (g == null)
            return false;
        // CSDirector direc = (CSDirector)
        // Bukkit.getPluginManager().getPlugin("CrackShot");
        // String node = direc.returnParentNode((Player) st.getLivingEntity());
        // if (node == null) {
        // return false;
        // }

        if (st.needsAmmo) {
            if (GunUtil.hasAmmo((Player) st.getLivingEntity(), g)) {
                final int amount = Gun.getAmount((Player) st.getLivingEntity());
                if (amount <= 0) {
                    GunUtil.basicReload(g, (Player) st.getLivingEntity(), false);
                    return false;
                }
            } else {
                return false;
            }
        }

        Vector faceAcc = ent.getEyeLocation().toVector().subtract(st.getLivingEntity().getEyeLocation().toVector());
        if (faceAcc.lengthSquared() > 0.0) {
            faceAcc = faceAcc.normalize();
        }
        faceAcc = st.fixForAcc(faceAcc);
        st.faceLocation(st.getLivingEntity().getEyeLocation().clone().add(faceAcc.multiply(10)));

        if (this.lastTimeShot.containsKey(st.getLivingEntity().getUniqueId())) {
            if (System.currentTimeMillis() - this.lastTimeShot.get(st.getLivingEntity().getUniqueId()) / 1000 < (Math.max(
                    g.isAutomatic() ? (10.0 / g.getFireRate()) / 20 : g.getDelayBetweenShotsInSeconds() * 1.5,
                    g.getDelayBetweenShotsInSeconds()))) {
                return false;
            }
        }
        this.lastTimeShot.put(st.getLivingEntity().getUniqueId(), System.currentTimeMillis());

        final double sway = g.getSway() * st.accuracy;
        if (g.usesCustomProjctiles()) {
            for (int i = 0; i < g.getBulletsPerShot(); i++) {
                final Vector go = st.getLivingEntity().getEyeLocation().getDirection().normalize();
                go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway))
                        .normalize();
                final Vector two = go.clone();
                g.getCustomProjectile().spawn(g, st.getLivingEntity().getEyeLocation(), (Player) st.getLivingEntity(), two);
            }
        } else {
            GunUtil.shootInstantVector(g, ((Player) st.getLivingEntity()), sway, g.getDamage(), g.getBulletsPerShot(), g.getMaxDistance());
        }

        GunUtil.playShoot(g, (Player) st.getLivingEntity());
        QAMain.DEBUG("Sentinel shooting!");
        if (st.needsAmmo) {
            final int amount = Gun.getAmount((Player) st.getLivingEntity()) - 1;
            final ItemMeta im = itm.getItemMeta();
            Gun.updateAmmo(g, (Player) st.getLivingEntity(), amount);
            itm.setItemMeta(im);
        }
        // direc.csminion.weaponInteraction((Player) st.getLivingEntity(), node, false);
        ((Player) st.getLivingEntity()).setItemInHand(itm);
        if (st.rangedChase) {
            st.attackHelper.rechase();
            QAMain.DEBUG("Sentinel rechase");
        }
        return true;
    }
}