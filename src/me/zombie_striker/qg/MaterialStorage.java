package me.zombie_striker.qg;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialStorage {

	private static List<MaterialStorage> store = new ArrayList<MaterialStorage>();
	
	public static MaterialStorage getMS(Material m, int d){
		for(MaterialStorage k : store){
			if(k.m==m &&( k.d==d || k.d==-1))
				return k;
		}
		MaterialStorage mm = new MaterialStorage(m, d);
		store.add(mm);
		return mm;
	}

	public static MaterialStorage getMS(ItemStack is){
		return getMS(is.getType(), is.getDurability());
	}
	
	private int d;
	private Material m;
	
	public int getData(){
		return d;
	}
	public Material getMat(){
		return m;
	}
	
	private MaterialStorage(Material m, int d) {
		this.m = m;
		this.d = d;
	}
}
