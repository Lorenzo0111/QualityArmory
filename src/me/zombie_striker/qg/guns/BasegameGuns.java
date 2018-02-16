package me.zombie_striker.qg.guns;

@Deprecated
public enum BasegameGuns {
	AK48(5), P30(2),MP5K(4),M16(7),HenryRifle(19),M40(13),Mouser(20),RPG(10),SW1911(12),Enfield(18),Remmington(8);
	private int d;
	private BasegameGuns( int j) {
		d = j;
	}
	public int getData(){
		return d;
	}
}
