
package somdudewillson.cyberhive.common.item;

import net.minecraft.world.item.Item;
import somdudewillson.cyberhive.common.block.RawNaniteGooBlock;

public class ItemNanitePile extends AbstractNaniteStorageItem {
	
	public ItemNanitePile(boolean fireResistant) {
		super((new Item.Properties()).stacksTo(64), fireResistant);
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public int getNanitesInItem() {
		return RawNaniteGooBlock.NANITES_PER_LAYER;
	}
}
