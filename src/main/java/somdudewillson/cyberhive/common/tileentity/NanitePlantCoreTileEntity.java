package somdudewillson.cyberhive.common.tileentity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;

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
	private byte[] growthData = new byte[26];
	
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
			if (isLoglike(adjState,adj,worldIn) || isNaniteStem(adjState)) {
				NanitePlantGrowerTileEntity.grow(worldIn, worldPosition, adj, worldPosition, (byte)(Byte.MIN_VALUE+3), growthData);
			}
		}
	}
	
	public static boolean isLoglike(BlockState state, BlockPos pos, World worldIn) {
		return BlockTags.LOGS.contains(state.getBlock());
	}
	public static boolean isNaniteStem(BlockState state) {
		return (state.getBlock() instanceof NanitePlantBlockA) || (state.getBlock() instanceof NanitePlantBlockB);
	}
	private static BlockPos[] diagonalOffsets = 
			BlockPos.betweenClosedStream(-1, -1, -1, 1, 1, 1)
			.filter(pos->!pos.equals(BlockPos.ZERO))
			.toArray(BlockPos[]::new);
	public static BlockPos[] getDiagAdjPosArray(final BlockPos center) {
		return Stream.of(diagonalOffsets)
				.map(pos->center.offset(pos))
				.toArray(BlockPos[]::new);
	}
	
	public void updateStemGrowthData(byte newDir) {
		if (growthData[newDir] == Byte.MAX_VALUE) {
			for (int i=0;i<growthData.length;i++) {
				growthData[newDir]/=2;
			}
		}
		
		growthData[newDir]++;
		this.setChanged();
	}

}
