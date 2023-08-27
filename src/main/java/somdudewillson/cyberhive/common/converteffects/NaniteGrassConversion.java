package somdudewillson.cyberhive.common.converteffects;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;

public class NaniteGrassConversion implements IBlockConversion {

	@Override
	public boolean validTarget(BlockPos inPos, BlockState state, World worldIn) {
		return state.getBlock() == Blocks.GRASS_BLOCK;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void doConversion(BlockPos inPos, BlockState inState, World worldIn) {
		worldIn.setBlockAndUpdate(inPos, CyberBlocks.NANITE_GRASS.defaultBlockState());
	}

}
