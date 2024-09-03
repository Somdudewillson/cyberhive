package somdudewillson.cyberhive.common.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;
import somdudewillson.cyberhive.common.tileentity.PressurizedNaniteGooTileEntity;

public class WorldNaniteUtils {
	
	private static final Direction[] SEARCH_DIRECTIONS = new Direction[] {
			Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
	};
	private static final int MAX_SEARCH_STEPS = 64;
	public static boolean trySiphonLayers(Level level, BlockPos startPos, int targetLayers) {
		HashSet<BlockPos> testedPositions = new HashSet<>();
		LinkedList<BlockPos> searchQueue = new LinkedList<>();
		LinkedList<Consumer<Level>> queuedLevelChanges = new LinkedList<>();
		testedPositions.add(startPos);
		searchQueue.add(startPos);
		
		int foundLayers = 0;
		int searchSteps = 0;
		while (searchSteps++<=MAX_SEARCH_STEPS && foundLayers<targetLayers && !searchQueue.isEmpty()) {
			BlockPos searchPos = searchQueue.poll();
			BlockState searchState = level.getBlockState(searchPos);
			int remainingLayersToFind = targetLayers-foundLayers;
			
			boolean wasValidNaniteBlock = false;
			if (searchState.is(CyberBlocks.RAW_NANITE_GOO.get())) {
				wasValidNaniteBlock = true;
				int layers = searchState.getValue(RawNaniteGooBlock.LAYERS);
				if (layers > remainingLayersToFind) {
					foundLayers += remainingLayersToFind;
					queuedLevelChanges.add(changeBlockStateFactory(searchPos, searchState.setValue(RawNaniteGooBlock.LAYERS, layers-remainingLayersToFind)));
				} else {
					foundLayers += layers;
					queuedLevelChanges.add(changeBlockStateFactory(searchPos, Blocks.AIR.defaultBlockState()));
				}
			} else if (searchState.is(CyberBlocks.PRESSURIZED_NANITE_GOO.get())) {
				Optional<PressurizedNaniteGooTileEntity> pressurizedBlockEntity = level.getBlockEntity(searchPos, CyberBlocks.PRESSURIZED_NANITE_GOO_TET.get());
				if (pressurizedBlockEntity.isPresent()) {
					wasValidNaniteBlock = true;
					int nanites = pressurizedBlockEntity.get().getNaniteQuantity();
					int layers = nanites/RawNaniteGooBlock.NANITES_PER_LAYER;
					if (layers > remainingLayersToFind) {
						foundLayers += remainingLayersToFind;
						queuedLevelChanges.add(changePressurizedNanitesFactory(pressurizedBlockEntity.get(), nanites-(remainingLayersToFind*RawNaniteGooBlock.NANITES_PER_LAYER) ));
					} else {
						foundLayers += layers;
						queuedLevelChanges.add(changePressurizedNanitesFactory(pressurizedBlockEntity.get(), nanites-(layers*RawNaniteGooBlock.NANITES_PER_LAYER) ));
					}
				}
			}
			
			if (wasValidNaniteBlock) {
				for (Direction searchDir : SEARCH_DIRECTIONS) {
					BlockPos adjPos = searchPos.relative(searchDir);
					if (!testedPositions.contains(adjPos)) {
						testedPositions.add(adjPos);
						searchQueue.add(adjPos);
					}
				}
			}
		}
		
		if (foundLayers == targetLayers) {
			queuedLevelChanges.forEach(sc -> sc.accept(level));
			return true;
		}
		return false;
	}
	
	private static Consumer<Level> changeBlockStateFactory(BlockPos pos, BlockState newState) {
		return l -> l.setBlockAndUpdate(pos, newState);
	}
	private static Consumer<Level> changePressurizedNanitesFactory(PressurizedNaniteGooTileEntity tileEntity, int newQuantity) {
		return l -> tileEntity.setNaniteQuantity((short) newQuantity);
	}
	
	public static boolean canReplace(BlockState testState) {
		return testState.getFluidState().isEmpty() && testState.canBeReplaced(Fluids.FLOWING_LAVA);
	}

}
