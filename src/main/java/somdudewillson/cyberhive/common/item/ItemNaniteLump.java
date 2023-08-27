
package somdudewillson.cyberhive.common.item;

import net.minecraft.item.Item;
import somdudewillson.cyberhive.common.itemgroup.ItemGroupCyberHive;

public class ItemNaniteLump extends Item {
	
	public ItemNaniteLump() {
		super((new Item.Properties()).tab(ItemGroupCyberHive.CYBERHIVE_GROUP).stacksTo(64).durability(0));
		
		setRegistryName("nanite_lump");
		// setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
	}

	@Override
	public int getEnchantmentValue() {
		return 0;
	}
}
