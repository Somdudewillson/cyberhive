package somdudewillson.cyberhive.common.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

public class NaniteFieldStemBlock extends NaniteStemBlock {
	
	public enum ChainedDistance implements StringRepresentable {
		ONE,
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		
		RESET;
		
		public static ChainedDistance getNextFurther(ChainedDistance current) {
			if (current == null) { return ONE; }
			switch (current) {
			case ONE:
				return TWO;
			case TWO:
				return THREE;
			case THREE:
				return FOUR;
			case FOUR:
				return FIVE;
			case FIVE:
				return SIX;
			case SIX:
				return SEVEN;
			case SEVEN:
				return RESET;
			case RESET:
				return TWO;
			default:
				return ONE;
			}
		}
		public ChainedDistance getNextFurther() { return ChainedDistance.getNextFurther(this); }
		
		public static boolean isCloser(ChainedDistance current, ChainedDistance other) {
			if (current == null) { return other != null; }
			switch (current) {
			case ONE:
				return other == null;
			case TWO:
				return other == ONE;
			case THREE:
				return other == TWO;
			case FOUR:
				return other == THREE;
			case FIVE:
				return other == FOUR;
			case SIX:
				return other == FIVE;
			case SEVEN:
				return other == SIX;
			case RESET:
				return other == SEVEN;
			default:
				return other != null;
			}
			
		}
		public boolean isCloser(ChainedDistance other) { return ChainedDistance.isCloser(this, other); }
		
		@Override
		public String getSerializedName() {
			return this.name();
		}
	}
	public static final EnumProperty<ChainedDistance> CHAINED_DISTANCE = EnumProperty.create("chained_distance", ChainedDistance.class);
	public static final BooleanProperty HAS_CHARGED = BooleanProperty.create("has_charge");
	
	public NaniteFieldStemBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD));

//		setRegistryName("nanite_stem_field");
		registerDefaultState(this.defaultBlockState()
				.setValue(CHAINED_DISTANCE, ChainedDistance.ONE)
        		.setValue(HAS_CHARGED, Boolean.FALSE));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(CHAINED_DISTANCE, HAS_CHARGED);
	}
}
