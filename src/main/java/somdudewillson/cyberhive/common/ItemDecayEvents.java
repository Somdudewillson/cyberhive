package somdudewillson.cyberhive.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.common.item.AbstractNaniteStorageItem;
import somdudewillson.cyberhive.common.utils.WorldNaniteUtils;

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
	
	private static final int MAX_HEIGHT_STEPS = 8;
	private void dumpNanitesFromItem(Level world, ItemEntity itemEntity) {
		BlockPos spawnPos = itemEntity.blockPosition();
		int heightSteps = 0;
		while (!WorldNaniteUtils.canReplace(world.getBlockState(spawnPos))) {
			spawnPos = spawnPos.above();
			if (spawnPos.getY()>=world.getMaxBuildHeight()-1) { return; }
			if (heightSteps++ > MAX_HEIGHT_STEPS) { return; }
		}
		if (spawnPos.getY()>=world.getMaxBuildHeight()-1) { return; }
		
		world.setBlockAndUpdate(spawnPos, CyberBlocks.PRESSURIZED_NANITE_GOO.get().defaultBlockState());
		world.getBlockEntity(spawnPos, CyberBlocks.PRESSURIZED_NANITE_GOO_TET.get())
			.ifPresent(te->te.setNaniteQuantity( (short) ((AbstractNaniteStorageItem)itemEntity.getItem().getItem()).getNanitesInItemStack(itemEntity.getItem()) ));
	}

}
