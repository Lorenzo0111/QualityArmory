package example.qualityarmory;

import org.bukkit.Material;

import me.zombie_striker.qualityarmory.guns.utils.WeaponSounds;
import me.zombie_striker.qualityarmory.guns.utils.WeaponType;

public class RegisteringNewGuns {

	public void onEnable() {
		me.zombie_striker.qualityarmory.api.QualityArmory
				.createAndLoadNewGun("test_gun", "&cTest Gun", Material.DIAMOND_AXE, 28, WeaponType.RIFLE,
						WeaponSounds.GUN_AUTO, true, "762", 3, 30, 1000)
				.setFullyAutomatic(3).setBulletsPerShot(10).setParticle(0, 0, 1).done();
		// This will create a new gun called "test_gun". Its a diamond axe that uses the
		// ID 28, is a Rifle, has ironsights, and uses 7.62 ammo. It does 3 damage, has
		// 30 bullets, and cost 1000. Iy's firerate is "3", which is the same as the
		// ASVal. For every shot, it will fire 10 projectiles, and will have a blue
		// particle trail
		
		//Recommended to us IDS either 27,28,29,30,31,32,33,34, or 35
	}
}
