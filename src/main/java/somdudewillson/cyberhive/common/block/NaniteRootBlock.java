package somdudewillson.cyberhive.common.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.tileentity.NaniteRootTileEntity;

public class NaniteRootBlock extends NaniteStemBlock implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;

	public NaniteRootBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(4.0F, 3.5F).sound(SoundType.WOOD));

		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(ATTACHED, false));
	}

	@Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NaniteRootTileEntity(pPos, pState);
    }
	
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		  return type == CyberBlocks.NANITE_ROOT_TET.get() ? NaniteRootTileEntity::tick : null;
	}

	private static final Direction[] ROOTING_ORDER = new Direction[] { Direction.DOWN, Direction.NORTH, Direction.EAST,
			Direction.SOUTH, Direction.WEST, Direction.UP };
	private BlockState updateRooting(Level pLevel, BlockPos pPos, BlockState pState) {
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

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
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		if (pState.getValue(ATTACHED)) {
			if (pDirection.getOpposite() == pState.getValue(FACING) && !rootableBlockState(pNeighborState)) {
				pState = pState.setValue(ATTACHED, false);
				pLevel.getBlockTicks().schedule(new ScheduledTick<Block>(this, pPos, 1, TickPriority.NORMAL, 0));
			}
		} else if (rootableBlockState(pNeighborState)) {
			pState = pState
					.setValue(FACING, pDirection.getOpposite())
					.setValue(ATTACHED, true);
		}

		return pState;
	}

	private boolean rootableBlockState(BlockState pState) {
		return pState.is(Blocks.GRASS_BLOCK) || pState.is(Blocks.DIRT) || pState.is(Blocks.COARSE_DIRT)
				|| pState.is(Blocks.PODZOL) || pState.is(Blocks.FARMLAND);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING, ATTACHED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return updateRooting(pContext.getLevel(), pContext.getClickedPos(), this.defaultBlockState());
	}

}
