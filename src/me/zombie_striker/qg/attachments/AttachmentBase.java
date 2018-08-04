package me.zombie_striker.qg.attachments;

import java.util.List;

import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import me.zombie_striker.qg.Main;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.guns.Gun;
import me.zombie_striker.qg.guns.utils.WeaponSounds;
import me.zombie_striker.qg.guns.utils.WeaponType;
import me.zombie_striker.qg.handlers.chargers.ChargingHandler;

public class AttachmentBase implements Comparable<AttachmentBase>{

	private MaterialStorage base;
	private Gun baseGun;
	private MaterialStorage ms;

	private String name;
	private String displayname;
	private List<String> lore;
	private double price;

	private String newSound = null;
	private float volume = -1;
	private boolean muzzleSmoke = false;
	private boolean isAutomatic = false;
	private ChargingHandler ch = null;
	private double delayShoot = -1;
	private double delayReload = -1;
	private int bulletsPerShot = -1;
	private int maxDistance = -1;
	private boolean supports18 = false;

	private boolean newParticles = false;
	private Particle particle = null;
	private double particleR = -1;
	private double particleG = -1;
	private double particleB = -1;
	
	private ItemStack[] craftingRequirements = null;
	private WeaponType newWeaponType = null;

	public AttachmentBase(MaterialStorage baseItem, MaterialStorage attachedItem, String newname, String newDisplayname,
			List<String> lore) {
		this.base = baseItem;
		baseGun = Main.gunRegister.get(baseItem);
		this.ms = attachedItem;
		this.name = newname;
		this.displayname = newDisplayname;
		this.lore = lore;

		setMuzzleSmoke(baseGun.useMuzzleSmoke());
		setSupports18(baseGun.is18Support());
	}

	public boolean hasNewSound() {
		return newSound != null;
	}

	public String getNewSound() {
		return newSound;
	}

	public void setNewSound(WeaponSounds newSound) {
		setNewSound(newSound.getName());
	}

	public void setNewSound(String newSound) {
		this.newSound = newSound;
	}

	public boolean hasNewVolume() {
		return volume != -1;
	}

	public void setNewVolume(float f) {
		this.volume = f;
	}

	public float getNewVolume() {
		return volume;
	}

	public MaterialStorage getBase() {
		return base;
	}

	public MaterialStorage getItem() {
		return ms;
	}

	public String getAttachmentName() {
		return name;
	}

	public String getDisplayName() {
		return displayname;
	}

	public List<String> getLore() {
		return lore;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isMuzzleSmoke() {
		return muzzleSmoke;
	}

	public void setMuzzleSmoke(boolean muzzleSmoke) {
		this.muzzleSmoke = muzzleSmoke;
	}

	public boolean isAutomatic() {
		return isAutomatic;
	}

	public void setAutomatic(boolean isAutomatic) {
		this.isAutomatic = isAutomatic;
	}

	public boolean isNewParticles() {
		return newParticles;
	}

	public void setNewParticles(Particle particle) {
		setNewParticles(particle, -1, -1, -1);
	}

	public void setNewParticles(Particle particle, double partr, double partg, double partb) {
		this.newParticles = true;
		this.particle = particle;
		if (partr != -1)
			this.particleR = (float) partr;
		if (partg != -1)
			this.particleG = (float) partg;
		if (partb != -1)
			this.particleB = (float) partb;
	}

	public Particle getParticle() {
		if(particle==null)
			return baseGun.getParticle();
		return particle;
	}

	public double getParticleR() {
		if(particleR==-1)
			return baseGun.getParticleR();
		return particleR;
	}

	public double getParticleG() {
		if(particleG==-1)
			return baseGun.getParticleG();
		return particleG;
	}

	public double getParticleB() {
		if(particleB==-1)
			return baseGun.getParticleB();
		return particleB;
	}

	public ChargingHandler getCh() {
		if(ch==null)
			return baseGun.getChargingVal();
		return ch;
	}

	public void setCh(ChargingHandler ch) {
		this.ch = ch;
	}

	public double getDelayShoot() {
		if(delayShoot==-1)
			return baseGun.getDelayBetweenShotsInSeconds();
		return delayShoot;
	}

	public void setDelayShoot(double delayShoot) {
		this.delayShoot = delayShoot;
	}

	public int getBulletsPerShot() {
		if(bulletsPerShot==-1)
			return baseGun.getBulletsPerShot();
		return bulletsPerShot;
	}

	public void setBulletsPerShot(int bulletsPerShot) {
		this.bulletsPerShot = bulletsPerShot;
	}

	public int getMaxDistance() {
		if(maxDistance==-1)
			return baseGun.getMaxDistance();
		return maxDistance;
	}

	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}

	public boolean isSupports18() {
		return supports18;
	}

	public void setSupports18(boolean supports18) {
		this.supports18 = supports18;
	}

	public double getDelayReload() {
		if(delayReload==-1)
			return baseGun.getReloadTime();
		return delayReload;
	}

	public void setDelayReload(double delayReload) {
		this.delayReload = delayReload;
	}

	public ItemStack[] getCraftingRequirements() {
		return craftingRequirements;
	}

	public void setCraftingRequirements(ItemStack[] craftingRequirements) {
		this.craftingRequirements = craftingRequirements;
	}

	public WeaponType getNewWeaponType() {
		return newWeaponType;
	}

	public void setNewWeaponType(WeaponType newWeaponType) {
		this.newWeaponType = newWeaponType;
	}

	@Override
	public int compareTo(AttachmentBase arg0) {
		return this.displayname.compareTo(arg0.getDisplayName());
	}

}
