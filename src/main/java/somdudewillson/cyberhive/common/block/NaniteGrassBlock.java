package somdudewillson.cyberhive.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.MapColor;
import somdudewillson.cyberhive.common.utils.GenericUtils;

public class NaniteGrassBlock extends Block {
	public static final int MAX_STRENGTH = 6;
	public static final IntegerProperty SPREAD_STRENGTH = IntegerProperty.create("spread_strength", 0, MAX_STRENGTH);
	
	public NaniteGrassBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.6F).sound(SoundType.GRASS).randomTicks());

		registerDefaultState(this.defaultBlockState().setValue(SPREAD_STRENGTH, Integer.valueOf(MAX_STRENGTH)));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(SPREAD_STRENGTH);
	}

	private static boolean canBeNaniteGrass(BlockState pState, LevelReader pLevelReader, BlockPos pPos) {
		BlockPos blockpos = pPos.above();
		BlockState blockstate = pLevelReader.getBlockState(blockpos);
		if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockstate.getFluidState().getAmount() == 8) {
			return false;
		} else {
			int i = LightEngine.getLightBlockInto(pLevelReader, pState, pPos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(pLevelReader, blockpos));
			return i < pLevelReader.getMaxLightLevel();
		}
	}

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!canBeNaniteGrass(pState, pLevel, pPos)) {
			if (!pLevel.isLoaded(pPos)) { return; } // Prevent loading unloaded chunks when checking survivability
			pLevel.setBlockAndUpdate(pPos, Blocks.DIRT.defaultBlockState());
		} else {
			if (pState.getValue(SPREAD_STRENGTH) == 0) { return; }
			if (pRandom.nextInt() >= pState.getValue(SPREAD_STRENGTH)) { return; }
			if (!pLevel.isAreaLoaded(pPos, 1)) { return; } // Prevent loading unloaded chunks when checking spread
			
			BlockPos[] adjArray = new BlockPos[] {
					pPos.north(),
					pPos.east(),
					pPos.south(),
					pPos.west(),

					pPos.north().above(),
					pPos.east().above(),
					pPos.south().above(),
					pPos.west().above(),
					pPos.north().below(),
					pPos.east().below(),
					pPos.south().below(),
					pPos.west().below()
			};
			GenericUtils.shuffleArray(adjArray, pRandom);
			
			for (int i=0;i<4;i++) {
				BlockState targetState = pLevel.getBlockState(adjArray[i]);
				if (targetState.getBlock() != Blocks.DIRT && targetState.getBlock() != Blocks.GRASS_BLOCK) {
					continue;
				}
				if (canBeNaniteGrass(pState, pLevel, adjArray[i])) {
					int newStrength = pState.getValue(SPREAD_STRENGTH);
					if (pRandom.nextFloat()<0.9) { newStrength--; }
					BlockState newNaniteGrassState = defaultBlockState().setValue(SPREAD_STRENGTH, newStrength);
					pLevel.setBlockAndUpdate(adjArray[i], newNaniteGrassState);
				}
			}
		}
	}
}
