package somdudewillson.cyberhive.common.tileentity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NaniteRootBlock;
import somdudewillson.cyberhive.common.block.NaniteStemBlock;
import somdudewillson.cyberhive.common.nanitedatacloud.NanitePlantData;

public class NaniteRootTileEntity extends BlockEntity {
    public static final List<Block> logsWood = ForgeRegistries.BLOCKS.tags().getTag(BlockTags.LOGS).stream().toList();
    
	private int tickOffset;
	
	private static String ageKey = "age";
	private short age = 0;
	private static String energyKey = "energy";
	private int energy = 0;
	private static String levelKey = "level";
	private byte level = 0;
	private static String growthKey = "growth_data";
	private NanitePlantData growthData = new NanitePlantData();
	
	public NaniteRootTileEntity(BlockPos pPos, BlockState pBlockState) {
		super(CyberBlocks.NANITE_ROOT_TET.get(), pPos, pBlockState);
		
		// Arrays.fill(growthData, Byte.MIN_VALUE);
		// this.setChanged();
	}
	
	@Override
    public void onLoad() {
		tickOffset = this.getBlockPos().hashCode();
    }

	@Override
    public void load(CompoundTag compound) {
        super.load(compound);
        
        age = compound.getShort(ageKey);
        energy = compound.getInt(energyKey);
        level = compound.getByte(levelKey);
        growthData.deserializeNBT(compound.getCompound(growthKey));
    }

	@Override
    public void saveAdditional(CompoundTag pCompound) {
		super.saveAdditional(pCompound);
        
		pCompound.putShort(ageKey, age);
		pCompound.putInt(energyKey, energy);
		pCompound.putByte(levelKey, level);
        pCompound.put(growthKey, growthData.serializeNBT());
    }

	public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntityRaw) {
		if (level.isClientSide()) { return; }
		NaniteRootTileEntity blockEntity = (NaniteRootTileEntity) blockEntityRaw;
		if ((level.getGameTime()+blockEntity.tickOffset & 31) != 0) { return; }
		
		blockEntity.age++;
		blockEntity.energy++;
		
		if (blockEntity.energy>=15) {
			blockEntity.expansionPulse(level);
			blockEntity.energy-=15;
		}
		
		blockEntity.setChanged();
	}
	
	private void expansionPulse(Level worldIn) {
		BlockPos[] adjacents = NaniteStemBlock.getDiagAdjPosArray(worldPosition);
		for (BlockPos adj : adjacents) {
			BlockState adjState = worldIn.getBlockState(adj);
			if (NaniteStemBlock.isLoglikeOrNaniteStemLike(adjState)) {
				NanitePlantGrowerTileEntity.grow(
						worldIn, 
						worldPosition, adj, 
						this.getBlockState().getValue(NaniteRootBlock.FACING), this.getBlockState().getValue(NaniteRootBlock.FACING), 
						(byte)(Byte.MIN_VALUE+3), growthData, (byte)0
						);
			}
		}
	}

}
