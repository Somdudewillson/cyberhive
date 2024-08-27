
package somdudewillson.cyberhive.common.item;

import net.minecraft.world.item.Item;

public class ItemNaniteLump extends Item {
	
	public ItemNaniteLump() {
		super((new Item.Properties()).stacksTo(64));
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}
}
