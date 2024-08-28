package somdudewillson.cyberhive.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.tileentity.NanitePlantGrowerTileEntity;

public class NanitePlantGrowerBlock extends Block implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	
	public NanitePlantGrowerBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 3.0F).sound(SoundType.WOOD));

		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING);
	}
	
	public static BlockState initializeBlockState(Direction facingDir) {
		return CyberBlocks.NANITE_PLANT_GROWER.get().defaultBlockState().setValue(FACING, facingDir);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NanitePlantGrowerTileEntity(pPos, pState);
    }
	
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		  return type == CyberBlocks.NANITE_PLANT_GROWER_TET.get() ? NanitePlantGrowerTileEntity::tick : null;
	}
}
