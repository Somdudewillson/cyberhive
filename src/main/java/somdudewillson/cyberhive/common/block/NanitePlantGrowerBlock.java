package somdudewillson.cyberhive.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import somdudewillson.cyberhive.common.tileentity.NanitePlantGrowerTileEntity;

public class NanitePlantGrowerBlock extends Block {
	public NanitePlantGrowerBlock() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 3.0F).sound(SoundType.WOOD));

		setRegistryName("nanite_plant_grower");
		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
	}

	@Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NanitePlantGrowerTileEntity();
    }
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}
