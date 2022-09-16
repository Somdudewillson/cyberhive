package somdudewillson.cyberhive.common.converteffects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockConversion {
	public boolean validTarget(BlockPos inPos, IBlockState state, World worldIn);
	public int getPriority();
	public void doConversion(BlockPos inPos, IBlockState inState, World worldIn);
}
