package somdudewillson.cyberhive.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.creativetab.TabCyberHive;

public class NaniteGrassBlock extends Block {	
	public NaniteGrassBlock() {
		super(Material.GRASS);

		setRegistryName("nanite_grass");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setSoundType(SoundType.PLANT);
		setCreativeTab(TabCyberHive.CYBERHIVE_TAB);
		setTickRandomly(true);
	}

	@Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rng) {
		if (worldIn.isRemote) { return; } 
		if (!worldIn.isBlockLoaded(pos)) { return; } // Prevent loading unloaded chunks with block update
		if (worldIn.getBlockState(pos.up()).isOpaqueCube()) {
			worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
			return;
		}
		
		Iterable<BlockPos> adjacents = BlockPos.getAllInBox(pos.down().down().south().west(), pos.up().north().east());
		int spreadCount = 0;
		for (BlockPos adjPos : adjacents) {
			
			IBlockState adjState = worldIn.getBlockState(adjPos);
			Block adjBlock = adjState.getBlock();
			if (adjBlock == Blocks.DIRT
					|| adjBlock == Blocks.GRASS
					|| adjBlock == Blocks.MYCELIUM) {
				if (worldIn.getBlockState(adjPos.up()).isOpaqueCube()) { continue; }
				worldIn.setBlockState(adjPos, CyberBlocks.NANITE_GRASS.getDefaultState());
				spreadCount++;
				if (spreadCount>=2) { return; }
			}
		}
    }
}
