package somdudewillson.cyberhive.common.data;

import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class RawGooBlockStateProvider extends BlockStateProvider {

	public RawGooBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
		super(output, modid, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		
		this.getVariantBuilder(CyberBlocks.RAW_NANITE_GOO.get())
			.forAllStates(s->new ConfiguredModel[] { new ConfiguredModel(buildGooModelForState(s)) });
	}
	
	private BlockModelBuilder buildGooModelForState(BlockState state) {
		int height = state.getValue(RawNaniteGooBlock.LAYERS);
		int scaledH = height*(16/RawNaniteGooBlock.MAX_HEIGHT);
		
		String extraPropString = state.getProperties().stream()
			.filter(p->p != RawNaniteGooBlock.LAYERS)
			.map(p->{
				if (p instanceof IntegerProperty) {
					IntegerProperty intP = (IntegerProperty) p;
					int propVal = state.getValue(intP);
					if (intP.getAllValues().anyMatch(v->v.value()<propVal)) {
						return p.getName()+"_"+propVal;
					}
				} else if (p instanceof BooleanProperty) {
					if (state.getValue((BooleanProperty)p)) {
						return p.getName();
					}
				}
				return null;
			})
			.filter(Objects::nonNull)
			.sorted()
			.collect(Collectors.joining("&"));
		if (!extraPropString.isBlank()) { extraPropString = "-" + extraPropString; }
		
		return models().getBuilder("raw_nanite_goo_"+height+extraPropString)
				.texture("texture", "block/rawnanitegoo"+extraPropString)
				.texture("particle", "block/rawnanitegoo"+extraPropString)
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
	} 

}
