package somdudewillson.cyberhive.common;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;
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
	
	private void dumpNanitesFromItem(Level world, ItemEntity itemEntity) {
		Optional<BlockPos> spawnPos = BlockPos.findClosestMatch(itemEntity.blockPosition(), 3, 3, p -> WorldNaniteUtils.canReplace(world.getBlockState(p)));
		if (spawnPos.isPresent()) {
			world.setBlockAndUpdate(spawnPos.get(), CyberBlocks.PRESSURIZED_NANITE_GOO.get()
					.defaultBlockState()
					.setValue(RawNaniteGooBlock.FIRE_IMMUNE, itemEntity.getItem().getItem().isFireResistant()));
			world.getBlockEntity(spawnPos.get(), CyberBlocks.PRESSURIZED_NANITE_GOO_TET.get())
				.ifPresent(te->te.setNaniteQuantity( (short) ((AbstractNaniteStorageItem)itemEntity.getItem().getItem()).getNanitesInItemStack(itemEntity.getItem()) ));
		}
	}

}
