package somdudewillson.cyberhive.common.converteffects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;

public class NaniteGrassConversion implements IBlockConversion {

	@Override
	public boolean validTarget(BlockPos inPos, IBlockState state, World worldIn) {
		return state.getBlock() == Blocks.GRASS;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void doConversion(BlockPos inPos, IBlockState inState, World worldIn) {
		worldIn.setBlockState(inPos, CyberBlocks.NANITE_GRASS.getDefaultState());
	}

}
