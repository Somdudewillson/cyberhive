
package somdudewillson.cyberhive.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import somdudewillson.cyberhive.common.CyberBlocks;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class ItemNaniteBucket extends AbstractNaniteStorageItem {

	public ItemNaniteBucket() {
		super((new Item.Properties()).stacksTo(1));
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public int getNanitesInItem() {
		return RawNaniteGooBlock.NANITES_PER_LAYER*RawNaniteGooBlock.MAX_HEIGHT;
	}

	@Override
	public ItemStack[] extraItems() {
		return new ItemStack[] {new ItemStack(Items.BUCKET)};
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		int layersToPlace = RawNaniteGooBlock.MAX_HEIGHT;
		
		BlockState clickedBlockState = context.getLevel().getBlockState(context.getClickedPos());
		if (clickedBlockState.is(CyberBlocks.RAW_NANITE_GOO.get()) 
				&& clickedBlockState.getValue(RawNaniteGooBlock.LAYERS)<RawNaniteGooBlock.MAX_HEIGHT) {
			layersToPlace -= RawNaniteGooBlock.MAX_HEIGHT-clickedBlockState.getValue(RawNaniteGooBlock.LAYERS);
			context.getLevel().setBlockAndUpdate(
					context.getClickedPos(), 
					clickedBlockState.setValue(RawNaniteGooBlock.LAYERS, RawNaniteGooBlock.MAX_HEIGHT));
		}
		
		if (layersToPlace<=0) { return InteractionResult.SUCCESS; }
		BlockPos targetPos = context.getClickedPos().relative(context.getClickedFace());
		BlockState targetState = context.getLevel().getBlockState(targetPos);
		if (targetState.canBeReplaced(Fluids.FLOWING_WATER)) {

			if (!context.getPlayer().getAbilities().instabuild) {
				context.getPlayer().setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
			}
			context.getLevel().playSound(
					context.getPlayer(), 
					targetPos.getX(), targetPos.getY(), targetPos.getZ(), 
					SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
			context.getLevel().gameEvent(context.getPlayer(), GameEvent.FLUID_PLACE, targetPos);
			context.getLevel().setBlockAndUpdate(
					targetPos, 
					CyberBlocks.RAW_NANITE_GOO.get().defaultBlockState().setValue(RawNaniteGooBlock.LAYERS, layersToPlace));
			
			return InteractionResult.SUCCESS;
		}
		
        return InteractionResult.PASS;
    }
}
