package somdudewillson.cyberhive.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.NanitePlantBlockA;
import somdudewillson.cyberhive.common.block.NanitePlantBlockB;
import somdudewillson.cyberhive.common.block.NanitePlantCoreBlock;

public class NanitePlantGrowerTileEntity extends TileEntity implements ITickable {
	
	private static String originKey = "origin_pos";
	private Vec3i originPos = null;
	private static String spreadKey = "spread";
	private boolean spread = false;
	private static String spreadDirKey = "spread_dir";
	private byte spreadDir = NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(new Vec3i(0,-1,0)).byteValue();
	private static String energyKey = "energy";
	private byte energy = Byte.MIN_VALUE;
	
	private final byte tickOffset;
	
	public NanitePlantGrowerTileEntity() {
		super();
		
		tickOffset = (byte) (this.hashCode()&15);
	}
	
	public void setCreationVariables(Vec3i originPos, byte spreadDir, byte energy) {
		this.originPos = originPos;
		this.spreadDir = spreadDir;
		this.energy = energy;
		
		this.markDirty();
	}

	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        
        originPos = new Vec3i(compound.getInteger(originKey+"x"),compound.getInteger(originKey+"y"),compound.getInteger(originKey+"z"));
        spread = compound.getBoolean(spreadKey);
        spreadDir = compound.getByte(spreadDirKey);
        energy = compound.getByte(energyKey);
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        
        compound.setInteger(originKey+"x", originPos.getX());
        compound.setInteger(originKey+"y", originPos.getY());
        compound.setInteger(originKey+"z", originPos.getZ());
        
        compound.setBoolean(spreadKey, spread);
        compound.setByte(spreadDirKey, spreadDir);
        compound.setByte(energyKey, energy);
        
        return compound;
    }

	@Override
	public void update() {
		if (world.isRemote) { return; }
		if ((world.getTotalWorldTime()+tickOffset & 15) != 0) { return; }

		if (!spread) {
			if (energy>Byte.MIN_VALUE) {
				expansionPulse();
			}
			
			spread = true;
			this.markDirty();
		} else {
			world.setBlockState(pos, NanitePlantCoreBlock.coreDirToBlockstate(spreadDir));
		}
	}
	
	private void expansionPulse() {
		BlockPos[] adjacents = NanitePlantCoreTileEntity.getDiagAdjPosArray(pos);
		for (BlockPos adj : adjacents) {
			if (pos.add(NanitePlantCoreBlock.coreDirToVector(spreadDir)).equals(adj)) {
				continue;
			}
			IBlockState adjState = world.getBlockState(adj);
			
			if (NanitePlantCoreTileEntity.isLoglike(adjState,adj,world)
					|| NanitePlantCoreTileEntity.isNaniteStem(adjState)) {
				grow(world, pos, adj, originPos, energy);
			}
		}
	}
	
	public static void grow(World worldIn, BlockPos pos, BlockPos adj, Vec3i originPos, byte energy) {
		byte newEnergy = (byte)(energy-1);
		byte newSpreadDir = NanitePlantCoreBlock.VECTOR_TO_CORE_DIR.get(pos.subtract(adj)).byteValue();
		byte existingSpreadDir = -1;
		IBlockState targetState = worldIn.getBlockState(adj);
		if (targetState.getBlock() instanceof NanitePlantBlockA) {
			existingSpreadDir = targetState.getValue(NanitePlantCoreBlock.CORE_DIR).byteValue();
			newEnergy = energy;
		} else if (targetState.getBlock() instanceof NanitePlantBlockB) {
			existingSpreadDir = (byte) (targetState.getValue(NanitePlantCoreBlock.CORE_DIR).byteValue()+16);
			newEnergy = energy;
		}
		if (existingSpreadDir != -1 && existingSpreadDir != newSpreadDir) { return; }
		
		
		IBlockState newGrower = CyberBlocks.NANITE_PLANT_GROWER.getDefaultState();
		worldIn.setBlockState(adj, newGrower);
		
		TileEntity newTileEntity = worldIn.getTileEntity(adj);
		if (newTileEntity == null) {
			throw new IllegalStateException("Tile entity of new grower doiesn't exist yet.");
		}
		if (!(newTileEntity instanceof NanitePlantGrowerTileEntity)) {
			throw new IllegalStateException("Tile entity of new grower is of wrong type!?");
		}
		NanitePlantGrowerTileEntity newGrowerEntity = (NanitePlantGrowerTileEntity) newTileEntity;
		newGrowerEntity.setCreationVariables(originPos, newSpreadDir, newEnergy);
	}

}
