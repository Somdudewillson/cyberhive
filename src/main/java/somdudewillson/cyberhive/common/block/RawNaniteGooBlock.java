package somdudewillson.cyberhive.common.block;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.converteffects.IBlockConversion;
import somdudewillson.cyberhive.common.converteffects.NaniteGrassConversion;
import somdudewillson.cyberhive.common.utils.NaniteConversionUtils;
import somdudewillson.cyberhive.common.utils.WorldNaniteUtils;

public class RawNaniteGooBlock extends Block {
	public static final int NANITES_PER_LAYER = 9;
	public static final int MAX_HEIGHT = 8;
	public static final int MAX_NANITES = NANITES_PER_LAYER*MAX_HEIGHT;
	public static final int SELF_SUPPORT_LAYERS = MAX_HEIGHT / 2;
	public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, MAX_HEIGHT);
	public static final BooleanProperty FIRE_IMMUNE = BooleanProperty.create("fire_immune");
	protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[] {Shapes.empty(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
	protected static final IBlockConversion[] blockConversions = new IBlockConversion[] { new NaniteGrassConversion() }; 
	
	public RawNaniteGooBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).speedFactor(0.7F).pushReaction(PushReaction.DESTROY).sound(SoundType.SLIME_BLOCK));
		
		registerDefaultState(this.defaultBlockState()
				.setValue(LAYERS, Integer.valueOf(MAX_HEIGHT))
				.setValue(FIRE_IMMUNE, false)
				);
	}
	
	public int tickRate(Level worldIn) {
		return 20;
	}

	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		switch (pType) {
		case LAND:
			return pState.getValue(LAYERS) < SELF_SUPPORT_LAYERS;
		case WATER:
		case AIR:
		default:
			return false;
		}
	}
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
	}
	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
			CollisionContext pContext) {
		if (pState.getValue(LAYERS)>=SELF_SUPPORT_LAYERS || pLevel.getFluidState(pPos.below()).isEmpty()) {
			return SHAPE_BY_LAYER[pState.getValue(LAYERS)-1];
		} else {
			return SHAPE_BY_LAYER[0];
		}
	}
	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
	}
	@Override
	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos,
			CollisionContext pContext) {
		return SHAPE_BY_LAYER[pState.getValue(LAYERS)];
	}
	@Override
	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
	}
    
    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
    	return Arrays.asList(NaniteConversionUtils.convertNanitesToItemStacks(
    			pState.getValue(LAYERS)*NANITES_PER_LAYER,
    			pState.getValue(FIRE_IMMUNE)));
    }

	@SuppressWarnings("deprecation")
	@Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
		if (pLevel.isClientSide) { return; }
		if (!pLevel.isAreaLoaded(pPos, 1)) { return; } // Prevent loading unloaded chunks with block update
		
		boolean shouldKeepTicking = innerTick(pState, pLevel, pPos, pRand);
		if (shouldKeepTicking) {
	    	pLevel.getBlockTicks().schedule(new ScheduledTick<Block>(
	    			this, pPos, this.tickRate(pLevel), TickPriority.LOW, 0));
		}
    }
	
	private boolean innerTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
		if (!pLevel.getFluidState(pPos.above()).isEmpty()) {
			Block.dropResources(pState, pLevel, pPos, null);
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
			return false;
		}
		
		// Consume food items
		if ( ((pLevel.getGameTime()/tickRate(pLevel)+pPos.asLong()) & 7) == 0 ) {
			boolean halt = tryConsumeItems(pState, pLevel, pPos, pRand);
			if (halt) { return false; }
		}

		Tuple<BlockPos, BlockState> newTarget = tryFall(pState, pLevel, pPos);
		pPos = newTarget.getA();
		pState = newTarget.getB();
		
		final BlockPos[] adjacent = new BlockPos[] {pPos.north(),pPos.east(),pPos.south(),pPos.west(),pPos.above(),pPos.below()};
		final BlockState[] adjStates = new BlockState[adjacent.length];
		IntStream.range(0, adjacent.length)
			.forEach(adjIdx->adjStates[adjIdx]=pLevel.getBlockState(adjacent[adjIdx]));
		
		
		int layers = pState.getValue(LAYERS);
		if (!pState.getValue(FIRE_IMMUNE) && tryBurn(pState, pLevel, pRand, adjacent, adjStates)) {
			if (pRand.nextFloat()<0.01) {
				pLevel.setBlockAndUpdate(pPos, pState.setValue(FIRE_IMMUNE, true));
				return true;
			}
			layers--;
			if (layers <= 0) {
				pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
				pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.2F, 0.95F);
				return false;
			}
		}
		layers = spread(pState, pLevel, pRand, adjacent, adjStates, layers);
		layers = cluster(pPos, pState, pLevel, pRand, adjacent, adjStates, layers);
		if (layers>0) {
			layers = performConversions(pState, pLevel, pPos, adjacent, adjStates, layers);
		}
		
		if (layers>0) {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(LAYERS, layers));
		} else {
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
			return false;
		}
		
		return true;
	}

	private boolean tryConsumeItems(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
		VoxelShape shape = getShape(pState, pLevel, pPos, null);
		AtomicBoolean makeFireImmune = new AtomicBoolean(false);
		double addedNanites = pLevel.getEntitiesOfClass( ItemEntity.class, shape.bounds().move(pPos.getCenter()).inflate(0.15, 0.334, 0.15) )
			.stream()
			.map(itemEntity->new Tuple<>(itemEntity, itemEntity.getItem()))
			.map(itemEntityTuple->new Tuple<>(
					itemEntityTuple.getA(), 
					NaniteConversionUtils.convertItemStackToNanites(itemEntityTuple.getA().getItem())*0.6 ))
			.filter(itemEntityTuple->itemEntityTuple.getB()>0)
			.mapToDouble(itemEntityTuple->{
				if (itemEntityTuple.getA().getItem().hasCraftingRemainingItem()) {
					pLevel.addFreshEntity(new ItemEntity(pLevel, 
							itemEntityTuple.getA().getX(), itemEntityTuple.getA().getY(), itemEntityTuple.getA().getZ(), 
							itemEntityTuple.getA().getItem().getCraftingRemainingItem(),
							0, 0.1, 0));
				}
				makeFireImmune.compareAndSet(false, itemEntityTuple.getA().getItem().getItem().isFireResistant());
				itemEntityTuple.getA().kill();
				return itemEntityTuple.getB();
			})
			.sum();
		if (addedNanites <= 0) { return false; }
		
		if (addedNanites > MAX_NANITES-(pState.getValue(LAYERS)*NANITES_PER_LAYER) ) {
			pLevel.setBlockAndUpdate(pPos, CyberBlocks.PRESSURIZED_NANITE_GOO.get().defaultBlockState()
					.setValue(FIRE_IMMUNE, pState.getValue(FIRE_IMMUNE) || makeFireImmune.get()));
			PressurizedNaniteGooBlock.setNaniteQuantity(pLevel, pPos, (short)Math.round(addedNanites+MAX_NANITES));
			return true;
		}
		
		int depth = 0;
		BlockPos fillPos = pPos;
		int roundedAddedLayers = (int) Math.floor((addedNanites/NANITES_PER_LAYER)+pRand.nextFloat()*0.99);
		boolean changedSelf = false;
		while (roundedAddedLayers > 0 && depth<5) {
			BlockState fillState = pLevel.getBlockState(fillPos);
			int filledLayers = 0;
			if (fillState.isAir()) {
				filledLayers = Math.min(MAX_HEIGHT, roundedAddedLayers);
				pLevel.setBlockAndUpdate(fillPos, pState
						.setValue(LAYERS, filledLayers)
						.setValue(FIRE_IMMUNE, pState.getValue(FIRE_IMMUNE) || makeFireImmune.get()));
			} else if (fillState.is(CyberBlocks.RAW_NANITE_GOO.get())) {
				filledLayers = Math.min(MAX_HEIGHT-fillState.getValue(LAYERS), roundedAddedLayers);
				pLevel.setBlockAndUpdate(fillPos, fillState
						.setValue(LAYERS, fillState.getValue(LAYERS)+filledLayers)
						.setValue(FIRE_IMMUNE, pState.getValue(FIRE_IMMUNE) || makeFireImmune.get() || fillState.getValue(FIRE_IMMUNE)));
				if (fillPos.equals(pPos)) { changedSelf = true; }
			} else {
				break;
			}
			
			fillPos = fillPos.above();
			depth++;
			roundedAddedLayers -= filledLayers;
		}
		
		return changedSelf;
	}

	private boolean tryBurn(BlockState pState, ServerLevel pLevel, RandomSource pRand, BlockPos[] adjacent, BlockState[] adjStates) {
		FluidState testState = adjStates[pRand.nextInt(adjStates.length)].getFluidState();
		if (testState.is(Fluids.LAVA) || testState.is(Fluids.FLOWING_LAVA)) {
			return true;
		}
		return false;
	}
	
	private int spread(BlockState pState, ServerLevel pLevel, RandomSource pRand, BlockPos[] adjacent, BlockState[] adjStates, int layers) {
		if (layers<=1) { return layers; }
		
		for (int adjIdx=0;adjIdx<adjacent.length;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			BlockState adjState = adjStates[adjIdx];
			if (layers<=1) { continue; }
			if (adjIdx>3) { continue; }
			Block adjBlock = adjState.getBlock();
			
			if (adjBlock == CyberBlocks.RAW_NANITE_GOO.get()) {
				int adjLayers = adjState.getValue(LAYERS);
				if (adjLayers<8 && layers-adjLayers+(pRand.nextFloat()-0.99)>1) {
					BlockState newState = adjState.setValue(LAYERS, adjLayers+1)
							.setValue(FIRE_IMMUNE, adjState.getValue(FIRE_IMMUNE) || pState.getValue(FIRE_IMMUNE));
					pLevel.setBlockAndUpdate(adjPos, newState);
					tryFall(newState, pLevel, adjPos);
					layers--;
					continue;
				}
				continue;
			}
			
			boolean generateNewBlock = false;
			if (adjState.isAir()) {
				generateNewBlock = true;
			} else if (WorldNaniteUtils.canReplace(adjState)) {
				pLevel.destroyBlock(adjPos, true);
				generateNewBlock = true;
			}
			if (generateNewBlock) {
				BlockState newState = pState.setValue(LAYERS, 1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				tryFall(newState, pLevel, adjPos);
				layers--;
				continue;
			}
		}
		
		return layers;
	}
	
	private int cluster(BlockPos pos, BlockState pState, ServerLevel pLevel, RandomSource pRand, BlockPos[] adjacent, BlockState[] adjStates, int layers) {
		if (layers > 1) { return layers; }
		if (( (pLevel.getGameTime()/tickRate(pLevel)+pos.asLong()) & 15 ) != 0 ) { return layers; }
		boolean hasAdjacentGoo = Arrays.stream(adjStates).anyMatch(s -> s.is(CyberBlocks.RAW_NANITE_GOO.get())||s.is(CyberBlocks.PRESSURIZED_NANITE_GOO.get()));
		if (hasAdjacentGoo) { return layers; }
		
		Optional<BlockPos> closestGoo = BlockPos.findClosestMatch(pos, 3, 1, tPos -> {
			BlockState tState = pLevel.getBlockState(tPos);
			if (tState.is(CyberBlocks.RAW_NANITE_GOO.get())||tState.is(CyberBlocks.PRESSURIZED_NANITE_GOO.get())) {
				int baseDist = pos.distManhattan(tPos);
				return IntStream.range(0, adjacent.length)
					.filter(i->WorldNaniteUtils.canReplace(adjStates[i]))
					.anyMatch(i->adjacent[i].distManhattan(tPos)<baseDist);
			}
			return false;
		});
		if (closestGoo.isEmpty()) {
			if (pRand.nextFloat() < 0.2) {
				pLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				return 0;
			}
			return layers;
		}
		
		int baseDist = pos.distManhattan(closestGoo.get());
		Optional<BlockPos> movePos = IntStream.range(0, adjacent.length)
				.filter(i->WorldNaniteUtils.canReplace(adjStates[i]))
				.filter(i->adjacent[i].distManhattan(closestGoo.get())<baseDist)
				.mapToObj(i->adjacent[i])
				.findAny();
		if (movePos.isEmpty()) { return layers; }
		
		pLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3 | 64);
		pLevel.setBlockAndUpdate(movePos.get(), pState);		
		return 0;
	}
	
	private int performConversions(BlockState pState, ServerLevel pLevel, BlockPos pPos, BlockPos[] adjacent, BlockState[] adjStates, int layers) {
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
	private Tuple<BlockPos, BlockState> tryFall(BlockState state, ServerLevel worldIn, BlockPos pos) {
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
			if (fallState.is(CyberBlocks.RAW_NANITE_GOO.get())) {
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
			
			if (fallState.getFluidState() != null
					&& !fallState.getFluidState().isEmpty()
					&& fallState.getFluidState().isSource()) {
				continue;
			}
			
			if (WorldNaniteUtils.canReplace(fallState)) {
				worldIn.destroyBlock(fallPos, true);
				worldIn.setBlockAndUpdate(fallPos, state);
				worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				
				result = new Tuple<BlockPos, BlockState>(fallPos, state);
				keepFalling = true;
			}
		} while (keepFalling);
		
		return result;
	}
	


	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		boolean isFireResistant = pState.getValue(FIRE_IMMUNE);
		
		boolean interaction = false;
		if (itemstack.is(Items.GLASS_BOTTLE) && WorldNaniteUtils.trySiphonLayers(pLevel, pPos, MAX_HEIGHT/4)) {
			pLevel.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
			itemstack.shrink(1);
			if (itemstack.isEmpty()) {
				pPlayer.setItemInHand(pHand, new ItemStack(CyberItems.NANITE_BOTTLE.getByFireResistance(isFireResistant)));
			} else if (!pPlayer.getInventory().add(new ItemStack(CyberItems.NANITE_BOTTLE.getByFireResistance(isFireResistant)))) {
				pPlayer.drop(new ItemStack(CyberItems.NANITE_BOTTLE.getByFireResistance(isFireResistant)), false);
			}

			pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, pPos);
			interaction = true;
		} else if (itemstack.is(Items.BUCKET) && WorldNaniteUtils.trySiphonLayers(pLevel, pPos, MAX_HEIGHT)) {
			pLevel.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
			if (!pPlayer.getAbilities().instabuild) {
				itemstack.shrink(1);
				if (itemstack.isEmpty()) {
					pPlayer.setItemInHand(pHand, new ItemStack(CyberItems.NANITE_BUCKET.getByFireResistance(isFireResistant)));
				} else if (!pPlayer.getInventory().add(new ItemStack(CyberItems.NANITE_BUCKET.getByFireResistance(isFireResistant)))) {
					pPlayer.drop(new ItemStack(CyberItems.NANITE_BUCKET.getByFireResistance(isFireResistant)), false);
				}
			}

			pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, pPos);
			interaction = true;
		}

		if (interaction) {
			return InteractionResult.sidedSuccess(pLevel.isClientSide);
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
	
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(LAYERS);
		pBuilder.add(FIRE_IMMUNE);
	}
    
    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
    	if (pLevel.isClientSide) { return; }
    	pLevel.getBlockTicks().schedule(new ScheduledTick<Block>(
    			this, pPos, this.tickRate(pLevel), TickPriority.LOW, 0));
    }
}
