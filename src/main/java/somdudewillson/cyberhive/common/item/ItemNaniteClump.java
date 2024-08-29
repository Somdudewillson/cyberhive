
package somdudewillson.cyberhive.common.item;

import net.minecraft.world.item.Item;

public class ItemNaniteClump extends AbstractNaniteStorageItem {
	
	public ItemNaniteClump() {
		super((new Item.Properties()).stacksTo(16));
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}

	@Override
	public int getNanitesInItem() {
		return 9*4;
	}
}
