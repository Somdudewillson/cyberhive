
package somdudewillson.cyberhive.creativetab;

import somdudewillson.cyberhive.item.ItemNaniteLump;
import somdudewillson.cyberhive.ElementsCyberhiveMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

@ElementsCyberhiveMod.ModElement.Tag
public class TabCyberHiveTab extends ElementsCyberhiveMod.ModElement {
	public TabCyberHiveTab(ElementsCyberhiveMod instance) {
		super(instance, 5);
	}

	@Override
	public void initElements() {
		tab = new CreativeTabs("tabcyber_hive_tab") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(ItemNaniteLump.block, (int) (1));
			}

			@SideOnly(Side.CLIENT)
			public boolean hasSearchBar() {
				return false;
			}
		};
	}
	public static CreativeTabs tab;
}
