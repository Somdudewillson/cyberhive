package somdudewillson.cyberhive.common.converteffects;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockConversion {
	public boolean validTarget(BlockPos inPos, BlockState state, World worldIn);
	public int getPriority();
	public void doConversion(BlockPos inPos, BlockState inState, World worldIn);
}
