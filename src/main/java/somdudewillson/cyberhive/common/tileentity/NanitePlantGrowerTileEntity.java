package somdudewillson.cyberhive.common.tileentity;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.block.NanitePlantGrowerBlock;
import somdudewillson.cyberhive.common.block.NaniteStemBlock;
import somdudewillson.cyberhive.common.nanitedatacloud.NanitePlantData;
import somdudewillson.cyberhive.common.nanitedatacloud.NanitePlantData.PlantDataField;
import somdudewillson.cyberhive.common.utils.GenericUtils;

public class NanitePlantGrowerTileEntity extends BlockEntity {
	
	private static String originKey = "origin_dir";
	private Direction originDir = Direction.UP;
	private static String spreadKey = "hasSpread";
	private boolean hasSpread = false;
	private static String spreadDirKey = "spread_dir";
	private byte spreadDir = NaniteStemBlock.VECTOR_TO_CORE_DIR.get(new Vec3i(0,-1,0)).byteValue();
	private static String energyKey = "energy";
	private byte energy = Byte.MIN_VALUE;
	private static String branchDepthKey = "branch_depth";
	private byte branchDepth = Byte.MIN_VALUE;
	private static String growthKey = "growth_data";
	private NanitePlantData growthData = new NanitePlantData();
	
	private final byte tickOffset;
	private final ArrayList<GrowthContext> queuedGrowthContexts = new ArrayList<>(3*3*3);
	
	public NanitePlantGrowerTileEntity(BlockPos pPos, BlockState pBlockState) {
		super(CyberBlocks.NANITE_PLANT_GROWER_TET.get(), pPos, pBlockState);

		tickOffset = (byte) (this.hashCode()&15);
		
		this.setChanged();
	}
	
	public void setCreationVariables(Direction originDir, byte spreadDir, byte energy, byte branchDepth, NanitePlantData growthData) {
		this.originDir = originDir;
		this.spreadDir = spreadDir;
		this.energy = energy;
		this.branchDepth = branchDepth;
		this.growthData = growthData;
		
		this.setChanged();
	}
	
	public void setCreationVariables(Direction originDir, byte energy, NanitePlantData growthData) {
		this.setCreationVariables(
				originDir,
				NaniteStemBlock.VECTOR_TO_CORE_DIR.get(originDir.getOpposite().getNormal()).byteValue(), 
				energy,
				(byte)0,
				growthData);
	}

	@Override
    public void load(CompoundTag compound) {
        super.load(compound);
        
        originDir = Direction.from3DDataValue(compound.getInt(originKey));
        hasSpread = compound.getBoolean(spreadKey);
        spreadDir = compound.getByte(spreadDirKey);
        energy = compound.getByte(energyKey);
        branchDepth = compound.getByte(branchDepthKey);
        growthData.deserializeNBT(compound.getCompound(growthKey));
    }

	@Override
    public void saveAdditional(CompoundTag pCompound) {
		super.saveAdditional(pCompound);
        
		pCompound.putInt(originKey, originDir.get3DDataValue());        
        pCompound.putBoolean(spreadKey, hasSpread);
        pCompound.putByte(spreadDirKey, spreadDir);
        pCompound.putByte(energyKey, energy);
        pCompound.putByte(branchDepthKey, branchDepth);
        pCompound.put(growthKey, growthData.serializeNBT());
    }

