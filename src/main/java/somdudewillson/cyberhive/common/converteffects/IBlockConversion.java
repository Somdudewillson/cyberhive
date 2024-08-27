package somdudewillson.cyberhive.common.converteffects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockConversion {
	public boolean validTarget(BlockPos inPos, BlockState state, Level worldIn);
	public int getPriority();
	public void doConversion(BlockPos inPos, BlockState inState, Level worldIn);
}
