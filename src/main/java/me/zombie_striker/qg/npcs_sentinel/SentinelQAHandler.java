package me.zombie_striker.qg.npcs_sentinel;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.mcmonkey.sentinel.SentinelIntegration;
import org.mcmonkey.sentinel.SentinelTrait;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.GunUtil;

public class SentinelQAHandler extends SentinelIntegration {

	@Override
	public String getTargetHelp() {
		return "";
	}



	@SuppressWarnings("deprecation")
	@Override
	public boolean tryAttack(SentinelTrait st, LivingEntity ent) {
		QAMain.DEBUG("Sentinel about to shoot!");
		if (!(st.getLivingEntity() instanceof Player)) {
			return false;
		}
		ItemStack itm = ((Player) st.getLivingEntity()).getItemInHand();
		Gun g = QualityArmory.getGun(itm);
		QAMain.DEBUG("Getting gun! gun = "+g);
		if (g == null)
			return false;
		// CSDirector direc = (CSDirector)
		// Bukkit.getPluginManager().getPlugin("CrackShot");
		// String node = direc.returnParentNode((Player) st.getLivingEntity());
		// if (node == null) {
		// return false;
		// }
		Vector faceAcc = ent.getEyeLocation().toVector().subtract(st.getLivingEntity().getEyeLocation().toVector());
		if (faceAcc.lengthSquared() > 0.0) {
			faceAcc = faceAcc.normalize();
		}
		faceAcc = st.fixForAcc(faceAcc);
		st.faceLocation(st.getLivingEntity().getEyeLocation().clone().add(faceAcc.multiply(10)));

		double sway = g.getSway() * 0.75;
		if (g.usesCustomProjctiles()) {
			for (int i = 0; i < g.getBulletsPerShot(); i++) {
				Vector go = st.getLivingEntity().getEyeLocation().getDirection().normalize();
				go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
						(Math.random() * 2 * sway) - sway)).normalize();
				Vector two = go.clone();// .multiply();
				g.getCustomProjectile().spawn(g, st.getLivingEntity().getEyeLocation(), (Player) st.getLivingEntity(),
						two);
			}
		} else {
			GunUtil.shootInstantVector(g, ((Player) st.getLivingEntity()), sway, g.getDurabilityDamage(), g.getBulletsPerShot(),
					g.getMaxDistance());
		}

		GunUtil.playShoot(g, (Player) st.getLivingEntity());
		QAMain.DEBUG("Sentinel shooting!");
		
		// direc.csminion.weaponInteraction((Player) st.getLivingEntity(), node, false);
		 ((Player) st.getLivingEntity()).setItemInHand(itm);
		if (st.rangedChase) {
			st.attackHelper.rechase();
			QAMain.DEBUG("Sentinel rechase");
		}
		return true;
	}
}