package somdudewillson.cyberhive.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import somdudewillson.cyberhive.CyberhiveMod;

public class NanitePlantBlockB extends Block {
	public NanitePlantBlockB() {
		super(Material.IRON);

		setRegistryName("nanite_plant_b");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setSoundType(SoundType.WOOD);
        setDefaultState(this.blockState.getBaseState()
        		.withProperty(NanitePlantCoreBlock.CORE_DIR, Integer.valueOf(0)));
	}

	@Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(NanitePlantCoreBlock.CORE_DIR, Integer.valueOf(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(NanitePlantCoreBlock.CORE_DIR)).intValue();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {NanitePlantCoreBlock.CORE_DIR});
    }
}
