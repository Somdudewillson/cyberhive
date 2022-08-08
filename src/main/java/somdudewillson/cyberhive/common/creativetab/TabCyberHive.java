
package somdudewillson.cyberhive.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import somdudewillson.cyberhive.CyberhiveMod;
import somdudewillson.cyberhive.common.CyberItems;

public class TabCyberHive extends CreativeTabs {
	public static final CreativeTabs CYBERHIVE_TAB = new TabCyberHive();
	
	public TabCyberHive() {
		super(CyberhiveMod.MODID);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(CyberItems.NANITE_LUMP);
	}

	@SideOnly(Side.CLIENT)
	public boolean hasSearchBar() {
		return false;
	}
}
