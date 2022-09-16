package somdudewillson.cyberhive.common.tileentity;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.utils.OreDictUtils;

public class NanitePlantCoreTileEntity extends TileEntity implements ITickable {
    public static final NonNullList<ItemStack> logsWood = OreDictionary.getOres("logWood");
    
	private int tickOffset;
	
	private static String ageKey = "age";
	private short age = 0;
	private static String energyKey = "energy";
	private int energy = 0;
	private static String levelKey = "level";
	private byte level = 0;
	private static String growthKey = "growth_data";
	private byte[] growthData = new byte[26];
	
	@Override
    public void onLoad() {
		tickOffset = this.getPos().hashCode();
    }

	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        age = compound.getShort(ageKey);
        energy = compound.getInteger(energyKey);
        level = compound.getByte(levelKey);
        growthData = compound.getByteArray(growthKey);
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        
        compound.setShort(ageKey, age);
        compound.setInteger(energyKey, energy);
        compound.setByte(levelKey, level);
        compound.setByteArray(growthKey, growthData);
        
        return compound;
    }

	@Override
	public void update() {
		if (world.isRemote) { return; }
		if ((world.getTotalWorldTime()+tickOffset & 31) != 0) { return; }
		
		age++;
		energy++;
		
		if (energy>=15) {
			expansionPulse(world);
			energy-=15;
		}
		
		this.markDirty();
	}
	
	private void expansionPulse(World worldIn) {
		BlockPos[] adjacents = getDiagAdjPosArray(pos);
		for (BlockPos adj : adjacents) {
			IBlockState adjState = worldIn.getBlockState(adj);
			if (isLoglike(adjState,adj,worldIn) || isNaniteStem(adjState)) {
				NanitePlantGrowerTileEntity.grow(worldIn, pos, adj, pos, (byte)(Byte.MIN_VALUE+3));
			}
		}
	}
	
	public static boolean isLoglike(IBlockState state, BlockPos pos, World worldIn) {
		ItemStack blockItem = state.getBlock().getPickBlock(state, null, worldIn, pos, null);
		return OreDictUtils.matchesAny(false, logsWood, blockItem);
	}
	public static boolean isNaniteStem(IBlockState state) {
		return (state.getBlock() instanceof NanitePlantBlockA) || (state.getBlock() instanceof NanitePlantBlockB);
	}
	private static BlockPos[] diagonalOffsets = 
			StreamSupport.stream(BlockPos.getAllInBox(-1, -1, -1, 1, 1, 1).spliterator(),false)
			.filter(pos->!pos.equals(BlockPos.ORIGIN))
			.toArray(BlockPos[]::new);
	public static BlockPos[] getDiagAdjPosArray(final BlockPos center) {
		return Stream.of(diagonalOffsets)
				.map(pos->center.add(pos))
				.toArray(BlockPos[]::new);
	}

}
