package somdudewillson.cyberhive.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.common.item.AbstractNaniteStorageItem;

public class ItemDecayEvents {
	
	@SubscribeEvent
	public void itemExpireEvent(ItemExpireEvent event) {
		ItemEntity itemEntity = event.getEntity();
		Level world = itemEntity.level();
		if (world.isClientSide()) { return; }
		
		if (itemEntity.getItem().getItem() instanceof AbstractNaniteStorageItem) {
			dumpNanitesFromItem(world, itemEntity);
		}
	}
	
	private void dumpNanitesFromItem(Level world, ItemEntity itemEntity) {
		BlockPos spawnPos = itemEntity.blockPosition();
		while (!world.getFluidState(spawnPos).isEmpty() || !world.getBlockState(spawnPos).canBeReplaced(Fluids.FLOWING_WATER)) {
			spawnPos = spawnPos.above();
			if (spawnPos.getY()>=world.getMaxBuildHeight()-1) { return; }
		}
		if (spawnPos.getY()>=world.getMaxBuildHeight()-1) { return; }
		
		world.setBlockAndUpdate(spawnPos, CyberBlocks.PRESSURIZED_NANITE_GOO.get().defaultBlockState());
		world.getBlockEntity(spawnPos, CyberBlocks.PRESSURIZED_NANITE_GOO_TET.get())
			.ifPresent(te->te.setNaniteQuantity( (short) ((AbstractNaniteStorageItem)itemEntity.getItem().getItem()).getNanitesInItemStack(itemEntity.getItem()) ));
	}

}
