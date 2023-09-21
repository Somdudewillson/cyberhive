package somdudewillson.cyberhive.common.block;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.utils.GenericUtils;

public abstract class NaniteStemBlock extends Block {

	public NaniteStemBlock(Properties properties) {
		super(properties);
	}

	public static final Vector3i[] CORE_DIR_TO_VECTOR = new Vector3i[] {
			new Vector3i(-1,-1,-1), // a0
			new Vector3i(-1,-1,0),  // a1
			new Vector3i(-1,-1,1),  // a2
			new Vector3i(-1,0,-1),  // a3
			new Vector3i(-1,0,0),   // a4
			new Vector3i(-1,0,1),   // a5
			new Vector3i(-1,1,-1),  // a6
			new Vector3i(-1,1,0),   // a7
			new Vector3i(-1,1,1),   // a8
			new Vector3i(0,-1,-1),  // a9
			new Vector3i(0,-1,0),   // a10
			new Vector3i(0,-1,1),   // a11
			new Vector3i(0,0,-1),   // a12
			new Vector3i(0,0,1),    // a13
			new Vector3i(0,1,-1),   // a14
			new Vector3i(0,1,0),    // a15
			new Vector3i(0,1,1),    // b0
			new Vector3i(1,-1,-1),  // b1
			new Vector3i(1,-1,0),   // b2
			new Vector3i(1,-1,1),   // b3
			new Vector3i(1,0,-1),   // b4
			new Vector3i(1,0,0),    // b5
			new Vector3i(1,0,1),    // b6
			new Vector3i(1,1,-1),   // b7
			new Vector3i(1,1,0),    // b8
			new Vector3i(1,1,1)     // b9
	};
	public static final HashMap<Vector3i,Integer> VECTOR_TO_CORE_DIR = GenericUtils.arrayToInverseMap(CORE_DIR_TO_VECTOR);
	public static final IntegerProperty CORE_DIR = IntegerProperty.create("core_dir", 0, 15);
	// Ordered so that direct adjacency comes first, then diagonals, then corners
	public static BlockPos[] diagonalOffsets = 
			BlockPos.betweenClosedStream(-1, -1, -1, 1, 1, 1)
			.filter(pos->!pos.equals(BlockPos.ZERO))
			.map(BlockPos::immutable)
			.sorted((a,b)->a.distManhattan(Vector3i.ZERO)-b.distManhattan(Vector3i.ZERO))
			.toArray(BlockPos[]::new);

	public static Vector3i coreDirToVector(int coreDir) {
		return CORE_DIR_TO_VECTOR[coreDir];
	}

	public static BlockState coreDirToBlockstate(int coreDir) {
		Block newBlock = CyberBlocks.NANITE_PLANT_A;
		if (coreDir>15) {
			coreDir -= 16;
			newBlock = CyberBlocks.NANITE_PLANT_B;
		}
		return newBlock.defaultBlockState().setValue(NaniteStemBlock.CORE_DIR, Integer.valueOf(coreDir));
	}

	public static BlockState VectorToBlockstate(Vector3i vector) {
		Integer coreDir = VECTOR_TO_CORE_DIR.get(vector);
		if (coreDir == null) { throw new IllegalArgumentException(vector.toString()+" is not a known vector."); }
	
		return coreDirToBlockstate(coreDir);
	}

	public static boolean isLoglike(BlockState state) {
		return BlockTags.LOGS.contains(state.getBlock());
	}

	public static boolean isLoglike(BlockPos pos, World worldIn) {
		return isLoglike(worldIn.getBlockState(pos));
	}

	public static boolean isNaniteStem(BlockState state) {
		return (state.getBlock() instanceof NanitePlantBlockA) || (state.getBlock() instanceof NanitePlantBlockB);
	}

	public static boolean isNaniteStem(BlockPos pos, World worldIn) {
		return isNaniteStem(worldIn.getBlockState(pos));
	}

	public static boolean isNaniteStemGrower(BlockState state) {
		return (state.getBlock() instanceof NanitePlantGrowerBlock);
	}

	public static boolean isNaniteStemGrower(BlockPos pos, World worldIn) {
		return isNaniteStemGrower(worldIn.getBlockState(pos));
	}

	public static boolean isLoglikeOrNaniteStem(BlockState state) {
		return isLoglike(state) || isNaniteStem(state);
	}

	public static boolean isLoglikeOrNaniteStem(BlockPos pos, World worldIn) {
		BlockState state = worldIn.getBlockState(pos);
		return isLoglikeOrNaniteStem(state);
	}

	public static boolean isLoglikeOrNaniteStemLike(BlockState state) {
		return isLoglikeOrNaniteStem(state) || isNaniteStemGrower(state);
	}

	public static boolean isLoglikeOrNaniteStemLike(BlockPos pos, World worldIn) {
		BlockState state = worldIn.getBlockState(pos);
		return isLoglikeOrNaniteStemLike(state);
	}

	public static BlockPos[] getDiagAdjPosArray(final BlockPos center) {
		return Stream.of(diagonalOffsets)
				.map(pos->center.offset(pos))
				.toArray(BlockPos[]::new);
	}

	public static void forEachAdjacentBlockPosNoCornerCutting(BlockPos center, Function<BlockPos, Boolean> action) {
		boolean[] skip = new boolean[CORE_DIR_TO_VECTOR.length];
		for (BlockPos adjOffset : diagonalOffsets) {
			int offsetDir = VECTOR_TO_CORE_DIR.get(adjOffset);
			if (skip[offsetDir]) { continue; }
			BlockPos adj = center.offset(adjOffset);
			
			if(action.apply(adj)) {
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.above(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.below(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.north(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.east(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.south(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.west(), skip);
				
				// diagonals
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.north().east(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.north().west(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.south().east(), skip);
				skip = NaniteStemBlock.markForSkipIfValid(adjOffset.south().west(), skip);
			}
		}
	}

	public static boolean[] markForSkipIfValid(BlockPos offset, boolean[] skip) {
		Integer dir = VECTOR_TO_CORE_DIR.get(offset);
		if (dir!=null) { skip[dir]=true; }
		return skip;
	}

}
