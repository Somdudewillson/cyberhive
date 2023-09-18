package somdudewillson.cyberhive.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.tileentity.NaniteRootTileEntity;

public class NaniteRootBlock extends NaniteStemBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;

	public NaniteRootBlock() {
		super(AbstractBlock.Properties.of(Material.METAL, MaterialColor.WOOD).strength(4.0F, 3.5F)
				.sound(SoundType.WOOD));

		setRegistryName("nanite_root");
		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(ATTACHED, false));
	}

	@Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NaniteRootTileEntity();
    }
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	private static final Direction[] ROOTING_ORDER = new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST,
			Direction.SOUTH, Direction.WEST, Direction.UP };
	private BlockState updateRooting(World pLevel, BlockPos pPos, BlockState pState) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

		for (Direction direction : ROOTING_ORDER) {
			blockpos$mutable.setWithOffset(pPos, direction);
			if (rootableBlockState(pLevel.getBlockState(blockpos$mutable))) {
				pState = pState
						.setValue(FACING, direction.getOpposite())
						.setValue(ATTACHED, true);
				break;
			}
		}
		return pState;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, IWorld pLevel,
			BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(ATTACHED)) {
			if (pFacing.getOpposite() == pState.getValue(FACING) && !rootableBlockState(pFacingState)) {
				pState = pState.setValue(ATTACHED, false);
				pLevel.getBlockTicks().scheduleTick(pCurrentPos, this, 1);
			}
		} else if (rootableBlockState(pFacingState)) {
			pState = pState
					.setValue(FACING, pFacing.getOpposite())
					.setValue(ATTACHED, true);
		}

		return pState;
	}

	private boolean rootableBlockState(BlockState pState) {
		return pState.is(Blocks.GRASS_BLOCK) || pState.is(Blocks.DIRT) || pState.is(Blocks.COARSE_DIRT)
				|| pState.is(Blocks.PODZOL) || pState.is(Blocks.FARMLAND);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING, ATTACHED);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext pContext) {
		return updateRooting(pContext.getLevel(), pContext.getClickedPos(), this.defaultBlockState());
	}

}
