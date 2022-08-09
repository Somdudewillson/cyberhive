
package somdudewillson.cyberhive.common.item;

import net.minecraft.item.Item;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.creativetab.TabCyberHive;

public class ItemNaniteLump extends Item {
	public ItemNaniteLump() {
		setMaxDamage(0);
		maxStackSize = 64;
		setRegistryName("nanite_lump");
		setUnlocalizedName(CyberhiveMod.MODID + "." + getRegistryName().getResourcePath());
		setCreativeTab(TabCyberHive.CYBERHIVE_TAB);
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}
}
