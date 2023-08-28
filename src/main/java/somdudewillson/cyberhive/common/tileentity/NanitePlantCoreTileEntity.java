package somdudewillson.cyberhive.common.tileentity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.block.NanitePlantCoreBlock;

public class NanitePlantCoreTileEntity extends TileEntity implements ITickableTileEntity {
    public static final List<Block> logsWood = BlockTags.LOGS.getValues();
    
	private int tickOffset;
	
	private static String ageKey = "age";
	private short age = 0;
	private static String energyKey = "energy";
	private int energy = 0;
	private static String levelKey = "level";
	private byte level = 0;
	private static String growthKey = "growth_data";
	private byte[] growthData = new byte[27];
	
	public NanitePlantCoreTileEntity() {
		super(CyberBlocks.NANITE_PLANT_CORE_TET);
		
		Arrays.fill(growthData, Byte.MIN_VALUE);
		this.setChanged();
	}
	
	@Override
    public void onLoad() {
		tickOffset = this.getBlockPos().hashCode();
    }

	@Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        
        age = compound.getShort(ageKey);
        energy = compound.getInt(energyKey);
        level = compound.getByte(levelKey);
        growthData = compound.getByteArray(growthKey);
    }

	@Override
    public CompoundNBT save(CompoundNBT pCompound) {
		pCompound = super.save(pCompound);
        
		pCompound.putShort(ageKey, age);
		pCompound.putInt(energyKey, energy);
		pCompound.putByte(levelKey, level);
		pCompound.putByteArray(growthKey, growthData);
        
        return pCompound;
    }

	@Override
	public void tick() {
		if (super.level.isClientSide()) { return; }
		if ((super.level.getGameTime()+tickOffset & 31) != 0) { return; }
		
		age++;
		energy++;
		
		if (energy>=15) {
			expansionPulse(super.level);
			energy-=15;
		}
		
		this.setChanged();
	}
	
	private void expansionPulse(World worldIn) {
		BlockPos[] adjacents = getDiagAdjPosArray(worldPosition);
		for (BlockPos adj : adjacents) {
			BlockState adjState = worldIn.getBlockState(adj);
			if (isLoglikeOrNaniteStem(adjState,adj,worldIn)) {
				NanitePlantGrowerTileEntity.grow(worldIn, worldPosition, NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(new Vector3i(0, 1, 0)).byteValue(), adj, worldPosition, (byte)(Byte.MIN_VALUE+3), growthData);
			}
		}
	}
	
	public static boolean isLoglike(BlockState state, BlockPos pos, World worldIn) {
		return BlockTags.LOGS.contains(state.getBlock());
	}
	public static boolean isLoglike(BlockPos pos, World worldIn) {
		return isLoglike(worldIn.getBlockState(pos), pos, worldIn);
	}
	public static boolean isNaniteStem(BlockState state) {
		return (state.getBlock() instanceof NanitePlantBlockA) || (state.getBlock() instanceof NanitePlantBlockB);
	}
	public static boolean isNaniteStem(BlockPos pos, World worldIn) {
		return isNaniteStem(worldIn.getBlockState(pos));
	}
	public static boolean isLoglikeOrNaniteStem(BlockState state, BlockPos pos, World worldIn) {
		return isLoglike(state, pos, worldIn) || isNaniteStem(state);
	}
	public static boolean isLoglikeOrNaniteStem(BlockPos pos, World worldIn) {
		BlockState state = worldIn.getBlockState(pos);
		return isLoglike(state, pos, worldIn) || isNaniteStem(state);
	}
	// Ordered so that direct adjacency comes first, then diagonals, then corners
	private static BlockPos[] diagonalOffsets = 
			BlockPos.betweenClosedStream(-1, -1, -1, 1, 1, 1)
			.filter(pos->!pos.equals(BlockPos.ZERO))
			.map(BlockPos::immutable)
			.sorted((a,b)->a.distManhattan(Vector3i.ZERO)-b.distManhattan(Vector3i.ZERO))
			.toArray(BlockPos[]::new);
	public static BlockPos[] getDiagAdjPosArray(final BlockPos center) {
		return Stream.of(diagonalOffsets)
				.map(pos->center.offset(pos))
				.toArray(BlockPos[]::new);
	}
	public static void forEachAdjacentBlockPosNoCornerCutting(BlockPos center, Function<BlockPos, Boolean> action) {
		boolean[] skip = new boolean[NanitePlantCoreBlock.CORE_DIR_TO_VECTOR.length];
		for (BlockPos adjOffset : diagonalOffsets) {
			int offsetDir = NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(adjOffset);
			if (skip[offsetDir]) { continue; }
			BlockPos adj = center.offset(adjOffset);
			
			if(action.apply(adj)) {
				skip = markForSkipIfValid(adjOffset.above(), skip);
				skip = markForSkipIfValid(adjOffset.below(), skip);
				skip = markForSkipIfValid(adjOffset.north(), skip);
				skip = markForSkipIfValid(adjOffset.east(), skip);
				skip = markForSkipIfValid(adjOffset.south(), skip);
				skip = markForSkipIfValid(adjOffset.west(), skip);
			}
		}
	}
	private static boolean[] markForSkipIfValid(BlockPos offset, boolean[] skip) {
		Integer dir = NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(offset);
		if (dir!=null) { skip[dir]=true; }
		return skip;
	}
	
	public void updateStemGrowthData(byte newDir, int adjacentLogLikeOrNaniteStem) {
		if (growthData[newDir] == Byte.MAX_VALUE) {
			rescaleGrowthData();
		}
		growthData[newDir]++;
		
		if (adjacentLogLikeOrNaniteStem>2) {
			if (growthData[26] == Byte.MAX_VALUE) {
				rescaleGrowthData();
			}
			growthData[26]++;
		}
		
		this.setChanged();
	}
	
	private void rescaleGrowthData() {
		for (int i=0;i<growthData.length;i++) {
			growthData[i] = (byte) ((growthData[i]-Byte.MIN_VALUE)/2+Byte.MIN_VALUE);
		}
	}

}
