package somdudewillson.cyberhive.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.creativetab.TabCyberHive;

public class PressurizedNaniteGooBlock extends Block {
	public static final int MAX_DENSITY = 16;
	public static final PropertyInteger DENSITY = PropertyInteger.create("density", 1, MAX_DENSITY);
	
	public PressurizedNaniteGooBlock() {
		super(Material.IRON);

		setRegistryName("pressurized_nanite_goo");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setSoundType(SoundType.SLIME);
		setCreativeTab(TabCyberHive.CYBERHIVE_TAB);
		setHardness(50);
        setDefaultState(this.blockState.getBaseState().withProperty(DENSITY, Integer.valueOf(MAX_DENSITY)));
	}
    
	@Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

	@Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

	@Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return CyberItems.NANITE_LUMP;
    }

    public int quantityDropped(Random rng) {
        return 1;
    }

	@Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rng) {
		if (worldIn.isRemote) { return; } 
		if (!worldIn.isBlockLoaded(pos)) { return; } // Prevent loading unloaded chunks with block update
		
		BlockPos[] adjacent = new BlockPos[] {pos.down(),pos.north(),pos.east(),pos.south(),pos.west(),pos.up()};
		int density = state.getValue(DENSITY);
		for (int adjIdx=0;adjIdx<adjacent.length && density>0;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			
			if (worldIn.isAirBlock(adjPos)) {
				IBlockState newState = CyberBlocks.RAW_NANITE_GOO.getDefaultState()
						.withProperty(RawNaniteGooBlock.LAYERS, 1);
				worldIn.setBlockState(adjPos, newState);
				if (rng.nextInt(16)==0) {density--;}
				continue;
			}
			IBlockState adjState = worldIn.getBlockState(adjPos);
			if (adjState.getBlock() == CyberBlocks.RAW_NANITE_GOO
					&& adjState.getValue(RawNaniteGooBlock.LAYERS)<RawNaniteGooBlock.MAX_HEIGHT) {
				IBlockState newState = adjState.withProperty(
						RawNaniteGooBlock.LAYERS,
						adjState.getValue(RawNaniteGooBlock.LAYERS)+1);
				worldIn.setBlockState(adjPos, newState);
				if (rng.nextInt(16)==0) {density--;}
			}
		}
		if (density>0) {
			worldIn.setBlockState(pos, state.withProperty(DENSITY, density));
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
		} else {
			worldIn.setBlockState(pos, CyberBlocks.RAW_NANITE_GOO
					.getDefaultState()
					.withProperty(RawNaniteGooBlock.LAYERS, RawNaniteGooBlock.MAX_HEIGHT));
		}
    }

	@Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DENSITY, Integer.valueOf((meta & (MAX_DENSITY-1)) + 1));
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(DENSITY)).intValue() - 1;
    }

    @Override public int quantityDropped(IBlockState state, int fortune, Random rng) {
    	return ((Integer)state.getValue(DENSITY)) + 1;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {DENSITY});
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    	worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
}
