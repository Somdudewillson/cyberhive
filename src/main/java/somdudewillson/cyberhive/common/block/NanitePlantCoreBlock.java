package somdudewillson.cyberhive.common.block;

import java.util.HashMap;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.tileentity.NanitePlantCoreTileEntity;
import somdudewillson.cyberhive.common.utils.MappingUtils;

public class NanitePlantCoreBlock extends Block {
	public static final IntegerProperty CORE_DIR = IntegerProperty.create("core_dir", 0, 15);
	
	public NanitePlantCoreBlock() {

		super(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 3.0F).sound(SoundType.SLIME_BLOCK));

		setRegistryName("nanite_plant_core");
		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
	}

	@Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NanitePlantCoreTileEntity();
    }
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	public static final Vector3i[] CORE_DIR_TO_VECTOR = new Vector3i[] {
			new Vector3i(-1,-1,-1),
			new Vector3i(-1,-1,0),
			new Vector3i(-1,-1,1),
			new Vector3i(-1,0,-1),
			new Vector3i(-1,0,0),
			new Vector3i(-1,0,1),
			new Vector3i(-1,1,-1),
			new Vector3i(-1,1,0),
			new Vector3i(-1,1,1),
			new Vector3i(0,-1,-1),
			new Vector3i(0,-1,0),
			new Vector3i(0,-1,1),
			new Vector3i(0,0,-1),
			new Vector3i(0,0,1),
			new Vector3i(0,1,-1),
			new Vector3i(0,1,0),
			new Vector3i(0,1,1),
			new Vector3i(1,-1,-1),
			new Vector3i(1,-1,0),
			new Vector3i(1,-1,1),
			new Vector3i(1,0,-1),
			new Vector3i(1,0,0),
			new Vector3i(1,0,1),
			new Vector3i(1,1,-1),
			new Vector3i(1,1,0),
			new Vector3i(1,1,1)
	};
	public static final HashMap<Vector3i,Integer> VECTOR_TO_CORE_DIR = MappingUtils.arrayToInverseMap(CORE_DIR_TO_VECTOR);
	public static Vector3i coreDirToVector(int coreDir) {
		return CORE_DIR_TO_VECTOR[coreDir];
	}
	public static BlockState coreDirToBlockstate(int coreDir) {
		Block newBlock = CyberBlocks.NANITE_PLANT_A;
		if (coreDir>15) {
			coreDir -= 16;
			newBlock = CyberBlocks.NANITE_PLANT_B;
		}
		return newBlock.defaultBlockState().setValue(CORE_DIR, Integer.valueOf(coreDir));
	}
	public static BlockState VectorToBlockstate(Vector3i vector) {
		Integer coreDir = VECTOR_TO_CORE_DIR.get(vector);
		if (coreDir == null) { throw new IllegalArgumentException(vector.toString()+" is not a known vector."); }

		return coreDirToBlockstate(coreDir);
	}
}
