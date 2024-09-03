package somdudewillson.cyberhive.common.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class RawGooBlockStateProvider extends BlockStateProvider {

	public RawGooBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
		super(output, modid, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		ModelFile[] layerModels = new ModelFile[RawNaniteGooBlock.MAX_HEIGHT];
		for (int h=1;h<=layerModels.length;h++) {
			int scaledH = h*(16/RawNaniteGooBlock.MAX_HEIGHT);
			BlockModelBuilder modelBuilder = models().getBuilder("raw_nanite_goo_"+h)
				.texture("texture", "block/rawnanitegoo")
				.texture("particle", "block/rawnanitegoo")
				.element()
					.from(0, 0, 0)
					.to(16, scaledH, 16)
					.allFaces((d,fb)-> {
						fb.texture("#texture");
						if (d != Direction.UP) {
							fb.cullface(d);
						}
						if (d == Direction.UP || d == Direction.DOWN) {
							fb.uvs(0, 0, 16, 16);
						} else {
							fb.uvs(0, 16-scaledH, 16, 16);
						}
					})
					.end();
			layerModels[h-1] = modelBuilder;
		}
		
		VariantBlockStateBuilder blockStateBuilder = this.getVariantBuilder(CyberBlocks.RAW_NANITE_GOO.get());
		for (int layerVal : RawNaniteGooBlock.LAYERS.getPossibleValues()) {
			blockStateBuilder = blockStateBuilder
				.partialState()
				.with(RawNaniteGooBlock.LAYERS, layerVal)
					.modelForState()
					.modelFile(layerModels[layerVal-1])
					.addModel();
		}
	}

}
