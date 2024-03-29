package somdudewillson.cyberhive.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.StateContainer;

public class NanitePlantBlockA extends NaniteStemBlock {
	public NanitePlantBlockA() {
		super(AbstractBlock.Properties.of(Material.METAL, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));

		setRegistryName("nanite_plant_a");
		registerDefaultState(this.defaultBlockState()
        		.setValue(NaniteStemBlock.CORE_DIR, Integer.valueOf(0)));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(NaniteStemBlock.CORE_DIR);
	}
}
