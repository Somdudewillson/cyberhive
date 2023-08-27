package somdudewillson.cyberhive.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class NaniteGrassBlock extends Block {	
	public NaniteGrassBlock() {
		super(AbstractBlock.Properties.of(Material.GRASS, MaterialColor.GRASS).strength(0.5F).sound(SoundType.GRASS));

		setRegistryName("nanite_grass");
		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getPath());
	}
}
