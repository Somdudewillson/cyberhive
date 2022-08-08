package somdudewillson.cyberhive.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

public class RawNaniteGooBlock extends Block {
	public static final int MAX_HEIGHT = 8;
	public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 8);
	protected static final AxisAlignedBB[] SHAPE_BY_LAYER = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	
	public RawNaniteGooBlock() {
		super(Material.IRON);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, Integer.valueOf(MAX_HEIGHT)));
	}

	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE_BY_LAYER[((Integer)state.getValue(LAYERS)).intValue()];
    }
    
	@Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

	@Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

	@Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Nullable
	@Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        int i = ((Integer)blockState.getValue(LAYERS)).intValue() - 1;
        float f = 1f/MAX_HEIGHT;
        AxisAlignedBB axisalignedbb = blockState.getBoundingBox(worldIn, pos);
        return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, (double)((float)i * f), axisalignedbb.maxZ);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.SNOWBALL;
    }

    public int quantityDropped(Random random)
    {
        return 1;
    }

	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!worldIn.isBlockLoaded(pos)) { return; } // Prevent loading unloaded chunks with block update
		
		BlockPos fallPos = pos.down();
		if (worldIn.isAirBlock(fallPos)) {
			worldIn.setBlockState(fallPos, state);
			worldIn.destroyBlock(pos, false);
		}
	}

	@Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LAYERS, Integer.valueOf((meta & (MAX_HEIGHT-1)) + 1));
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LAYERS)).intValue() - 1;
    }
}
