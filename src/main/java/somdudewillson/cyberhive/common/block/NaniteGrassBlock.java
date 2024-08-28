package somdudewillson.cyberhive.common.block;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.phys.Vec3;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.utils.GenericUtils;
import somdudewillson.cyberhive.common.utils.NaniteSharedEffects;

public class NaniteGrassBlock extends Block {
	public static final int MAX_STRENGTH = 5;
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
		} else if (blockstate.is(CyberBlocks.RAW_NANITE_GOO.get()) && blockstate.getValue(RawNaniteGooBlock.LAYERS) < 4) {
			return true;
		} else if (blockstate.getFluidState().getAmount() == 8) {
			return false;
		} else {
			int i = LightEngine.getLightBlockInto(pLevelReader, pState, pPos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(pLevelReader, blockpos));
			return i < pLevelReader.getMaxLightLevel();
		}
	}
	
	private static final Set<Block> CONSUMABLE_PLANTS = Set.of(Blocks.GRASS, Blocks.FERN, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
	private static void placeNaniteGrass(BlockState sourceState, ServerLevel pLevel, BlockPos targetPos, RandomSource pRandom) {
		BlockState aboveState = pLevel.getBlockState(targetPos.above());
		boolean consumedPlant = false;
		if (aboveState.is(BlockTags.FLOWERS) || CONSUMABLE_PLANTS.contains(aboveState.getBlock())) {
			consumedPlant = true;
			pLevel.destroyBlock(targetPos.above(), false);
		}
		
		int newStrength = sourceState.getValue(SPREAD_STRENGTH);
		float strengthReductionRoll = pRandom.nextFloat();
		float strengthReductionTarget = consumedPlant?0.5f:0.9f;
		if (strengthReductionRoll<strengthReductionTarget) {
			newStrength--;
		} else if (consumedPlant) {
			spawnConsumptionWasteCloud(pLevel, targetPos.above().getCenter());
		}
		
		BlockState newNaniteGrassState = sourceState.getBlock().defaultBlockState()
				.setValue(SPREAD_STRENGTH, newStrength);
		pLevel.setBlockAndUpdate(targetPos, newNaniteGrassState);
	}

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!canBeNaniteGrass(pState, pLevel, pPos)) {
			if (!pLevel.isLoaded(pPos)) { return; } // Prevent loading unloaded chunks when checking survivability
			pLevel.setBlockAndUpdate(pPos, Blocks.DIRT.defaultBlockState());
		} else {
			tryEatPlant(pState, pLevel, pPos);
			trySpread(pState, pLevel, pPos, pRandom);
		}
	}
	
	private void tryEatPlant(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
		BlockState aboveState = pLevel.getBlockState(pPos.above());
		if (aboveState.is(BlockTags.FLOWERS) || CONSUMABLE_PLANTS.contains(aboveState.getBlock())) {
			pLevel.destroyBlock(pPos.above(), false);
			
			if (pState.getValue(SPREAD_STRENGTH) < MAX_STRENGTH/2) {
				pState.setValue(SPREAD_STRENGTH, pState.getValue(SPREAD_STRENGTH)+1);
			} else {
				spawnConsumptionWasteCloud(pLevel, pPos.above().getCenter());
			}
		}
	}

	private void trySpread(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
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
				placeNaniteGrass(pState, pLevel, adjArray[i], pRandom);
			}
		}
	}
	
	private static void spawnConsumptionWasteCloud(ServerLevel pLevel, Vec3 targetPos) {
		NaniteSharedEffects.makeNaniteCloud(
				pLevel, targetPos, 
				2, -0.5f, 10, 20*3, 
				null, 80, 0);
	}
}
