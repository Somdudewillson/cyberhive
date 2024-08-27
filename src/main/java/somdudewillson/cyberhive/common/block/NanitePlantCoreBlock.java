package somdudewillson.cyberhive.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.tileentity.NaniteRootTileEntity;

public class NanitePlantCoreBlock extends Block implements EntityBlock {
	public NanitePlantCoreBlock() {

		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 3.0F).sound(SoundType.SLIME_BLOCK));
	}

	@Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NaniteRootTileEntity(pPos, pState);
    }
	
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		  return type == CyberBlocks.NANITE_ROOT_TET.get() ? NaniteRootTileEntity::tick : null;
	}
}
