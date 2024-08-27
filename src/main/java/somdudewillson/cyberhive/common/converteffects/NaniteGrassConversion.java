package somdudewillson.cyberhive.common.converteffects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.cyberhive.common.CyberBlocks;

public class NaniteGrassConversion implements IBlockConversion {

	@Override
	public boolean validTarget(BlockPos inPos, BlockState state, Level worldIn) {
		return state.getBlock() == Blocks.GRASS_BLOCK;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void doConversion(BlockPos inPos, BlockState inState, Level worldIn) {
		worldIn.setBlockAndUpdate(inPos, CyberBlocks.NANITE_GRASS.get().defaultBlockState());
	}

}
