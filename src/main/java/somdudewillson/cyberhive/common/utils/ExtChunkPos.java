package somdudewillson.cyberhive.common.utils;

import java.util.stream.Stream;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ExtChunkPos extends ChunkPos {

	public ExtChunkPos(BlockPos pPos) {
		super(pPos);
	}

	public ExtChunkPos(int pX, int pZ) {
		super(pX, pZ);
	}

	public ExtChunkPos(long p_i48713_1_) {
		super(p_i48713_1_);
	}

	public ExtChunkPos(ChunkPos baseChunkPos) {
		super(baseChunkPos.x, baseChunkPos.z);
	}

	public ExtChunkPos north() {
		return this.relative(Direction.NORTH);
	}

	public ExtChunkPos north(int pDistance) {
		return this.relative(Direction.NORTH, pDistance);
	}

	public ExtChunkPos south() {
		return this.relative(Direction.SOUTH);
	}

	public ExtChunkPos south(int pDistance) {
		return this.relative(Direction.SOUTH, pDistance);
	}

	public ExtChunkPos west() {
		return this.relative(Direction.WEST);
	}

	public ExtChunkPos west(int pDistance) {
		return this.relative(Direction.WEST, pDistance);
	}

	public ExtChunkPos east() {
		return this.relative(Direction.EAST);
	}

	public ExtChunkPos east(int pDistance) {
		return this.relative(Direction.EAST, pDistance);
	}

	public ExtChunkPos relative(Direction pDistance) {
		return new ExtChunkPos(this.x + pDistance.getStepX(), this.z + pDistance.getStepZ());
	}

	/**
	 * Offsets this Vector by the given distance in the specified direction.
	 */
	public ExtChunkPos relative(Direction pDirection, int pDistance) {
		return pDistance == 0 ? this
				: new ExtChunkPos(this.x + pDirection.getStepX() * pDistance,
						this.z + pDirection.getStepZ() * pDistance);
	}
	
	public BlockPos blockPosWorldSpaceToChunkSpace(BlockPos pos) {
		return pos.offset(-this.getMinBlockX(), 0, -this.getMinBlockZ());
	}
	public BlockPos blockPosChunkSpaceToWorldSpace(BlockPos pos) {
		return pos.offset(this.getMinBlockX(), 0, this.getMinBlockZ());
	}

	public static Stream<ChunkPos> rangeClosed(ChunkPos pCenter, int pRadius) {
		return ChunkPos.rangeClosed(pCenter, pRadius).map(ExtChunkPos::new);
	}

	public static Stream<ChunkPos> rangeClosed(final ChunkPos pStart, final ChunkPos pEnd) {
		return ChunkPos.rangeClosed(pStart, pEnd).map(ExtChunkPos::new);
	}

}
