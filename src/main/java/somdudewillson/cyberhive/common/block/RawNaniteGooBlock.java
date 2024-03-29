package somdudewillson.cyberhive.common.block;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.converteffects.IBlockConversion;
import somdudewillson.cyberhive.common.converteffects.NaniteGrassConversion;

public class RawNaniteGooBlock extends Block {
	public static final int MAX_HEIGHT = 8;
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, MAX_HEIGHT);
	protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[] {VoxelShapes.empty(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
	protected static final IBlockConversion[] blockConversions = new IBlockConversion[] { new NaniteGrassConversion() };
	protected static final Material NANITE_GOO_MATERIAL = new Material(MaterialColor.METAL, false, false, false, false, false, true, PushReaction.DESTROY); 
	
	public RawNaniteGooBlock() {
		super(AbstractBlock.Properties.of(NANITE_GOO_MATERIAL).strength(4.0F, 6.0F).speedFactor(0.7F).sound(SoundType.SLIME_BLOCK));

		setRegistryName("raw_nanite_goo");
		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		registerDefaultState(this.defaultBlockState().setValue(LAYERS, Integer.valueOf(MAX_HEIGHT)));
	}
	
	public int tickRate(World worldIn) {
		return 20;
	}

	@Override
	public boolean isPathfindable(BlockState pState, IBlockReader pLevel, BlockPos pPos, PathType pType) {
		switch (pType) {
		case LAND:
			return pState.getValue(LAYERS) < MAX_HEIGHT / 2;
		case WATER:
		case AIR:
		default:
			return false;
		}
	}
	@Override
	public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
		return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
	}
	@Override
	public VoxelShape getCollisionShape(BlockState pState, IBlockReader pLevel, BlockPos pPos,
			ISelectionContext pContext) {
		return SHAPE_BY_LAYER[pState.getValue(LAYERS) - 1];
	}
	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, IBlockReader pReader, BlockPos pPos) {
		return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
	}
	@Override
	public VoxelShape getVisualShape(BlockState pState, IBlockReader pReader, BlockPos pPos,
			ISelectionContext pContext) {
		return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
	}
	@Override
	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
	}
    
    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
    	LinkedList<ItemStack> dropList = new LinkedList<ItemStack>();
    	dropList.add(new ItemStack(CyberItems.NANITE_LUMP, pState.getValue(LAYERS)));
    	return dropList;
    }

	@Override
    public void tick(BlockState pState, ServerWorld pLevel, BlockPos pPos, Random pRand) {
		if (pLevel.isClientSide) { return; } 
		if (!pLevel.isLoaded(pPos)) { return; } // Prevent loading unloaded chunks with block update

		Tuple<BlockPos, BlockState> newTarget = tryFall(pState, pLevel, pPos);		
		pPos = newTarget.getA();
		pState = newTarget.getB();
		
		final BlockPos[] adjacent = new BlockPos[] {pPos.north(),pPos.east(),pPos.south(),pPos.west(),pPos.above(),pPos.below()};
		final BlockState[] adjStates = new BlockState[adjacent.length];
		IntStream.range(0, adjacent.length)
			.forEach(adjIdx->adjStates[adjIdx]=pLevel.getBlockState(adjacent[adjIdx]));
		
		int layers = pState.getValue(LAYERS);
		layers = spread(pState, pLevel, pRand, adjacent, adjStates, layers);
		layers = performConversions(pState, pLevel, pPos, adjacent, adjStates, layers);
		
		if (layers>0) {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(LAYERS, layers));
			pLevel.getBlockTicks().scheduleTick(pPos, this, this.tickRate(pLevel));
		} else {
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
		}
    }
	
	private int spread(BlockState pState, ServerWorld pLevel, Random pRand, BlockPos[] adjacent, BlockState[] adjStates, int layers) {
		for (int adjIdx=0;adjIdx<adjacent.length;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			BlockState adjState = adjStates[adjIdx];
			if (layers<=1) { continue; }
			if (adjIdx>3) { continue; }
			
			if (pLevel.isEmptyBlock(adjPos)) {
				BlockState newState = pState.getBlock().defaultBlockState().setValue(LAYERS, 1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				tryFall(newState, pLevel, adjPos);
				layers--;
				continue;
			}
			
			Block adjBlock = adjState.getBlock();
			if (adjBlock == CyberBlocks.RAW_NANITE_GOO) {
				int adjLayers = adjState.getValue(LAYERS);
				if (adjLayers<8 && layers-adjLayers+pRand.nextInt(2)>1) {
					BlockState newState = adjState.setValue(LAYERS, adjLayers+1);
					pLevel.setBlockAndUpdate(adjPos, newState);
					tryFall(newState, pLevel, adjPos);
					layers--;
					continue;
				}
			}
		}
		
		return layers;
	}
	
	private int performConversions(BlockState pState, ServerWorld pLevel, BlockPos pPos, BlockPos[] adjacent, BlockState[] adjStates, int layers) {
		IBlockConversion currentPriorityConversion = null;
		int conversionTargetAdjIdx = 0;
		int currentPriority = -1;
		for (int adjIdx=0;adjIdx<adjacent.length;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			BlockState adjState = adjStates[adjIdx];
			
			for (IBlockConversion conversion : blockConversions) {
				int priority = conversion.getPriority();
				
				if (priority>=currentPriority && conversion.validTarget(adjPos, adjState, pLevel)) {
					currentPriorityConversion = conversion;
					conversionTargetAdjIdx = adjIdx;
					currentPriority = priority;
				}
			}
		}
		if (currentPriority>-1) {
			currentPriorityConversion.doConversion(
					adjacent[conversionTargetAdjIdx],
					adjStates[conversionTargetAdjIdx],
					pLevel);
			layers--;
		}
		
		return layers;
	}
	
	// TODO: Maybe add rotated model to create nanite goo "waterfalls"?
	private Tuple<BlockPos, BlockState> tryFall(BlockState state, ServerWorld worldIn, BlockPos pos) {
		Tuple<BlockPos, BlockState> result = new Tuple<BlockPos, BlockState>(pos,state);
		
		boolean keepFalling = false;
		do {
			keepFalling = false;
			pos = result.getA();
			state = result.getB();
			
			BlockPos fallPos = pos.below();
			if (worldIn.isEmptyBlock(fallPos)) {				
				worldIn.setBlockAndUpdate(fallPos, state);
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

				result = new Tuple<BlockPos, BlockState>(fallPos, state);
				keepFalling = true;
				continue;
			}
			
			BlockState fallState = worldIn.getBlockState(fallPos);
			if (fallState.getBlock() == CyberBlocks.RAW_NANITE_GOO) {
				if (fallState.getValue(LAYERS)>=MAX_HEIGHT) { continue; }
				
				int targetLayers = fallState.getValue(LAYERS);
				int ownLayers = state.getValue(LAYERS);
				int availableSpace = MAX_HEIGHT-targetLayers;
				
				BlockState newState;
				if (availableSpace>=ownLayers) {
					newState = fallState.setValue(LAYERS, targetLayers+ownLayers);
					worldIn.setBlockAndUpdate(fallPos, fallState.setValue(LAYERS, targetLayers+ownLayers));
					worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					
					result = new Tuple<BlockPos, BlockState>(fallPos,newState);
				} else {
					worldIn.setBlockAndUpdate(fallPos, fallState.setValue(LAYERS, MAX_HEIGHT));
					newState = state.setValue(LAYERS, ownLayers-availableSpace);
					worldIn.setBlockAndUpdate(pos, newState);
					
					result = new Tuple<BlockPos, BlockState>(pos,newState);
				}
				continue;
			}
			
			if (fallState.getMaterial().isLiquid()
					&& fallState.getFluidState() != null
					&& fallState.getFluidState().isSource()) {
				continue;
			}
			
			if (fallState.canBeReplaced(new BlockItemUseContext(worldIn, null, null, ItemStack.EMPTY, new BlockRayTraceResult(Vector3d.ZERO, Direction.UP, pos, false)))) {
				worldIn.destroyBlock(fallPos, true);
				worldIn.setBlockAndUpdate(fallPos, state);
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				
				result = new Tuple<BlockPos, BlockState>(fallPos, state);
				keepFalling = true;
			}
		} while (keepFalling);
		
		return result;
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(LAYERS);
	}
    
    @Override
    public void onPlace(BlockState pState, World pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
    	if (pLevel.isClientSide) { return; }
    	pLevel.getBlockTicks().scheduleTick(pPos, this, this.tickRate(pLevel));
    }
}
