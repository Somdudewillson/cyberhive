
package somdudewillson.cyberhive.common.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 0;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
		return 1F;
	}
}
