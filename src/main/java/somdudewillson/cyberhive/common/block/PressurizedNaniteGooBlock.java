package somdudewillson.cyberhive.common.block;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;

public class PressurizedNaniteGooBlock extends Block {
	public static final int MAX_DENSITY = 16;
	public static final IntegerProperty DENSITY = IntegerProperty.create("density", 1, MAX_DENSITY);
	
	public PressurizedNaniteGooBlock() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(50.0F).sound(SoundType.SLIME_BLOCK));

		setRegistryName("pressurized_nanite_goo");
		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		registerDefaultState(this.defaultBlockState().setValue(DENSITY, Integer.valueOf(MAX_DENSITY)));
	}
	
	public int tickRate(World worldIn) {
		return 20;
	}
    
    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
    	LinkedList<ItemStack> dropList = new LinkedList<ItemStack>();
    	dropList.add(new ItemStack(CyberItems.NANITE_LUMP, pState.getValue(DENSITY)));
    	return dropList;
    }

	@Override
	public void tick(BlockState pState, ServerWorld pLevel, BlockPos pPos, Random pRand) {
		if (pLevel.isClientSide) { return; } 
		if (!pLevel.isLoaded(pPos)) { return; } // Prevent loading unloaded chunks with block update
		
		if (tryFall(pState, pLevel, pPos)) {
			return;
		}
		
		BlockPos[] adjacent = new BlockPos[] {pPos.north(),pPos.east(),pPos.south(),pPos.west(),pPos.above()};
		int density = pState.getValue(DENSITY);
		for (int adjIdx=0;adjIdx<adjacent.length && density>0;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			
			if (pLevel.isEmptyBlock(adjPos)) {
				BlockState newState = CyberBlocks.RAW_NANITE_GOO.defaultBlockState()
						.setValue(RawNaniteGooBlock.LAYERS, 1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				if (pRand.nextInt(16)==0) {density--;}
				continue;
			}
			BlockState adjState = pLevel.getBlockState(adjPos);
			if (adjState.getBlock() == CyberBlocks.RAW_NANITE_GOO
					&& adjState.getValue(RawNaniteGooBlock.LAYERS)<RawNaniteGooBlock.MAX_HEIGHT) {
				BlockState newState = adjState.setValue(
						RawNaniteGooBlock.LAYERS,
						adjState.getValue(RawNaniteGooBlock.LAYERS)+1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				if (pRand.nextInt(16)==0) {density--;}
			}
		}
		if (density>0) {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(DENSITY, density));
			pLevel.getBlockTicks().scheduleTick(pPos, this, this.tickRate(pLevel));
		} else {
			pLevel.setBlockAndUpdate(pPos, CyberBlocks.RAW_NANITE_GOO
					.defaultBlockState()
					.setValue(RawNaniteGooBlock.LAYERS, RawNaniteGooBlock.MAX_HEIGHT));
		}
    }
	
	private boolean tryFall(BlockState pState, ServerWorld pLevel, BlockPos pPos) {
		BlockState belowBlockState = pLevel.getBlockState(pPos.below());
		Block belowBlock = belowBlockState.getBlock();
		
		if (belowBlock == CyberBlocks.RAW_NANITE_GOO) {
			pLevel.setBlockAndUpdate(pPos.below(), pState);
			pLevel.setBlockAndUpdate(pPos, belowBlockState);
			return true;
		}
		if (belowBlockState.canBeReplaced(new BlockItemUseContext(pLevel, null, null, ItemStack.EMPTY, new BlockRayTraceResult(Vector3d.ZERO, Direction.UP, pPos, false)))) {
			pLevel.setBlockAndUpdate(pPos.below(), pState);
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
			return true;
		}
		
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(DENSITY);
	}
    
    @Override
    public void onPlace(BlockState pState, World pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
    	if (pLevel.isClientSide) { return; }
    	pLevel.getBlockTicks().scheduleTick(pPos, this, this.tickRate(pLevel));
    }
}
