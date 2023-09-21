package somdudewillson.cyberhive.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.tileentity.NanitePlantGrowerTileEntity;

public class NanitePlantGrowerBlock extends Block {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	
	public NanitePlantGrowerBlock() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 3.0F).sound(SoundType.WOOD));

		setRegistryName("nanite_plant_grower");
		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING);
	}
	
	public static BlockState initializeBlockState(Direction facingDir) {
		return CyberBlocks.NANITE_PLANT_GROWER.defaultBlockState().setValue(FACING, facingDir);
	}

	@Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NanitePlantGrowerTileEntity();
    }
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}
