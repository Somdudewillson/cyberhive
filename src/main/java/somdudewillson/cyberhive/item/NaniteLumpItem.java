
package somdudewillson.cyberhive.item;

import somdudewillson.cyberhive.init.CyberhiveModTabs;

import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class NaniteLumpItem extends Item {
	public NaniteLumpItem() {
		super(new Item.Properties().tab(CyberhiveModTabs.TAB_CYBER_HIVE_TAB).stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 0;
	}
}
