package somdudewillson.cyberhive.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.block.NanitePlantGrowerBlock;
import somdudewillson.cyberhive.common.block.NaniteRootBlock;
import somdudewillson.cyberhive.common.block.NaniteStemBlock;
import somdudewillson.cyberhive.common.nanitedatacloud.NanitePlantData;
import somdudewillson.cyberhive.common.nanitedatacloud.NanitePlantData.PlantDataField;

public class NanitePlantGrowerTileEntity extends TileEntity implements ITickableTileEntity {
	
	private static String originKey = "origin_dir";
	private Direction originDir = Direction.UP;
	private static String spreadKey = "hasSpread";
	private boolean hasSpread = false;
	private static String spreadDirKey = "spread_dir";
	private byte spreadDir = NaniteStemBlock.VECTOR_TO_CORE_DIR.get(new Vector3i(0,-1,0)).byteValue();
	private static String energyKey = "energy";
	private byte energy = Byte.MIN_VALUE;
	private static String growthKey = "growth_data";
	private NanitePlantData growthData = new NanitePlantData();
	
	private final byte tickOffset;
	
	public NanitePlantGrowerTileEntity() {
		super(CyberBlocks.NANITE_PLANT_GROWER_TET);

		tickOffset = (byte) (this.hashCode()&15);
		
		this.setChanged();
	}
	
	public void setCreationVariables(Direction originDir, byte spreadDir, byte energy, NanitePlantData growthData) {
		this.originDir = originDir;
		if (getBlockState()!=null) { level.setBlockAndUpdate(worldPosition, getBlockState().setValue(NaniteRootBlock.FACING, originDir)); }
		this.spreadDir = spreadDir;
		this.energy = energy;
		this.growthData = growthData;
		
		this.setChanged();
	}
	
	public void setCreationVariables(Direction originDir, byte energy, NanitePlantData growthData) {
		this.setCreationVariables(
				originDir, 
				NaniteStemBlock.VECTOR_TO_CORE_DIR.get(originDir.getOpposite().getNormal()).byteValue(), 
				energy, 
				growthData);
	}

	@Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        
        originDir = Direction.from3DDataValue(compound.getInt(originKey));
        hasSpread = compound.getBoolean(spreadKey);
        spreadDir = compound.getByte(spreadDirKey);
        energy = compound.getByte(energyKey);
        growthData.deserializeNBT(compound.getCompound(growthKey));
    }

	@Override
    public CompoundNBT save(CompoundNBT pCompound) {
		pCompound = super.save(pCompound);
        
		pCompound.putInt(originKey, originDir.get3DDataValue());        
        pCompound.putBoolean(spreadKey, hasSpread);
        pCompound.putByte(spreadDirKey, spreadDir);
        pCompound.putByte(energyKey, energy);
        pCompound.put(growthKey, growthData.serializeNBT());
        
        return pCompound;
    }

	@Override
	public void tick() {
		if (level.isClientSide) { return; }
		if ((level.getGameTime()+tickOffset & 15) != 0) { return; }

		if (!hasSpread) {
			if (energy>Byte.MIN_VALUE) {
				expansionPulse();
			}
			
			hasSpread = true;
			this.setChanged();
		} else {
			level.setBlockAndUpdate(worldPosition, NaniteStemBlock.coreDirToBlockstate(spreadDir));
		}
	}
	
	private void expansionPulse() {
		NaniteStemBlock.forEachAdjacentBlockPosNoCornerCutting(worldPosition, this::expansionStep);
	}
	private boolean expansionStep(BlockPos adj) {
		if (worldPosition.offset(NaniteStemBlock.coreDirToVector(spreadDir)).equals(adj)) {
			// Don't spread backwards
			return false;
		}
		BlockState adjState = level.getBlockState(adj);
		
		if (NaniteStemBlock.isLoglikeOrNaniteStem(adjState,adj,level)) {
			if (grow(level, worldPosition, adj, originDir, energy, growthData)) {
				updateOriginGrowthData(
						adj.subtract(worldPosition),
						 countAdjacentLogLikeOrNaniteStem(adj, level));
				return true;
			}
		} else if (adjState.getBlock() == Blocks.AIR) {
			// Potentially grow out into the air
			Direction ownFacing = this.getBlockState().getValue(NanitePlantGrowerBlock.FACING);
			Vector3i directionNormal = adj.subtract(worldPosition);
			
			float growthProb = NanitePlantData.getCorrespondingGrowthProbability(
					ownFacing,
					directionNormal,
					growthData
					);
			if (level.random.nextFloat() >= growthProb) {
				return false;
			}
			if (countAdjacentLogLikeOrNaniteStem(adj, level)>2) {
				return false;
			}

			return grow(level, worldPosition, adj, originDir, energy, growthData);
		}
		
		return false;
	}
	
	public static int countAdjacentLogLikeOrNaniteStem(BlockPos center, World worldIn) {
		int adjLogs = 0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStem(center.above(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStem(center.below(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStem(center.north(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStem(center.east(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStem(center.south(), worldIn)?1:0;
		adjLogs += NaniteStemBlock.isLoglikeOrNaniteStem(center.west(), worldIn)?1:0;
		return adjLogs;
	}
	
	public static boolean grow(World worldIn, BlockPos pos, BlockPos adj, Direction originDir, byte energy, NanitePlantData growthData) {
		byte newEnergy = (byte)(energy-1);
		BlockPos newSpreadVec = pos.subtract(adj);
		byte newSpreadDir = NaniteStemBlock.VECTOR_TO_CORE_DIR.get(newSpreadVec).byteValue();
		byte existingSpreadDir = -1;
		BlockState targetState = worldIn.getBlockState(adj);
		
		if (targetState.getBlock() instanceof NanitePlantBlockA) {
			existingSpreadDir = targetState.getValue(NaniteStemBlock.CORE_DIR).byteValue();
			newEnergy = energy;
		} else if (targetState.getBlock() instanceof NanitePlantBlockB) {
			existingSpreadDir = (byte) (targetState.getValue(NaniteStemBlock.CORE_DIR).byteValue()+16);
			newEnergy = energy;
		}
		if (existingSpreadDir != -1 && existingSpreadDir != newSpreadDir) { return false; }
		
		BlockState newGrower = CyberBlocks.NANITE_PLANT_GROWER.defaultBlockState();
		worldIn.setBlockAndUpdate(adj, newGrower);
		
		TileEntity newTileEntity = worldIn.getBlockEntity(adj);
		if (newTileEntity == null) {
			throw new IllegalStateException("Tile entity of new grower doesn't exist yet.");
		}
		if (!(newTileEntity instanceof NanitePlantGrowerTileEntity)) {
			throw new IllegalStateException("Tile entity of new grower is of wrong type!?");
		}
		NanitePlantGrowerTileEntity newGrowerEntity = (NanitePlantGrowerTileEntity) newTileEntity;
		newGrowerEntity.setCreationVariables(originDir, newSpreadDir, newEnergy, growthData);
		
		return true;
	}

	private void updateOriginGrowthData(BlockPos directionNormal, int adjacentLogLikeOrNaniteStem) {
		Direction ownFacing = this.getBlockState().getValue(NanitePlantGrowerBlock.FACING);
		PlantDataField growthDirectionKey = NanitePlantData.getCorrespondingGrowthKey(ownFacing, directionNormal);
		
		if (growthDirectionKey!=null) { growthData.incrementWeight(growthDirectionKey); }
	}
}
