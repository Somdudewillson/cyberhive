package somdudewillson.cyberhive.common.tileentity;

import java.util.Arrays;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.block.NanitePlantCoreBlock;

public class NanitePlantGrowerTileEntity extends TileEntity implements ITickableTileEntity {
	
	private static String originKey = "origin_pos";
	private Vector3i originPos = null;
	private static String spreadKey = "spread";
	private boolean spread = false;
	private static String spreadDirKey = "spread_dir";
	private byte spreadDir = NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(new Vector3i(0,-1,0)).byteValue();
	private static String energyKey = "energy";
	private byte energy = Byte.MIN_VALUE;
	private static String growthKey = "growth_data";
	private byte[] growthData = new byte[27];
	
	private final byte tickOffset;
	
	public NanitePlantGrowerTileEntity() {
		super(CyberBlocks.NANITE_PLANT_GROWER_TET);

		Arrays.fill(growthData, Byte.MIN_VALUE);
		tickOffset = (byte) (this.hashCode()&15);
		
		this.setChanged();
	}
	
	public void setCreationVariables(Vector3i originPos, byte spreadDir, byte energy, byte[] growthData) {
		this.originPos = originPos;
		this.spreadDir = spreadDir;
		this.energy = energy;
		this.growthData = growthData;
		
		this.setChanged();
	}

	@Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        
        originPos = new Vector3i(compound.getInt(originKey+"x"),compound.getInt(originKey+"y"),compound.getInt(originKey+"z"));
        spread = compound.getBoolean(spreadKey);
        spreadDir = compound.getByte(spreadDirKey);
        energy = compound.getByte(energyKey);
        growthData = compound.getByteArray(growthKey);
    }

	@Override
    public CompoundNBT save(CompoundNBT pCompound) {
		pCompound = super.save(pCompound);
        
		pCompound.putInt(originKey+"x", originPos.getX());
		pCompound.putInt(originKey+"y", originPos.getY());
        pCompound.putInt(originKey+"z", originPos.getZ());
        
        pCompound.putBoolean(spreadKey, spread);
        pCompound.putByte(spreadDirKey, spreadDir);
        pCompound.putByte(energyKey, energy);
        pCompound.putByteArray(growthKey, growthData);
        
        return pCompound;
    }

	@Override
	public void tick() {
		if (level.isClientSide) { return; }
		if ((level.getGameTime()+tickOffset & 15) != 0) { return; }

		if (!spread) {
			if (energy>Byte.MIN_VALUE) {
				expansionPulse();
			}
			
			spread = true;
			this.setChanged();
		} else {
			level.setBlockAndUpdate(worldPosition, NanitePlantCoreBlock.coreDirToBlockstate(spreadDir));
		}
	}
	
	private void expansionPulse() {
		NanitePlantCoreTileEntity.forEachAdjacentBlockPosNoCornerCutting(worldPosition, this::expansionStep);
	}
	private boolean expansionStep(BlockPos adj) {
		if (worldPosition.offset(NanitePlantCoreBlock.coreDirToVector(spreadDir)).equals(adj)) {
			return false;
		}
		BlockState adjState = level.getBlockState(adj);
		
		if (NanitePlantCoreTileEntity.isLoglikeOrNaniteStem(adjState,adj,level)) {
			if (grow(level, worldPosition, spreadDir, adj, originPos, energy, growthData)) {
				updateOriginGrowthData(
						NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(worldPosition.subtract(adj)).byteValue(),
						 countAdjacentLogLikeOrNaniteStem(adj, level));
				return true;
			}
		} else if (adjState.getBlock() == Blocks.AIR) {
			// Potentially grow out into the air
			float max = 64;
			byte direction = NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(worldPosition.subtract(adj)).byteValue();
			for (byte val : growthData) { max = Math.max(max, val-Byte.MIN_VALUE); }
			max *= 1.5;
			
			if (level.random.nextFloat() >= ((growthData[direction]-Byte.MIN_VALUE)/max)) {
				return false;
			}
			if (level.random.nextFloat() < 1-((growthData[26]-Byte.MIN_VALUE)/max)
					&& countAdjacentLogLikeOrNaniteStem(adj, level)>2) {
				return false;
			}
			
			return grow(level, worldPosition, spreadDir, adj, originPos, energy, growthData);
		}
		
		return false;
	}
	
	public static int countAdjacentLogLikeOrNaniteStem(BlockPos center, World worldIn) {
		int adjLogs = 0;
		adjLogs += NanitePlantCoreTileEntity.isLoglikeOrNaniteStem(center.above(), worldIn)?1:0;
		adjLogs += NanitePlantCoreTileEntity.isLoglikeOrNaniteStem(center.below(), worldIn)?1:0;
		adjLogs += NanitePlantCoreTileEntity.isLoglikeOrNaniteStem(center.north(), worldIn)?1:0;
		adjLogs += NanitePlantCoreTileEntity.isLoglikeOrNaniteStem(center.east(), worldIn)?1:0;
		adjLogs += NanitePlantCoreTileEntity.isLoglikeOrNaniteStem(center.south(), worldIn)?1:0;
		adjLogs += NanitePlantCoreTileEntity.isLoglikeOrNaniteStem(center.west(), worldIn)?1:0;
		return adjLogs;
	}
	
	public static boolean grow(World worldIn, BlockPos pos, byte ownSpreadDir, BlockPos adj, Vector3i originPos, byte energy, byte[] growthData) {
		byte newEnergy = (byte)(energy-1);
		BlockPos newSpreadVec = pos.subtract(adj);
		byte newSpreadDir = NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(newSpreadVec).byteValue();
		byte existingSpreadDir = -1;
		BlockState targetState = worldIn.getBlockState(adj);
		
		if (targetState.getBlock() instanceof NanitePlantBlockA) {
			existingSpreadDir = targetState.getValue(NanitePlantCoreBlock.CORE_DIR).byteValue();
			newEnergy = energy;
		} else if (targetState.getBlock() instanceof NanitePlantBlockB) {
			existingSpreadDir = (byte) (targetState.getValue(NanitePlantCoreBlock.CORE_DIR).byteValue()+16);
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
		newGrowerEntity.setCreationVariables(originPos, newSpreadDir, newEnergy, growthData);
		
		return true;
	}

	private void updateOriginGrowthData(byte newDir, int adjacentLogLikeOrNaniteStem) {
		if (originPos == null) { return; }
		
		TileEntity originTile = level.getBlockEntity(new BlockPos(originPos));
		if (!(originTile instanceof NanitePlantCoreTileEntity)) { return; }
		
		NanitePlantCoreTileEntity originCoreEntity = (NanitePlantCoreTileEntity) originTile;
		originCoreEntity.updateStemGrowthData(newDir, adjacentLogLikeOrNaniteStem);
	}
}
