package somdudewillson.cyberhive.block;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Mob;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;

public class RawNaniteGooBlock extends Block {
	public static final int MAX_HEIGHT = 8;
	public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
	protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{Shapes.empty(), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
	
	public RawNaniteGooBlock() {
		super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).sound(SoundType.SLIME_BLOCK).strength(-1, 3600000).noCollission()
				.friction(0.7f).speedFactor(0.9f).jumpFactor(0.9f).randomTicks());
		this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, Integer.valueOf(MAX_HEIGHT)));
	}

	@Override
	public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return BlockPathTypes.WALKABLE;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	public VoxelShape getShape(BlockState state, BlockGetter blkGetter, BlockPos pos, CollisionContext colCtx) {
		return SHAPE_BY_LAYER[state.getValue(LAYERS)];
	}
	
	public VoxelShape getCollisionShape(BlockState state, BlockGetter blkGetter, BlockPos pos, CollisionContext colCtx) {
		return SHAPE_BY_LAYER[state.getValue(LAYERS) - 1];
	}
	
	public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blkGetter, BlockPos pos) {
		return SHAPE_BY_LAYER[state.getValue(LAYERS)];
	}
	
	public VoxelShape getVisualShape(BlockState state, BlockGetter blkGetter, BlockPos pos, CollisionContext colCtx) {
		return SHAPE_BY_LAYER[state.getValue(LAYERS)];
	}
	
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> dropsOriginal = super.getDrops(state, builder);
		if (!dropsOriginal.isEmpty())
			return dropsOriginal;
		return Collections.singletonList(new ItemStack(this, 1));
	}

	
	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		if (!world.isLoaded(pos)) { return; } // Prevent loading unloaded chunks with block update
		
		BlockPos fallPos = pos.below();
	}
}
