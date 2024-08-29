package somdudewillson.cyberhive.common.tileentity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class PressurizedNaniteGooTileEntity extends BlockEntity {
	private static final String naniteQuantityKey = "nanite_quantity";
	@Getter
	private short naniteQuantity = RawNaniteGooBlock.MAX_NANITES*8;
	
	public void setNaniteQuantity(short newNaniteQuantity) {
		naniteQuantity = newNaniteQuantity;
		this.setChanged();
	}
	
	public PressurizedNaniteGooTileEntity(BlockPos pPos, BlockState pBlockState) {
		super(CyberBlocks.PRESSURIZED_NANITE_GOO_TET.get(), pPos, pBlockState);
		
		this.setChanged();
	}

	@Override
    public void load(CompoundTag compound) {
        super.load(compound);
        
        naniteQuantity = compound.getShort(naniteQuantityKey);
    }

	@Override
    public void saveAdditional(CompoundTag pCompound) {
		super.saveAdditional(pCompound);
                
        pCompound.putShort(naniteQuantityKey, naniteQuantity);
    }
}
