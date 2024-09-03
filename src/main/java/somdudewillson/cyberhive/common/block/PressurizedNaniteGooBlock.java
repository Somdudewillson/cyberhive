package somdudewillson.cyberhive.common.block;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.tileentity.PressurizedNaniteGooTileEntity;
import somdudewillson.cyberhive.common.utils.NaniteConversionUtils;

public class PressurizedNaniteGooBlock extends Block implements EntityBlock {	
	public PressurizedNaniteGooBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(50.0F).sound(SoundType.SLIME_BLOCK));
	}
	
	public int tickRate(Level worldIn) {
		return 20;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PressurizedNaniteGooTileEntity(pPos, pState);
    }
	
	public static short getNaniteQuantity(Level pLevel, BlockPos pPos) {
		Optional<PressurizedNaniteGooTileEntity> blockEntity = pLevel.getBlockEntity(pPos, CyberBlocks.PRESSURIZED_NANITE_GOO_TET.get());
		return blockEntity.map(e->e.getNaniteQuantity()).orElse((short) 0);
	}
	public static void setNaniteQuantity(Level pLevel, BlockPos pPos, short newNaniteQuantity) {
		Optional<PressurizedNaniteGooTileEntity> blockEntity = pLevel.getBlockEntity(pPos, CyberBlocks.PRESSURIZED_NANITE_GOO_TET.get());
		blockEntity.ifPresent(e->e.setNaniteQuantity(newNaniteQuantity));
	}

	@SuppressWarnings("deprecation")
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (!pIsMoving && !pNewState.is(CyberBlocks.RAW_NANITE_GOO.get())) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof PressurizedNaniteGooTileEntity) {
				
				ItemStack[] itemStacks = NaniteConversionUtils.convertNanitesToItemStacks(
						((PressurizedNaniteGooTileEntity) blockEntity).getNaniteQuantity());
				for (ItemStack itemStack : itemStacks) {
					Containers.dropItemStack(
							pLevel, 
							pPos.getX(), pPos.getY(), pPos.getZ(), 
							itemStack);
				}
				
			}
		}
		
		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}
    
    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
    	return Collections.emptyList();
    }

	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
		if (pLevel.isClientSide) { return; } 
		if (!pLevel.isAreaLoaded(pPos, 1)) { return; } // Prevent loading unloaded chunks with block update
		
		if (tryFall(pState, pLevel, pPos)) {
			return;
		}
		if (tryDrift(pState, pLevel, pPos, pRand)) {
			return;
		}
		BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
		if (!(blockEntity instanceof PressurizedNaniteGooTileEntity)) {
			pLevel.destroyBlock(pPos, false);
		}
		PressurizedNaniteGooTileEntity pressurizedEntity = (PressurizedNaniteGooTileEntity) blockEntity;
		
		short naniteQuantity = pressurizedEntity.getNaniteQuantity();
		naniteQuantity = spread(pLevel, pPos, pRand, naniteQuantity);
		
		if (naniteQuantity>RawNaniteGooBlock.MAX_NANITES) {
			pressurizedEntity.setNaniteQuantity(naniteQuantity);
	    	pLevel.getBlockTicks().schedule(new ScheduledTick<Block>(
	    			this, pPos, this.tickRate(pLevel), TickPriority.LOW, 0));
		} else {
			pLevel.setBlockAndUpdate(pPos, CyberBlocks.RAW_NANITE_GOO.get().defaultBlockState()
					.setValue(RawNaniteGooBlock.LAYERS, naniteQuantity/RawNaniteGooBlock.NANITES_PER_LAYER));
		}
    }
	
	private boolean tryFall(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
		BlockState belowBlockState = pLevel.getBlockState(pPos.below());
		
		if (belowBlockState.is(CyberBlocks.RAW_NANITE_GOO.get()) 
				&& belowBlockState.getValue(RawNaniteGooBlock.LAYERS)<RawNaniteGooBlock.MAX_HEIGHT) {
			moveSelf(pLevel, pPos, pState, pPos.below(), belowBlockState);
			return true;
		}
		if (belowBlockState.canBeReplaced(Fluids.FLOWING_WATER) && belowBlockState.getFluidState().isEmpty()) {
			pLevel.destroyBlock(pPos.below(), true);
			moveSelf(pLevel, pPos, pState, pPos.below(), Blocks.AIR.defaultBlockState());
			return true;
		}
		
		return false;
	}
	
	private boolean tryDrift(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
		float driftRoll = pRand.nextFloat();
		BlockState consideredState = pState;
		BlockPos consideredPos = pPos;
		if (
				(
						(driftRoll < 0.8 
						&& ( (consideredState = pLevel.getBlockState( (consideredPos = pPos.above())) )
								.is(CyberBlocks.RAW_NANITE_GOO.get())) 
								|| !consideredState.getFluidState().isEmpty() )
						||
						(driftRoll < 0.3
						&& ( (consideredState = pLevel.getBlockState( (consideredPos = pPos.relative(Direction.values()[pRand.nextInt(2, 6)]))) )
								.is(CyberBlocks.RAW_NANITE_GOO.get()))
								|| !consideredState.getFluidState().isEmpty() )
				)
				&& (!consideredState.hasProperty(RawNaniteGooBlock.LAYERS) || consideredState.getValue(RawNaniteGooBlock.LAYERS)==RawNaniteGooBlock.MAX_HEIGHT)
			) {
			moveSelf(pLevel, pPos, pState, consideredPos, consideredState);
			return true;
		}

		return false;
	}
	
	private void moveSelf(ServerLevel pLevel, BlockPos curPos, BlockState curState, BlockPos targetPos, @Nullable BlockState targetState) {
		BlockState targetOrigState = pLevel.getBlockState(targetPos);
		if (targetState == null) { targetState = targetOrigState; }

		pLevel.setBlock(targetPos, curState, (targetState==targetOrigState) ? 3|64 : 3);
		pLevel.getBlockEntity(targetPos).load(pLevel.getBlockEntity(curPos).saveWithId());
		pLevel.setBlock(curPos, targetState, 3|64);
	}
	
	private short spread(ServerLevel pLevel, BlockPos pPos, RandomSource pRand, short naniteQuantity) {
		BlockPos[] adjacent = new BlockPos[] {pPos.north(),pPos.east(),pPos.south(),pPos.west(),pPos.above()};
		
		for (int adjIdx=0;adjIdx<adjacent.length && naniteQuantity>RawNaniteGooBlock.MAX_NANITES;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			BlockState adjState = pLevel.getBlockState(adjPos);
			
			if (adjState.isAir()) {
				BlockState newState = CyberBlocks.RAW_NANITE_GOO.get().defaultBlockState()
						.setValue(RawNaniteGooBlock.LAYERS, 1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				naniteQuantity -= RawNaniteGooBlock.NANITES_PER_LAYER;
				continue;
			}
			if (adjState.getBlock() == CyberBlocks.RAW_NANITE_GOO.get()
					&& adjState.getValue(RawNaniteGooBlock.LAYERS)<RawNaniteGooBlock.MAX_HEIGHT) {
				BlockState newState = adjState.setValue(
						RawNaniteGooBlock.LAYERS,
						adjState.getValue(RawNaniteGooBlock.LAYERS)+1);
				pLevel.setBlockAndUpdate(adjPos, newState);
				naniteQuantity -= RawNaniteGooBlock.NANITES_PER_LAYER;
			}
		}
		
		return naniteQuantity;
	}
    
    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
    	if (pLevel.isClientSide) { return; }
    	pLevel.getBlockTicks().schedule(new ScheduledTick<Block>(
    			this, pPos, this.tickRate(pLevel), TickPriority.LOW, 0));
    }
}
