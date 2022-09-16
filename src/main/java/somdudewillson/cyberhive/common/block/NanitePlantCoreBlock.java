package somdudewillson.cyberhive.common.block;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.creativetab.TabCyberHive;
import somdudewillson.cyberhive.common.tileentity.NanitePlantCoreTileEntity;
import somdudewillson.cyberhive.common.utils.MappingUtils;

public class NanitePlantCoreBlock extends Block implements ITileEntityProvider {
	public static final PropertyInteger CORE_DIR = PropertyInteger.create("core_dir", 0, 15);
	
	public NanitePlantCoreBlock() {
		super(Material.IRON);

		setRegistryName("nanite_plant_core");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setSoundType(SoundType.SLIME);
		setCreativeTab(TabCyberHive.CYBERHIVE_TAB);
	}

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new NanitePlantCoreTileEntity();
    }
	
	public static final Vec3i[] CORE_DIR_TO_VECTOR = new Vec3i[] {
			new Vec3i(-1,-1,-1),
			new Vec3i(-1,-1,0),
			new Vec3i(-1,-1,1),
			new Vec3i(-1,0,-1),
			new Vec3i(-1,0,0),
			new Vec3i(-1,0,1),
			new Vec3i(-1,1,-1),
			new Vec3i(-1,1,0),
			new Vec3i(-1,1,1),
			new Vec3i(0,-1,-1),
			new Vec3i(0,-1,0),
			new Vec3i(0,-1,1),
			new Vec3i(0,0,-1),
			new Vec3i(0,0,1),
			new Vec3i(0,1,-1),
			new Vec3i(0,1,0),
			new Vec3i(0,1,1),
			new Vec3i(1,-1,-1),
			new Vec3i(1,-1,0),
			new Vec3i(1,-1,1),
			new Vec3i(1,0,-1),
			new Vec3i(1,0,0),
			new Vec3i(1,0,1),
			new Vec3i(1,1,-1),
			new Vec3i(1,1,0),
			new Vec3i(1,1,1)
	};
	public static final HashMap<Vec3i,Integer> VECTOR_TO_CORE_DIR = MappingUtils.arrayToInverseMap(CORE_DIR_TO_VECTOR);
	public static Vec3i coreDirToVector(int coreDir) {
		return CORE_DIR_TO_VECTOR[coreDir];
	}
	public static IBlockState coreDirToBlockstate(int coreDir) {
		Block newBlock = CyberBlocks.NANITE_PLANT_A;
		if (coreDir>15) {
			coreDir -= 16;
			newBlock = CyberBlocks.NANITE_PLANT_B;
		}
		return newBlock.getDefaultState().withProperty(CORE_DIR, Integer.valueOf(coreDir));
	}
	public static IBlockState VectorToBlockstate(Vec3i vector) {
		Integer coreDir = VECTOR_TO_CORE_DIR.get(vector);
		if (coreDir == null) { throw new IllegalArgumentException(vector.toString()+" is not a known vector."); }

		return coreDirToBlockstate(coreDir);
	}
}
