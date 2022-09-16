package somdudewillson.cyberhive.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.creativetab.TabCyberHive;

public class NaniteGrassBlock extends Block {	
	public NaniteGrassBlock() {
		super(Material.GRASS);

		setRegistryName("nanite_grass");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setSoundType(SoundType.PLANT);
		setCreativeTab(TabCyberHive.CYBERHIVE_TAB);
	}
}
