package somdudewillson.cyberhive.common.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.CyberItems;
import somdudewillson.cyberhive.common.creativetab.TabCyberHive;

public class RawNaniteGooBlock extends Block {
	public static final int MAX_HEIGHT = 8;
	public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, MAX_HEIGHT);
	protected static final AxisAlignedBB[] SHAPE_BY_LAYER = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	
	public RawNaniteGooBlock() {
		super(Material.IRON);

		setRegistryName("raw_nanite_goo");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setSoundType(SoundType.SLIME);
		setCreativeTab(TabCyberHive.CYBERHIVE_TAB);
        setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, Integer.valueOf(MAX_HEIGHT)));
	}

	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE_BY_LAYER[((Integer)state.getValue(LAYERS)).intValue()];
    }
    
	@Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
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

    @Nullable
	@Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        int i = ((Integer)blockState.getValue(LAYERS)).intValue() - 1;
        float f = 1f/MAX_HEIGHT;
        AxisAlignedBB axisalignedbb = blockState.getBoundingBox(worldIn, pos);
        return new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.maxX, (double)((float)i * f), axisalignedbb.maxZ);
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

		Tuple<BlockPos,IBlockState> newTarget = tryFall(state, worldIn, pos);		
		pos = newTarget.getFirst();
		state = newTarget.getSecond();
		
		BlockPos[] adjacent = new BlockPos[] {pos.north(),pos.east(),pos.south(),pos.west()};
		int layers = state.getValue(LAYERS);
		for (int adjIdx=0;adjIdx<adjacent.length && layers>1;adjIdx++) {
			BlockPos adjPos = adjacent[adjIdx];
			
			if (worldIn.isAirBlock(adjPos)) {
				IBlockState newState = state.getBlock().getDefaultState().withProperty(LAYERS, 1);
				worldIn.setBlockState(adjPos, newState);
				tryFall(newState, worldIn, adjPos);
				layers--;
				continue;
			}
			
			IBlockState adjState = worldIn.getBlockState(adjPos);
			Block adjBlock = adjState.getBlock();
			if (adjBlock == CyberBlocks.RAW_NANITE_GOO
					&& adjState.getValue(LAYERS)<layers-1) {
				IBlockState newState = adjState.withProperty(LAYERS, adjState.getValue(LAYERS)+1);
				worldIn.setBlockState(adjPos, newState);
				tryFall(newState, worldIn, adjPos);
				layers--;
				continue;
			}
		}
		
		IBlockState belowBlockState = worldIn.getBlockState(pos.down());
		if (belowBlockState.getBlock() == Blocks.GRASS) {
			worldIn.setBlockState(pos.down(), CyberBlocks.NANITE_GRASS.getDefaultState());
			layers--;
		}
		
		if (layers>0) {
			worldIn.setBlockState(pos, state.withProperty(LAYERS, layers));
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
		} else {
			worldIn.setBlockToAir(pos);
		}
    }
	
	private Tuple<BlockPos,IBlockState> tryFall(IBlockState state, World worldIn, BlockPos pos) {
		Tuple<BlockPos,IBlockState> result = new Tuple<BlockPos,IBlockState>(pos,state);
		
		boolean keepFalling = false;
		do {
			keepFalling = false;
			pos = result.getFirst();
			state = result.getSecond();
			
			BlockPos fallPos = pos.down();
			if (worldIn.isAirBlock(fallPos)) {
				worldIn.setBlockState(fallPos, state);
				worldIn.setBlockToAir(pos);
				
				result = new Tuple<BlockPos,IBlockState>(fallPos, state);
				keepFalling = true;
				continue;
			}
			
			IBlockState fallState = worldIn.getBlockState(fallPos);
			if (fallState.getBlock() == CyberBlocks.RAW_NANITE_GOO
					&& fallState.getValue(LAYERS)<MAX_HEIGHT) {
				int targetLayers = fallState.getValue(LAYERS);
				int ownLayers = state.getValue(LAYERS);
				int availableSpace = MAX_HEIGHT-targetLayers;
				
				IBlockState newState;
				if (availableSpace>=ownLayers) {
					newState = fallState.withProperty(LAYERS, targetLayers+ownLayers);
					worldIn.setBlockState(fallPos, fallState.withProperty(LAYERS, targetLayers+ownLayers));
					worldIn.setBlockToAir(pos);
					
					result = new Tuple<BlockPos,IBlockState>(fallPos,newState);
				} else {
					worldIn.setBlockState(fallPos, fallState.withProperty(LAYERS, MAX_HEIGHT));
					newState = state.withProperty(LAYERS, ownLayers-availableSpace);
					worldIn.setBlockState(pos, newState);
					
					result = new Tuple<BlockPos,IBlockState>(pos,newState);
				}
			}
		} while (keepFalling);
		
		return result;
	}

	@Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LAYERS, Integer.valueOf((meta & (MAX_HEIGHT-1)) + 1));
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LAYERS)).intValue() - 1;
    }

    @Override public int quantityDropped(IBlockState state, int fortune, Random rng) {
    	return ((Integer)state.getValue(LAYERS)) + 1;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {LAYERS});
    }
    
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    	worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
}
