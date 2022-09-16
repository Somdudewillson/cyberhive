package somdudewillson.cyberhive.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.tileentity.NanitePlantGrowerTileEntity;

public class NanitePlantGrowerBlock extends Block implements ITileEntityProvider {
	public NanitePlantGrowerBlock() {
		super(Material.IRON);

		setRegistryName("nanite_plant_grower");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setSoundType(SoundType.WOOD);
	}

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new NanitePlantGrowerTileEntity();
    }
}
