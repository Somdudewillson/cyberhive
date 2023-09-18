package somdudewillson.cyberhive.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NaniteRootBlock;
import somdudewillson.cyberhive.common.block.NaniteStemBlock;
import somdudewillson.cyberhive.common.nanitedatacloud.NanitePlantData;

public class NaniteRootTileEntity extends TileEntity implements ITickableTileEntity {
    public static final List<Block> logsWood = BlockTags.LOGS.getValues();
    
	private int tickOffset;
	
	private static String ageKey = "age";
	private short age = 0;
	private static String energyKey = "energy";
	private int energy = 0;
	private static String levelKey = "level";
	private byte level = 0;
	private static String growthKey = "growth_data";
	private NanitePlantData growthData = new NanitePlantData();
	
	public NaniteRootTileEntity() {
		super(CyberBlocks.NANITE_ROOT_TET);
		
		// Arrays.fill(growthData, Byte.MIN_VALUE);
		// this.setChanged();
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
        growthData.deserializeNBT(compound.getCompound(growthKey));
    }

	@Override
    public CompoundNBT save(CompoundNBT pCompound) {
		pCompound = super.save(pCompound);
        
		pCompound.putShort(ageKey, age);
		pCompound.putInt(energyKey, energy);
		pCompound.putByte(levelKey, level);
        pCompound.put(growthKey, growthData.serializeNBT());
        
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
		BlockPos[] adjacents = NaniteStemBlock.getDiagAdjPosArray(worldPosition);
		for (BlockPos adj : adjacents) {
			BlockState adjState = worldIn.getBlockState(adj);
			if (NaniteStemBlock.isLoglikeOrNaniteStem(adjState,adj,worldIn)) {
				NanitePlantGrowerTileEntity.grow(worldIn, worldPosition, adj, this.getBlockState().getValue(NaniteRootBlock.FACING), (byte)(Byte.MIN_VALUE+3), growthData);
			}
		}
	}

}
