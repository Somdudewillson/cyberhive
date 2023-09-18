package somdudewillson.cyberhive.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import somdudewillson.cyberhive.common.tileentity.NaniteRootTileEntity;

public class NanitePlantCoreBlock extends Block {
	public NanitePlantCoreBlock() {

		super(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 3.0F).sound(SoundType.SLIME_BLOCK));

		setRegistryName("nanite_plant_core");
	}

	@Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NaniteRootTileEntity();
    }
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}
