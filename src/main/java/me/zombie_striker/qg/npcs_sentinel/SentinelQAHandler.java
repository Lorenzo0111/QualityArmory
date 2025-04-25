package me.zombie_striker.qg.npcs_sentinel;

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

import java.util.HashMap;
import java.util.UUID;

public class SentinelQAHandler extends SentinelIntegration {

	@Override
	public String getTargetHelp() {
		return "";
	}


	private HashMap<UUID,Long> lastTimeShot = new HashMap<>();

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

		if(st.needsAmmo){
			if(GunUtil.hasAmmo((Player) st.getLivingEntity(),g)){
				int amount = Gun.getAmount((Player) st.getLivingEntity());
				if(amount <=0){
					GunUtil.basicReload(g, (Player) st.getLivingEntity(),false);
					return false;
				}
			}else{
				return false;
			}
		}


		Vector faceAcc = ent.getEyeLocation().toVector().subtract(st.getLivingEntity().getEyeLocation().toVector());
		if (faceAcc.lengthSquared() > 0.0) {
			faceAcc = faceAcc.normalize();
		}
		faceAcc = st.fixForAcc(faceAcc);
		st.faceLocation(st.getLivingEntity().getEyeLocation().clone().add(faceAcc.multiply(10)));

		if(lastTimeShot.containsKey(st.getLivingEntity().getUniqueId())){
			if(System.currentTimeMillis()-lastTimeShot.get(st.getLivingEntity().getUniqueId())/1000 < (Math.max(g.isAutomatic()?(10.0/g.getFireRate())/20:g.getDelayBetweenShotsInSeconds()*1.5,g.getDelayBetweenShotsInSeconds()))){
				return false;
			}
		}
		lastTimeShot.put(st.getLivingEntity().getUniqueId(),System.currentTimeMillis());

		double sway = g.getSway() * st.accuracy;
		if (g.usesCustomProjectiles()) {
			for (int i = 0; i < g.getBulletsPerShot(); i++) {
				Vector go = st.getLivingEntity().getEyeLocation().getDirection().normalize();
				go.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
						(Math.random() * 2 * sway) - sway)).normalize();
				Vector two = go.clone();
				g.getCustomProjectile().spawn(g, st.getLivingEntity().getEyeLocation(), (Player) st.getLivingEntity(),
						two);
			}
		} else {
			GunUtil.shootInstantVector(g, ((Player) st.getLivingEntity()), sway, g.getDurabilityDamage(), g.getBulletsPerShot(),
					g.getMaxDistance());
		}

		GunUtil.playShoot(g, (Player) st.getLivingEntity());
		QAMain.DEBUG("Sentinel shooting!");
		if(st.needsAmmo){
			int amount = Gun.getAmount((Player) st.getLivingEntity()) - 1;
			ItemMeta im = itm.getItemMeta();
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