	public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntityRaw) {
		if (level.isClientSide) { return; }
		NanitePlantGrowerTileEntity blockEntity = (NanitePlantGrowerTileEntity) blockEntityRaw;
		if ((level.getGameTime()+blockEntity.tickOffset & 15) != 0) { return; }

		if (!blockEntity.hasSpread) {
			if (blockEntity.energy>Byte.MIN_VALUE) {
				blockEntity.expansionPulse();
			}
			
			blockEntity.hasSpread = true;
			blockEntity.setChanged();
		} else {
			level.setBlockAndUpdate(pos, NaniteStemBlock.coreDirToBlockstate(blockEntity.spreadDir));
		}
	}
	
	private void expansionPulse() {
		queuedGrowthContexts.clear();
		NaniteStemBlock.forEachAdjacentBlockPosNoCornerCutting(worldPosition, this::expansionStep);
		
		if (queuedGrowthContexts.isEmpty()) { return; }
		GenericUtils.shuffleCollection(queuedGrowthContexts, level.getRandom());

		GrowthContext firstGrowthContext = queuedGrowthContexts.get(0);
		if (!NaniteStemBlock.isLoglike(firstGrowthContext.getTargetState())) {
			OptionalInt logLikeIdx = IntStream.range(0, queuedGrowthContexts.size())
				.filter(idx->NaniteStemBlock.isLoglike(queuedGrowthContexts.get(idx).getTargetState()))
				.findAny();
			if (logLikeIdx.isPresent()) {
				GrowthContext logGrowthContext = queuedGrowthContexts.get(logLikeIdx.getAsInt());
				queuedGrowthContexts.set(logLikeIdx.getAsInt(), firstGrowthContext);
				queuedGrowthContexts.set(0, logGrowthContext);
				firstGrowthContext = logGrowthContext;
			}
		}
		int newEnergy = energy-(firstGrowthContext.isNewGrowth()?1:0);
		if (newEnergy>=Byte.MIN_VALUE) {
			growFromContext(level, firstGrowthContext, originDir, growthData, (byte) (newEnergy), branchDepth);
		}

		int remainingEnergy = energy;
		for (int i = 1; i < queuedGrowthContexts.size(); i++) {
			GrowthContext currentGrowthContext = queuedGrowthContexts.get(i);
			
			byte persistentCost = (byte) ((currentGrowthContext.getTargetPos().hashCode()&3)>0?1:0);
			byte growthCost = (byte) (currentGrowthContext.isNewGrowth()?1:0);
			growthCost += persistentCost;
			
			if (remainingEnergy>=Byte.MIN_VALUE+growthCost) {
				newEnergy = energy-growthCost;
				if (!currentGrowthContext.isNewGrowth() && newEnergy<(Byte.MIN_VALUE+1)) { newEnergy = (Byte.MIN_VALUE+1); }
				if (newEnergy<Byte.MIN_VALUE) { continue; }
				
				growFromContext(level, currentGrowthContext, originDir, growthData, (byte) (newEnergy), branchDepth);
				remainingEnergy -= persistentCost;
			} else if (!currentGrowthContext.isNewGrowth()) {
				growFromContext(level, currentGrowthContext, originDir, growthData, (byte) (Byte.MIN_VALUE+1), branchDepth);
			}
		}
		energy = (byte) Math.max(remainingEnergy,Byte.MIN_VALUE);
		queuedGrowthContexts.clear();
	}
	private boolean expansionStep(BlockPos adj) {
		if (worldPosition.offset(NaniteStemBlock.coreDirToVector(spreadDir)).equals(adj)) {
			// Don't spread backwards
			return false;
		}
		BlockState adjState = level.getBlockState(adj);

		Direction ownFacing = this.getBlockState().getValue(NanitePlantGrowerBlock.FACING);
		Vec3i directionNormal = adj.subtract(worldPosition);
		if (NaniteStemBlock.isLoglikeOrNaniteStem(adjState)) {
			boolean isBranch = NanitePlantData.isBranch(ownFacing, directionNormal);
			if (tryQueueGrowthContext(level, queuedGrowthContexts, worldPosition, adj, originDir, ownFacing, isBranch)) {
				updateOriginGrowthData(
						adj.subtract(worldPosition),
						 countAdjacentLogLikeOrNaniteStemLike(adj, level));
				return true;
			}
		} else if (adjState.getBlock() == Blocks.AIR) {
			// Potentially grow out into the air
			
			float growthProb = NanitePlantData.getCorrespondingGrowthProbability(
					ownFacing,
					directionNormal,
					growthData,
					branchDepth
					);
			if (level.random.nextFloat() >= growthProb) {
				return false;
			}
			boolean isBranch = NanitePlantData.isBranch(ownFacing, directionNormal);
			if (countAdjacentLogLikeOrNaniteStemLike(adj, level)>(isBranch?1:2)) {
				return false;
			}

			return tryQueueGrowthContext(
					level, queuedGrowthContexts,
					worldPosition, adj, originDir, ownFacing,
					isBranch
					);
		}
		
		return false;
	}
	
	public static int countAdjacentLogLikeOrNaniteStemLike(BlockPos center, Level worldIn) {
		int adjLogs = 0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStemLike(center.above(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStemLike(center.below(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStemLike(center.north(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStemLike(center.east(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStemLike(center.south(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStemLike(center.west(), worldIn)?1:0;
		return adjLogs;
	}
	
	@Data
	protected static class GrowthContext {
		private final BlockPos targetPos;
		private final BlockState targetState;
		private final byte newSpreadDir;
		private final Direction newDirection;
		private final boolean newGrowth, isBranch;
	}
	
	public static boolean tryQueueGrowthContext(Level worldIn, ArrayList<GrowthContext> queuedGrowthContexts, BlockPos pos, BlockPos adj, Direction originDir, Direction oldDir, boolean isBranch) {
		boolean newGrowth = true;
		BlockPos newSpreadVec = pos.subtract(adj);
		byte newSpreadDir = NaniteStemBlock.VECTOR_TO_CORE_DIR.get(newSpreadVec).byteValue();
		byte existingSpreadDir = -1;
		BlockState targetState = worldIn.getBlockState(adj);
		
		if (targetState.getBlock() instanceof NanitePlantBlockA) {
			existingSpreadDir = targetState.getValue(NaniteStemBlock.CORE_DIR).byteValue();
			newGrowth = false;
		} else if (targetState.getBlock() instanceof NanitePlantBlockB) {
			existingSpreadDir = (byte) (targetState.getValue(NaniteStemBlock.CORE_DIR).byteValue()+16);
			newGrowth = false;
		}
		if (existingSpreadDir != -1 && existingSpreadDir != newSpreadDir) { return false; }

		Direction newDirection = NanitePlantData.calculateNewDirection(pos, adj, oldDir);
		if (newDirection == null) { return false; }
		
		queuedGrowthContexts.add(new GrowthContext(adj, targetState, newSpreadDir, newDirection, newGrowth, isBranch));		
		return true;
	}
	
	public static void growFromContext(Level worldIn, GrowthContext context, Direction originDir, NanitePlantData growthData, byte newEnergy, byte currentBranchDepth) {
		BlockState newGrower = NanitePlantGrowerBlock.initializeBlockState(context.getNewDirection());
		worldIn.setBlockAndUpdate(context.getTargetPos(), newGrower);
		
		BlockEntity newTileEntity = worldIn.getBlockEntity(context.getTargetPos());
		if (newTileEntity == null) {
			throw new IllegalStateException("Tile entity of new grower doesn't exist yet.");
		}
		if (!(newTileEntity instanceof NanitePlantGrowerTileEntity)) {
			throw new IllegalStateException("Tile entity of new grower is of wrong type!?");
		}
		
		NanitePlantGrowerTileEntity newGrowerEntity = (NanitePlantGrowerTileEntity) newTileEntity;
		newGrowerEntity.setCreationVariables(originDir, context.getNewSpreadDir(), newEnergy, (byte) (currentBranchDepth+(context.isBranch()?1:0)), growthData);
	}
	
	public static boolean grow(Level worldIn, BlockPos pos, BlockPos adj, Direction originDir, Direction oldDir, byte energy, NanitePlantData growthData, byte newBranchDepth) {
		ArrayList<GrowthContext> singletonContext = new ArrayList<>(1);
		boolean canGrow = tryQueueGrowthContext(worldIn, singletonContext, pos, adj, originDir, oldDir, false);
		if (!canGrow) { return false; }
		
		growFromContext(worldIn, singletonContext.get(0), originDir, growthData, energy, newBranchDepth);
		return true;
	}

	private void updateOriginGrowthData(BlockPos directionNormal, int adjacentLogLikeOrNaniteStem) {
		Direction ownFacing = this.getBlockState().getValue(NanitePlantGrowerBlock.FACING);
		PlantDataField growthDirectionKey = NanitePlantData.getCorrespondingGrowthKey(ownFacing, directionNormal);
		
		if (growthDirectionKey!=null) { growthData.incrementWeight(growthDirectionKey); }
	}
}
