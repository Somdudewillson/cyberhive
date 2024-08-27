package somdudewillson.cyberhive.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class NaniteGrassBlock extends Block {	
	public NaniteGrassBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.6F).sound(SoundType.GRASS));
		
		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getPath());
	}
}
