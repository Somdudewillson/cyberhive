package somdudewillson.cyberhive.common.block;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;

public class PressurizedNaniteGooBlock extends Block {
	public static final int MAX_DENSITY = 16;
	public static final IntegerProperty DENSITY = IntegerProperty.create("density", 1, MAX_DENSITY);
	
	public PressurizedNaniteGooBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(50.0F).sound(SoundType.SLIME_BLOCK));

		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		registerDefaultState(this.defaultBlockState().setValue(DENSITY, Integer.valueOf(MAX_DENSITY)));
	}
	
	public int tickRate(Level worldIn) {
		return 20;
	}
    
    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
    	LinkedList<ItemStack> dropList = new LinkedList<ItemStack>();
    	dropList.add(new ItemStack(CyberItems.NANITE_LUMP.get(), pState.getValue(DENSITY)));
    	return dropList;
    }

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
		if (pLevel.isClientSide) { return; } 
		if (!pLevel.isLoaded(pPos)) { return; } // Prevent loading unloaded chunks with block update
		
		if (tryFall(pState, pLevel, pPos)) {
			return;
		}
		
		int density = pState.getValue(DENSITY);
		density = spread(pLevel, pPos, pRand, density);
		
		if (density>0) {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(DENSITY, density));
	    	pLevel.getBlockTicks().schedule(new ScheduledTick<Block>(
	    			this, pPos, this.tickRate(pLevel), TickPriority.LOW, 0));
		} else {
			pLevel.setBlockAndUpdate(pPos, CyberBlocks.RAW_NANITE_GOO.get().defaultBlockState()
					.setValue(RawNaniteGooBlock.LAYERS, RawNaniteGooBlock.MAX_HEIGHT));
		}
    }
	
	private boolean tryFall(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
		BlockState belowBlockState = pLevel.getBlockState(pPos.below());
		Block belowBlock = belowBlockState.getBlock();
		
		if (belowBlock == CyberBlocks.RAW_NANITE_GOO.get()) {
			pLevel.setBlockAndUpdate(pPos.below(), pState);
			pLevel.setBlockAndUpdate(pPos, belowBlockState);
			return true;
		}
		if (belowBlockState.canBeReplaced(new BlockPlaceContext(pLevel, null, null, ItemStack.EMPTY, new BlockHitResult(Vec3.ZERO, Direction.UP, pPos, false)))) {
			pLevel.destroyBlock(pPos.below(), true);
			pLevel.setBlockAndUpdate(pPos.below(), pState);
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
			return true;
		}
		
		return false;
	}
	
	private int spread(ServerLevel pLevel, BlockPos pPos, RandomSource pRand, int density) {
		BlockPos[] adjacent = new BlockPos[] {pPos.north(),pPos.east(),pPos.south(),pPos.west(),pPos.above()};
		
		for (int adjIdx=0;adjIdx<adjacent.length && density>0;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			
			if (pLevel.isEmptyBlock(adjPos)) {
				BlockState newState = CyberBlocks.RAW_NANITE_GOO.get().defaultBlockState()
						.setValue(RawNaniteGooBlock.LAYERS, 1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				if (pRand.nextInt(16)==0) {density--;}
				continue;
			}
			BlockState adjState = pLevel.getBlockState(adjPos);
			if (adjState.getBlock() == CyberBlocks.RAW_NANITE_GOO.get()
					&& adjState.getValue(RawNaniteGooBlock.LAYERS)<RawNaniteGooBlock.MAX_HEIGHT) {
				BlockState newState = adjState.setValue(
						RawNaniteGooBlock.LAYERS,
						adjState.getValue(RawNaniteGooBlock.LAYERS)+1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				if (pRand.nextInt(16)==0) {density--;}
			}
		}
		
		return density;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(DENSITY);
	}
    
    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
    	if (pLevel.isClientSide) { return; }
    	pLevel.getBlockTicks().schedule(new ScheduledTick<Block>(
    			this, pPos, this.tickRate(pLevel), TickPriority.LOW, 0));
    }
}
