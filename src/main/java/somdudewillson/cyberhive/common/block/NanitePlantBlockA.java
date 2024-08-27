package somdudewillson.cyberhive.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;

public class NanitePlantBlockA extends NaniteStemBlock {
	public NanitePlantBlockA() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 3.0F).sound(SoundType.WOOD));

		registerDefaultState(this.defaultBlockState()
        		.setValue(NaniteStemBlock.CORE_DIR, Integer.valueOf(0)));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(NaniteStemBlock.CORE_DIR);
	}
}
